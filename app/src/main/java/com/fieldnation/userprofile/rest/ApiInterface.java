package com.fieldnation.userprofile.rest;


import com.fieldnation.userprofile.model.UserListApiResponse;
import com.fieldnation.userprofile.model.generic.GenericServerResponse;
import com.fieldnation.userprofile.utils.APIEndPoints;
import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiInterface {

    //user request
    @GET(APIEndPoints.USER_LIST_EXTENSION)
    Call <UserListApiResponse> getUserList();


}
