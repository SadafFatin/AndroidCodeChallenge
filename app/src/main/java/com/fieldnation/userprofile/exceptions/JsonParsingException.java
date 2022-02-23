package com.fieldnation.userprofile.exceptions;


import androidx.annotation.Nullable;

import com.google.gson.JsonParseException;

public class JsonParsingException extends JsonParseException {

   // JsonSyntaxException
    /**
     * Creates exception with the specified message. If you are wrapping another exception, consider
     * using JsonParseException instead.
     *
     * @param msg error message describing a possible cause of this exception.
     */
    public JsonParsingException(String msg) {
        super(msg);
    }

    @Nullable
    @Override
    public String getMessage() {
        return "Whoops! Error while parsing the response.";
    }

}
