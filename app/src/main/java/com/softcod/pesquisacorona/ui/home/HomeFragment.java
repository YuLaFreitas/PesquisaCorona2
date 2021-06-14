package com.softcod.pesquisacorona.ui.home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.softcod.pesquisacorona.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    HomeViewModel hvm;
    View v;
    ProgressDialog pd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        HomeViewModel homeViewModel = new ViewModelProvider(this)
                .get(HomeViewModel.class);
        v = getView();
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final TextView textView = binding.lista;

        homeViewModel.getText().observe(getViewLifecycleOwner(),
                new Observer<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(@Nullable String s) {
                textView.setMovementMethod(new ScrollingMovementMethod());
                textView.setText(s);
            }
        });

        hvm = new HomeViewModel();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        pd = new ProgressDialog(this.getContext());
        pd.setTitle("Um momento");
        pd.setMessage("Estamos colhendo os dados.");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.setIcon(android.R.drawable.ic_dialog_info);
        pd.show();

    }

    @Override
    public void onResume() {
        super.onResume();

        pd.dismiss();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}