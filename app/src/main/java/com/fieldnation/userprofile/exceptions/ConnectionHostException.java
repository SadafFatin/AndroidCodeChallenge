package com.fieldnation.userprofile.exceptions;

import java.net.ConnectException;

public class ConnectionHostException extends ConnectException {


    @Override
    public String getMessage() {
        return "Whoops! Couldn't connect to host.Please try again.";
    }

}
