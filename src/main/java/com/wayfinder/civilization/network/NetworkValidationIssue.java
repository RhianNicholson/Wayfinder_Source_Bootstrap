package com.wayfinder.civilization.network;

public record NetworkValidationIssue(
        NetworkValidationCode code,
        String explanation
) {}
