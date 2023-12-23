package com.example.flagquiz.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flagquiz.databinding.RecyclerListBinding;
import com.example.flagquiz.models.User;

import java.util.ArrayList;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreHolder> {
    ArrayList<User> userArrayList;
    Context context;

    public ScoreAdapter(ArrayList<User> userArrayList, Context context) {
        this.userArrayList = userArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ScoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerListBinding binding=RecyclerListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ScoreHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ScoreHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.binding.recyclerMyUserNameText.setText(user.getRank() + ". " + user.getUserName());
        holder.binding.recyclerMyUserScoreText.setText(user.getScore());
    }


    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ScoreHolder extends RecyclerView.ViewHolder{
        RecyclerListBinding binding;

        public ScoreHolder(RecyclerListBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
