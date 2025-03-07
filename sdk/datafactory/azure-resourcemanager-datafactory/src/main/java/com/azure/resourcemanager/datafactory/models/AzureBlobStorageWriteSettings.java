// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.datafactory.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Azure blob write settings.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeName("AzureBlobStorageWriteSettings")
@Fluent
public final class AzureBlobStorageWriteSettings extends StoreWriteSettings {
    /*
     * Indicates the block size(MB) when writing data to blob. Type: integer (or Expression with resultType integer).
     */
    @JsonProperty(value = "blockSizeInMB")
    private Object blockSizeInMB;

    /**
     * Creates an instance of AzureBlobStorageWriteSettings class.
     */
    public AzureBlobStorageWriteSettings() {
    }

    /**
     * Get the blockSizeInMB property: Indicates the block size(MB) when writing data to blob. Type: integer (or
     * Expression with resultType integer).
     * 
     * @return the blockSizeInMB value.
     */
    public Object blockSizeInMB() {
        return this.blockSizeInMB;
    }

    /**
     * Set the blockSizeInMB property: Indicates the block size(MB) when writing data to blob. Type: integer (or
     * Expression with resultType integer).
     * 
     * @param blockSizeInMB the blockSizeInMB value to set.
     * @return the AzureBlobStorageWriteSettings object itself.
     */
    public AzureBlobStorageWriteSettings withBlockSizeInMB(Object blockSizeInMB) {
        this.blockSizeInMB = blockSizeInMB;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AzureBlobStorageWriteSettings withMaxConcurrentConnections(Object maxConcurrentConnections) {
        super.withMaxConcurrentConnections(maxConcurrentConnections);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AzureBlobStorageWriteSettings withDisableMetricsCollection(Object disableMetricsCollection) {
        super.withDisableMetricsCollection(disableMetricsCollection);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AzureBlobStorageWriteSettings withCopyBehavior(Object copyBehavior) {
        super.withCopyBehavior(copyBehavior);
        return this;
    }

    /**
     * Validates the instance.
     * 
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    @Override
    public void validate() {
        super.validate();
    }
}
