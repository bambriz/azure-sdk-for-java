// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.containerservicefleet.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.containerservicefleet.ContainerServiceFleetManager;
import com.azure.resourcemanager.containerservicefleet.models.ManagedClusterUpdate;
import com.azure.resourcemanager.containerservicefleet.models.ManagedClusterUpgradeSpec;
import com.azure.resourcemanager.containerservicefleet.models.ManagedClusterUpgradeType;
import com.azure.resourcemanager.containerservicefleet.models.NodeImageSelection;
import com.azure.resourcemanager.containerservicefleet.models.NodeImageSelectionType;
import com.azure.resourcemanager.containerservicefleet.models.UpdateGroup;
import com.azure.resourcemanager.containerservicefleet.models.UpdateRun;
import com.azure.resourcemanager.containerservicefleet.models.UpdateRunStrategy;
import com.azure.resourcemanager.containerservicefleet.models.UpdateStage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class UpdateRunsCreateOrUpdateMockTests {
    @Test
    public void testCreateOrUpdate() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr =
            "{\"properties\":{\"provisioningState\":\"Succeeded\",\"updateStrategyId\":\"qumiek\",\"strategy\":{\"stages\":[{\"name\":\"zikhl\",\"groups\":[{\"name\":\"hdgqggeb\"},{\"name\":\"unygaeqid\"},{\"name\":\"qfatpxllrxcyjm\"},{\"name\":\"a\"}],\"afterStageWaitInSeconds\":1120671201},{\"name\":\"varmywdmj\",\"groups\":[{\"name\":\"bjhhyx\"}],\"afterStageWaitInSeconds\":1337445939},{\"name\":\"lyc\",\"groups\":[{\"name\":\"hp\"},{\"name\":\"xkgymareqnajxqu\"},{\"name\":\"jhkycub\"}],\"afterStageWaitInSeconds\":1237082530}]},\"managedClusterUpdate\":{\"upgrade\":{\"type\":\"Full\",\"kubernetesVersion\":\"sofwqmzqalkrmnji\"},\"nodeImageSelection\":{\"type\":\"Consistent\"}},\"status\":{\"status\":{\"startTime\":\"2021-03-03T15:53:26Z\",\"completedTime\":\"2021-05-14T23:31:40Z\",\"state\":\"Skipped\"},\"stages\":[{\"status\":{},\"name\":\"aabjyvayffimrz\",\"groups\":[{},{},{}],\"afterStageWaitStatus\":{}},{\"status\":{},\"name\":\"gsexne\",\"groups\":[{}],\"afterStageWaitStatus\":{}},{\"status\":{},\"name\":\"wmewzsyy\",\"groups\":[{},{}],\"afterStageWaitStatus\":{}}],\"nodeImageSelection\":{\"selectedNodeImageVersions\":[{}]}}},\"eTag\":\"judpfrxt\",\"id\":\"hzv\",\"name\":\"ytdw\",\"type\":\"qbrqubpaxhexiili\"}";

        Mockito.when(httpResponse.getStatusCode()).thenReturn(200);
        Mockito.when(httpResponse.getHeaders()).thenReturn(new HttpHeaders());
        Mockito
            .when(httpResponse.getBody())
            .thenReturn(Flux.just(ByteBuffer.wrap(responseStr.getBytes(StandardCharsets.UTF_8))));
        Mockito
            .when(httpResponse.getBodyAsByteArray())
            .thenReturn(Mono.just(responseStr.getBytes(StandardCharsets.UTF_8)));
        Mockito
            .when(httpClient.send(httpRequest.capture(), Mockito.any()))
            .thenReturn(
                Mono
                    .defer(
                        () -> {
                            Mockito.when(httpResponse.getRequest()).thenReturn(httpRequest.getValue());
                            return Mono.just(httpResponse);
                        }));

        ContainerServiceFleetManager manager =
            ContainerServiceFleetManager
                .configure()
                .withHttpClient(httpClient)
                .authenticate(
                    tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                    new AzureProfile("", "", AzureEnvironment.AZURE));

        UpdateRun response =
            manager
                .updateRuns()
                .define("ivgvvcna")
                .withExistingFleet("kallatmel", "uipiccjzk")
                .withUpdateStrategyId("nxxmueedndrdv")
                .withStrategy(
                    new UpdateRunStrategy()
                        .withStages(
                            Arrays
                                .asList(
                                    new UpdateStage()
                                        .withName("wq")
                                        .withGroups(Arrays.asList(new UpdateGroup().withName("healmfmtda")))
                                        .withAfterStageWaitInSeconds(687186912),
                                    new UpdateStage()
                                        .withName("dvwvgpio")
                                        .withGroups(Arrays.asList(new UpdateGroup().withName("xrtfudxep")))
                                        .withAfterStageWaitInSeconds(1902888428),
                                    new UpdateStage()
                                        .withName("qagvrvm")
                                        .withGroups(Arrays.asList(new UpdateGroup().withName("ukghimdblxgw")))
                                        .withAfterStageWaitInSeconds(1772217773))))
                .withManagedClusterUpdate(
                    new ManagedClusterUpdate()
                        .withUpgrade(
                            new ManagedClusterUpgradeSpec()
                                .withType(ManagedClusterUpgradeType.FULL)
                                .withKubernetesVersion("hfjx"))
                        .withNodeImageSelection(new NodeImageSelection().withType(NodeImageSelectionType.CONSISTENT)))
                .withIfMatch("mjwosytx")
                .withIfNoneMatch("tcs")
                .create();

        Assertions.assertEquals("qumiek", response.updateStrategyId());
        Assertions.assertEquals("zikhl", response.strategy().stages().get(0).name());
        Assertions.assertEquals("hdgqggeb", response.strategy().stages().get(0).groups().get(0).name());
        Assertions.assertEquals(1120671201, response.strategy().stages().get(0).afterStageWaitInSeconds());
        Assertions.assertEquals(ManagedClusterUpgradeType.FULL, response.managedClusterUpdate().upgrade().type());
        Assertions.assertEquals("sofwqmzqalkrmnji", response.managedClusterUpdate().upgrade().kubernetesVersion());
        Assertions
            .assertEquals(
                NodeImageSelectionType.CONSISTENT, response.managedClusterUpdate().nodeImageSelection().type());
    }
}
