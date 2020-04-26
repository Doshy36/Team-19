package com.example.discovernorthumberland;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static String userId;
    private static String accessToken;
    private DrawerLayout drawer;
    private static boolean userLoggedIn = false;
    private static NavigationView navViewStatic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navViewStatic = navigationView;
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();




        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainMenuActivity()).commit();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //Sets action depending on which navigation item is selected
        switch (menuItem.getItemId()) {
            case R.id.nav_search:
                Intent intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_login:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.nav_bookmarks:
                Intent bookmarksIntent = new Intent(this, BookmarksActivity.class);
                startActivity(bookmarksIntent);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode == RESULT_OK){
                assert data != null;
                String logStatus = data.getStringExtra("Log Status");
                if(logStatus.equalsIgnoreCase("LoggedIn")){

                    NavigationView navigationView = findViewById(R.id.nav_view);
                    Menu menu = navigationView.getMenu();
                    MenuItem menuItem = menu.findItem(R.id.nav_login);
                    menuItem.setTitle("Log Out");

                }else if(logStatus.equalsIgnoreCase("LoggedOut")){

                    NavigationView navigationView = findViewById(R.id.nav_view);
                    Menu menu = navigationView.getMenu();
                    MenuItem menuItem = menu.findItem(R.id.nav_login);
                    menuItem.setTitle("Log In");

                }
            }
        }
    }

    public void onNavBarButtonClick(View view) {
        drawer.openDrawer(GravityCompat.START);
    }

    public static void logUserIn(final String accessToken, String userId) {
        MainActivity.userId = userId;
        MainActivity.accessToken = accessToken;
        userLoggedIn = true;
        Menu menu = navViewStatic.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_login);
        menuItem.setTitle("Log Out");


    }


    public static String getUserID() {
        return userId;
    }

    public static boolean getUserLoggedIn() {
        return userLoggedIn;
    }

    public static String getAccessToken(){
        return accessToken;
    }

    public static void logOut() {
        userLoggedIn = false;
        userId = null;
        Menu menu = navViewStatic.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_login);
        menuItem.setTitle("Log In");
    }

}

