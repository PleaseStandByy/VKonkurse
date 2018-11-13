package com.example.pleasestop.vkonkurse.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.Repository;
import com.example.pleasestop.vkonkurse.ViewsMvp.NewCompetitionView;
import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

@InjectViewState
public class NewCompetitionPresenter extends MvpPresenter<NewCompetitionView> {

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


    private void loadNewCompetitions(Integer delay){
        repository.loadAllCompetition(repository.userID)
                .delay(delay,TimeUnit.SECONDS)
                .subscribe(new Consumer<CompetitionsList<Competition>>() {
                    @Override
                    public void accept(CompetitionsList<Competition> competitionCompetitionsList) throws Exception {
                        for(Competition competition : competitionCompetitionsList.getItems()){

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(throwable instanceof SocketTimeoutException){
                            loadNewCompetitions(10);
                        }
                    }
                });
    }
}
