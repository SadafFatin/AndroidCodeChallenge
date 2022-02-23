package com.fieldnation.userprofile.exceptions;


import androidx.annotation.Nullable;

public class GenericException extends Exception {
    public GenericException(String msg) {
        super(msg);
    }

    @Nullable
    @Override
    public String getMessage() {
        return "Whoops! Something went wrong.Please try again";
    }
}