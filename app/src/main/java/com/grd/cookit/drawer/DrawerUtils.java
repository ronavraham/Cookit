package com.grd.cookit.drawer;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.grd.cookit.R;
import com.grd.cookit.activities.AuthActivity;
import com.grd.cookit.activities.FeedActivity;
//import com.grd.cookit.activities.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.IOException;

public class DrawerUtils {

    public static void getDrawer(final Activity activity, Toolbar toolbar) throws IOException {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String name = "not logged in";
        String email = "";
        Drawable image = null;

        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withHeaderBackground(R.drawable.drawer_header_background)
                .withActivity(activity)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(name)
                                .withEmail(email)
                                .withIcon(FontAwesome.Icon.faw_user)
                ).build();

        PrimaryDrawerItem feedItem = new PrimaryDrawerItem().withIdentifier(0).withName("feed")
                .withIcon(R.drawable.ic_menu_gallery);
        PrimaryDrawerItem profile = new PrimaryDrawerItem().withIdentifier(1).withName("profile")
                .withIcon(R.drawable.ic_menu_send);

        SecondaryDrawerItem logoutItem = new SecondaryDrawerItem().withIdentifier(2).withName("logout")
                .withIcon(FontAwesome.Icon.faw_user_slash);

        Drawer.OnDrawerItemClickListener clickListener = new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                Intent intent = new Intent(activity, FeedActivity.class);

                switch (((int) drawerItem.getIdentifier())) {
                    case 0:
                        intent = new Intent(activity, FeedActivity.class);
                        break;
                   // case 1:
                       // intent = new Intent(activity, ProfileActivity.class);
                      //  break;
                    case 2:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(activity, AuthActivity.class);
                        break;
                    default:
                        ;
                }

                activity.startActivity(intent);
                return true;
            }
        };

        Drawer result = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(activity)
                .withToolbar(toolbar)
                .addDrawerItems(
                        feedItem,
                        profile,
                        new DividerDrawerItem(),
                        logoutItem)
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener(clickListener)
                .build();
    }
}
