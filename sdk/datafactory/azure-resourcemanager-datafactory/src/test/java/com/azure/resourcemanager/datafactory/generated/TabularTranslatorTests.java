// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datafactory.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.datafactory.models.TabularTranslator;
import com.azure.resourcemanager.datafactory.models.TypeConversionSettings;

public final class TabularTranslatorTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        TabularTranslator model = BinaryData.fromString(
            "{\"type\":\"TabularTranslator\",\"columnMappings\":\"dataqrkeyh\",\"schemaMapping\":\"dataaezkbrvtaul\",\"collectionReference\":\"dataqvtpkodijcn\",\"mapComplexValuesToString\":\"datao\",\"mappings\":\"datavcyqjjxhijbfi\",\"typeConversion\":\"datahoxule\",\"typeConversionSettings\":{\"allowDataTruncation\":\"databirhgjmphyacd\",\"treatBooleanAsNumber\":\"datamp\",\"dateTimeFormat\":\"datagkxshhljtku\",\"dateTimeOffsetFormat\":\"dataytfuqzs\",\"timeSpanFormat\":\"datab\",\"culture\":\"datayf\"},\"\":{\"lscokafaqqipv\":\"datajeitkfhzvscndb\"}}")
            .toObject(TabularTranslator.class);
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        TabularTranslator model = new TabularTranslator().withColumnMappings("dataqrkeyh")
            .withSchemaMapping("dataaezkbrvtaul").withCollectionReference("dataqvtpkodijcn")
            .withMapComplexValuesToString("datao").withMappings("datavcyqjjxhijbfi").withTypeConversion("datahoxule")
            .withTypeConversionSettings(new TypeConversionSettings().withAllowDataTruncation("databirhgjmphyacd")
                .withTreatBooleanAsNumber("datamp").withDateTimeFormat("datagkxshhljtku")
                .withDateTimeOffsetFormat("dataytfuqzs").withTimeSpanFormat("datab").withCulture("datayf"));
        model = BinaryData.fromObject(model).toObject(TabularTranslator.class);
    }
}
