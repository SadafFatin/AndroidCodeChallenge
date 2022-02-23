package com.fieldnation.userprofile.exceptions;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "Whoops! Its seems you don't have internet connection, please try again later!";
    }


}