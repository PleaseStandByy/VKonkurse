package com.example.pleasestop.vkonkurse.presenters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.Repository;
import com.example.pleasestop.vkonkurse.Utils.Constans;
import com.example.pleasestop.vkonkurse.ViewsMvp.CloseCompetitionView;
import com.example.pleasestop.vkonkurse.ViewsMvp.NewCompetitionView;
import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class CloseCompetitionPresenter extends MvpPresenter<CloseCompetitionView> {

    CopyOnWriteArrayList<Competition> competitionList;
    @Inject
    Repository repository;

    public CloseCompetitionPresenter(){
        MyApp.getNetComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showError("Список конкурсов пока пуст");
        loadCompetitions(0);
    }


    @SuppressLint("CheckResult")
    public  void loadCompetitions(Integer delay){
        getViewState().loading(true);
        repository.loadNotActiveCompetitions(repository.userID)
                .subscribe(new Consumer<CompetitionsList<Competition>>() {
                    @Override
                    public void accept(final CompetitionsList<Competition> competitionCompetitionsList) throws Exception {
                        repository.contestListDelay = competitionCompetitionsList.getContestListDelay();
                        repository.contestRequestDelay = competitionCompetitionsList.getContestRequestDelay();
                        repository.vkDelay = competitionCompetitionsList.getVkDelay();
                        competitionList = new CopyOnWriteArrayList<>(competitionCompetitionsList.getItems());
                        getWalls();
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
                        return repository.getTextFromPost(competition);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("getWallJopa", "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.i("getWallJopa", "onNext: ");
                        Competition competition = (Competition) o;
                        if(competition.getText() == null) {
                            competitionList.remove(competition);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("getWallJopa", "onError: ");

                    }

                    @Override
                    public void onComplete() {
                        Log.i("getWallJopa", "onComplete: ");
                        getViewState().loading(false);
                        getViewState().addList(competitionList);
                    }
                });
    }

    public void clearData(){
        if(competitionList!=null)
            competitionList.clear();
        getViewState().updateList();
    }

    public void openWinner(String userId){
        String url = Constans.VK_URL + "id" + userId;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        getViewState().openWinner(i);
    }
}
