// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.recoveryservicessiterecovery.models;

import com.azure.core.annotation.Immutable;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Details of the appliance resource. */
@Immutable
public final class ApplianceResourceDetails {
    /*
     * A value indicating the total capacity of appliance resource.
     */
    @JsonProperty(value = "capacity", access = JsonProperty.Access.WRITE_ONLY)
    private Long capacity;

    /*
     * A value indicating the utilization percentage by gateway agent on appliance.
     */
    @JsonProperty(value = "processUtilization", access = JsonProperty.Access.WRITE_ONLY)
    private Double processUtilization;

    /*
     * A value indicating the total utilization percentage for all processes on the appliance.
     */
    @JsonProperty(value = "totalUtilization", access = JsonProperty.Access.WRITE_ONLY)
    private Double totalUtilization;

    /*
     * A value indicating the status of appliance resource.
     */
    @JsonProperty(value = "status", access = JsonProperty.Access.WRITE_ONLY)
    private String status;

    /** Creates an instance of ApplianceResourceDetails class. */
    public ApplianceResourceDetails() {
    }

    /**
     * Get the capacity property: A value indicating the total capacity of appliance resource.
     *
     * @return the capacity value.
     */
    public Long capacity() {
        return this.capacity;
    }

    /**
     * Get the processUtilization property: A value indicating the utilization percentage by gateway agent on appliance.
     *
     * @return the processUtilization value.
     */
    public Double processUtilization() {
        return this.processUtilization;
    }

    /**
     * Get the totalUtilization property: A value indicating the total utilization percentage for all processes on the
     * appliance.
     *
     * @return the totalUtilization value.
     */
    public Double totalUtilization() {
        return this.totalUtilization;
    }

    /**
     * Get the status property: A value indicating the status of appliance resource.
     *
     * @return the status value.
     */
    public String status() {
        return this.status;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }
}
