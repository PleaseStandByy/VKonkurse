package com.example.pleasestop.vkonkurse.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.pleasestop.vkonkurse.ViewsMvp.CloseCompetitionView;

@InjectViewState
public class CloseCompetitionPresenter extends MvpPresenter<CloseCompetitionView> {

    CloseCompetitionPresenter(){

    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
    }

    public String getNamePresenter(){
        return this.getClass().toString();
    }
}
