package com.example.flagquiz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class LeftMenuFragment extends Fragment {


    public LeftMenuFragment() {
        // Required empty public constructor
    }

    public static LeftMenuFragment newInstance(String param1, String param2) {
        LeftMenuFragment fragment = new LeftMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_left_menu, container, false);
    }
}