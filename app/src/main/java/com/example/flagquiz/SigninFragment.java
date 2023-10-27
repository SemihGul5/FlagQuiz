package com.example.flagquiz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flagquiz.databinding.FragmentSigninBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;


public class SigninFragment extends Fragment {
    private FragmentSigninBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    String name;
    String userName;
    String email;
    String password;
    String passwordRetry;
    public SigninFragment() {
        // Required empty public constructor
    }

    public static SigninFragment newInstance(String param1, String param2) {
        SigninFragment fragment = new SigninFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSigninBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goToLogin(view);
        userSignin(view);
    }

    private void userSignin(View view) {
        binding.kayitolkayitOlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=binding.signInUserNameText.getText().toString();
                userName=binding.singInUserNickNameText.getText().toString();
                email=binding.singInUserEmailText.getText().toString();
                password=binding.singInPasswordText.getText().toString();
                passwordRetry=binding.singInPasswordRetryText.getText().toString();

                if (name.equals("")||userName.equals("")||email.equals("")||passwordRetry.equals("")||password.equals(""))
                {
                    Snackbar.make(view,"Tüm alanları doldurun",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    if (password.equals(passwordRetry)){
                        CollectionReference reference=firestore.collection("Users");
                        Query query=reference.whereEqualTo("userName",userName);//firestore'da bu kullanıcı adının olup olmadığı alınır.
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().isEmpty()){
                                            //kullanıcı adı yok, diğer yerler tamam. kaydet.
                                            saveInFireStoreUserNameCollection(userName,email);
                                            //saveInFireStoreUserCollection();
                                            saveAuthUser(email,password,view);
                                        }
                                        else{
                                            Snackbar.make(view, "Bu kullanıcı adı daha önceden alınmış", Snackbar.LENGTH_SHORT).show();
                                            binding.singInUserNickNameText.setBackgroundColor(getResources().getColor(R.color.Kırmızı));

                                        }
                                    }
                                    else{
                                        Snackbar.make(view, "Sorgu hatası: " + task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                            }
                        });
                    }
                    else {
                        Snackbar.make(view,"Şifreler aynı olmalıdır",Snackbar.LENGTH_SHORT).show();
                        binding.singInPasswordRetryText.setBackgroundColor(getResources().getColor(R.color.Kırmızı));
                        binding.singInPasswordText.setBackgroundColor(getResources().getColor(R.color.Kırmızı));
                    }
                }
            }
        });
    }
    private void saveAuthUser(String email,String password,View view){
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user=auth.getCurrentUser();//oluşturulan userı aldık, mail göndermek için
                sendEmail(user,view);//aktivasyon maili gönderilecek
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });
    }
    private void saveInFireStoreUserNameCollection(String userName,String email){
        HashMap<String, String> data=new HashMap<>();
        String userNameUpper=userName.toUpperCase();
        data.put("userName",userNameUpper);
        data.put("email",email);

        firestore.collection("Users").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //kullanıcı adı kayıt başarılı, mesaj göstermeye gerek yok
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void sendEmail(FirebaseUser user,View view){
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                NavDirections directions=SigninFragmentDirections.actionSigninFragmentToLoginFragment();
                Navigation.findNavController(view).navigate(directions);
                Toast.makeText(getContext(),"Email gönderildi, hesabınızı doğrulayın.",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view,e.getLocalizedMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private void goToLogin(View view) {
        binding.kayitolGirisYapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections directions=SigninFragmentDirections.actionSigninFragmentToLoginFragment();
                Navigation.findNavController(view).navigate(directions);
            }
        });
    }
}