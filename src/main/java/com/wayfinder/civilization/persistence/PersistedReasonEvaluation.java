package com.wayfinder.civilization.persistence;

import com.wayfinder.civilization.reason.ReasonEvaluation;

import java.util.List;

public record PersistedReasonEvaluation(
        PersistedReason primary,
        List<PersistedReason> supporting
) {
    public PersistedReasonEvaluation {
        supporting = List.copyOf(supporting);
    }

    public static PersistedReasonEvaluation fromDomain(ReasonEvaluation reasons) {
        return new PersistedReasonEvaluation(
                PersistedReason.fromDomain(reasons.primary()),
                reasons.supporting().stream()
                        .map(PersistedReason::fromDomain)
                        .toList()
        );
    }

    public ReasonEvaluation toDomain() {
        return new ReasonEvaluation(
                primary.toDomain(),
                supporting.stream()
                        .map(PersistedReason::toDomain)
                        .toList()
        );
    }
}
