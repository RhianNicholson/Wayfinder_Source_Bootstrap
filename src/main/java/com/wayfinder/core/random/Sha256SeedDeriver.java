package com.wayfinder.core.random;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Sha256SeedDeriver implements SeedDeriver {
    @Override
    public long derive(RandomStreamKey key) {
        String input = key.worldSeed() + "|" + key.regionKey() + "|" + key.stage()
            + "|" + key.scope() + "|" + key.generationVersion();
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(input.getBytes(StandardCharsets.UTF_8));
            return ByteBuffer.wrap(digest, 0, Long.BYTES).getLong();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 must be available on the JVM", exception);
        }
    }
}
