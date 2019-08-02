package com.grd.cookit.repositories;

import com.google.android.gms.tasks.Tasks;
import com.grd.cookit.model.AppLocalDb;
import com.grd.cookit.model.entities.User;
import com.grd.cookit.model.firebase.UserFirebase;

import java.util.concurrent.Executors;

public class UserRepository {
    public static final UserRepository instance = new UserRepository();

    public void saveUser(User user) {
        Tasks.call(Executors.newSingleThreadExecutor(), () -> {
            UserFirebase.getInstance().getUserByUid(user.googleUid, userRes -> {
                if (userRes == null) {
                    UserFirebase.getInstance().saveUser(user);
                    AppLocalDb.db.usersDao().insertUser(user);
                }
            });

            return true;
        });
    }
}
