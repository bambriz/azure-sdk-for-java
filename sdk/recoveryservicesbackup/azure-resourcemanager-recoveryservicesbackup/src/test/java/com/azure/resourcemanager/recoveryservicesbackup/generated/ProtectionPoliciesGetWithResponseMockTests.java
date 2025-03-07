// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicesbackup.generated;

import com.azure.core.credential.AccessToken;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.recoveryservicesbackup.RecoveryServicesBackupManager;
import com.azure.resourcemanager.recoveryservicesbackup.models.ProtectionPolicyResource;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class ProtectionPoliciesGetWithResponseMockTests {
    @Test
    public void testGetWithResponse() throws Exception {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        ArgumentCaptor<HttpRequest> httpRequest = ArgumentCaptor.forClass(HttpRequest.class);

        String responseStr =
            "{\"properties\":{\"backupManagementType\":\"ProtectionPolicy\",\"protectedItemsCount\":2111335393,\"resourceGuardOperationRequests\":[\"hrg\",\"nzhctmjtsgh\",\"bcbcpz\"]},\"eTag\":\"pzeqacdldtz\",\"location\":\"pypefcpczshnuq\",\"tags\":{\"tu\":\"izupfkhu\"},\"id\":\"zx\",\"name\":\"mtvtvegwqiukvzwy\",\"type\":\"wtthaokgksk\"}";

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

        RecoveryServicesBackupManager manager =
            RecoveryServicesBackupManager
                .configure()
                .withHttpClient(httpClient)
                .authenticate(
                    tokenRequestContext -> Mono.just(new AccessToken("this_is_a_token", OffsetDateTime.MAX)),
                    new AzureProfile("", "", AzureEnvironment.AZURE));

        ProtectionPolicyResource response =
            manager
                .protectionPolicies()
                .getWithResponse("skoynuiylpc", "aewse", "vesk", com.azure.core.util.Context.NONE)
                .getValue();

        Assertions.assertEquals("pypefcpczshnuq", response.location());
        Assertions.assertEquals("izupfkhu", response.tags().get("tu"));
        Assertions.assertEquals(2111335393, response.properties().protectedItemsCount());
        Assertions.assertEquals("hrg", response.properties().resourceGuardOperationRequests().get(0));
        Assertions.assertEquals("pzeqacdldtz", response.etag());
    }
}
