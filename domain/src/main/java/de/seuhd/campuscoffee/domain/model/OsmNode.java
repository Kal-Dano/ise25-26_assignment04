package de.seuhd.campuscoffee.domain.model;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents an OpenStreetMap node with relevant Point of Sale information.
 * This is the domain model for OSM data before it is converted to a POS object.
 *
 * @param nodeId The OpenStreetMap node ID.
 * @param name Optional display name from tags (e.g., "name").
 * @param amenity Optional amenity tag (e.g., "cafe").
 * @param shop Optional shop tag (e.g., "bakery").
 * @param street Optional street name from tags (addr:street).
 * @param houseNumber Optional house number from tags (addr:housenumber).
 * @param postalCode Optional postal code from tags (addr:postcode).
 * @param city Optional city name from tags (addr:city).
 */
@Builder
public record OsmNode(
        @NonNull Long nodeId,
        @Nullable String name,
        @Nullable String amenity,
        @Nullable String shop,
        @Nullable String street,
        @Nullable String houseNumber,
        @Nullable Integer postalCode,
        @Nullable String city
) {}
