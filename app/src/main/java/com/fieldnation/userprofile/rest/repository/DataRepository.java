package com.fieldnation.userprofile.rest.repository;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fieldnation.userprofile.R;
import com.fieldnation.userprofile.exceptions.GenericException;
import com.fieldnation.userprofile.exceptions.JsonParsingException;
import com.fieldnation.userprofile.model.UserListApiResponse;
import com.fieldnation.userprofile.model.generic.DataStateWrapper;
import com.fieldnation.userprofile.model.generic.GenericServerError;
import com.fieldnation.userprofile.model.generic.GenericServerResponse;
import com.fieldnation.userprofile.rest.ApiInterface;
import com.fieldnation.userprofile.rest.GenericApiClient;
import com.fieldnation.userprofile.utils.Utils;
import com.google.gson.JsonSyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class DataRepository {

    private static final String TAG = "DataRepository";
    private final ApiInterface genericApiInterface;
    private final Context context;
    private DataStateWrapper.Builder builder;
    private MutableLiveData<DataStateWrapper> userListLiveData;

    public DataRepository(Context application) {
        this.context = application;
        this.genericApiInterface = GenericApiClient.getRetrofitClient(application).create(ApiInterface.class);
        this.builder = new DataStateWrapper.Builder();
    }

    /**
     * Users data method
     **/
    public LiveData<DataStateWrapper> getUsersList() {
        this.userListLiveData = new MutableLiveData<>();
        genericApiInterface.getUserList().enqueue(new Callback<>() {
            /**
             * Invoked for a received HTTP response.
             *
             * <p>Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call {Response.isSuccessful()} to determine if the response indicates success.
             *
             * @param call
             * @param response
             */

            @Override
            public void onResponse(@NonNull Call<UserListApiResponse>call,
                                   @NonNull Response<UserListApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    builder.jsonObjectData(response.body());
                    builder.status(DataStateWrapper.Status.SUCCESSFULL);
                    userListLiveData.setValue(builder.build());

                } else {
                    builder = prepareServerErrorResponse(response);
                    userListLiveData.setValue(builder.build());
                }
                if (!call.isCanceled()) {
                    call.cancel();
                }
            }

            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected exception
             * occurred creating the request or processing the response.
             *
             * @param call
             * @param t
             */
            @Override
            public void onFailure(@NonNull Call<UserListApiResponse> call, @NonNull Throwable t) {
                builder = prepareNetworkErrorResponse(t);
                userListLiveData.setValue(builder.build());
                if (!call.isCanceled()) {
                    call.cancel();
                }
            }
        });
        return userListLiveData;
    }


    /**
     * Factory Methods for error handling
     */
    private DataStateWrapper.Builder prepareServerErrorResponse(Response response) {
        String errorMsg;
        try {
            if (response.errorBody() != null) {
                String errorResponseBody = response.errorBody().string();
                try {
                    errorMsg = Utils.fromJson(errorResponseBody, GenericServerError.class).getError().getMessage();
                    return new DataStateWrapper.Builder(DataStateWrapper.Status.SERVERERROR, errorMsg);
                } catch (Exception exception) {
                    return prepareNetworkErrorResponse(exception);
                }
            } else {
                return prepareNetworkErrorResponse(new GenericException(context.getApplicationContext().getString(R.string.label_action_failed)));
            }
        } catch (Exception exception) {
            return prepareNetworkErrorResponse(exception);
        }
    }


    public DataStateWrapper.Builder prepareNetworkErrorResponse(Throwable error) {
        String errorMsg;
        Log.d(TAG, Utils.toJsonString(error));
        if (error instanceof JsonParsingException || error instanceof JsonSyntaxException) {
            errorMsg = context.getApplicationContext().getString(R.string.label_response_process_error);
        } else {
            errorMsg = error.getLocalizedMessage();
        }
        return new DataStateWrapper.Builder(DataStateWrapper.Status.NETWORKERROR, errorMsg);
    }


}

