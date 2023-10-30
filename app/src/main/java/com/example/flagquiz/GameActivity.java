package com.example.flagquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import com.example.flagquiz.databinding.ActivityGameBinding;

public class GameActivity extends AppCompatActivity {
    private ActivityGameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGameBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        binding.progressBarGame.setProgress(100);
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
                    binding.kalanSureText.setTextColor(getColor(R.color.Yeşil));
                } else if (sc<40&&sc>=20) {
                    binding.progressBarGame.getProgressDrawable().setColorFilter(Color.parseColor("#FFEB3B"), PorterDuff.Mode.SRC_IN);
                    binding.kalanSureText.setTextColor(getColor(R.color.Sarı));
                }
                else {
                    binding.progressBarGame.getProgressDrawable().setColorFilter(Color.parseColor("#F44336"), PorterDuff.Mode.SRC_IN);
                    binding.kalanSureText.setTextColor(getColor(R.color.Kırmızı));
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


}