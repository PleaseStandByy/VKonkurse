package com.example.pleasestop.vkonkurse.presenters;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.Repository;
import com.example.pleasestop.vkonkurse.ViewsMvp.NewCompetitionView;
import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;


import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

@InjectViewState
public class NewCompetitionPresenter extends MvpPresenter<NewCompetitionView> {

    List<Competition> competitionList;
    @Inject
    Repository repository;

    public NewCompetitionPresenter(){
        MyApp.getNetComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        loadNewCompetitions(0);
    }


    @SuppressLint("CheckResult")
    private void loadNewCompetitions(Integer delay){
        getViewState().loading(true);
        repository.loadAllCompetition(repository.userID, delay)
                .subscribe(new Consumer<CompetitionsList<Competition>>() {
                    @Override
                    public void accept(final CompetitionsList<Competition> competitionCompetitionsList) throws Exception {
                        repository.contestListDelay = competitionCompetitionsList.getContestListDelay();
                        repository.contestRequestDelay = competitionCompetitionsList.getContestRequestDelay();
                        repository.vkDelay = competitionCompetitionsList.getVkDelay();
                        competitionList = competitionCompetitionsList.getItems();
                        for(Competition competition : competitionList){
                            repository.loadTextFromGroup(competition);
                        }
                        getViewState().loading(false);
                        getViewState().addList(competitionCompetitionsList.getItems());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getViewState().loading(false);
                        getViewState().showError(throwable.getMessage());
//                        if(throwable instanceof SocketTimeoutException){
//                            loadNewCompetitions(10);
//                        }
                    }
                });
    }
}
