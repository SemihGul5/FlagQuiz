package com.example.flagquiz.ui.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flagquiz.MainActivity;
import com.example.flagquiz.R;
import com.example.flagquiz.databinding.FragmentDeleteAccountBinding;
import com.example.flagquiz.databinding.FragmentPasswordChangeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;


public class DeleteAccountFragment extends Fragment {

    private FragmentDeleteAccountBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private String userEmail;

    public DeleteAccountFragment() {
        // Gerekli boş yapıcı method
    }

    public static DeleteAccountFragment newInstance(String param1, String param2) {
        return new DeleteAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        userEmail = (user != null) ? user.getEmail() : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDeleteAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.deleteAccountButton.setOnClickListener(this::buttonClicked);
    }

    private void buttonClicked(View view) {
        allEnabled(false);
        binding.progressBarDeleteAccount.setVisibility(View.VISIBLE);

        String email = binding.deleteAccountEmailText.getText().toString();
        String password = binding.deleteAccountPasswordText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "E-mail ve şifre alanını giriniz.", Toast.LENGTH_SHORT).show();
            allEnabled(true);
            binding.progressBarDeleteAccount.setVisibility(View.GONE);
        } else {
            showDeleteConfirmationDialog();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Hesabı Sil");
        builder.setMessage("Hesabınızı silmek istediğinizden emin misiniz?");
        builder.setPositiveButton("Evet", (dialog, which) -> deleteUserAccount());
        builder.setNegativeButton("Hayır", (dialog, which) -> {
            dialog.dismiss();
            allEnabled(true);
            binding.progressBarDeleteAccount.setVisibility(View.GONE);
        });
        builder.show();
    }

    private void deleteUserAccount() {
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Hesap başarıyla silindi.", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    navigateToMainActivity();
                    deleteUserInFirestore();
                } else {
                    Toast.makeText(getContext(), "Hesap silme işlemi başarısız", Toast.LENGTH_SHORT).show();
                }
                allEnabled(true);
                binding.progressBarDeleteAccount.setVisibility(View.GONE);
            });
        } else {
            Toast.makeText(getContext(), "Kullanıcı oturumu açılmamış", Toast.LENGTH_SHORT).show();
            allEnabled(true);
            binding.progressBarDeleteAccount.setVisibility(View.GONE);
        }
    }

    private void deleteUserInFirestore() {
        firestore.collection("Users").whereEqualTo("email", userEmail).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    firestore.collection("Users").document(document.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Log.e("Firestore", "Kullanıcı koleksiyonu başarıyla silindi"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Kullanıcı koleksiyonu silme hatası", e));
                }
            } else {
                Log.e("Firestore", "Kullanıcı koleksiyonu silme hatası");
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
    }

    private void allEnabled(Boolean status) {
        binding.deleteAccountButton.setEnabled(status);
        binding.deleteAccountPasswordText.setEnabled(status);
        binding.deleteAccountEmailText.setEnabled(status);
    }
}
