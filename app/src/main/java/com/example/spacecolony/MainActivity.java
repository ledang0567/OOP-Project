package com.example.spacecolony;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.spacecolony.data.DataManager;
import com.example.spacecolony.ui.fragments.MedbayFragment;
import com.example.spacecolony.ui.fragments.MissionFragment;
import com.example.spacecolony.ui.fragments.QuartersFragment;
import com.example.spacecolony.ui.fragments.SimulatorFragment;
import com.example.spacecolony.ui.fragments.StatsFragment;
import com.example.spacecolony.ui.fragments.HomeFragment;


public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DataManager.load(this);

        fragmentManager = getSupportFragmentManager();

        // Show home screen by default
        if (savedInstanceState == null) {
            showFragment(new HomeFragment());
        }

        setupBottomNav();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if      (id == R.id.nav_home)       showFragment(new HomeFragment());
            else if (id == R.id.nav_quarters)   showFragment(new QuartersFragment());
            else if (id == R.id.nav_simulator)  showFragment(new SimulatorFragment());
            else if (id == R.id.nav_mission)    showFragment(new MissionFragment());
            else if (id == R.id.nav_stats)      showFragment(new StatsFragment());
            return true;
        });
    }
    public void showFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    public void showMedbay() {
        showFragment(new MedbayFragment());
    }
}