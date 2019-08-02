package com.grd.cookit.viewModels;

import androidx.lifecycle.ViewModel;

import com.grd.cookit.model.entities.User;
import com.grd.cookit.repositories.UserRepository;

public class UserViewModel extends ViewModel {
    public void saveUser(User user) {
        UserRepository.instance.saveUser(user);
    }
}
