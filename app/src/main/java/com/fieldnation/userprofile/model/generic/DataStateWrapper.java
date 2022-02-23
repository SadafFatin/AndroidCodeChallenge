package com.fieldnation.userprofile.model.generic;

public class DataStateWrapper {

    private Status status;
    private String message;
    private Object jsonObjectData;
    private String jsonStringData;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getJsonObjectData() {
        return jsonObjectData;
    }

    public void setJsonObjectData(Object jsonObjectData) {
        this.jsonObjectData = jsonObjectData;
    }

    public String getJsonStringData() {
        return jsonStringData;
    }

    public void setJsonStringData(String jsonStringData) {
        this.jsonStringData = jsonStringData;
    }

    private DataStateWrapper(Builder dataStateWrapper) {
        this.status = dataStateWrapper.status;
        this.jsonObjectData = dataStateWrapper.jsonObjectData;
        this.jsonStringData = dataStateWrapper.jsonStringData;
        this.message = dataStateWrapper.message;

        System.out.println("Called constructor");

    }


    public static class Builder {
        private Status status;
        private String message;
        private Object jsonObjectData;
        private String jsonStringData;

        public Builder(Status status, String message) {
            this.status = status;
            this.message = message;
        }

        public Builder() {
        }

        public Builder jsonObjectData(Object jsonObjectData) {
            this.jsonObjectData = jsonObjectData;
            return this;
        }

        public Builder jsonStringData(String jsonStringData) {
            this.jsonStringData = jsonStringData;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public DataStateWrapper build() {
            return new DataStateWrapper(this);
        }


    }


    public enum Status {
        SUCCESSFULL,
        NETWORKERROR,
        GENERICERROR,
        SERVERERROR
    }

}
