package com.example.flagquiz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flagquiz.databinding.FragmentSigninBinding;


public class SigninFragment extends Fragment {
    private FragmentSigninBinding binding;


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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSigninBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
}