package com.fieldnation.userprofile.model.generic;

import com.google.gson.annotations.SerializedName;

public class GenericServerResponse<T> {


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public GenericServerError getError() {
        return error;
    }

    public void setError(GenericServerError error) {
        this.error = error;
    }

    @SerializedName("error")
    private GenericServerError error;
    @SerializedName("success")
    private boolean success;
    @SerializedName(value="data", alternate="")
    private T data;
    public GenericServerResponse() {
    }

    public GenericServerResponse(GenericServerError error, boolean success, T data) {
        this.error = error;
        this.success = success;
        this.data = data;
    }
}
