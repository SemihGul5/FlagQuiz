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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flagquiz.databinding.ActivityGameBinding;
import com.example.flagquiz.ui.about.AboutFragment;
import com.example.flagquiz.ui.dashboard.DashboardFragment;
import com.example.flagquiz.ui.home.HomeFragment;
import com.example.flagquiz.ui.notifications.NotificationsFragment;
import com.example.flagquiz.ui.share.ShareFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMain2Binding binding;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    TextView userNameTextView;
    TextView userEmailTextView;
    String userName="";
    String email,nf,score,kalanHak;
    ArrayList<User> userArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMain2Binding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();


        userArrayList=new ArrayList<>();
        getEmailAndUserName();



        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        userNameTextView=header.findViewById(R.id.nav_header_userNameText);
        userEmailTextView=header.findViewById(R.id.nav_header_userEmailText);



        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackground(null);



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.nav_home){
                    openFragment(new HomeFragment());
                    return true;
                } else if (item.getItemId()==R.id.nav_dashboard) {
                    openFragment(new DashboardFragment());
                    return  true;
                }
                return false;
            }
        });


        fragmentManager =getSupportFragmentManager();
        openFragment(new HomeFragment());


    }

    private void getEmailAndUserName() {
        if (user != null) {
            email = user.getEmail();
            firestore.collection("Users").whereEqualTo("email", email).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(MainActivity2.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Map<String, Object> data = documentSnapshot.getData();
                            userName = (String) data.get("userName");
                            nf = (String) data.get("name_family");
                            kalanHak = (String) data.get("kalanHak");
                            score = (String) data.get("score");

                            User newUser = new User(nf, userName, email, score, kalanHak, documentSnapshot.getId());
                                userNameTextView.setText(newUser.getUserName());
                                userEmailTextView.setText(newUser.geteMail());

                        }
                    }
                }
            });
        }
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();
        if (itemId==R.id.nav_about){
            openFragment(new AboutFragment());
        } else if (itemId==R.id.nav_contackUs) {
            openFragment(new NotificationsFragment());
        } else if (itemId==R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Uygulamanın google play linki");

            // Intent'i başlatın
            startActivity(Intent.createChooser(sharingIntent, "Paylaş:"));
        } else if (itemId==R.id.nav_logout) {
            binding.progressBarM2.setVisibility(View.VISIBLE);
            auth.signOut();
            Intent intent= new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
            finish();
            binding.progressBarM2.setVisibility(View.GONE);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();

        }

    }
    private void openFragment(Fragment fragment){
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }



}