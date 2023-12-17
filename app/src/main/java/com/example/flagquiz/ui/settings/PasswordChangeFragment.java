package com.example.flagquiz.ui.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flagquiz.R;
import com.example.flagquiz.databinding.FragmentPasswordChangeBinding;
import com.example.flagquiz.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class PasswordChangeFragment extends Fragment {
    private FragmentPasswordChangeBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;

    public PasswordChangeFragment() {
        // Required empty public constructor
    }

    public static PasswordChangeFragment newInstance(String param1, String param2) {
        PasswordChangeFragment fragment = new PasswordChangeFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPasswordChangeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return  root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.passwordUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordUpdateButtonClicked(view);
            }
        });
    }

    private void passwordUpdateButtonClicked(View view) {
        if (user != null) {
            String oldPassword = binding.oldPasswordText.getText().toString();
            String newPassword = binding.newPasswordText.getText().toString();
            String newPasswordRetry=binding.newPasswordRetryText.getText().toString();

            if(!oldPassword.isEmpty()&&!newPassword.isEmpty()&&!newPasswordRetry.isEmpty()){
                if(newPasswordRetry.matches(newPassword)){
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(reauthTask -> {
                                if (reauthTask.isSuccessful()) {
                                    // Eski şifre doğrulama başarılı, şimdi yeni şifreyi güncelle
                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    // Şifre değiştirme başarılı
                                                    Toast.makeText(getContext(), "Şifre başarıyla değiştirildi.", Toast.LENGTH_SHORT).show();
                                                    //textboxların içini sil
                                                    binding.oldPasswordText.setText("");
                                                    binding.newPasswordText.setText("");
                                                    binding.newPasswordRetryText.setText("");
                                                    //ayarlar fragmentına geri dön
                                                    NavController navController= Navigation.findNavController(view);
                                                    navController.navigate(R.id.action_passwordChangeFragment_to_settingsFragment);
                                                } else {
                                                    // Şifre değiştirme başarısız
                                                    Toast.makeText(getContext(), "Şifre değiştirme başarısız. Hata: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // Eski şifre doğrulama başarısız
                                    Toast.makeText(getContext(), "Eski şifre doğrulama başarısız. Hata: " + reauthTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else{
                Toast.makeText(getContext(), "Yeni şifreler aynı değil !", Toast.LENGTH_SHORT).show();
            }
        }
    }
}