package com.example.flagquiz;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.flagquiz.databinding.ActivityGameBinding;
import com.example.flagquiz.ui.about.AboutFragment;
import com.example.flagquiz.ui.dashboard.DashboardFragment;
import com.example.flagquiz.ui.home.HomeFragment;
import com.example.flagquiz.ui.notifications.NotificationsFragment;
import com.example.flagquiz.ui.share.ShareFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.flagquiz.databinding.ActivityMain2Binding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMain2Binding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);


        allFindViewById();

    }

    private void bottomNavigationItemSelected() {
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId()==R.id.navigation_home){
                replaceFragment(new HomeFragment());
            }
            else if (item.getItemId()==R.id.navigation_dashboard) {
                replaceFragment(new DashboardFragment());

            }
            return true;
        });
    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    private void allFindViewById() {
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        navigationView=findViewById(R.id.nav_view);
    }

}