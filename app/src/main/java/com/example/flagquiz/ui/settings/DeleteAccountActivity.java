package com.example.flagquiz.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.flagquiz.MainActivity;
import com.example.flagquiz.R;
import com.example.flagquiz.databinding.ActivityChangeUserEmailBinding;
import com.example.flagquiz.databinding.ActivityDeleteAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteAccountActivity extends AppCompatActivity {
    private ActivityDeleteAccountBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //geri butonu
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Şifre Değiştir");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);


        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();
        userEmail=user.getEmail();
        allEnabled(true);



        binding.deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClicked(view);
            }
        });
    }

    private void buttonClicked(View view) {
        allEnabled(false);
        binding.progressBarDeleteAccount.setVisibility(View.VISIBLE);

        String email=binding.deleteAccountEmailText.getText().toString();
        String password=binding.deleteAccountPasswordText.getText().toString();

        if(email.isEmpty()||password.isEmpty()){
            Toast.makeText(this, "E-mail ve şifre alanını giriniz.", Toast.LENGTH_SHORT).show();
        }
        else{
            //hesap silme için uyarı dialogu çıkart
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Hesap Silme");
            builder.setMessage("Hesabınızı silmek istediğinizden emin misiniz?");
            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //evet'e tıklandı, hesabı sil
                    // Kullanıcının girdiği email ve şifre ile Firebase hesabını sil
                    if(user!=null){
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //hesap başarıyla silindi. yapılacak işlemler;
                                    Toast.makeText(getApplicationContext(), "Hesap başarıyla silindi.", Toast.LENGTH_SHORT).show();
                                    allEnabled(true);
                                    auth.signOut();

                                    // Hesap silindiği için bir aktiviteye yönlendirebilirsiniz, örneğin giriş ekranına.
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);

                                    binding.progressBarDeleteAccount.setVisibility(View.GONE);

                                    //koleksiyondan da silme işlemi için
                                    deleteUserInFirestore();
                                }
                                else{
                                    //hesap silme işlemi başarısız oldu
                                    Toast.makeText(getApplicationContext(), "Hesap silme işlemi başarısız", Toast.LENGTH_SHORT).show();
                                    allEnabled(true);
                                    binding.progressBarDeleteAccount.setVisibility(View.GONE);

                                }
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(), "Kullanıcı oturumu açılmamış", Toast.LENGTH_SHORT).show();
                        allEnabled(true);
                        binding.progressBarDeleteAccount.setVisibility(View.GONE);

                    }


                }
            });
            builder.setNegativeButton("Hayır",null);
            builder.show();
            allEnabled(true);
            binding.progressBarDeleteAccount.setVisibility(View.GONE);


        }
    }

    private void deleteUserInFirestore() {
        firestore.collection("Users").whereEqualTo("email",userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Hedef belgeyi sil
                        firestore.collection("Users").document(document.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Belge başarıyla silindiğinde yapılacak işlemler
                                    //Toast.makeText(getContext(), "Kullanıcı başarıyla silindi.", Toast.LENGTH_SHORT).show();
                                    Log.e("Firestore", "Kullanıcı koleksiyonunu başarıyla silindi");
                                })
                                .addOnFailureListener(e -> {
                                    // Belge silme işlemi başarısız olduğunda yapılacak işlemler
                                    //Toast.makeText(getContext(), "Kullanıcı silme işlemi başarısız oldu.", Toast.LENGTH_SHORT).show();
                                    Log.e("Firestore", "Kullanıcı koleksiyonunu silme hatası, for içinde");
                                });
                    }
                }
                else {
                    Log.e("Firestore", "Kullanıcı koleksiyonunu silme hatası");
                }
            }
        });
    }
    private void allEnabled(Boolean status){
        binding.deleteAccountButton.setEnabled(status);
        binding.deleteAccountPasswordText.setEnabled(status);
        binding.deleteAccountEmailText.setEnabled(status);
    }
}