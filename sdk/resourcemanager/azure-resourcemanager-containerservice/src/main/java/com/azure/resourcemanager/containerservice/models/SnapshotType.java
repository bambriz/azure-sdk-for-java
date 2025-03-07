// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.containerservice.models;

import com.azure.core.util.ExpandableStringEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Collection;

/**
 * The type of a snapshot. The default is NodePool.
 */
public final class SnapshotType extends ExpandableStringEnum<SnapshotType> {
    /**
     * Static value NodePool for SnapshotType.
     */
    public static final SnapshotType NODE_POOL = fromString("NodePool");

    /**
     * Creates a new instance of SnapshotType value.
     *
     * @deprecated Use the {@link #fromString(String)} factory method.
     */
    @Deprecated
    public SnapshotType() {
    }

    /**
     * Creates or finds a SnapshotType from its string representation.
     *
     * @param name a name to look for.
     * @return the corresponding SnapshotType.
     */
    @JsonCreator
    public static SnapshotType fromString(String name) {
        return fromString(name, SnapshotType.class);
    }

    /**
     * Gets known SnapshotType values.
     *
     * @return known SnapshotType values.
     */
    public static Collection<SnapshotType> values() {
        return values(SnapshotType.class);
    }
}
