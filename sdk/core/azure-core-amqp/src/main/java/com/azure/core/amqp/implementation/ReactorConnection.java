// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.core.amqp.implementation;

import com.azure.core.amqp.AmqpConnection;
import com.azure.core.amqp.AmqpEndpointState;
import com.azure.core.amqp.AmqpManagementNode;
import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.core.amqp.AmqpRetryPolicy;
import com.azure.core.amqp.AmqpSession;
import com.azure.core.amqp.AmqpShutdownSignal;
import com.azure.core.amqp.ClaimsBasedSecurityNode;
import com.azure.core.amqp.exception.AmqpException;
import com.azure.core.amqp.implementation.handler.ConnectionHandler;
import com.azure.core.amqp.implementation.handler.SessionHandler;
import com.azure.core.util.logging.ClientLogger;
import org.apache.qpid.proton.amqp.transport.ReceiverSettleMode;
import org.apache.qpid.proton.amqp.transport.SenderSettleMode;
import org.apache.qpid.proton.engine.BaseHandler;
import org.apache.qpid.proton.engine.Connection;
import org.apache.qpid.proton.engine.Session;
import org.apache.qpid.proton.message.Message;
import org.apache.qpid.proton.reactor.Reactor;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.azure.core.amqp.exception.AmqpErrorCondition.TIMEOUT_ERROR;
import static com.azure.core.amqp.implementation.AmqpLoggingUtils.addShutdownSignal;
import static com.azure.core.amqp.implementation.AmqpLoggingUtils.addSignalTypeAndResult;
import static com.azure.core.amqp.implementation.AmqpLoggingUtils.createContextWithConnectionId;
import static com.azure.core.amqp.implementation.ClientConstants.EMIT_RESULT_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.ENTITY_PATH_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.FULLY_QUALIFIED_NAMESPACE_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.HOSTNAME_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.LINK_NAME_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.SESSION_NAME_KEY;
import static com.azure.core.amqp.implementation.ClientConstants.SIGNAL_TYPE_KEY;
import static com.azure.core.util.FluxUtil.monoError;

/**
 * An AMQP connection backed by proton-j.
 */
public class ReactorConnection implements AmqpConnection {
    private static final String CBS_SESSION_NAME = "cbs-session";
    private static final String CBS_ADDRESS = "$cbs";
    private static final String CBS_LINK_NAME = "cbs";

    private static final String MANAGEMENT_SESSION_NAME = "mgmt-session";
    private static final String MANAGEMENT_ADDRESS = "$management";
    private static final String MANAGEMENT_LINK_NAME = "mgmt";

    private final ClientLogger logger;
    private final ConcurrentMap<String, SessionSubscription> sessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AmqpManagementNode> managementNodes = new ConcurrentHashMap<>();

    private final AtomicBoolean isDisposed = new AtomicBoolean();
    private final Sinks.One<AmqpShutdownSignal> shutdownSignalSink = Sinks.one();
    private final Flux<AmqpEndpointState> endpointStates;
    private final Sinks.Empty<Void> isClosedMono = Sinks.empty();

    private final String connectionId;
    private final Mono<Connection> connectionMono;
    private final ConnectionHandler handler;
    private final ReactorHandlerProvider handlerProvider;
    private final AmqpLinkProvider linkProvider;
    private final TokenManagerProvider tokenManagerProvider;
    private final MessageSerializer messageSerializer;
    private final ConnectionOptions connectionOptions;
    private final ReactorProvider reactorProvider;
    private final AmqpRetryPolicy retryPolicy;
    private final SenderSettleMode senderSettleMode;
    private final ReceiverSettleMode receiverSettleMode;
    private final Duration operationTimeout;
    private final Composite subscriptions;

    private ReactorExecutor reactorExecutor;

    private volatile ClaimsBasedSecurityChannel cbsChannel;
    private volatile AmqpChannelProcessor<RequestResponseChannel> cbsChannelProcessor;
    private volatile Connection connection;
    private final boolean isV2;

