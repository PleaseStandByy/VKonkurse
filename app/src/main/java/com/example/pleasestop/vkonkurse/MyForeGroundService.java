package com.example.pleasestop.vkonkurse;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;

import com.example.pleasestop.vkonkurse.Utils.Constans;
import com.example.pleasestop.vkonkurse.Utils.VkUtil;
import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;
import com.example.pleasestop.vkonkurse.model.IsMemberResult;
import com.example.pleasestop.vkonkurse.model.VkRequestTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.pleasestop.vkonkurse.MyApp.CHANNEL_ID;
import static com.example.pleasestop.vkonkurse.Repository.TAG;
import static com.example.pleasestop.vkonkurse.Utils.Constans.ERROR_MESSAGE;
import static com.example.pleasestop.vkonkurse.Utils.Constans.INFO_MESSAGE;

public class MyForeGroundService extends Service {

    String TAG_VK_TASK = "tagvk";
    @Inject
    Repository repository;
    Notification notification;
    private Integer vkDelay;
    private Integer contestRequestDelay;
    private Integer contestListDelay;
    private Integer checkResolutionDelay;
    private Integer lastCompId;

    private Integer rejectDelay = 0;
    Disposable disposable;
    Disposable disposableWait;
    Disposable disposableResolution;
    private Observer observerVkReqsponseTask;

