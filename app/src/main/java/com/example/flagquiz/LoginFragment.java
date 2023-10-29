package com.example.flagquiz;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flagquiz.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private FirebaseAuth auth;
    String email,password;

    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();

        FirebaseUser user=auth.getCurrentUser();
        if (user!=null&&user.isEmailVerified()){
            Intent intent= new Intent(getContext(), MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                                Intent intent= new Intent(getContext(), MainActivity2.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else{
                                Snackbar.make(view,"E-mail adresinizi doğrulayın!",Snackbar.LENGTH_SHORT).show();
                            }
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

    private void goToSignin(View view) {
        binding.kayitOlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections directions=LoginFragmentDirections.actionLoginFragmentToSigninFragment();
                Navigation.findNavController(view).navigate(directions);

            }
        });
    }

}