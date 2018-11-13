package com.example.pleasestop.vkonkurse.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;
import com.example.pleasestop.vkonkurse.model.IsMemberResult;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.MyForeGroundService;
import com.example.pleasestop.vkonkurse.presenters.NewCompetitionPresenter;
import com.example.pleasestop.vkonkurse.R;
import com.example.pleasestop.vkonkurse.Repository;
import com.example.pleasestop.vkonkurse.ViewsMvp.NewCompetitionView;
import com.example.pleasestop.vkonkurse.VkUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static com.example.pleasestop.vkonkurse.Repository.TAG;

/**
 * Все новые конкурсы будут появляться здесь
 */
public class NewCompetitionFragments extends MvpAppCompatFragment implements NewCompetitionView {

    @Inject
    Repository repository;

    @Inject
    SharedPreferences preferences;

    @InjectPresenter
    NewCompetitionPresenter presenter;

    @ProvidePresenter
    NewCompetitionPresenter getPresenter(){
        return new NewCompetitionPresenter();
    }

    @BindView(R.id.error_message)
    TextView errorMessege;


    @BindView(R.id.switchService)
    Switch swichService;

    @OnClick(R.id.switchService)
    public void click(){
        if(!swichService.getSplitTrack()){
            swichService.setSplitTrack(true);
            startService();
        } else {
            swichService.setSplitTrack(false);
            stopService();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_compitations_fragment, container, false);
        MyApp.getNetComponent().inject(this);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void startService(){
        String input = "test";
        Intent serviceIntent = new Intent(getActivity(), MyForeGroundService.class);
        serviceIntent.putExtra("inputExtra", input);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }


    @Override
    public void stopService() {
        Intent serviceIntent = new Intent(getActivity(), MyForeGroundService.class);
        getActivity().stopService(serviceIntent);
    }

    @Override
    public void showError(String error) {
//        Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
        errorMessege.setText(error);
    }

    @Override
    public void loading(boolean visible) {

    }
}
