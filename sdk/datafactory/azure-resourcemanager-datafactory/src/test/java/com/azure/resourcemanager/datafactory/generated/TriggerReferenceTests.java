// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datafactory.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.datafactory.models.TriggerReference;
import com.azure.resourcemanager.datafactory.models.TriggerReferenceType;
import org.junit.jupiter.api.Assertions;

public final class TriggerReferenceTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        TriggerReference model = BinaryData.fromString("{\"type\":\"TriggerReference\",\"referenceName\":\"dfiwj\"}")
            .toObject(TriggerReference.class);
        Assertions.assertEquals(TriggerReferenceType.TRIGGER_REFERENCE, model.type());
        Assertions.assertEquals("dfiwj", model.referenceName());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        TriggerReference model
            = new TriggerReference().withType(TriggerReferenceType.TRIGGER_REFERENCE).withReferenceName("dfiwj");
        model = BinaryData.fromObject(model).toObject(TriggerReference.class);
        Assertions.assertEquals(TriggerReferenceType.TRIGGER_REFERENCE, model.type());
        Assertions.assertEquals("dfiwj", model.referenceName());
    }
}
