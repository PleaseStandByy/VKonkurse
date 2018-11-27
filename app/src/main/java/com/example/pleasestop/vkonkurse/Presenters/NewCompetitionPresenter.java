package com.example.pleasestop.vkonkurse.presenters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bumptech.glide.Glide;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.R;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.pleasestop.vkonkurse.MyApp.getContext;

@InjectViewState
public class NewCompetitionPresenter extends MvpPresenter<NewCompetitionView> {

    List<Competition> competitionList;
    @Inject
    Repository repository;

    public NewCompetitionPresenter() {
        MyApp.getNetComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showError("Список конкурсов пока пуст");
        loadNewCompetitions(0);
    }


    @SuppressLint("CheckResult")
    public  void loadNewCompetitions(Integer delay) {
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
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getViewState().loading(false);
                        getViewState().showError(throwable.getMessage());
                    }
                });
    }

    private void getWalls() {
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
                        if (competition.getText() == null) {
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
        if(competitionList != null)
            competitionList.clear();
    }
    public void loadImage(View view, String link) {
        Glide.with(getContext())
                .load(link)
                .error(R.drawable.ic_close_white_24dp)
                .into((ImageView) view);
    }

}
