package com.example.pleasestop.vkonkurse.presenters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.R;
import com.example.pleasestop.vkonkurse.Repository;
import com.example.pleasestop.vkonkurse.Utils.Constans;
import com.example.pleasestop.vkonkurse.ViewsMvp.CloseCompetitionView;
import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.pleasestop.vkonkurse.Utils.Constans.ERROR_MESSAGE;
import static com.example.pleasestop.vkonkurse.Utils.Constans.INFO_MESSAGE;

@InjectViewState
public class CloseCompetitionPresenter extends MvpPresenter<CloseCompetitionView> {

    CopyOnWriteArrayList<Competition> competitionListWithError;
    CopyOnWriteArrayList<Competition> competitionList;
    @Inject
    Repository repository;

    public CloseCompetitionPresenter(){
        MyApp.getNetComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
//        getViewState().showMessage("Список конкурсов пока пуст", Constans.INFO_MESSAGE);
        competitionList = new CopyOnWriteArrayList<>();
        competitionListWithError = new CopyOnWriteArrayList<>();
        loadCompetitions(0);
    }


    @SuppressLint("CheckResult")
    public  void loadCompetitions(Integer delay){
        competitionListWithError.clear();
        getViewState().clearData();
        getViewState().loading(true);
        repository.loadNotActiveCompetitions(repository.userID)
                .subscribe(new Consumer<CompetitionsList<Competition>>() {
                    @Override
                    public void accept(final CompetitionsList<Competition> competitionCompetitionsList) throws Exception {
                        repository.contestListDelay = competitionCompetitionsList.getContestListDelay();
                        repository.contestRequestDelay = competitionCompetitionsList.getContestRequestDelay();
                        repository.vkDelay = competitionCompetitionsList.getVkDelay();
                        addWallToCompetitions(competitionCompetitionsList.getItems());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getViewState().loading(false);
                        getViewState().showMessage(throwable.getMessage(), Constans.ERROR_MESSAGE);
                    }
                });
    }
    private void addWallToCompetitions(List<Competition> list){

        competitionList.addAll(list);
        if(competitionList.isEmpty()){
            getViewState().loading(false);
            getViewState().showMessage(MyApp.getContext().getResources().getString(R.string.list_is_empty), INFO_MESSAGE);
        } else {
            getWalls(list);
        }
    }
    private void getWalls(List<Competition> list){
        final Integer[] delay = {0};
        for(Competition competition : list){
            if(competition.getId() == 34){
                list.remove(competition);
                break;
            }
        }
        try {
            Observable.fromIterable(list)
                    .flatMap(new Function<Competition, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(Competition competition) throws Exception {
                            delay[0] += Constans.delayGetWall;
                            return repository.getTextFromPost(competition, delay[0]);
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
                            try {
                                Competition competition = (Competition) o;

                                if (competition.getText() == null) {
                                    competitionList.remove(competition);
                                    competitionListWithError.add(competition);
                                    list.remove(competition);
                                } else {
                                    if (competition.getText().equals("")) {
                                        competitionList.remove(competition);
                                        list.remove(competition);
                                    }
                                }


                            } catch (Exception e) {
                                e.getLocalizedMessage();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("getWallJopa", "onError: ");
                            getViewState().loading(false);
                            getViewState().showMessage("Ошибка загрузки", ERROR_MESSAGE);

                        }

                        @Override
                        public void onComplete() {
                            Log.i("getWallJopa", "onComplete: ");
                            getViewState().loading(false);
                            if (!competitionListWithError.isEmpty()) {
                                addWallToCompetitions(new ArrayList<>(competitionListWithError));
                                for (Competition competition : competitionListWithError) {
                                    competitionList.remove(competition);
                                }
                                competitionListWithError.clear();
                            }
                            getViewState().addList(list);
                        }
                    });
        } catch (Exception e){
            e.getLocalizedMessage();
        }
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
