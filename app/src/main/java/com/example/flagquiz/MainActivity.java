package com.example.flagquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.flagquiz.databinding.ActivityMain2Binding;
import com.example.flagquiz.databinding.ActivityMainBinding;
import com.example.flagquiz.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        auth=FirebaseAuth.getInstance();

        FirebaseUser user=auth.getCurrentUser();
        if (user!=null&&user.isEmailVerified()){
            Intent intent= new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
            finish();
        }

        goToSignin(view);

        userLogin(view);
        forgotPassword(view);
    }
    private void forgotPassword(View view) {
        binding.textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=binding.userEmailText.getText().toString();
                if (email.equals("")){
                    Snackbar.make(view,"E-mail alanı dolu olmalıdır!",Snackbar.LENGTH_SHORT).show();
                }
                else{
                    auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Snackbar.make(view,"Şifre sıfırlama e-mail'i gönderildi.",Snackbar.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(view,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    private void userLogin(View view) {
        binding.girisYapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBarLogin.setVisibility(View.VISIBLE);
                email=binding.userEmailText.getText().toString();
                password=binding.userPasswordText.getText().toString();

                if (email.equals("")||password.equals("")){
                    Snackbar.make(view,"Email ve şifreyi girin.",Snackbar.LENGTH_SHORT).show();
                }
                else{
                    auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if (Objects.requireNonNull(auth.getCurrentUser()).isEmailVerified()){
                                Intent intent= new Intent(MainActivity.this, MainActivity2.class);
                                startActivity(intent);
                                finish();

                                binding.progressBarLogin.setVisibility(View.GONE);

                            }
                            else{
                                Snackbar.make(view,"E-mail adresinizi doğrulayın!",Snackbar.LENGTH_SHORT).show();
                                binding.progressBarLogin.setVisibility(View.GONE);

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.progressBarLogin.setVisibility(View.VISIBLE);

                            Snackbar.make(view,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
                            binding.progressBarLogin.setVisibility(View.GONE);

                        }
                    });
                }
            }
        });

    }

    private void goToSignin(View view) {
        binding.kayitOlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NavDirections directions=LoginFragmentDirections.actionLoginFragmentToSigninFragment();
                //Navigation.findNavController(view).navigate(directions);

                Intent intent= new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);

            }
        });
    }
}