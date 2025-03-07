// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datafactory.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.datafactory.models.ActivityDependency;
import com.azure.resourcemanager.datafactory.models.ActivityOnInactiveMarkAs;
import com.azure.resourcemanager.datafactory.models.ActivityPolicy;
import com.azure.resourcemanager.datafactory.models.ActivityState;
import com.azure.resourcemanager.datafactory.models.AzureDataExplorerCommandActivity;
import com.azure.resourcemanager.datafactory.models.DependencyCondition;
import com.azure.resourcemanager.datafactory.models.LinkedServiceReference;
import com.azure.resourcemanager.datafactory.models.UserProperty;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;

public final class AzureDataExplorerCommandActivityTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        AzureDataExplorerCommandActivity model = BinaryData.fromString(
            "{\"type\":\"AzureDataExplorerCommand\",\"typeProperties\":{\"command\":\"dataxxhbrysnszsehoe\",\"commandTimeout\":\"datawbykrn\"},\"linkedServiceName\":{\"referenceName\":\"bkvzwqgmfhlnqy\",\"parameters\":{\"bsaaxstnziv\":\"datayfncwiyfzu\",\"cxygjhclnyoktc\":\"dataccgtujiwzbzed\"}},\"policy\":{\"timeout\":\"datathjgbrxmxqskem\",\"retry\":\"datajjf\",\"retryIntervalInSeconds\":354823036,\"secureInput\":true,\"secureOutput\":true,\"\":{\"vwalhawoptiq\":\"datacnidubocmjiib\"}},\"name\":\"u\",\"description\":\"vtapcxsm\",\"state\":\"Inactive\",\"onInactiveMarkAs\":\"Succeeded\",\"dependsOn\":[{\"activity\":\"ylrvztaelpux\",\"dependencyConditions\":[\"Completed\",\"Failed\",\"Failed\",\"Succeeded\"],\"\":{\"llewe\":\"dataumtnrcvovhyqexu\",\"cdckcpf\":\"datagvqbsyth\"}},{\"activity\":\"mmgfwxthr\",\"dependencyConditions\":[\"Succeeded\"],\"\":{\"ckkbnaseny\":\"datamgosclhj\",\"hgcchzu\":\"datahmwzgfankeoloros\"}},{\"activity\":\"pkhfh\",\"dependencyConditions\":[\"Completed\",\"Failed\"],\"\":{\"yjffpuuyky\":\"datayfkini\",\"yml\":\"databpn\",\"fijvaxuv\":\"datatnnsjc\"}}],\"userProperties\":[{\"name\":\"ptldaaxglxhbn\",\"value\":\"datay\"},{\"name\":\"winle\",\"value\":\"datahtykebtvn\"},{\"name\":\"dcclpbhntoiviue\",\"value\":\"datariehooxqkc\"},{\"name\":\"yydtnl\",\"value\":\"datakyiqjtx\"}],\"\":{\"zsr\":\"datarftidkjotvhivvo\"}}")
            .toObject(AzureDataExplorerCommandActivity.class);
        Assertions.assertEquals("u", model.name());
        Assertions.assertEquals("vtapcxsm", model.description());
        Assertions.assertEquals(ActivityState.INACTIVE, model.state());
        Assertions.assertEquals(ActivityOnInactiveMarkAs.SUCCEEDED, model.onInactiveMarkAs());
        Assertions.assertEquals("ylrvztaelpux", model.dependsOn().get(0).activity());
        Assertions.assertEquals(DependencyCondition.COMPLETED, model.dependsOn().get(0).dependencyConditions().get(0));
        Assertions.assertEquals("ptldaaxglxhbn", model.userProperties().get(0).name());
        Assertions.assertEquals("bkvzwqgmfhlnqy", model.linkedServiceName().referenceName());
        Assertions.assertEquals(354823036, model.policy().retryIntervalInSeconds());
        Assertions.assertEquals(true, model.policy().secureInput());
        Assertions.assertEquals(true, model.policy().secureOutput());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        AzureDataExplorerCommandActivity model = new AzureDataExplorerCommandActivity().withName("u")
            .withDescription("vtapcxsm").withState(ActivityState.INACTIVE)
            .withOnInactiveMarkAs(ActivityOnInactiveMarkAs.SUCCEEDED)
            .withDependsOn(Arrays.asList(
                new ActivityDependency().withActivity("ylrvztaelpux")
                    .withDependencyConditions(Arrays.asList(DependencyCondition.COMPLETED, DependencyCondition.FAILED,
                        DependencyCondition.FAILED, DependencyCondition.SUCCEEDED))
                    .withAdditionalProperties(mapOf()),
                new ActivityDependency().withActivity("mmgfwxthr")
                    .withDependencyConditions(Arrays.asList(DependencyCondition.SUCCEEDED))
                    .withAdditionalProperties(mapOf()),
                new ActivityDependency().withActivity("pkhfh")
                    .withDependencyConditions(Arrays.asList(DependencyCondition.COMPLETED, DependencyCondition.FAILED))
                    .withAdditionalProperties(mapOf())))
            .withUserProperties(Arrays.asList(new UserProperty().withName("ptldaaxglxhbn").withValue("datay"),
                new UserProperty().withName("winle").withValue("datahtykebtvn"),
                new UserProperty().withName("dcclpbhntoiviue").withValue("datariehooxqkc"),
                new UserProperty().withName("yydtnl").withValue("datakyiqjtx")))
            .withLinkedServiceName(new LinkedServiceReference().withReferenceName("bkvzwqgmfhlnqy")
                .withParameters(mapOf("bsaaxstnziv", "datayfncwiyfzu", "cxygjhclnyoktc", "dataccgtujiwzbzed")))
            .withPolicy(new ActivityPolicy().withTimeout("datathjgbrxmxqskem").withRetry("datajjf")
                .withRetryIntervalInSeconds(354823036).withSecureInput(true).withSecureOutput(true)
                .withAdditionalProperties(mapOf()))
            .withCommand("dataxxhbrysnszsehoe").withCommandTimeout("datawbykrn");
        model = BinaryData.fromObject(model).toObject(AzureDataExplorerCommandActivity.class);
        Assertions.assertEquals("u", model.name());
        Assertions.assertEquals("vtapcxsm", model.description());
        Assertions.assertEquals(ActivityState.INACTIVE, model.state());
        Assertions.assertEquals(ActivityOnInactiveMarkAs.SUCCEEDED, model.onInactiveMarkAs());
        Assertions.assertEquals("ylrvztaelpux", model.dependsOn().get(0).activity());
        Assertions.assertEquals(DependencyCondition.COMPLETED, model.dependsOn().get(0).dependencyConditions().get(0));
        Assertions.assertEquals("ptldaaxglxhbn", model.userProperties().get(0).name());
        Assertions.assertEquals("bkvzwqgmfhlnqy", model.linkedServiceName().referenceName());
        Assertions.assertEquals(354823036, model.policy().retryIntervalInSeconds());
        Assertions.assertEquals(true, model.policy().secureInput());
        Assertions.assertEquals(true, model.policy().secureOutput());
    }

    // Use "Map.of" if available
    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> mapOf(Object... inputs) {
        Map<String, T> map = new HashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            String key = (String) inputs[i];
            T value = (T) inputs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}
