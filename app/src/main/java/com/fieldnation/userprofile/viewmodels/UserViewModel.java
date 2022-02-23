package com.fieldnation.userprofile.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fieldnation.userprofile.model.generic.DataStateWrapper;
import com.fieldnation.userprofile.rest.repository.DataRepository;

public class UserViewModel extends AndroidViewModel{

    private DataRepository dataRepository;
    private MutableLiveData<DataStateWrapper> userList;


    public UserViewModel(@NonNull Application application) {
        super(application);
        this.dataRepository = new DataRepository(application);
    }

    public LiveData<DataStateWrapper> getUserList() {
        return dataRepository.getUsersList();
    }





}
