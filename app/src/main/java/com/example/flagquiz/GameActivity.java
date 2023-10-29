package com.example.flagquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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


    }
    @Override
    protected void onPause() {
        super.onPause();
        // Uygulama arka plana atıldığında burada yapmak istediğiniz işlemleri gerçekleştirin.
        Intent intent= new Intent(GameActivity.this, MainActivity2.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Uygulama ön plana geldiğinde burada yapmak istediğiniz işlemleri gerçekleştirin.
        // Örneğin, önceki durumu geri yükleyebilir veya oyununuzu yeniden başlatabilirsiniz.
        Intent intent= new Intent(GameActivity.this, MainActivity2.class);

    }

}