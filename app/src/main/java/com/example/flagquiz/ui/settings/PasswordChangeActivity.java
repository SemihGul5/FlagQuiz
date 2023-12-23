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
import com.example.flagquiz.databinding.ActivityPasswordChangeBinding;
import com.example.flagquiz.interfaces.PasswordCheckCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PasswordChangeActivity extends AppCompatActivity implements PasswordCheckCallback {
    private ActivityPasswordChangeBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordChangeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Tanımlamalar
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        allEnabled(true);

        // Geri butonu
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Şifre Değiştir");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        binding.passwordUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = binding.oldPasswordText.getText().toString();
                passwordUpdateButtonClicked(oldPassword);
            }
        });
    }

    private void allEnabled(boolean enabledStatus) {
        binding.oldPasswordText.setEnabled(enabledStatus);
        binding.passwordUpdateButton.setEnabled(enabledStatus);
    }

    private void passwordUpdateButtonClicked(String oldPassword) {
        allEnabled(false);
        binding.progressBarSettingsPasswordChange.setVisibility(View.VISIBLE);

        isOldPasswordCorrect(user, oldPassword);
    }

    private void isOldPasswordCorrect(FirebaseUser user, String oldPassword) {
        // Kullanıcının şifresini yeniden doğrulamak için reauthenticate metodu kullanılır
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        boolean isCorrect = task.isSuccessful();
                        //Şifre güncelleme işlemine devam et
                        onResult(isCorrect);
                    }
                });
    }

    private boolean isPasswordValid(String newPassword, String newPasswordRepeat) {
        return newPassword.equals(newPasswordRepeat);
    }

    @Override
    public void onResult(boolean isCorrect) {
        if (isCorrect) {
            auth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Şifre sıfırlama e-posta gönderme başarılı
                        Toast.makeText(getApplicationContext(), "E-posta adresiniz başarıyla güncellendi ve şifre sıfırlama bağlantısı gönderildi", Toast.LENGTH_SHORT).show();
                    } else {
                        // Şifre sıfırlama e-posta gönderme başarısız
                        Snackbar.make(findViewById(android.R.id.content), "Şifre sıfırlama e-posta gönderme hatası: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                    binding.progressBarSettingsPasswordChange.setVisibility(View.GONE);
                    allEnabled(true);
                }
            });
            /*user.updatePassword(binding.newPasswordText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Şifre güncelleme başarılı
                                Toast.makeText(getApplicationContext(), "Şifreniz başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                            } else {
                                // Şifre güncelleme başarısız
                                Snackbar.make(findViewById(android.R.id.content), "Şifre güncelleme hatası: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                binding.newPasswordRetryText.setText("");
                                binding.oldPasswordText.setText("");
                                binding.newPasswordText.setText("");
                            }


                        }
                    });*/
        } else {
            // Eski şifre yanlış veya yeni şifre ile tekrarı uyuşmuyor
            Snackbar.make(findViewById(android.R.id.content), "Eski şifre yanlış.", Snackbar.LENGTH_SHORT).show();
            binding.progressBarSettingsPasswordChange.setVisibility(View.GONE);
            allEnabled(true);
        }
    }
}