    /**
     * Creates a new AMQP connection that uses proton-j.
     *
     * @param connectionId Identifier for the connection.
     * @param connectionOptions A set of options used to create the AMQP connection.
     * @param reactorProvider Provides proton-j Reactor instances.
     * @param handlerProvider Provides {@link BaseHandler} to listen to proton-j reactor events.
     * @param linkProvider Provides amqp links for send and receive.
     * @param tokenManagerProvider Provides the appropriate token manager to authorize with CBS node.
     * @param messageSerializer Serializer to translate objects to and from proton-j {@link Message messages}.
     * @param senderSettleMode to set as {@link SenderSettleMode} on sender.
     * @param receiverSettleMode to set as {@link ReceiverSettleMode} on receiver.
     * @param isV2 (temporary) flag to use either v1 or v2 receiver.
     */
    public ReactorConnection(String connectionId, ConnectionOptions connectionOptions, ReactorProvider reactorProvider,
        ReactorHandlerProvider handlerProvider, AmqpLinkProvider linkProvider, TokenManagerProvider tokenManagerProvider,
        MessageSerializer messageSerializer, SenderSettleMode senderSettleMode,
        ReceiverSettleMode receiverSettleMode, boolean isV2) {

        this.connectionOptions = connectionOptions;
        this.reactorProvider = reactorProvider;
        this.connectionId = connectionId;
        this.logger = new ClientLogger(ReactorConnection.class, createContextWithConnectionId(connectionId));
        this.handlerProvider = handlerProvider;
        this.linkProvider = linkProvider;
        this.tokenManagerProvider = Objects.requireNonNull(tokenManagerProvider,
            "'tokenManagerProvider' cannot be null.");
        this.messageSerializer = messageSerializer;
        this.handler = handlerProvider.createConnectionHandler(connectionId, connectionOptions);

        this.retryPolicy = RetryUtil.getRetryPolicy(connectionOptions.getRetry());
        this.operationTimeout = connectionOptions.getRetry().getTryTimeout();
        this.senderSettleMode = senderSettleMode;
        this.receiverSettleMode = receiverSettleMode;
        this.isV2 = isV2;

        this.connectionMono = Mono.fromCallable(this::getOrCreateConnection)
            .flatMap(reactorConnection -> {
                final Mono<AmqpEndpointState> activeEndpoint = getEndpointStates()
                    .filter(state -> state == AmqpEndpointState.ACTIVE)
                    .next()
                    .timeout(operationTimeout, Mono.error(() -> new AmqpException(true, TIMEOUT_ERROR,
                        String.format("Connection '%s' not active within the timout: %s.", connectionId, operationTimeout),
                        handler.getErrorContext())));
                return activeEndpoint.thenReturn(reactorConnection);
            })
            .doOnError(error -> {
                if (isDisposed.getAndSet(true)) {
                    logger.verbose("Connection was already disposed: Error occurred while connection was starting.", error);
                } else {
                    closeAsync(new AmqpShutdownSignal(false, false,
                        "Error occurred while connection was starting. Error: " + error)).subscribe();
                }
            });

        this.endpointStates = this.handler.getEndpointStates()
            .takeUntilOther(shutdownSignalSink.asMono())
            .map(state -> {
                logger.verbose("State {}", state);
                return AmqpEndpointStateUtil.getConnectionState(state);
            })
            .onErrorResume(error -> {
                if (!isDisposed.getAndSet(true)) {
                    logger.verbose("Disposing of active sessions due to error.");
                    return closeAsync(new AmqpShutdownSignal(false, false, error.getMessage())).then(Mono.error(error));
                } else {
                    return Mono.error(error);
                }
            })
            .doOnComplete(() -> {
                if (!isDisposed.getAndSet(true)) {
                    logger.verbose("Disposing of active sessions due to connection close.");
                    closeAsync(new AmqpShutdownSignal(false, false, "Connection handler closed.")).subscribe();
                }
            })
            .cache(1);

        this.subscriptions = Disposables.composite(this.endpointStates.subscribe());
    }

