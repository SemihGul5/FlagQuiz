package com.example.flagquiz.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flagquiz.MainActivity;
import com.example.flagquiz.R;
import com.example.flagquiz.SignInActivity;
import com.example.flagquiz.databinding.FragmentPasswordChangeBinding;
import com.example.flagquiz.databinding.FragmentUserEmailChangeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;


public class UserEmailChangeFragment extends Fragment {
    private FragmentUserEmailChangeBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;

    public UserEmailChangeFragment() {
        // Required empty public constructor
    }


    public static UserEmailChangeFragment newInstance(String param1, String param2) {
        UserEmailChangeFragment fragment = new UserEmailChangeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allEnabled(true);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserEmailChangeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return  root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.userEmailChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClicked(view);
            }
        });

    }

    private void buttonClicked(View view) {
        binding.progressBarSettingsPasswordChange.setVisibility(View.VISIBLE);

        String newEmail=binding.newEmailText.getText().toString();
        String newEmailRetry=binding.newEmailRetryText.getText().toString();

        if(!newEmail.isEmpty()||!newEmailRetry.isEmpty()){
            if (user != null) {
                user.updateEmail(newEmail)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                sendEmail(user,view);
                                updateFirebaseCollection(newEmail);
                                Toast.makeText(getContext(), "Email güncellendi: " + newEmail, Toast.LENGTH_SHORT).show();
                                binding.progressBarSettingsPasswordChange.setVisibility(View.GONE);
                                auth.signOut();
                                Intent intent= new Intent(getContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(),  "Email güncelleme hatası: " + task.getException(), Toast.LENGTH_SHORT).show();
                                binding.progressBarSettingsPasswordChange.setVisibility(View.GONE);
                            }
                        });
            } else {
                Toast.makeText(getContext(),  "Kullanıcı oturumu açmamış.", Toast.LENGTH_SHORT).show();
                binding.progressBarSettingsPasswordChange.setVisibility(View.GONE);
            }
        }else{
            Toast.makeText(getContext(), "Tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            binding.progressBarSettingsPasswordChange.setVisibility(View.GONE);
        }
    }

    private void updateFirebaseCollection(String email) {
        // Kullanıcı adına göre sorgu oluştur
        firestore.collection("Users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Belirli kullanıcının dokümanını bulduğumuzda güncelleme işlemini yap
                            String documentId = document.getId();
                            updateUserEmail(documentId, email);
                        }
                    } else {
                        Toast.makeText(getContext(),  "Kullanıcı sorgusu başarısız: ", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void updateUserEmail(String documentId, String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("Users").document(documentId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", email);

        documentReference.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Kullanıcı adı ve soyadı güncellendi.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Kullanıcı adı ve soyadı güncelleme hatası: ", e);
                });
    }

    private void allEnabled(Boolean status){
        binding.newEmailRetryText.setEnabled(status);
        binding.newEmailText.setEnabled(status);
        binding.userEmailChangeButton.setEnabled(status);
    }
    private void sendEmail(FirebaseUser user,View view){
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(),"Email gönderildi, hesabınızı doğrulayın.",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}