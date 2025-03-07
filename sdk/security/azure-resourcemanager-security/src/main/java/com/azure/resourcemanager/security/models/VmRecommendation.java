// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.security.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents a machine that is part of a machine group. */
@Fluent
public final class VmRecommendation {
    /*
     * The configuration status of the machines group or machine or rule
     */
    @JsonProperty(value = "configurationStatus")
    private ConfigurationStatus configurationStatus;

    /*
     * The recommendation action of the machine or rule
     */
    @JsonProperty(value = "recommendationAction")
    private RecommendationAction recommendationAction;

    /*
     * The full resource id of the machine
     */
    @JsonProperty(value = "resourceId")
    private String resourceId;

    /*
     * The machine supportability of Enforce feature
     */
    @JsonProperty(value = "enforcementSupport")
    private EnforcementSupport enforcementSupport;

    /** Creates an instance of VmRecommendation class. */
    public VmRecommendation() {
    }

    /**
     * Get the configurationStatus property: The configuration status of the machines group or machine or rule.
     *
     * @return the configurationStatus value.
     */
    public ConfigurationStatus configurationStatus() {
        return this.configurationStatus;
    }

    /**
     * Set the configurationStatus property: The configuration status of the machines group or machine or rule.
     *
     * @param configurationStatus the configurationStatus value to set.
     * @return the VmRecommendation object itself.
     */
    public VmRecommendation withConfigurationStatus(ConfigurationStatus configurationStatus) {
        this.configurationStatus = configurationStatus;
        return this;
    }

    /**
     * Get the recommendationAction property: The recommendation action of the machine or rule.
     *
     * @return the recommendationAction value.
     */
    public RecommendationAction recommendationAction() {
        return this.recommendationAction;
    }

    /**
     * Set the recommendationAction property: The recommendation action of the machine or rule.
     *
     * @param recommendationAction the recommendationAction value to set.
     * @return the VmRecommendation object itself.
     */
    public VmRecommendation withRecommendationAction(RecommendationAction recommendationAction) {
        this.recommendationAction = recommendationAction;
        return this;
    }

    /**
     * Get the resourceId property: The full resource id of the machine.
     *
     * @return the resourceId value.
     */
    public String resourceId() {
        return this.resourceId;
    }

    /**
     * Set the resourceId property: The full resource id of the machine.
     *
     * @param resourceId the resourceId value to set.
     * @return the VmRecommendation object itself.
     */
    public VmRecommendation withResourceId(String resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    /**
     * Get the enforcementSupport property: The machine supportability of Enforce feature.
     *
     * @return the enforcementSupport value.
     */
    public EnforcementSupport enforcementSupport() {
        return this.enforcementSupport;
    }

    /**
     * Set the enforcementSupport property: The machine supportability of Enforce feature.
     *
     * @param enforcementSupport the enforcementSupport value to set.
     * @return the VmRecommendation object itself.
     */
    public VmRecommendation withEnforcementSupport(EnforcementSupport enforcementSupport) {
        this.enforcementSupport = enforcementSupport;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
    }
}
