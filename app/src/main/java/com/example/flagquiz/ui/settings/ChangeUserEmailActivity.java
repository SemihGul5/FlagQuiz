package com.example.flagquiz.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.flagquiz.MainActivity;
import com.example.flagquiz.R;
import com.example.flagquiz.databinding.ActivityChangeUserEmailBinding;
import com.example.flagquiz.databinding.ActivityChangeUserNameBinding;
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

public class ChangeUserEmailActivity extends AppCompatActivity {
    private ActivityChangeUserEmailBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeUserEmailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //geri butonu
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Şifre Değiştir");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        allEnabled(true);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();


        binding.userEmailChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClicked(view);
            }
        });

    }
    private void buttonClicked(View view) {
        binding.progressBarSettingsPasswordChange.setVisibility(View.VISIBLE);
        allEnabled(false);
        String newEmail=binding.newEmailText.getText().toString();
        String newEmailRetry=binding.newEmailRetryText.getText().toString();

        if(!newEmail.isEmpty()||!newEmailRetry.isEmpty()){
            if (user != null) {
                user.updateEmail(newEmail)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                sendEmail(user,view);
                                updateFirebaseCollection(newEmail);
                                Toast.makeText(this, "Email güncellendi: " + newEmail, Toast.LENGTH_SHORT).show();
                                binding.progressBarSettingsPasswordChange.setVisibility(View.GONE);
                                auth.signOut();
                                Intent intent= new Intent(this, MainActivity.class);
                                startActivity(intent);
                                allEnabled(true);

                            } else {
                                Toast.makeText(this,  "E: " + task.getException(), Toast.LENGTH_SHORT).show();
                                binding.progressBarSettingsPasswordChange.setVisibility(View.GONE);
                                allEnabled(true);

                            }
                        });
            } else {
                Toast.makeText(this,  "Kullanıcı oturumu açmamış.", Toast.LENGTH_SHORT).show();
                binding.progressBarSettingsPasswordChange.setVisibility(View.GONE);
                allEnabled(true);

            }
        }else{
            Toast.makeText(this, "Tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            binding.progressBarSettingsPasswordChange.setVisibility(View.GONE);
            allEnabled(true);

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
                Toast.makeText(this,  "Kullanıcı sorgusu başarısız: ", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"Email gönderildi, hesabınızı doğrulayın.",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}