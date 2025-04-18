package com.example.IdentityService.Exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9000, "Uncategorized exception!"),
    USERNAME_EXISTED(1000, "Username already exists!"),
    EMAIL_EXISTED(1001, "Email already exists!"),
    EMAIL_NOT_EXISTES(1002, "You haven't signed up for an account yet. Please sign up now!"),
    PASSWORD_MUST_BE_DIFFERENCE(1003, "You must set a password difference from the old one!"),
    INCORRECT_PASSWORD(1004, "Password is incorrect!"),
    USER_NOT_EXISTES(1005, "User not existed!"),
    AVATAR_NOT_EXISTS(1006, "Avatar not exists"),
    EXPIRED_LOGIN_SESSION(1007, "Expired login session! Please login to continue!"),
    FILM_NOT_FOUND(1008, "This film is not found!"),
    INVALID_FILE(1009, "Failed to upload media files.");

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
