// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.appplatform.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/** OpenAPI properties of Spring Cloud Gateway route config. */
@Fluent
public final class GatewayRouteConfigOpenApiProperties {
    /*
     * The URI of OpenAPI specification.
     */
    @JsonProperty(value = "uri")
    private String uri;

    /**
     * Get the uri property: The URI of OpenAPI specification.
     *
     * @return the uri value.
     */
    public String uri() {
        return this.uri;
    }

    /**
     * Set the uri property: The URI of OpenAPI specification.
     *
     * @param uri the uri value to set.
     * @return the GatewayRouteConfigOpenApiProperties object itself.
     */
    public GatewayRouteConfigOpenApiProperties withUri(String uri) {
        this.uri = uri;
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
