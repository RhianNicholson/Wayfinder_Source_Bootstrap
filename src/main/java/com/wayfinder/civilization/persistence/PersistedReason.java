package com.wayfinder.civilization.persistence;

import com.wayfinder.civilization.reason.ReasonRecord;
import com.wayfinder.civilization.reason.ReasonType;

public record PersistedReason(
        String type,
        double contribution,
        String explanation
) {
    public static PersistedReason fromDomain(ReasonRecord reason) {
        return new PersistedReason(
                reason.type().name(),
                reason.contribution(),
                reason.developerExplanation()
        );
    }

    public ReasonRecord toDomain() {
        return new ReasonRecord(
                ReasonType.valueOf(type),
                contribution,
                explanation
        );
    }
}