    /**
     * Establish a connection to the broker and wait for it to active.
     *
     * @return the {@link Mono} that completes once the broker connection is established and active.
     */
    public Mono<ReactorConnection> connectAndAwaitToActive() {
        return this.connectionMono
            .handle((c, sink) -> {
                if (isDisposed()) {
                    // Today 'connectionMono' emits QPID-connection even if the endpoint terminated with
                    // 'completion' without ever emitting any state. (Had it emitted a state and never
                    // emitted 'active', then timeout-error would have happened, then 'handle' won't be
                    // running, same with endpoint terminating with any error).
                    sink.error(new AmqpException(true,
                        String.format("Connection '%s' completed without being active.", connectionId),
                        null));
                } else {
                    sink.complete();
                }
            })
            .thenReturn(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<AmqpEndpointState> getEndpointStates() {
        return endpointStates;
    }

    /**
     * Gets the shutdown signal associated with this connection. When it emits, the underlying connection is closed.
     *
     * @return Shutdown signals associated with this connection. It emits a signal when the underlying connection is
     *     closed.
     */
    @Override
    public Flux<AmqpShutdownSignal> getShutdownSignals() {
        return shutdownSignalSink.asMono().cache().flux();
    }

    @Override
    public Mono<AmqpManagementNode> getManagementNode(String entityPath) {
        return Mono.defer(() -> {
            if (isDisposed()) {
                return monoError(logger.atError().addKeyValue(ENTITY_PATH_KEY, entityPath),
                    Exceptions.propagate(new IllegalStateException("Connection is disposed. Cannot get management instance.")));
            }

            final AmqpManagementNode existing = managementNodes.get(entityPath);
            if (existing != null) {
                return Mono.just(existing);
            }

            final TokenManager tokenManager = new AzureTokenManagerProvider(connectionOptions.getAuthorizationType(),
                connectionOptions.getFullyQualifiedNamespace(), connectionOptions.getAuthorizationScope())
                .getTokenManager(getClaimsBasedSecurityNode(), entityPath);

            return tokenManager.authorize().thenReturn(managementNodes.compute(entityPath, (key, current) -> {
                if (current != null) {
                    logger.info("A management node exists already, returning it.");

                    // Close the token manager we had created during this because it is unneeded now.
                    tokenManager.close();
                    return current;
                }

                final String sessionName = entityPath + "-" + MANAGEMENT_SESSION_NAME;
                final String linkName = entityPath + "-" + MANAGEMENT_LINK_NAME;
                final String address = entityPath + "/" + MANAGEMENT_ADDRESS;

                logger.atInfo()
                    .addKeyValue(ENTITY_PATH_KEY, entityPath)
                    .addKeyValue(LINK_NAME_KEY, linkName)
                    .addKeyValue("address", address)
                    .log("Creating management node.");

                final AmqpChannelProcessor<RequestResponseChannel> requestResponseChannel =
                    createRequestResponseChannel(sessionName, linkName, address);
                return new ManagementChannel(requestResponseChannel, getFullyQualifiedNamespace(), entityPath,
                    tokenManager);
            }));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<ClaimsBasedSecurityNode> getClaimsBasedSecurityNode() {
        return connectionMono.then(Mono.fromCallable(() -> getOrCreateCBSNode()));
    }

    @Override
    public String getId() {
        return connectionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullyQualifiedNamespace() {
        return handler.getHostname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxFrameSize() {
        return handler.getMaxFrameSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getConnectionProperties() {
        return handler.getConnectionProperties();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<AmqpSession> createSession(String sessionName) {
        return connectionMono.map(connection -> {
            return sessionMap.computeIfAbsent(sessionName, key -> {
                final SessionHandler sessionHandler = handlerProvider.createSessionHandler(connectionId,
                    getFullyQualifiedNamespace(), key, connectionOptions.getRetry().getTryTimeout());
                final Session session = connection.session();

                BaseHandler.setHandler(session, sessionHandler);
                final AmqpSession amqpSession = createSession(key, session, sessionHandler);
                final Disposable subscription = amqpSession.getEndpointStates()
                    .subscribe(state -> {
                    }, error -> {
                        // If we were already disposing of the connection, the session would be removed.
                        if (isDisposed.get()) {
                            return;
                        }

                        logger.atInfo()
                            .addKeyValue(SESSION_NAME_KEY, sessionName)
                            .log("Error occurred. Removing and disposing session", error);
                        removeSession(key);
                    }, () -> {
                        // If we were already disposing of the connection, the session would be removed.
                        if (isDisposed.get()) {
                            return;
                        }

                        logger.atVerbose()
                            .addKeyValue(SESSION_NAME_KEY, sessionName)
                            .log("Complete. Removing and disposing session.");
                        removeSession(key);
                    });

                return new SessionSubscription(amqpSession, subscription);
            });
        }).flatMap(sessionSubscription -> {
            final Mono<AmqpEndpointState> activeSession = sessionSubscription.getSession().getEndpointStates()
                .filter(state -> state == AmqpEndpointState.ACTIVE)
                .next()
                .timeout(retryPolicy.getRetryOptions().getTryTimeout(), Mono.error(() -> new AmqpException(true,
                    TIMEOUT_ERROR, String.format(
                        "connectionId[%s] sessionName[%s] Timeout waiting for session to be active.", connectionId,
                    sessionName), handler.getErrorContext())))
                .doOnError(error -> {
                    // Clean up the subscription if there was an error waiting for the session to become active.

                    if (!(error instanceof AmqpException)) {
                        return;
                    }

                    final AmqpException amqpException = (AmqpException) error;
                    if (amqpException.getErrorCondition() == TIMEOUT_ERROR) {
                        final SessionSubscription removed = sessionMap.remove(sessionName);
                        removed.dispose();
                    }
                });

            return activeSession.thenReturn(sessionSubscription.getSession());
        });
    }

    /**
     * Creates a new AMQP session with the given parameters.
     *
     * @param sessionName Name of the AMQP session.
     * @param session The reactor session associated with this session.
     * @param handler Session handler for the reactor session.
     *
     * @return A new instance of AMQP session.
     */
    protected AmqpSession createSession(String sessionName, Session session, SessionHandler handler) {
        return new ReactorSession(this, session, handler, sessionName, reactorProvider,
            handlerProvider, linkProvider, getClaimsBasedSecurityNode(), tokenManagerProvider, messageSerializer,
            connectionOptions.getRetry());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeSession(String sessionName) {
        if (sessionName == null) {
            return false;
        }

        final SessionSubscription removed = sessionMap.remove(sessionName);
        if (removed != null) {
            removed.dispose();
        }

        return removed != null;
    }

    @Override
    public boolean isDisposed() {
        return isDisposed.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // Because the reactor executor schedules the pending close after the timeout, we want to give sufficient time
        // for the rest of the tasks to run.
        final Duration timeout = operationTimeout.plus(operationTimeout);
        closeAsync().block(timeout);
    }

    /**
     * Gets the active AMQP connection for this instance.
     *
     * @return The AMQP connection.
     *
     * @throws AmqpException if the {@link Connection} was not transitioned to an active state within the given
     *     {@link AmqpRetryOptions#getTryTimeout() operation timeout}.
     */
    protected Mono<Connection> getReactorConnection() {
        return connectionMono;
    }

    /**
     * Creates a bidirectional link between the message broker and the client.
     *
     * @param sessionName Name of the session.
     * @param linkName Name of the link.
     * @param entityPath Address to the message broker.
     *
     * @return A new {@link RequestResponseChannel} to communicate with the message broker.
     */
    protected AmqpChannelProcessor<RequestResponseChannel> createRequestResponseChannel(String sessionName,
        String linkName, String entityPath) {

        Objects.requireNonNull(entityPath, "'entityPath' cannot be null.");

        final Flux<RequestResponseChannel> createChannel = createSession(sessionName)
            .cast(ReactorSession.class)
            .map(reactorSession -> new RequestResponseChannel(this, getId(), getFullyQualifiedNamespace(), linkName,
                entityPath, reactorSession.session(), connectionOptions.getRetry(), handlerProvider, reactorProvider,
                messageSerializer, senderSettleMode, receiverSettleMode, handlerProvider.getMetricProvider(getFullyQualifiedNamespace(), entityPath), isV2))
            .doOnNext(e -> {
                logger.atInfo()
                    .addKeyValue(ENTITY_PATH_KEY, entityPath)
                    .addKeyValue(LINK_NAME_KEY, linkName)
                    .log("Emitting new response channel.");
            })
            // Create channel only when connection is active, to avoid repeatedly requesting and closing channels
            // after connection emits the shutdown signal.
            .repeat(() -> !this.isDisposed());

        Map<String, Object> loggingContext = createContextWithConnectionId(connectionId);
        loggingContext.put(ENTITY_PATH_KEY, entityPath);

        return createChannel
            .subscribeWith(new AmqpChannelProcessor<>(getFullyQualifiedNamespace(), channel -> channel.getEndpointStates(), retryPolicy, loggingContext));
    }

    // Note: The V1 'createRequestResponseChannel(...)' internal API will be removed once entirely on the V2 stack.
    Mono<RequestResponseChannel> newRequestResponseChannel(String sessionName, String linksNamePrefix, String entityPath) {
        assert isV2;
        Objects.requireNonNull(entityPath, "'entityPath' cannot be null.");

        return createSession(sessionName)
            .cast(ReactorSession.class)
            .map(reactorSession -> new RequestResponseChannel(this, getId(), getFullyQualifiedNamespace(), linksNamePrefix,
                entityPath, reactorSession.session(), connectionOptions.getRetry(), handlerProvider, reactorProvider,
                messageSerializer, senderSettleMode, receiverSettleMode, handlerProvider.getMetricProvider(getFullyQualifiedNamespace(), entityPath), isV2));
    }

    @Override
    public Mono<Void> closeAsync() {
        if (isDisposed.getAndSet(true)) {
            logger.verbose("Connection was already closed. Not disposing again.");
            return isClosedMono.asMono();
        }

        return closeAsync(new AmqpShutdownSignal(false, true, "Disposed by client."));
    }

    /**
     * Disposes of the connection.
     *
     * @param shutdownSignal Shutdown signal to emit.
     * @return A mono that completes when the connection is disposed.
     */
    public Mono<Void> closeAsync(AmqpShutdownSignal shutdownSignal) {
        addShutdownSignal(logger.atInfo(), shutdownSignal).log("Disposing of ReactorConnection.");
        final Sinks.EmitResult result = shutdownSignalSink.tryEmitValue(shutdownSignal);

        if (result.isFailure()) {
            // It's possible that another one was already emitted, so it's all good.
            addShutdownSignal(logger.atInfo(), shutdownSignal)
                .addKeyValue(EMIT_RESULT_KEY, result)
                .log("Unable to emit shutdown signal.");
        }

        final Mono<Void> cbsCloseOperation;
        if (cbsChannelProcessor != null) {
            cbsCloseOperation = cbsChannelProcessor.flatMap(channel -> channel.closeAsync());
        } else {
            cbsCloseOperation = Mono.empty();
        }

        final Mono<Void> managementNodeCloseOperations = Mono.when(
            Flux.fromStream(managementNodes.values().stream()).flatMap(node -> node.closeAsync()));

        final Mono<Void> closeReactor = Mono.fromRunnable(() -> {
            logger.verbose("Scheduling closeConnection work.");
            final ReactorDispatcher dispatcher = reactorProvider.getReactorDispatcher();

            if (dispatcher != null) {
                try {
                    dispatcher.invoke(() -> closeConnectionWork());
                } catch (IOException e) {
                    logger.warning("IOException while scheduling closeConnection work. Manually disposing.", e);

                    closeConnectionWork();
                } catch (RejectedExecutionException e) {
                    // Not logging error here again because we have to log the exception when we throw it.
                    logger.info("Could not schedule closeConnection work. Manually disposing.");

                    closeConnectionWork();
                }
            } else {
                closeConnectionWork();
            }
        });

        return Mono.whenDelayError(
                cbsCloseOperation.doFinally(signalType ->
                    logger.atVerbose()
                        .addKeyValue(SIGNAL_TYPE_KEY, signalType)
                        .log("Closed CBS node.")),
                managementNodeCloseOperations.doFinally(signalType ->
                    logger.atVerbose()
                        .addKeyValue(SIGNAL_TYPE_KEY, signalType)
                        .log("Closed management nodes.")))
            .then(closeReactor.doFinally(signalType ->
                logger.atVerbose()
                    .addKeyValue(SIGNAL_TYPE_KEY, signalType)
                    .log("Closed reactor dispatcher.")))
            .then(isClosedMono.asMono());
    }

    private synchronized void closeConnectionWork() {
        if (connection == null) {
            isClosedMono.emitEmpty((signalType, emitResult) -> {
                addSignalTypeAndResult(logger.atInfo(), signalType, emitResult)
                    .log("Unable to complete closeMono.");

                return false;
            });

            return;
        }

        connection.close();
        handler.close();

        final ArrayList<Mono<Void>> closingSessions = new ArrayList<>();
        sessionMap.values().forEach(link -> closingSessions.add(link.isClosed()));

        // We shouldn't need to add a timeout to this operation because executorCloseMono schedules its last
        // remaining work after OperationTimeout has elapsed and closes afterwards.
        final Mono<Void> closedExecutor = reactorExecutor != null ? Mono.defer(() -> {
            synchronized (this) {
                logger.info("Closing executor.");
                return reactorExecutor.closeAsync();
            }
        }) : Mono.empty();

        // Close all the children and the ReactorExecutor.
        final Mono<Void> closeSessionAndExecutorMono = Mono.when(closingSessions)
            .timeout(operationTimeout)
            .onErrorResume(error -> {
                logger.info("Timed out waiting for all sessions to close.");
                return Mono.empty();
            })
            .then(closedExecutor)
            .then(Mono.fromRunnable(() -> {
                isClosedMono.emitEmpty((signalType, result) -> {
                    addSignalTypeAndResult(logger.atWarning(), signalType, result)
                        .log("Unable to emit connection closed signal.");
                    return false;
                });

                subscriptions.dispose();
            }));

        subscriptions.add(closeSessionAndExecutorMono.subscribe());
    }

    private synchronized ClaimsBasedSecurityNode getOrCreateCBSNode() {
        if (cbsChannel == null) {
            logger.info("Setting CBS channel.");
            cbsChannelProcessor = createRequestResponseChannel(CBS_SESSION_NAME, CBS_LINK_NAME, CBS_ADDRESS);
            cbsChannel = new ClaimsBasedSecurityChannel(
                cbsChannelProcessor,
                connectionOptions.getTokenCredential(), connectionOptions.getAuthorizationType(),
                connectionOptions.getRetry());
        }

        return cbsChannel;
    }

    private synchronized Connection getOrCreateConnection() throws IOException {
        if (connection == null) {
            logger.atInfo()
                .addKeyValue(HOSTNAME_KEY, handler.getHostname())
                .addKeyValue("port", handler.getProtocolPort())
                .log("Creating and starting connection.");

            final Reactor reactor = reactorProvider.createReactor(connectionId, handler.getMaxFrameSize());
            connection = reactor.connectionToHost(handler.getHostname(), handler.getProtocolPort(), handler);

            final ReactorExceptionHandler reactorExceptionHandler = new ReactorExceptionHandler();

            reactorExecutor = reactorProvider.createExecutor(reactor, connectionId,
                connectionOptions.getFullyQualifiedNamespace(), reactorExceptionHandler, connectionOptions.getRetry());

            // To avoid inconsistent synchronization of executor, we set this field with the closeAsync method.
            // It will not be kicked off until subscribed to.
            final Mono<Void> executorCloseMono = Mono.defer(() -> {
                synchronized (this) {
                    return reactorExecutor.closeAsync();
                }
            });

            // We shouldn't need to add a timeout to this operation because executorCloseMono schedules its last
            // remaining work after OperationTimeout has elapsed and closes afterwards.
            reactorProvider.getReactorDispatcher().getShutdownSignal()
                .flatMap(signal -> {
                    reactorExceptionHandler.onConnectionShutdown(signal);
                    return executorCloseMono;
                })
                .onErrorResume(error -> {
                    reactorExceptionHandler.onConnectionError(error);
                    return executorCloseMono;
                })
                .subscribe();

            reactorExecutor.start();
        }

        return connection;
    }

    /**
     * ReactorExceptionHandler handles exceptions that occur in the reactor.
     */
    public final class ReactorExceptionHandler extends AmqpExceptionHandler {
        private ReactorExceptionHandler() {
            super();
        }

        @Override
        public void onConnectionError(Throwable exception) {
            logger.atInfo()
                .addKeyValue(FULLY_QUALIFIED_NAMESPACE_KEY, getFullyQualifiedNamespace())
                .log("onConnectionError, Starting new reactor", exception);

            if (!isDisposed.getAndSet(true)) {
                logger.atVerbose()
                    .addKeyValue(FULLY_QUALIFIED_NAMESPACE_KEY, getFullyQualifiedNamespace())
                    .log("onReactorError: Disposing.");

                closeAsync(new AmqpShutdownSignal(false, false,
                    "onReactorError: " + exception.toString()))
                    .subscribe();
            }
        }

        @Override
        void onConnectionShutdown(AmqpShutdownSignal shutdownSignal) {
            addShutdownSignal(logger.atInfo(), shutdownSignal)
                .addKeyValue(FULLY_QUALIFIED_NAMESPACE_KEY, getFullyQualifiedNamespace())
                .log("onConnectionShutdown. Shutting down.");

            if (!isDisposed.getAndSet(true)) {
                logger.atVerbose()
                    .addKeyValue(FULLY_QUALIFIED_NAMESPACE_KEY, getFullyQualifiedNamespace())
                    .log("onConnectionShutdown: disposing.");

                closeAsync(shutdownSignal).subscribe();
            }
        }
    }

    private static final class SessionSubscription {
        private final AtomicBoolean isDisposed = new AtomicBoolean();
        private final AmqpSession session;
        private final Disposable subscription;

        private SessionSubscription(AmqpSession session, Disposable subscription) {
            this.session = session;
            this.subscription = subscription;
        }

        private AmqpSession getSession() {
            return session;
        }

        private void dispose() {
            if (isDisposed.getAndSet(true)) {
                return;
            }

            if (session instanceof ReactorSession) {
                ((ReactorSession) session).closeAsync("Closing session.", null, true)
                    .subscribe();
            } else {
                session.dispose();
            }

            subscription.dispose();
        }

        private Mono<Void> isClosed() {
            if (session instanceof ReactorSession) {
                return ((ReactorSession) session).isClosed();
            } else {
                return Mono.empty();
            }
        }
    }
}
