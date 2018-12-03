package com.example.pleasestop.vkonkurse;

import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.example.pleasestop.vkonkurse.Utils.Constans;
import com.example.pleasestop.vkonkurse.Utils.VkUtil;
import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;
import com.example.pleasestop.vkonkurse.model.IsMemberResult;
import com.example.pleasestop.vkonkurse.model.VkRequestTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class Repository {
    public static final String TAG ="jopa";
    public static final String TAG1="jopa1";
    public static final String TAG2="jopa2";
    @Inject
    ApiService apiService;
    @Inject
    SharedPreferences preferences;

    public String token;
    public String userID;
    public Integer vkDelay;
    public Integer contestRequestDelay;
    public Integer contestListDelay;

    ConcurrentLinkedQueue<VkRequestTask> vkRequestTasks;
    CopyOnWriteArraySet<String> vkRequestTaskIds;

    public Repository (){
        MyApp.getNetComponent().inject(this);
        vkRequestTasks = new ConcurrentLinkedQueue<>();
        vkRequestTaskIds = new CopyOnWriteArraySet<>();
    }

    public Observable<CompetitionsList<Competition>> loadAllCompetition(String vkUId) {
        return apiService.loadAllCompetition(vkUId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CompetitionsList<Competition>> loadNotActiveCompetitions(String vkUId) {
        return apiService.loadNotActiveCompetitions(vkUId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CompetitionsList<Competition>> loadAllCompetition(String vkUId, Integer delay) {
        return apiService.loadAllCompetition(vkUId)
                .subscribeOn(Schedulers.io())
                .delay(delay, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<IsMemberResult> loadResolution(Integer id, String vkUid, boolean isMember) {
        return apiService.checkResolution(id, vkUid, isMember)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public void participationDone(final Competition competition, String vkUid){
        apiService.participationDone(competition.getId().toString(), vkUid)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(JsonObject jsonObject) throws Exception {
                        competition.isClose = true;
                    }
                });
    }
    public Observable<JsonObject> getWall(final Competition competition) {
        Pair<String, String> pairIdAndPostid = VkUtil.getGroupAndPostIds(competition.getLink());
        String posts = "-" + pairIdAndPostid.first +
                "_" + pairIdAndPostid.second;
        String s1 = preferences.getString(Constans.TOKEN,"");
        String s2 = VKSdk.getApiVersion();
        return apiService.getWall(s1, s2, posts)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public Observable<Competition> getTextFromPost(final  Competition competition){
        return getWall(competition)
                .map(new Function<JsonObject, Competition>() {
            @Override
            public Competition apply(JsonObject jsonObject) throws Exception {
                if(jsonObject != null)
                    if(jsonObject.getAsJsonArray("response") != null)
                        if(jsonObject.getAsJsonArray("response").size() > 0) {
                            jsonObject = (JsonObject) jsonObject.getAsJsonArray("response").get(0);
                            competition.setText(jsonObject.get("text").getAsString());
                            competition.setImageLinks(new ArrayList<String>());
                            try {
                                JsonArray jsonArray = jsonObject.getAsJsonArray("attachments");
                                for(JsonElement json : jsonArray ){
                                    competition.getImageLinks().add(((JsonObject) json).get("photo").getAsJsonObject().get("photo_807").getAsString());
                                }
                            } catch (Exception e){

                            }
                        }
                return competition;
            }
        });

    }
    public void setLike(String ownerId, String itemId) {
        String str1 = preferences.getString(Constans.TOKEN,"");
        String str2 = VKSdk.getApiVersion();
        String str3 = "post";
        String str4 = "-" + ownerId;
        Integer str5 = Integer.valueOf(itemId);
        apiService.setLike(
                    str1,
                    str2,
                    str3,
                    str4,
                    str5
                )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(JsonObject jsonObject) throws Exception {
                        Log.i(TAG1, jsonObject.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG1, throwable.toString());
                    }
                });
    }

    public void joinToGroup(String groupId) {
        apiService.joinToGroup(
                    preferences.getString(Constans.TOKEN,""),
                    VKSdk.getApiVersion(),
                    groupId
                )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(JsonObject jsonObject) throws Exception {
                        Log.i(TAG1, jsonObject.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG1, throwable.toString());
                    }
                });
    }

    public void joinToGroupSdksss(String groupId) {
        apiService.joinToGroup(
                preferences.getString(Constans.TOKEN,""),
                VKSdk.getApiVersion(),
                groupId
        )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(JsonObject jsonObject) throws Exception {
                        Log.i(TAG1, jsonObject.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG1, throwable.toString());
                    }
                });
    }


    public void joinToGroupSdk(final String groupId){
        Log.i(TAG, "test run");
        Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Log.i(TAG, "test run end");
                Log.i(TAG, "Происходит подписка на группу " + groupId);
                VKRequest request = new VKRequest("groups.join", VKParameters.from("access_token", token ,"group_id", groupId));
                final Integer[] resp = new Integer[1];
                resp[0] = 0;
                request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        Log.i(TAG, "Подписка оформлена на группу " + groupId);
                    }

                    @Override
                    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                        super.attemptFailed(request, attemptNumber, totalAttempts);
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        Log.i(TAG, "onError: ");
                    }

                });
                return resp[0];
            }
            })
            .delay(vkDelay, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe();
    }

    public Disposable setLike(final String type, final String owner_id, final String item_id){
        return Single.fromCallable(new Callable() {
            @Override
            public Object call() throws Exception {
                Log.i(TAG, "Ставиться лайк " + owner_id  + " " + item_id);
                VKRequest request = new VKRequest("likes.add", VKParameters.from("access_token",
                        token,"type",
                        type, "owner_id", "-" + owner_id, "item_id",
                        item_id));
                request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        Log.i(TAG, "Лайк поставлен" + owner_id  + " " + item_id);
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

                Log.i("jopa", "opa");
                return 0;
            }
        })
                .delay(vkDelay, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe();

    }

    public void joinToSponsorGroup(Competition competition, final boolean isVkSdk){
        getWall(competition).subscribe(new Consumer<JsonObject>() {
            @Override
            public void accept(JsonObject jsonObject) throws Exception {
                JsonArray jsonArray = jsonObject.getAsJsonArray("response");
                jsonObject = (JsonObject) jsonArray.get(0);
                String text = jsonObject.get("text").getAsString();
                if(isVkSdk){
                    joinToGroupSdk(VkUtil.getSponsorId(text));
                } else {
                    joinToGroup(VkUtil.getSponsorId(text));
                }
                Log.i(TAG1, jsonObject.toString());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.i(TAG1, throwable.getMessage());
            }
        });
    }
}
