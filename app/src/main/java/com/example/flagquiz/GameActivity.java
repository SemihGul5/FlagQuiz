package com.example.flagquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.example.flagquiz.databinding.ActivityGameBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class GameActivity extends AppCompatActivity implements OnDataPassedListener,OnAnswerSelectedListener,OnAlertDialogDismissListener {
    private ActivityGameBinding binding;
    private int score;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    private CountDownTimer timer;
    private OnNewScoreListener scoreListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        QuestionFragment questionFragment = new QuestionFragment();
        scoreListener = new OnNewScoreListener() {
            @Override
            public void scoreUpdate(int newScore) {

            }
        };
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.gameFrameLayout, questionFragment)
                .commit();

        progressbarAndTimer();

    }
    private void progressbarAndTimer() {
        binding.progressBarGame.getProgressDrawable().setColorFilter(Color.parseColor("#70F155"), PorterDuff.Mode.SRC_IN);

        timer=new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long l) {

                long sc=l/1000;
                // Geri sayım her saniye gerçekleştiğinde burası çalışır
                binding.kalanSureText.setText("Kalan süre "+l/1000);
                int progressBarValue = (int) (l / 1000); // Kalan süre saniye cinsinden
                binding.progressBarGame.setProgress(progressBarValue);

                if (sc<=60&&sc>=40){
                    binding.progressBarGame.getProgressDrawable().setColorFilter(Color.parseColor("#70F155"), PorterDuff.Mode.SRC_IN);
                    binding.kalanSureText.setTextColor(binding.getRoot().getContext().getResources()
                            .getColor(R.color.Yeşil));
                } else if (sc<40&&sc>=20) {
                    binding.progressBarGame.getProgressDrawable().setColorFilter(Color.parseColor("#FFEB3B"), PorterDuff.Mode.SRC_IN);
                    binding.kalanSureText.setTextColor(binding.getRoot().getContext().getResources()
                            .getColor(R.color.Sarı));
                }
                else {
                    binding.progressBarGame.getProgressDrawable().setColorFilter(Color.parseColor("#F44336"), PorterDuff.Mode.SRC_IN);
                    binding.kalanSureText.setTextColor(binding.getRoot().getContext().getResources()
                            .getColor(R.color.Kırmızı));
                }
            }
            @Override
            public void onFinish() {
                binding.kalanSureText.setText("Süre doldu!");
                binding.progressBarGame.setProgress(0);
                //eğer score önceki scoredan yüksekse değiştir.

                if (auth.getCurrentUser() != null) {
                    firestore.collection("Users").whereEqualTo("email", auth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Toast.makeText(GameActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (value != null) {
                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                    Map<String, Object> data = documentSnapshot.getData();
                                    String sscore = (String) data.get("score");
                                    int nscore=Integer.parseInt(sscore);
                                    if (score>nscore){
                                        //yeni score'u kaydet
                                        updateFirebase();
                                        scoreListener.scoreUpdate(score);
                                    }
                                }
                            }
                        }
                    });
                }
                showAlertDialog("Oyun Bitti","Süre Doldu !");
            }
        }.start();
    }
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(GameActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }


    private void updateFirebase() {
        Query query=firestore.collection("Users").whereEqualTo("email",auth.getCurrentUser().getEmail());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Belirli bir kritere uyan belgeyi güncelle
                    String userId = document.getId();
                    String newScore=String.valueOf(score);
                    firestore.collection("Users").document(userId).update("score", newScore);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GameActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        Intent intent= new Intent(GameActivity.this, MainActivity2.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDataPassed(String data,int score) {
        // Veri geldiğinde yeni bir fragment oluşturun ve bu veriyi ilet.
        QuestionFragment newFragment = QuestionFragment.newInstance(score);

        // Yeni fragment'ı yüklemek için bir FragmentTransaction kullan.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.gameFrameLayout, newFragment);
        transaction.commit();
        binding.scoreText.setText(String.valueOf(score));
    }

    @Override
    public void onAnswerSelected(boolean isCorrect) {
        if (isCorrect) {
            score++;
            binding.scoreText.setText(String.valueOf(score));  // Gösterilen puanı güncelle
        }
    }

    @Override
    public void onAlertDialogDismissed(boolean isGet) {
        if(isGet){
            timer.cancel();
            showAlertDialog("Oyun Bitti","Yanlış cevap verdiniz !");

        }
    }

}