package com.example.pleasestop.vkonkurse.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.example.pleasestop.vkonkurse.R;

/**
 * Все новые конкурсы будут появляться здесь
 */
public class NewCompitationFragments extends MvpAppCompatFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_new_compitation_fragment, container, false);

        return view;
    }
}
