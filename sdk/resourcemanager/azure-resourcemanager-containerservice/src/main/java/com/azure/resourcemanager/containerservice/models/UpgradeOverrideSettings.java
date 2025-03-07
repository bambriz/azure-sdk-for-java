// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.containerservice.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

/**
 * Settings for overrides when upgrading a cluster.
 */
@Fluent
public final class UpgradeOverrideSettings {
    /*
     * Whether to force upgrade the cluster. Note that this option instructs upgrade operation to bypass upgrade
     * protections such as checking for deprecated API usage. Enable this option only with caution.
     */
    @JsonProperty(value = "forceUpgrade")
    private Boolean forceUpgrade;

    /*
     * Until when the overrides are effective. Note that this only matches the start time of an upgrade, and the
     * effectiveness won't change once an upgrade starts even if the `until` expires as upgrade proceeds. This field is
     * not set by default. It must be set for the overrides to take effect.
     */
    @JsonProperty(value = "until")
    private OffsetDateTime until;

    /**
     * Creates an instance of UpgradeOverrideSettings class.
     */
    public UpgradeOverrideSettings() {
    }

    /**
     * Get the forceUpgrade property: Whether to force upgrade the cluster. Note that this option instructs upgrade
     * operation to bypass upgrade protections such as checking for deprecated API usage. Enable this option only with
     * caution.
     *
     * @return the forceUpgrade value.
     */
    public Boolean forceUpgrade() {
        return this.forceUpgrade;
    }

    /**
     * Set the forceUpgrade property: Whether to force upgrade the cluster. Note that this option instructs upgrade
     * operation to bypass upgrade protections such as checking for deprecated API usage. Enable this option only with
     * caution.
     *
     * @param forceUpgrade the forceUpgrade value to set.
     * @return the UpgradeOverrideSettings object itself.
     */
    public UpgradeOverrideSettings withForceUpgrade(Boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
        return this;
    }

    /**
     * Get the until property: Until when the overrides are effective. Note that this only matches the start time of an
     * upgrade, and the effectiveness won't change once an upgrade starts even if the `until` expires as upgrade
     * proceeds. This field is not set by default. It must be set for the overrides to take effect.
     *
     * @return the until value.
     */
    public OffsetDateTime until() {
        return this.until;
    }

    /**
     * Set the until property: Until when the overrides are effective. Note that this only matches the start time of an
     * upgrade, and the effectiveness won't change once an upgrade starts even if the `until` expires as upgrade
     * proceeds. This field is not set by default. It must be set for the overrides to take effect.
     *
     * @param until the until value to set.
     * @return the UpgradeOverrideSettings object itself.
     */
    public UpgradeOverrideSettings withUntil(OffsetDateTime until) {
        this.until = until;
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
