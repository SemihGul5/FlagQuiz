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
import com.example.flagquiz.databinding.FragmentSettingsBinding;
import com.example.flagquiz.databinding.FragmentUserNameChangeBinding;
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


public class UserNameChangeFragment extends Fragment {
    private FragmentUserNameChangeBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser user;
    public UserNameChangeFragment() {
        // Required empty public constructor
    }

    public static UserNameChangeFragment newInstance(String param1, String param2) {
        UserNameChangeFragment fragment = new UserNameChangeFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        allEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserNameChangeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return  root;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.userNameChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameChangeButtonClicked(view);
            }
        });

    }
    private void allEnabled(boolean enabledStatus){
        binding.userNameChangeButton.setEnabled(enabledStatus);
        binding.newUserNameText.setEnabled(enabledStatus);
    }

    private void userNameChangeButtonClicked(View view) {
        binding.progressBarSettingsUserNameChange.setVisibility(view.VISIBLE);
        allEnabled(false);


        String newUserName=binding.newUserNameText.getText().toString();
        String email=user.getEmail();
        //koleksiyonun referansını al
        CollectionReference reference=firestore.collection("Users");
        //email adresine göre sorgu yap
        Query query=reference.whereEqualTo(email,"email");
        Query userNameQuery=reference.whereEqualTo("userName",newUserName);//girilen kullanıcı adının zaten olup olmadığı kontrolü için

        if(!newUserName.isEmpty()){
            userNameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().isEmpty()){//kullanıcı adı yok, güncellenebilinir

                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            // Kullanıcı bulundu, userName alanını güncelle
                                            String documentId = document.getId();

                                            // Kullanıcı adını güncelle
                                            firestore.document(documentId).update("userName", newUserName)
                                                    .addOnSuccessListener(aVoid -> {
                                                        binding.newUserNameText.setText("");
                                                        // Başarıyla güncellendi
                                                        allEnabled(true);
                                                        binding.progressBarSettingsUserNameChange.setVisibility(view.GONE);

                                                        Toast.makeText(getContext(),"Kullanıcı adı güncellendi: " + newUserName, Toast.LENGTH_SHORT).show();
                                                        //ayarlar fragmentına geri dön
                                                        NavController navController= Navigation.findNavController(view);
                                                        navController.navigate(R.id.action_userNameChangeFragment_to_settingsFragment);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Güncelleme sırasında bir hata oluştu
                                                        allEnabled(true);
                                                        binding.progressBarSettingsUserNameChange.setVisibility(view.GONE);
                                                        Toast.makeText(getContext(), "Kullanıcı adı güncelleme hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }
                                    else{
                                        Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        allEnabled(true);
                                        binding.progressBarSettingsUserNameChange.setVisibility(view.GONE);
                                    }
                                }
                            });
                        }
                        else{
                            Snackbar.make(view, "Bu kullanıcı adı daha önceden alınmış", Snackbar.LENGTH_SHORT).show();
                            allEnabled(true);
                            binding.progressBarSettingsUserNameChange.setVisibility(view.GONE);
                        }
                    }
                    else{
                        Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        allEnabled(true);
                        binding.progressBarSettingsUserNameChange.setVisibility(view.GONE);
                    }
                }
            });
        }
        else{
            Toast.makeText(getContext(), "Kullanıcı adı alanı boş bırakılamaz", Toast.LENGTH_SHORT).show();
            allEnabled(true);
            binding.progressBarSettingsUserNameChange.setVisibility(view.GONE);
        }

    }
}