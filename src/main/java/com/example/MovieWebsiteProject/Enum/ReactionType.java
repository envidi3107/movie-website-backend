package com.example.MovieWebsiteProject.Enum;

import com.example.MovieWebsiteProject.Exception.AppException;

public enum ReactionType {
    LIKE,
    DISLIKE;

    public static void checkInvalidReaction(String reactionType) {
        boolean isValid = false;
        for (ReactionType type : ReactionType.values()) {
            if (type.name().equals(reactionType)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) throw new AppException(ErrorCode.INVALID_REACTION_TYPE);
    }
}


