package com.fieldnation.userprofile.model.generic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Error {
    public Error(String message, String type, String code, String traceID) {
        this.message = message;
        this.type = type;
        this.code = code;
        this.traceID = traceID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    public Error(String message, String type, String code, String traceID, String status, String error) {
        this.message = message;
        this.type = type;
        this.code = code;
        this.traceID = traceID;
        this.status = status;
        this.error = error;
    }

    @SerializedName("message")
    @Expose
    String message;
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("code")
    @Expose
    String code;
    @SerializedName("traceID")
    @Expose
    String traceID;
    @SerializedName("status")
    @Expose
    String status;
    @SerializedName("error")
    @Expose
    String error;




    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }




}
