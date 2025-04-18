package com.example.IdentityService.Common;

public enum SuccessCode {
    SUCCESS(2000, "success!"),
    SIGN_UP_SUCCESSFULLY(2001, "Your account has been created succesfully!"),
    LOG_IN_SUCCESSFULLY(2002, "Log in successfully!"),
    LOG_OUT_SUCCESSFULLY(2003, "Log out successfully!"),
    UPDATED_SUCCESSFULLY(2004, "Your account has been updated succesfully!"),
    DELETED_SUCCESSFULLY(2005, "Your account has been deleted succesfully!"),
    UPDATED_AVATAR_SUCCESSFULLY(2006, "Updated avatar successfully!"),
    UPDATED_PASSWORD_SUCCESSFULLY(2007, "Updated password successfully!"),
    UPLOAD_FILM_SUCCESSFULLY(2008, "System film uploaded successfully.");

    SuccessCode(int code, String message) {
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
