package com.example.flagquiz.ui.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.flagquiz.R;
import com.example.flagquiz.databinding.ActivityGameBinding;
import com.example.flagquiz.databinding.FragmentSettingsBinding;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private FragmentSettingsBinding binding;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        arrayList=new ArrayList<>();
        arrayList.add("Şifreyi Değiştir");
        arrayList.add("Kullanıcı Adı Değiştir");
        arrayList.add("E - mail Değiştir");
        arrayList.add("Hesabı Sil");

        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        binding.settListView.setAdapter(arrayAdapter);

        listViewItemSelected(view);
    }
    private void listViewItemSelected(View view) {
        binding.settListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = arrayAdapter.getItem(i);
                NavController navController = Navigation.findNavController(view);

                if (selected.equals("Şifreyi Değiştir")) {
                    navController.navigate(R.id.action_settingsFragment_to_passwordChangeFragment);
                } else if (selected.equals("Kullanıcı Adı Değiştir")) {
                    navController.navigate(R.id.action_settingsFragment_to_userNameChangeFragment);
                } else if (selected.equals("E - mail Değiştir")) {
                    navController.navigate(R.id.action_settingsFragment_to_userEmailChangeFragment);
                } else if (selected.equals("Hesabı Sil")) {
                    navController.navigate(R.id.action_settingsFragment_to_deleteAccountFragment);
                }
            }
        });
    }
}