package com.wayfinder.civilization.persistence;

import com.wayfinder.core.id.GeographicFeatureId;
import com.wayfinder.geography.model.Landmark;
import com.wayfinder.geography.model.LandmarkType;

import java.util.UUID;

public record PersistedLandmark(
        String id,
        PersistedPosition anchor,
        String type,
        double salience,
        double prominence,
        double isolation
) {
    public static PersistedLandmark fromDomain(Landmark landmark) {
        return new PersistedLandmark(
                landmark.id().value().toString(),
                PersistedPosition.fromDomain(landmark.anchor()),
                landmark.type().name(),
                landmark.salience(),
                landmark.prominence(),
                landmark.isolation()
        );
    }

    public Landmark toDomain() {
        return new Landmark(
                new GeographicFeatureId(UUID.fromString(id)),
                anchor.toDomain(),
                LandmarkType.valueOf(type),
                salience,
                prominence,
                isolation
        );
    }
}
