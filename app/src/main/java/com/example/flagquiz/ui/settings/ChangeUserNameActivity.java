package com.example.flagquiz.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flagquiz.MainActivity2;
import com.example.flagquiz.R;
import com.example.flagquiz.databinding.ActivityChangeUserNameBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ChangeUserNameActivity extends AppCompatActivity {
    private ActivityChangeUserNameBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeUserNameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        allEnabled(true);

        // Geri butonu
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Kullanıcı Adı Değiştir");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        binding.userNameChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameChangeButtonClicked(view);
            }
        });
    }

    private void allEnabled(boolean enabledStatus) {
        binding.userNameChangeButton.setEnabled(enabledStatus);
        binding.newUserNameText.setEnabled(enabledStatus);
    }

    private void userNameChangeButtonClicked(View view) {
        binding.progressBarSettingsUserNameChange.setVisibility(View.VISIBLE);
        allEnabled(false);

        String newUserName = binding.newUserNameText.getText().toString().toUpperCase().toString();

        if (!newUserName.isEmpty()) {
            // Kullanıcı adının daha önceden alınıp alınmadığını kontrol et
            isUserNameAvailable(newUserName, view);
        } else {
            Toast.makeText(this, "Kullanıcı adı alanı boş bırakılamaz", Toast.LENGTH_SHORT).show();
            allEnabled(true);
            binding.progressBarSettingsUserNameChange.setVisibility(View.GONE);
        }
    }

    private void isUserNameAvailable(String newUserName, View view) {
        CollectionReference reference = firestore.collection("Users");
        Query userNameQuery = reference.whereEqualTo("userName", newUserName);

        userNameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        // Kullanıcı adı daha önce alınmamış, güncelleme yapılabilir
                        updateUserName(newUserName, view);
                    } else {
                        Snackbar.make(view, "Bu kullanıcı adı daha önceden alınmış", Snackbar.LENGTH_SHORT).show();
                        allEnabled(true);
                        binding.progressBarSettingsUserNameChange.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    allEnabled(true);
                    binding.progressBarSettingsUserNameChange.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateUserName(String newUserName, View view) {
        CollectionReference reference = firestore.collection("Users");
        Query query = reference.whereEqualTo("email", user.getEmail());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Kullanıcı bulundu, userName alanını güncelle
                        String documentId = document.getId();

                        // Kullanıcı adını güncelle
                        reference.document(documentId).update("userName", newUserName)
                                .addOnSuccessListener(aVoid -> {
                                    binding.newUserNameText.setText("");
                                    // Başarıyla güncellendi
                                    allEnabled(true);
                                    binding.progressBarSettingsUserNameChange.setVisibility(View.GONE);

                                    Toast.makeText(getApplicationContext(), "Kullanıcı adı güncellendi: " + newUserName, Toast.LENGTH_SHORT).show();
                                    // Ayarlar fragmentına geri dön
                                    //Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                                    //startActivity(intent);
                                    binding.newUserNameText.setText("");
                                })
                                .addOnFailureListener(e -> {
                                    // Güncelleme sırasında bir hata oluştu
                                    allEnabled(true);
                                    binding.progressBarSettingsUserNameChange.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Kullanıcı adı güncelleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    allEnabled(true);
                    binding.progressBarSettingsUserNameChange.setVisibility(View.GONE);
                }
            }
        });
    }
}
