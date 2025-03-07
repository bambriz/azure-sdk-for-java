// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.hybridcontainerservice.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.hybridcontainerservice.models.ProvisionedClusterPoolUpgradeProfileProperties;

public final class ProvisionedClusterPoolUpgradeProfilePropertiesTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        ProvisionedClusterPoolUpgradeProfileProperties model
            = BinaryData.fromString("{\"kubernetesVersion\":\"bciqfouflm\",\"isPreview\":false}")
                .toObject(ProvisionedClusterPoolUpgradeProfileProperties.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        ProvisionedClusterPoolUpgradeProfileProperties model = new ProvisionedClusterPoolUpgradeProfileProperties();
        model = BinaryData.fromObject(model).toObject(ProvisionedClusterPoolUpgradeProfileProperties.class);
    }
}
