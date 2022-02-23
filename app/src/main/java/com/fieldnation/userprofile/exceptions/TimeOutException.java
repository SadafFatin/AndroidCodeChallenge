package com.fieldnation.userprofile.exceptions;

import androidx.annotation.Nullable;

import java.net.SocketTimeoutException;

public class TimeOutException extends SocketTimeoutException {
    @Nullable
    @Override
    public String getMessage() {
        return "Whoops! Could not connect to the server within time.";
    }
}
