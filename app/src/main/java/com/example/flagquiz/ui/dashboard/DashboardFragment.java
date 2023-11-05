package com.example.flagquiz.ui.dashboard;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flagquiz.ScoreAdapter;
import com.example.flagquiz.User;
import com.example.flagquiz.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    String score,userName;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    ScoreAdapter adapter;
    ArrayList<User> userArrayList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();
        userArrayList=new ArrayList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        adapter=new ScoreAdapter(userArrayList,getContext());
        getEmailAndUserName();
        getAllScores();
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void getAllScores(){
        userArrayList.clear();
        if (user!=null){
            firestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Map<String, Object> data = documentSnapshot.getData();

                            score = (String) data.get("score");
                            userName=(String) data.get("userName");
                            String email=(String) data.get("email");
                            String kalanhak=(String) data.get("kalanHak");
                            String nf=(String) data.get("name_family");
                            binding.dashboardMyUserScoreText.setText(score);
                            binding.dashboardMyUserNameText.setText(userName);

                            User user1=new User(nf,userName,email,score,kalanhak);
                            userArrayList.add(user1);
                        }
                        adapter.notifyDataSetChanged();

                    }
                }
            });
        }
    }

    private void getEmailAndUserName() {
        if (user != null) {
            firestore.collection("Users").whereEqualTo("email", user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Map<String, Object> data = documentSnapshot.getData();

                            score = (String) data.get("score");
                            userName=(String) data.get("userName");
                            binding.dashboardMyUserScoreText.setText(score);
                            binding.dashboardMyUserNameText.setText(userName);
                        }
                    }
                }
            });
        }
    }
}