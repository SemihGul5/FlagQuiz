package com.example.flagquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import com.example.flagquiz.databinding.ActivityGameBinding;

public class GameActivity extends AppCompatActivity implements OnDataPassedListener,OnAnswerSelectedListener  {
    private ActivityGameBinding binding;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGameBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        QuestionFragment questionFragment=new QuestionFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.gameFrameLayout, questionFragment)
                .commit();

        progressbarAndTimer();

    }
    private void progressbarAndTimer() {
        binding.progressBarGame.getProgressDrawable().setColorFilter(Color.parseColor("#70F155"), PorterDuff.Mode.SRC_IN);

        new CountDownTimer(60000,1000) {
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

            }
        }.start();
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
        // Veri geldiğinde yeni bir fragment oluşturun ve bu veriyi iletebilirsiniz.
        QuestionFragment newFragment = QuestionFragment.newInstance(score);

        // Yeni fragment'ı yüklemek için bir FragmentTransaction kullanın.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.gameFrameLayout, newFragment);
        transaction.commit();
        binding.scoreText.setText(String.valueOf(score));
    }

    @Override
    public void onAnswerSelected(boolean isCorrect) {
        if (isCorrect) {
            score++;  // Puanı artırın
            binding.scoreText.setText(String.valueOf(score));  // Gösterilen puanı güncelleyin
        }
    }
}