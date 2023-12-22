package com.example.flagquiz.ui.dashboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flagquiz.interfaces.OnNewScoreListener;
import com.example.flagquiz.adapters.ScoreAdapter;
import com.example.flagquiz.models.User;
import com.example.flagquiz.databinding.FragmentDashboardBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class DashboardFragment extends Fragment implements OnNewScoreListener {

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
        userArrayList.clear();

        getAllScores();
        binding.recyclerView.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void getAllScores() {
        userArrayList.clear();
        if (user != null) {
            firestore.collection("Users").orderBy("score", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        int rank = 1; // Initialize rank
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Map<String, Object> data = documentSnapshot.getData();

                            String score = (String) data.get("score");
                            String userName = (String) data.get("userName");
                            String email = (String) data.get("email");
                            String kalanhak = (String) data.get("kalanHak");
                            String nf = (String) data.get("name_family");

                            User user1 = new User(nf, userName, email, score, kalanhak);
                            user1.setRank(String.valueOf(rank++));
                            userArrayList.add(user1);

                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void scoreUpdate(int newScore) {
        Query query=firestore.collection("Users").whereEqualTo("email",auth.getCurrentUser().getEmail());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Belirli bir kritere uyan belgeyi güncelle
                    String userId = document.getId();
                    String newScore=String.valueOf(score);
                    firestore.collection("Users").document(userId).update("score", newScore);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}