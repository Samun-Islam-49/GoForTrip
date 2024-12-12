package com.samun.gofortrip.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.samun.gofortrip.R;
import com.samun.gofortrip.databinding.ActivityMainDrawerBinding;
import com.samun.gofortrip.fragments.AddPkgFragment;
import com.samun.gofortrip.fragments.AllPkgFragment;
import com.samun.gofortrip.fragments.BookListFragment;
import com.samun.gofortrip.fragments.UserBookListFragment;
import com.samun.gofortrip.helpers.ContactDiag;
import com.samun.gofortrip.helpers.PrefManager;
import com.samun.gofortrip.helpers.UserVerification;
import com.samun.gofortrip.models.UserInfo;
import com.squareup.picasso.Picasso;

import static com.samun.gofortrip.activities.SplashActivity.isNotUser;
import static com.samun.gofortrip.activities.SplashActivity.menAdPkg;
import static com.samun.gofortrip.activities.SplashActivity.menBkList;
import static com.samun.gofortrip.fragments.BookListFragment.REQUEST_CALL;
import static com.samun.gofortrip.fragments.UserBookListFragment.isApproved;

public class MainDrawerActivity extends AppCompatActivity {
    ActivityMainDrawerBinding b;
    UserInfo userInfo;
    ShapeableImageView profImg;
    TextView nameTV, emailTV, userTV;
    AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_main_drawer);
        setSupportActionBar(b.toolbar);
        init();
        checkDefault(savedInstanceState);
        setUserInfo();

        handleNavItemClick();


    }

    private void init() {
        initDrawer();
        initHeader();

        userInfo = PrefManager.getUserInfo(this);
    }

    private void initHeader() {
        View navHeader = b.navView.getHeaderView(0);
        profImg = navHeader.findViewById(R.id.profile_image);
        nameTV = navHeader.findViewById(R.id.name);
        emailTV = navHeader.findViewById(R.id.email);
        userTV = navHeader.findViewById(R.id.is_user);
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, b.drawer, b.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        b.drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setUserInfo() {
        nameTV.setText(userInfo.getName());
        emailTV.setText(userInfo.getEmail());
        Picasso.get()
                .load(userInfo.getImgUrl())
                .placeholder(R.drawable.ic_baseline_account_box)
                .fit()
                .centerCrop()
                .into(profImg);

        if (!UserVerification.isUser(userInfo.getEmail())) {
            userTV.setText(isNotUser());
        }
    }

    private void checkDefault(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AllPkgFragment()).commit();
            b.navView.setCheckedItem(R.id.nav_pkg);
        }
    }

    private void handleNavItemClick() {
        b.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_pkg:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllPkgFragment()).commit();
                        break;
                    case R.id.nav_pending:
                        isApproved = false;
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserBookListFragment()).commit();
                        break;

                    case R.id.nav_approved :
                        isApproved = true;
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserBookListFragment()).commit();
                        break;

                    case R.id.nav_contact :
                        ContactDiag diag = new ContactDiag(MainDrawerActivity.this);
                        diag.show();
                        break;
                }

                b.drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuOrder = 1;
        if (!UserVerification.isUser(userInfo.getEmail())) {
            menu.add(Menu.NONE, 1, menuOrder, menAdPkg());
            menuOrder++;

            menu.add(Menu.NONE, 2, menuOrder, menBkList());
            menuOrder++;
        }

        menu.add(Menu.NONE, 3, menuOrder, "Sign Out");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:     /** ADD PACKAGE */
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddPkgFragment()).addToBackStack(null).commit();
                break;

            case 2:     /** BOOK LIST */
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BookListFragment()).addToBackStack(null).commit();
                break;

            case 3:     /** LOG OUT */
                PrefManager.deleteLog(this);
                PrefManager.deleteUserInfo(this);
                startActivity(new Intent(this, SplashActivity.class));
                Toast.makeText(this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (b.drawer.isDrawerOpen(GravityCompat.START)) {
            b.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}