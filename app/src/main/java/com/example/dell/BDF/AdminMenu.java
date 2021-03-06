package com.example.dell.BDF;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class AdminMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static int REQUEST_CODE_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
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
        getMenuInflater().inflate(R.menu.admin_menu, menu);
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

        }  else if (id == R.id.Add_Hospital) {

            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != MockPackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);

                    // If any permission above not allowed by user, this condition will execute every time, else your else part will work
                } else {
                    AddHospital addHospital = new AddHospital();
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.content,addHospital).commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (id == R.id.Delete_Hospital) {

            DeleteHospital deleteHospital = new DeleteHospital();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,deleteHospital).commit();

        } else if (id == R.id.Add_Dept) {

            Add_Department add_department = new Add_Department();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,add_department).commit();

        } else if (id == R.id.Delete_Dept) {

            Delete_Department delete_department = new Delete_Department();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,delete_department).commit();

        } else if (id == R.id.Add_Doc) {

            AddDoctor addDoctor = new AddDoctor();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,addDoctor).commit();

        } else if (id == R.id.Delete_Doc) {

            DeleteDoctor deleteDoctor = new DeleteDoctor();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,deleteDoctor).commit();

        } else if (id == R.id.Delete_Block_User) {

            DeleteUser deleteUser = new DeleteUser();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,deleteUser).commit();

        } else if (id == R.id.Change_Password) {

            ChangePassword changePassword = new ChangePassword();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content,changePassword).commit();

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Req Code", "" + requestCode);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == MockPackageManager.PERMISSION_GRANTED ) {
                AddHospital addHospital = new AddHospital();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.content,addHospital).commit();

            }
            else{
                // Failure Stuff
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
