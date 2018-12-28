package com.example.pleasestop.vkonkurse.presenters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.R;
import com.example.pleasestop.vkonkurse.Repository;
import com.example.pleasestop.vkonkurse.Utils.Constans;
import com.example.pleasestop.vkonkurse.Utils.VkUtil;
import com.example.pleasestop.vkonkurse.ViewsMvp.NewCompetitionView;
import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;
import com.example.pleasestop.vkonkurse.model.IsMemberResult;
import com.example.pleasestop.vkonkurse.model.VkRequestTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.pleasestop.vkonkurse.Repository.TAG;
import static com.example.pleasestop.vkonkurse.Utils.Constans.ERROR_MESSAGE;
import static com.example.pleasestop.vkonkurse.Utils.Constans.INFO_MESSAGE;

@InjectViewState
public class NewCompetitionPresenter extends MvpPresenter<NewCompetitionView> {

    public List<Competition> competitionList;
    @Inject
    Repository repository;

    public NewCompetitionPresenter() {
        MyApp.getNetComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
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
                        for(Competition competition : competitionList)
                            competition.isLoading = false;
                        if(competitionList.isEmpty()){
                            getViewState().loading(false);
                            getViewState().showMessage(MyApp.getContext().getResources().getString(R.string.list_is_empty), INFO_MESSAGE);
                        } else
                            getWalls();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getViewState().loading(false);
                        getViewState().showMessage(throwable.getMessage(), ERROR_MESSAGE);
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
                        } else {
                            setSpannableText(competition);
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

    public Boolean competitionIsloading(){
        try {
            if(competitionList == null)
                return false;
            for(Competition competition : competitionList){
                if(competition.isLoading)
                    return true;
            }
        } catch (Exception e){
            return true;
        }
        return false;
    }
    public void clearData(){
        if(competitionList != null)
            competitionList.clear();
    }

    public void runCompetition(View v, Competition competition){
        Button button = (Button) v;
        LinearLayout layout = (LinearLayout) button.getParent();

        final Boolean[] isMember = {true};

        if(button.getText().equals(MyApp.getContext().getResources().getString(R.string.run_compitation))) {
            competition.isLoading = true;
            Observable.fromIterable(competition.getListSponsorGroupId())
                    .flatMap(new Function<String, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(String s) throws Exception {
                           return repository.isMember(s);
                        }
                    }).subscribe(new Observer<Object>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Object o) {
                            if((Integer)o == 0)
                                isMember[0] = false;
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            if(!isMember[0]) {
                                layout.findViewById(R.id.progress_mini).setVisibility(View.VISIBLE);
                                ((Button)layout.findViewById(R.id.buttonAutorization)).setText("Выполняются действия");
                                competition.isClose = false;
                                checkLoad(competition, layout);
                                competition.runVkRequests(repository);
                                chechUserIsMember(competition);
                            } else {
                                getViewState().showMessage("Вы уже учавствуете в этом конкурсе", INFO_MESSAGE);
                                getViewState().removeItem(competition);
                            }
                        }

            });
        } else
            if(button.getText().equals(MyApp.getContext().getResources().getString(R.string.cancel_compitation))) {
                competition.isLoading = true;
                layout.findViewById(R.id.progress_mini).setVisibility(View.VISIBLE);
                ((Button)layout.findViewById(R.id.buttonAutorization)).setText("Выполняются действия");
                checkLoadForCancel(competition, layout);
                try {
                    repository.canselCompetition(competition);
                } catch (Exception e) {
                    getViewState().showMessage("Конкурс больше не активен.", INFO_MESSAGE);
                    getViewState().removeItem(competition);
                }
            }

    }


    public void checkResolution(final Competition competition, final boolean isMember, Integer delay){
        if(!isMember) {
            joinToGroup(competition);
        }
        repository.loadResolution(competition.getId(), repository.userID, isMember)
                .delay(delay,TimeUnit.SECONDS)
                .subscribe(new Consumer<IsMemberResult>() {
                    @Override
                    public void accept(IsMemberResult isMemberResult) throws Exception {
                        Log.i(TAG, "accept: " + isMemberResult.getParticipation());
                        if(!competition.isClose) {
                            switch (isMemberResult.getParticipation()) {
                                case "ALLOWED":
                                    competition.runVkRequests(repository);
                                    setLike(competition);
                                    joinToSponsorGroup(competition);
                                    finishCompitition(competition);
                                    break;
                                case "REJECTED":
                                    checkResolution(competition, isMember, 2);
                                    break;
                                case "REJECTED_FOREVER":
                                    //удаяем конкурс из списка
                                    break;
                            }
                        } else {

                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        checkResolution(competition, isMember, 2);
                    }
                });
    }

    void chechUserIsMember(final Competition competition){
        Pair<String, String> pairIdAndPostid = VkUtil.getGroupAndPostIds(competition.getLink());
        competition.setPairIdAndPostid(pairIdAndPostid);
        VKRequest request =
                new VKRequest("groups.isMember", VKParameters.from("access_token", repository.token, VKApiConst.GROUP_ID, pairIdAndPostid.first,
                        VKApiConst.USER_ID, repository.userID ,
                        VKApiConst.EXTENDED, 1));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try {
                    JSONObject json = response.json.getJSONObject("response");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject)jsonParser.parse(json.toString());
                    Integer member = jsonObject.get("member").getAsInt();
                    if(member == 1){
                        checkResolution(competition, true, 0 );
                    } else {
                        checkResolution(competition, false, 0 );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

        });
    }

    public void checkLoad(final Competition competition, final View v){
        Observable.just(competition)
                .delay(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Competition>() {
                    @Override
                    public void accept(Competition comp) throws Exception {
                        if(competition.isClose){
                            getViewState().finishCompitition(v,competition, true);
                        } else {
                            checkLoad(competition,v);
                        }
                    }
                });
    }

    public void checkLoadForCancel(final Competition competition, final View v ){
        Observable.just(competition)
                .delay(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Competition>() {
                   @Override
                   public void accept(Competition comp) throws Exception {
                       if (!competition.isClose) {
                           getViewState().finishCompitition(v, competition, false);
                       } else {
                           checkLoadForCancel(competition, v);
                       }
                }
        });
    }

    public void joinToGroup(Competition competition) {
        VkRequestTask task = new VkRequestTask();
        task.createJoinToGroupSdk(competition.getPairIdAndPostid().first);
        checkTask(task, competition);
    }

    public void joinToSponsorGroup(Competition competition){
        VkRequestTask task = new VkRequestTask();
        task.createJoinToSponsorGroupSdk(competition);
        checkTask(task, competition);
    }

    public void finishCompitition(Competition competition){
        VkRequestTask task = new VkRequestTask();
        task.createparticipationDone(competition);
        checkTask(task, competition);
    }

    public void setLike(Competition competition) {
        VkRequestTask task = new VkRequestTask();
        task.createSetLike("post",
                competition.getPairIdAndPostid().first,
                competition.getPairIdAndPostid().second);
        checkTask(task, competition);
    }

    public void removeLike(Competition competition) {
        VkRequestTask task = new VkRequestTask();
        task.createSetLike("post",
                competition.getPairIdAndPostid().first,
                competition.getPairIdAndPostid().second);
        checkTask(task, competition);
    }

    public void leaveFromGroup(Competition competition) {
        VkRequestTask task = new VkRequestTask();
        task.createJoinToGroupSdk(competition.getPairIdAndPostid().first);
        checkTask(task, competition);
    }

    void checkTask(VkRequestTask task, Competition competition){
        if(!competition.getVkRequestTaskIds().contains(task.getIdTask())){
            competition.getVkRequestTasks().add(task);
            competition.getVkRequestTaskIds().add(task.getIdTask());
        }
    }

    public void setSpannableText(Competition competition) {
        Integer count = competition.getText().split("СПОНСОРА")[0].length();
        SpannableString ss = new SpannableString(competition.getText());

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String url = Constans.VK_URL + "club" + competition.getListSponsorGroupId().get(0);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getViewState().openSponsorGroup(i);
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(0xFF03A0D1);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan1, count, count+8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        competition.setSpanText(ss);
    }
}
