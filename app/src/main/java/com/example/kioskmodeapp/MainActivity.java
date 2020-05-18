package com.example.kioskmodeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    final private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NextActivity.class));
            }
        });
        setUpKioskMode();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!kioskMode.isLocked(this)) {
            super.onBackPressed();
        }
    }

    private void setUpKioskMode() {
        if (!MySharedPreferences.isAppLaunched(this)) {
            Log.d(TAG, "onCreate() locking the app first time");
            kioskMode.lockUnlock(this, true);
            MySharedPreferences.saveAppLaunched(this, true);
        } else {
            //check if app was locked
            if (MySharedPreferences.isAppInKioskMode(this)) {
                Log.d(TAG, "onCreate() locking the app");
                kioskMode.lockUnlock(this, true);
            }
        }
    }

    /**
     * show settings dialog
     */
    private void showSettingsDialog() {
        SettingFragment settingFragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putBoolean(SettingFragment.LOCKED_BUNDLE_KEY, kioskMode.isLocked(this));
        settingFragment.setArguments(args);
        settingFragment.show(fragmentManager, settingFragment.getClass().getSimpleName());
        settingFragment.setActionHandler(new SettingFragment.IActionHandler() {
            @Override
            public void isLocked(boolean isLocked) {
                int msg = isLocked ? R.string.setting_device_locked : R.string.setting_device_unlocked;
                kioskMode.lockUnlock(MainActivity.this, isLocked);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
