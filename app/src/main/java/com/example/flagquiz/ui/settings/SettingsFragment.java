package com.example.flagquiz.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.flagquiz.R;
import com.example.flagquiz.databinding.FragmentHomeBinding;
import com.example.flagquiz.databinding.FragmentSettingsBinding;

import java.util.ArrayList;


public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList=new ArrayList<>();
        arrayList.add("Şifreyi Değiştir");
        arrayList.add("Kullanıcı Adı Değiştir");
        arrayList.add("E - mail Değiştir");
        arrayList.add("Hesabı Sil");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return  root;



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,arrayList);
        binding.settListView.setAdapter(arrayAdapter);

        listViewItemSelected(view);
    }

    private void listViewItemSelected(View view) {
        binding.settListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = arrayAdapter.getItem(i);

                if (selected.equals("Şifreyi Değiştir")) {
                    Intent intent= new Intent(getContext(), PasswordChangeActivity.class);
                    startActivity(intent);

                } else if (selected.equals("Kullanıcı Adı Değiştir")) {
                    Intent intent= new Intent(getContext(), ChangeUserNameActivity.class);
                    startActivity(intent);

                } else if (selected.equals("E - mail Değiştir")) {
                    Intent intent= new Intent(getContext(), ChangeUserEmailActivity.class);
                    startActivity(intent);

                } else if (selected.equals("Hesabı Sil")) {
                    Intent intent= new Intent(getContext(), DeleteAccountActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}