package com.wayfinder.civilization.relationship;

public enum RelationshipValidationCode {
    SOURCE_NODE_MISSING,
    TARGET_NODE_MISSING,
    SELF_REFERENCE,
    INCOMPATIBLE_NODE_PURPOSES,
    DUPLICATE_RELATIONSHIP,
    INSUFFICIENT_REASON,
    SCORE_BELOW_THRESHOLD
}
