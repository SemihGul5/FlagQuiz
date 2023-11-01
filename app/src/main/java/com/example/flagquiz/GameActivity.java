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

public class GameActivity extends AppCompatActivity implements OnDataPassedListener  {
    private ActivityGameBinding binding;

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



    }
    @Override
    protected void onPause() {
        super.onPause();
        Intent intent= new Intent(GameActivity.this, MainActivity2.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDataPassed(String data) {
        // Veri geldiğinde yeni bir fragment oluşturun ve bu veriyi iletebilirsiniz.
        QuestionFragment newFragment = new QuestionFragment();

        // Yeni fragment'ı yüklemek için bir FragmentTransaction kullanın.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.gameFrameLayout, newFragment);
        transaction.commit();
    }
}