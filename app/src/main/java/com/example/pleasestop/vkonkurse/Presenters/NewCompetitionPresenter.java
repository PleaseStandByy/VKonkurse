package com.example.pleasestop.vkonkurse.presenters;

import android.annotation.SuppressLint;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.Repository;
import com.example.pleasestop.vkonkurse.ViewsMvp.NewCompetitionView;
import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;


import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
        getViewState().showError("Список конкурсов пока пуст");
//        loadNewCompetitions(0);
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

                        getWalls();
                        getViewState().loading(false);
                        getViewState().addList(competitionCompetitionsList.getItems());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getViewState().loading(false);
                        getViewState().showError(throwable.getMessage());
                    }
                });
    }

    private void getWalls(){
        Observable.fromIterable(competitionList)
                .flatMap(new Function<Competition, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Competition competition) throws Exception {
                        return repository.getWall(competition);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("getWallJopa", "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.i("getWallJopa", "onNext: ");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("getWallJopa", "onError: ");

                    }

                    @Override
                    public void onComplete() {
                        Log.i("getWallJopa", "onComplete: ");

                    }
                });


//        for(Competition competition : competitionList){
//            repository.loadTextFromGroup(competition);
//        }
    }
}
