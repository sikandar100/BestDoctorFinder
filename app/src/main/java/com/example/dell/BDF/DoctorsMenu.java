package com.example.dell.BDF;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class DoctorsMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Best Doctor Finder");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Welcome welcome = new Welcome();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content, welcome).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doctors_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {

            Welcome welcome = new Welcome();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content, welcome).commit();

        } else if (id == R.id.Patients_Requests) {

            Patient_Requests patient_requests = new Patient_Requests();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,patient_requests).commit();

        } else if (id == R.id.Patients_List) {

            Patients_List patients_list = new Patients_List();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,patients_list).commit();

        } else if (id == R.id.View_Profile) {

            ViewDocProfile viewDocProfile = new ViewDocProfile();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,viewDocProfile).commit();

        }else if (id == R.id.Add_Profile) {

            Doc_Profile doc_profile = new Doc_Profile();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,doc_profile).commit();

        }else if (id == R.id.Delete_Profile) {

            DeleteProfile deleteProfile = new DeleteProfile();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,deleteProfile).commit();

        } else if (id == R.id.Add_Qualifications) {

            Add_Qualifications add_qualifications = new Add_Qualifications();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,add_qualifications).commit();


        } else if (id == R.id.Change_Password) {

            ChangePassword changePassword = new ChangePassword();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,changePassword).commit();

        } else if (id == R.id.About_Us) {
            AboutUs aboutUs = new AboutUs();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,aboutUs).commit();

        } else if (id == R.id.Contact_Us) {
            ContactUs contactUsFragment = new ContactUs();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,contactUsFragment).commit();

        } else if (id == R.id.LogOut) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
