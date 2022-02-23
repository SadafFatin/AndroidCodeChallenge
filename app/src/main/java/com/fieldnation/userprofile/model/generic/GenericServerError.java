package com.fieldnation.userprofile.model.generic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenericServerError {


    public GenericServerError(Error error) {
        this.error = error;
    }

    public Error getError() {
        return this.error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @SerializedName("error")
    @Expose
    private Error error;


}

