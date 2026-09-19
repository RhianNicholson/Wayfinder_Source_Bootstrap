package com.wayfinder.civilization.relationship;

public record RelationshipTransition(
        RelationshipProposal proposal,
        CivilizationRelationship relationship
) {}
