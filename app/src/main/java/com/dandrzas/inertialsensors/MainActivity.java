package com.dandrzas.inertialsensors;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dandrzas.inertialsensors.data.CSVDataSaver;
import com.dandrzas.inertialsensors.data.DataManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private DataManager dataManager;
    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionButton buttonStart;
    private final int PERMISSSION_MEMORY_WRITE_CODE = 1;
    private CSVDataSaver csvDataSaver = CSVDataSaver.getInstance();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            csvDataSaver.init(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataManager.getInstance().init(this);
        dataManager = DataManager.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_sensorsdata, R.id.nav_orientation, R.id.nav_compass,
                R.id.nav_bubblelevel, R.id.nav_pedometer, R.id.nav_movement)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        buttonStart = findViewById(R.id.floatingActionButton_start);

        // Processing state display after Activity recreate
        if (dataManager.isComputingRunning()) {
            buttonStart.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_white_24dp));
        } else {
            buttonStart.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_white_24dp));
        }

        // FOA
        buttonStart.setOnClickListener(view ->
        {
            boolean isEnable = dataManager.isComputingRunning();

            if (!isEnable) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        csvDataSaver.createFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dataManager.startComputing();
                    buttonStart.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_white_24dp));
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSSION_MEMORY_WRITE_CODE);
                    buttonStart.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_white_24dp));
                }

            } else {
                dataManager.stopComputing();
                try {
                    csvDataSaver.closeFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buttonStart.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_white_24dp));
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSSION_MEMORY_WRITE_CODE){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    try {
                        csvDataSaver.init(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    csvDataSaver.createFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            dataManager.startComputing();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(intent, 666);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}