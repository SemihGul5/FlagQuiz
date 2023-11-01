package com.example.flagquiz.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.flagquiz.GameActivity;
import com.example.flagquiz.MainActivity2;
import com.example.flagquiz.User;
import com.example.flagquiz.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    String score;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goToGame(view);

        getEmailAndUserName();


    }

    private void goToGame(View view) {
        binding.gameStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), GameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void getEmailAndUserName() {
        if (user != null) {
            firestore.collection("Users").whereEqualTo("email", user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Map<String, Object> data = documentSnapshot.getData();

                            if (binding != null && binding.textViewMyHighScore != null) {
                                score = (String) data.get("score");
                                binding.textViewMyHighScore.setText("En Yüksek Puanım: " + score);
                            }
                            else{
                                binding.textViewMyHighScore.setText("null??");

                            }


                        }
                    }
                }
            });
        }
    }
}