    @Inject
    SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.getNetComponent().inject(this);
        if(!preferences.getBoolean(Constans.IS_AUTO, false)){
            Intent serviceIntent = new Intent(getApplicationContext(), MyForeGroundService.class);
            stopService(serviceIntent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        String input = intent.getStringExtra("inputExtra");
//        observerVkReqsponseTask = new Observer<VkRequestTask>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(VkRequestTask vkRequestTask) {
//                Log.i("groupcheck", "observerVkReqsponseTask :  next");
//                vkRequestTask.run(repository);
//                repository.vkRequestTaskIds.remove(vkRequestTask.getIdTask());
//                runVkRequests(vkDelay);
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.i("groupcheck", "observerVkReqsponseTask :  error");
//                runVkRequests(vkDelay);
//            }
//
//            @Override
//            public void onComplete() {
//                Log.i("groupcheck", "observerVkReqsponseTask :  complete");
//            }
//        };
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(this.getString(R.string.auto_load))
                .setContentText(this.getString(R.string.content_text_notification))
                .setSmallIcon(R.drawable.ic_main_icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        loadCompitation(0);
        runVkRequests(2);

        return START_STICKY;
    }

    void loadCompitation(Integer delay){
        Log.i(TAG, "loadCompitation: ");
        checkResolutionDelay = 0;
        showError("loadCompitation");
        disposable = repository.loadAllCompetition(repository.userID)
                .delay(delay,TimeUnit.SECONDS)
                .subscribe(new Consumer<CompetitionsList<Competition>>() {
                    @Override
                    public void accept(CompetitionsList<Competition> competitionCompetitionsList) throws Exception {
                        Log.i("groupcheck", competitionCompetitionsList.getCount() + "");
                        contestListDelay = competitionCompetitionsList.getContestListDelay();
                        contestRequestDelay = competitionCompetitionsList.getContestRequestDelay();
                        vkDelay = competitionCompetitionsList.getVkDelay();
                        lastCompId = competitionCompetitionsList.getItems().get(competitionCompetitionsList.getItems().size()-1).getId();
                        Log.i("groupcheck", lastCompId +  " lastcompid ");
                        for(Competition competition : competitionCompetitionsList.getItems()){
                            chechUserIsMember(competition, 0);
                        }
                        loadCompitationWithId(contestRequestDelay);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(throwable instanceof SocketTimeoutException){
                            loadCompitation(10);
                        }
                        showError(throwable.getMessage());
                    }
                });

    }

    void loadCompitationWithId(Integer delay){
        Log.i("groupcheck", "competitionCompetitionsList");
        checkResolutionDelay = 0;
        repository.loadWithId(repository.userID, lastCompId)
                .delay(delay,TimeUnit.SECONDS)
                .subscribe(new Consumer<CompetitionsList<Competition>>() {
                    @Override
                    public void accept(final CompetitionsList<Competition> competitionCompetitionsList) throws Exception {
                        Log.i("groupcheck", competitionCompetitionsList.getCount() + "");
                        repository.contestListDelay = competitionCompetitionsList.getContestListDelay();
                        repository.contestRequestDelay = competitionCompetitionsList.getContestRequestDelay();
                        repository.vkDelay = competitionCompetitionsList.getVkDelay();
                        contestRequestDelay = competitionCompetitionsList.getContestRequestDelay();
                        lastCompId = competitionCompetitionsList.getItems().get(competitionCompetitionsList.getItems().size()-1).getId();
                        Log.i("groupcheck", lastCompId +  "");
                        for(Competition competition : competitionCompetitionsList.getItems()){
                            Log.i("groupcheck",   "кароч это чек резолюшн" + competition.getId());
                            chechUserIsMember(competition, 0);
                        }
                        loadCompitationWithId(contestRequestDelay);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        loadCompitationWithId(contestRequestDelay);
                    }
                });

    }

    void checkResolution(final Competition competition, final boolean isMember, Integer delay){
        showError("checkResolution");
        if(!isMember) {
            Log.i("groupcheck", "врыв в группу " + competition.getId());
            joinToGroup(competition.getPairIdAndPostid().first);
        }
        Log.i("groupcheck", "внутри чекрезолюшена = " + competition.getId());
        disposableResolution = repository.loadResolution(competition.getId(), repository.userID, isMember)
                .delay(delay,TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<IsMemberResult>() {
                    @Override
                    public void accept(IsMemberResult isMemberResult) throws Exception {
                        Log.i("groupcheck", "accept: " + isMemberResult.getParticipation() + " " + competition.getId());
                        switch (isMemberResult.getParticipation()){
                            case "ALLOWED" :
                                setLike("post",
                                        competition.getPairIdAndPostid().first,
                                        competition.getPairIdAndPostid().second);
                                joinToSponsorGroup(competition);
                                break;
                            case "REJECTED":
                                checkResolution(competition, isMember, contestListDelay * 1000);
                                break;
                            case "REJECTED_FOREVER":
                                showError("REJECTED_FOREVER");
                                //удаяем конкурс из списка
                                break;
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        showMessage(throwable.getMessage());
                        Log.i(TAG, "acceptCheckResolution: " + throwable.toString());
                        checkResolution(competition, isMember, contestListDelay * 1000);
                    }
                });
    }
    void chechUserIsMember(final Competition competition, Integer delay){
        Log.i("groupcheck", competition.getId() + "          chechUserIsMember начало функции");
        checkResolutionDelay += 1000;
        Pair<String, String> pairIdAndPostid = VkUtil.getGroupAndPostIds(competition.getLink());
        competition.setPairIdAndPostid(pairIdAndPostid);
        repository.isMember(pairIdAndPostid.first)
                .delay(delay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if(integer == -100){
                            chechUserIsMember(competition, 1000);
                        }
                        if(integer == 1){
                            checkResolution(competition, true, checkResolutionDelay );
                        } else {
                            checkResolution(competition, false, checkResolutionDelay );
                        }
                    }
                });
//        VKRequest request =
//                new VKRequest("groups.isMember", VKParameters.from("access_token", repository.token, VKApiConst.GROUP_ID, pairIdAndPostid.first,
//                        VKApiConst.USER_ID, repository.userID ,
//                        VKApiConst.EXTENDED, 1));
//        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//                super.onComplete(response);
//                Log.i("groupcheck", competition.getId() + "          " + response.responseString );
//                try {
//                    JSONObject json = response.json.getJSONObject("response");
//                    JsonParser jsonParser = new JsonParser();
//                    JsonObject jsonObject = (JsonObject)jsonParser.parse(json.toString());
//                    Integer member = jsonObject.get("member").getAsInt();
//                    if(member == 1){
//                        checkResolution(competition, true, checkResolutionDelay );
//                    } else {
//                        checkResolution(competition, false, checkResolutionDelay );
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
//                super.attemptFailed(request, attemptNumber, totalAttempts);
//                Log.i("groupcheck", "atemptFailed");
//                chechUserIsMember(competition);
//            }
//
//            @Override
//            public void onError(VKError error) {
//                super.onError(error);
//                Log.i("groupcheck", "atemptFailed");
//                chechUserIsMember(competition);
//                showError(error.errorMessage);
//            }
//
//        });
    }

    public void joinToGroup(final String groupId) {
        VkRequestTask task = new VkRequestTask();
        task.createJoinToGroup(groupId);
        checkTask(task);
    }

    public void joinToSponsorGroup(Competition competition){
        VkRequestTask task = new VkRequestTask();
        task.createJoinToSponsorGroup(competition);
        checkTask(task);
    }
    public void setLike(String type, String owner_id, String item_id ) {
        VkRequestTask task = new VkRequestTask();
        task.createSetLike(type,owner_id,item_id);
        checkTask(task);
    }

    void checkTask(VkRequestTask task){
        if(!repository.vkRequestTaskIds.contains(task.getIdTask())){
            repository.vkRequestTasks.add(task);
            repository.vkRequestTaskIds.add(task.getIdTask());
        }
    }

    private void runVkRequests(Integer delay){
        Log.i("groupcheck", "runVkRequests: " + delay);
        if(delay == null)
            delay = 2;
        Log.i(TAG_VK_TASK, "runVkRequests: " + delay);
        if(repository.vkRequestTasks.peek() != null) {
            Log.i(TAG_VK_TASK, "peek not null");
            io.reactivex.Observable.just(repository.vkRequestTasks.poll())
                    .delay(delay, TimeUnit.SECONDS)
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Observer<VkRequestTask>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(VkRequestTask vkRequestTask) {
                            Log.i("groupcheck", "observerVkReqsponseTask :  next");
                            vkRequestTask.run(repository);
                            repository.vkRequestTaskIds.remove(vkRequestTask.getIdTask());
                            runVkRequests(vkDelay);

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("groupcheck", "observerVkReqsponseTask :  error");
                            runVkRequests(vkDelay);
                        }

                        @Override
                        public void onComplete() {
                            Log.i("groupcheck", "observerVkReqsponseTask :  complete");
                        }
                    });
        } else {
            Log.i(TAG_VK_TASK, "peek is null");
//            disposableWait =
                    io.reactivex.Observable.just(delay)
                    .delay(delay, TimeUnit.SECONDS)
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            runVkRequests(integer);
                        }
                    });
        }
    }

    private Notification getMyActivityNotification(String text){
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), 0);

        return new Notification.Builder(this)
                .setContentTitle("Автозагрузка")
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_ab_app)
                .setContentIntent(contentIntent).getNotification();
    }

    public void showError(String error) {
//        Notification notification = getMyActivityNotification(error);
//
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(1, notification);
    }
    @Override
    public void onDestroy() {
        if(disposable != null)
            disposable.dispose();
        if(disposableWait != null)
            disposableWait.dispose();
        if(disposableResolution != null)
            disposableResolution.dispose();
        repository.vkRequestTasks.clear();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

