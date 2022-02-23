package com.fieldnation.userprofile.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fieldnation.userprofile.adapters.UserListAdapter;
import com.fieldnation.userprofile.databinding.FragmentUserListBinding;
import com.fieldnation.userprofile.model.User;
import com.fieldnation.userprofile.model.UserListApiResponse;
import com.fieldnation.userprofile.model.generic.DataStateWrapper;
import com.fieldnation.userprofile.utils.UiUtils;
import com.fieldnation.userprofile.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {

    private FragmentUserListBinding binding;
    private UserViewModel userViewModel;
    private UserListAdapter userListAdapter;
    private List<User> userList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentUserListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(this.requireActivity().getApplication()).create(UserViewModel.class);
        loadUserList();
        binding.userListSearchView.setOnQueryTextListener(this);


        userList = new ArrayList<>();
        userListAdapter = new UserListAdapter(userList, requireContext().getApplicationContext());
        binding.userList.setAdapter(userListAdapter);
    }


    private void loadUserList() {
        this.userViewModel.getUserList().observe(getViewLifecycleOwner(), dataStateWrapper -> {

            if (dataStateWrapper.getStatus().equals(DataStateWrapper.Status.SUCCESSFULL)) {
                UserListApiResponse userListApiResponse = (UserListApiResponse) dataStateWrapper.getJsonObjectData();
                userListAdapter.addContents(userListApiResponse.getData());
                makeListVisible();
            } else {
                UiUtils.showToast(requireContext(), dataStateWrapper.getMessage());
                binding.shimmerViewInvestmentList.setVisibility(View.GONE);
            }
        });
    }

    private void makeListVisible() {
        binding.shimmerViewInvestmentList.setVisibility(View.GONE);
        binding.userList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        userListAdapter.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        userListAdapter.getFilter().filter(newText);
        return true;
    }





}