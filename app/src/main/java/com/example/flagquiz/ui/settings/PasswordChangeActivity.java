package com.example.flagquiz.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.flagquiz.MainActivity2;
import com.example.flagquiz.R;
import com.example.flagquiz.databinding.ActivityPasswordChangeBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChangeActivity extends AppCompatActivity {
    private ActivityPasswordChangeBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityPasswordChangeBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);


        //tanımlamalar
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        allEnabled(true);

        //geri butonu
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Şifre Değiştir");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        binding.passwordUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordUpdateButtonClicked(view);
            }
        });




    }

    private void allEnabled(boolean enabledStatus){
        binding.oldPasswordText.setEnabled(enabledStatus);
        binding.newPasswordText.setEnabled(enabledStatus);
        binding.newPasswordRetryText.setEnabled(enabledStatus);
        binding.passwordUpdateButton.setEnabled(enabledStatus);
    }


    private void passwordUpdateButtonClicked(View view) {
        binding.progressBarSettingsPasswordChange.setVisibility(view.VISIBLE);
        allEnabled(false);


        if (user != null) {
            String oldPassword = binding.oldPasswordText.getText().toString();
            String newPassword = binding.newPasswordText.getText().toString();
            String newPasswordRetry=binding.newPasswordRetryText.getText().toString();

            if(!oldPassword.equals("")&&!newPassword.equals("")&&!newPasswordRetry.equals("")){
                if(newPasswordRetry.matches(newPassword)){
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(reauthTask -> {
                                if (reauthTask.isSuccessful()) {
                                    // Eski şifre doğrulama başarılı, şimdi yeni şifreyi güncelle
                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    binding.progressBarSettingsPasswordChange.setVisibility(view.GONE);
                                                    // Şifre değiştirme başarılı
                                                    Toast.makeText(this, "Şifre başarıyla değiştirildi.", Toast.LENGTH_SHORT).show();
                                                    //textboxların içini sil
                                                    binding.oldPasswordText.setText("");
                                                    binding.newPasswordText.setText("");
                                                    binding.newPasswordRetryText.setText("");
                                                    allEnabled(true);

                                                    //ayarlar fragmentına geri dön
                                                    Intent intent=new Intent(getApplicationContext(), MainActivity2.class);
                                                    startActivity(intent);
                                                } else {
                                                    binding.progressBarSettingsPasswordChange.setVisibility(view.GONE);
                                                    // Şifre değiştirme başarısız
                                                    Toast.makeText(this, "Şifre değiştirme başarısız. Hata: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    allEnabled(true);

                                                }
                                            });
                                } else {
                                    binding.progressBarSettingsPasswordChange.setVisibility(view.GONE);
                                    // Eski şifre doğrulama başarısız
                                    Toast.makeText(this, "Eski şifre doğrulama başarısız. Hata: " + reauthTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    allEnabled(true);

                                }
                            });
                }
            }
            else{
                binding.progressBarSettingsPasswordChange.setVisibility(view.GONE);
                Toast.makeText(this, "Yeni şifreler aynı değil !", Toast.LENGTH_SHORT).show();
                allEnabled(true);

            }
        }
    }
}