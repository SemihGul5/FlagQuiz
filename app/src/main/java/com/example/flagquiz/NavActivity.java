package com.example.flagquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.flagquiz.databinding.ActivityMain2Binding;
import com.example.flagquiz.databinding.ActivityNavBinding;

public class NavActivity extends AppCompatActivity {
    private ActivityNavBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityNavBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
    }
}