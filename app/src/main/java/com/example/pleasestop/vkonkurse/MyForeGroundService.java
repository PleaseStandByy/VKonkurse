package com.example.pleasestop.vkonkurse;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;

import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;
import com.example.pleasestop.vkonkurse.model.IsMemberResult;
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

import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static com.example.pleasestop.vkonkurse.MyApp.CHANNEL_ID;
import static com.example.pleasestop.vkonkurse.Repository.TAG;

public class MyForeGroundService extends Service {

    @Inject
    Repository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.getNetComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_ab_app)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        loadCompitation(0);

        return START_NOT_STICKY;
    }

    void loadCompitation(Integer delay){
        showError("loadCompitation");
        repository.loadAllCompetition(repository.userID)
                .delay(delay,TimeUnit.SECONDS)
                .subscribe(new Consumer<CompetitionsList<Competition>>() {
                    @Override
                    public void accept(CompetitionsList<Competition> competitionCompetitionsList) throws Exception {
                        for(Competition competition : competitionCompetitionsList.getItems()){
                            chechUserIsMember(competition);
                        }
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
    void checkResolution(final Competition competition, final boolean isMember, Integer delay){
        showError("checkResolution");
        if(!isMember) {
            joinToGroup(competition.getPairIdAndPostid().first);
        }
        repository.loadResolution(competition.getId(), repository.userID, isMember)
                .delay(delay,TimeUnit.SECONDS)
                .subscribe(new Consumer<IsMemberResult>() {
                    @Override
                    public void accept(IsMemberResult isMemberResult) throws Exception {
                        Log.i(TAG, "accept: " + isMemberResult.getParticipation());
                        switch (isMemberResult.getParticipation()){
                            case "ALLOWED" :
                                setLike("post",
                                        competition.getPairIdAndPostid().first,
                                        competition.getPairIdAndPostid().second);
                                joinToSponsorGroup(competition);
                                showError("ALLOWED");
                                break;
                            case "REJECTED":
                                showError("REJECTED");
                                checkResolution(competition, isMember, 10);
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
                        showError(throwable.getMessage());
                        Log.i(TAG, "acceptCheckResolution: " + throwable.toString());
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
                showError(error.errorMessage);
            }

        });
    }

    public void joinToGroup(final String groupId){
        Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                VKRequest request = new VKRequest("groups.join", VKParameters.from("access_token", repository.token ,"group_id", groupId));
                final Integer[] resp = new Integer[1];
                resp[0] = 0;
                request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            JSONObject json = response.json.getJSONObject("response");
                            JsonParser jsonParser = new JsonParser();
                            JsonObject jsonObject = (JsonObject)jsonParser.parse(json.toString());
                            resp[0] = jsonObject.get("likes").getAsInt();
                            Log.i("jopa", resp[0].toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VKError error) {
                        showError(error.errorMessage);
                        super.onError(error);
                    }

                });
                return resp[0];
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }

    public void joinToSponsorGroup(Competition competition){
        final String[] infoPost = new String[1];
        String posts = "-" + competition.getPairIdAndPostid().first +
                "_" + competition.getPairIdAndPostid().second;
        VKRequest request = new VKRequest("wall.getById", VKParameters.from("access_token", repository.token, "posts", posts));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(response.responseString);
                JsonArray jsonArray = jsonObject.getAsJsonArray("response");
                jsonObject = (JsonObject) jsonArray.get(0);
                String text = jsonObject.get("text").getAsString();
                joinToGroup(VkUtil.getSponsorId(text));
                Log.i("jopa", infoPost[0].toString());
            }

            @Override
            public void onError(VKError error) {
                showError(error.errorMessage);
                super.onError(error);
            }

        });
    }
    public  Integer setLike(String type, String owner_id, String item_id ) {
        final Integer[] likes = new Integer[1];
        VKRequest request = new VKRequest("likes.add", VKParameters.from("access_token",
                repository.token,"type",
                type, "owner_id", "-" + owner_id, "item_id",
                item_id));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject json = response.json.getJSONObject("response");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject)jsonParser.parse(json.toString());
                    likes[0] = jsonObject.get("likes").getAsInt();
                    Log.i("jopa", likes[0].toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                showError(error.errorMessage);
                super.onError(error);
            }

        });

        Log.i("jopa", "opa");
        return likes[0];
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
//        Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
        Log.i(TAG, "error");
        Notification notification = getMyActivityNotification(error);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

