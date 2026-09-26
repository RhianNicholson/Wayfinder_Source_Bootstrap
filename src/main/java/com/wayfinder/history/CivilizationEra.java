package com.wayfinder.history;

/**
 * Civilization-owned historical era identity.
 *
 * Era identity is intentionally independent of Minecraft world time.
 */
public record CivilizationEra(int value) {
    public CivilizationEra {
        if (value < 1)
            throw new IllegalArgumentException("Civilization era must be >= 1");
    }

    public CivilizationEra next() {
        return new CivilizationEra(value + 1);
    }

    public String key() {
        return "wayfinder-era:" + value;
    }
}
