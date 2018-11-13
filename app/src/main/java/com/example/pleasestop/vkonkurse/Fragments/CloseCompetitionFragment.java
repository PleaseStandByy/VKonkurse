package com.example.pleasestop.vkonkurse.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.R;

import javax.inject.Inject;
/**
 * Все завершенные конкурсы будут появляться здесь
 */
public class CloseCompetitionFragment extends MvpAppCompatFragment {


    @Inject
    SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_compitations_fragment, container, false);
        MyApp.getNetComponent().inject(this);

        return view;
    }
}
