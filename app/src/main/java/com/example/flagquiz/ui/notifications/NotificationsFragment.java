package com.example.flagquiz.ui.notifications;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.flagquiz.databinding.FragmentNotificationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textNotifications;
        //notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getEmailAndUserName();
        binding.sendMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClicked();
            }
        });
    }

    private void buttonClicked() {
        String title = binding.notifiactionMessageTitleText.getText().toString();
        String message = binding.notifiactionMessageContextText.getText().toString();

        if (!title.equals("") && !message.equals("")) {
            binding.progressBarSendMail.setVisibility(View.VISIBLE);
            sendEmail("mailto:abrebostudio@gmail.com?subject=" + title + "&body=" + message);

            // E-posta gönderildikten sonra Firestore işlemi
            updateRemainingSendQuota();
        } else {
            Toast.makeText(getContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRemainingSendQuota() {
        if (user != null) {
            // Kullanıcının "kalan gönderim hakkı" alanını azalt
            firestore.collection("Users")
                    .whereEqualTo("email", user.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Firestore'dan mevcut değeri al
                                String currentQuota = document.getString("kalanHak").toString();
                                int cCurrentQuota=Integer.parseInt(currentQuota);

                                // Azaltılmış değeri Firestore'a geri yükle
                                firestore.collection("Users")
                                        .document(document.getId())
                                        .update("kalanHak", String.valueOf(cCurrentQuota - 1))
                                        .addOnSuccessListener(aVoid -> {
                                            // Başarılı bir şekilde güncellendiğinde yapılacak işlemler
                                        })
                                        .addOnFailureListener(e -> {
                                            // Güncelleme işlemi başarısız olduğunda yapılacak işlemler
                                            Toast.makeText(getContext(), "Güncelleme işlemi başarısız oldu", Toast.LENGTH_SHORT).show();
                                        });
                                binding.progressBarSendMail.setVisibility(View.GONE);

                            }
                        } else {
                            // Firestore veri getirme işlemi başarısız olduğunda yapılacak işlemler
                            Toast.makeText(getContext(), "Veri getirme işlemi başarısız oldu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void sendEmail(String mailto) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "E-posta uygulaması bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void getEmailAndUserName() {
        if (user != null) {
            firestore.collection("Users").whereEqualTo("email", user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Map<String, Object> data = documentSnapshot.getData();

                            String hak = (String) data.get("kalanHak");
                            binding.textView5.setText("Kalan gönderim hakkı: "+hak);

                        }
                    }
                }
            });
        }
    }
}