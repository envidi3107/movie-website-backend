package com.example.MovieWebsiteProject.Common;

public enum SuccessCode {
    SUCCESS(2000, "success!"),
    SIGN_UP_SUCCESSFULLY(2001, "Your account has been created succesfully!"),
    LOG_IN_SUCCESSFULLY(2002, "Log in successfully!"),
    LOG_OUT_SUCCESSFULLY(2003, "Log out successfully!"),
    UPDATED_SUCCESSFULLY(2004, "Your account has been updated succesfully!"),
    DELETED_SUCCESSFULLY(2005, "Your account has been deleted succesfully!"),
    UPDATED_AVATAR_SUCCESSFULLY(2006, "Updated avatar successfully!"),
    UPDATED_PASSWORD_SUCCESSFULLY(2007, "Updated password successfully!"),
    UPLOAD_FILM_SUCCESSFULLY(2008, "System film uploaded successfully."),
    TOKEN_IS_VALID(2009, "Token is valid!"),
    UPDATE_FILM_SUCCESSFULLY(2010, "Film has been updated successfully!"),
    CREATE_PLAYLIST_SUCCESSFULLY(2011, "Playlist has been created successfully!"),
    DELETE_PLAYLIST_SUCCESSFULLY(2012, "Playlist has been deleted successfully!"),
    DELETE_FILM_SUCCESSFULLY(2013, "Film has been deleted successfully!"),
    UPDATE_PLAYLIST_SUCCESSFULLY(2014, "Playlist has been updated successfully!"),
    DELETE_PLAYLIST_FILM_SUCCESSFULLY(2015, "Playlist film has been deleted successfully!");


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
