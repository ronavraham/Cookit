package com.grd.cookit.model;

import androidx.room.Room;

import com.grd.cookit.Cookit;

public class AppLocalDb {
    static public AppLocalDbRepository db =
            Room.databaseBuilder(Cookit.context,
                    AppLocalDbRepository.class, "cookitDb.db")
                    .fallbackToDestructiveMigration().build();
}
