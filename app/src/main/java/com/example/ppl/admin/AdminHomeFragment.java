package com.example.ppl.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.ppl.R;


public class AdminHomeFragment extends Fragment {

    private ImageView belanja, pajak, rka, fakultas, tahunan, rencana, rekap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adminhome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        belanja = view.findViewById(R.id.belanja);
        pajak = view.findViewById(R.id.pajak);
        rka = view.findViewById(R.id.rka);
        fakultas = view.findViewById(R.id.fakultas);
        tahunan = view.findViewById(R.id.tahunan);
        rencana = view.findViewById(R.id.rencana);
        rekap = view.findViewById(R.id.rekap);

        belanja.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new AdminAnalysisFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        pajak.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new AdminAnalysisFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        rka.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new AdminAnalysisFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        fakultas.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new AdminAnalysisFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        tahunan.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new AdminAnalysisFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        rencana.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new AdminAnalysisFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        rekap.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new AdminAnalysisFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
}