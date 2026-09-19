package com.wayfinder.civilization.reason;

import java.util.List;

public record ReasonEvaluation(
        ReasonRecord primary,
        List<ReasonRecord> supporting
) {
    public ReasonEvaluation {
        if (primary == null) throw new IllegalArgumentException("primary reason is required");
        supporting = List.copyOf(supporting);
    }
}
