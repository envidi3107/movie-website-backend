package com.example.IdentityService.Common;

import lombok.Getter;

public enum ReactionType {
    LIKE("like"),
    DISLIKE("dislike");

    @Getter
    private final String type;

    ReactionType(String type) {
        this.type = type;
    }

    public static String fromString(String value) {
        for (ReactionType r : values()) {
            if (r.getType().equalsIgnoreCase(value)) {
                return r.getType();
            }
        }
        throw new IllegalArgumentException("Unknown reaction type: " + value);
    }
}


