package com.example.pleasestop.vkonkurse.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.example.pleasestop.vkonkurse.Utils.Constans;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.MyForeGroundService;
import com.example.pleasestop.vkonkurse.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class SettingsFragment extends MvpAppCompatFragment {

    @Inject
    SharedPreferences preferences;

    @BindView(R.id.switchService)
    Switch swichService;

    @OnCheckedChanged(R.id.switchService)
    public void check(){
        if(!swichService.isChecked()){
            preferences.edit().putBoolean(Constans.IS_AUTO, false).commit();
            stopService();
        } else {
            startService();
            preferences.edit().putBoolean(Constans.IS_AUTO, true).commit();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_settgins_fragment, container, false);
        MyApp.getNetComponent().inject(this);
        Switch switchService = view.findViewById(R.id.switchService);
        switchService.setChecked(preferences.getBoolean(Constans.IS_AUTO, false));
        ButterKnife.bind(this, view);
        return view;
    }

    public void startService(){
        stopService();
        String input = "test";
        Intent serviceIntent = new Intent(getActivity(), MyForeGroundService.class);
        serviceIntent.putExtra("inputExtra", input);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }


    public void stopService() {
        Intent serviceIntent = new Intent(getActivity(), MyForeGroundService.class);
        getActivity().stopService(serviceIntent);
    }


}
