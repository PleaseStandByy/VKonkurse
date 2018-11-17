package com.example.pleasestop.vkonkurse;

import android.util.Log;
import android.util.Pair;

import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;
import com.example.pleasestop.vkonkurse.model.IsMemberResult;
import com.example.pleasestop.vkonkurse.model.VkRequestTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class Repository {
    public static final String TAG ="jopa";
    @Inject
    ApiService apiService;

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


    public Observable<CompetitionsList<Competition>> loadAllCompetition(String vkUId, Integer delay) {
        return apiService.loadAllCompetition(vkUId)
                .subscribeOn(Schedulers.io())
                .delay(delay, TimeUnit.SECONDS)
                .observeOn(Schedulers.io());
    }

    public Observable<IsMemberResult> loadResolution(Integer id, String vkUid, boolean isMember) {
        return apiService.checkResolution(id, vkUid, isMember)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public Disposable joinToGroup(final String groupId){
        Log.i(TAG, "test run");
        return Single.fromCallable(new Callable<Integer>() {
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

    public void joinToSponsorGroup(Competition competition){
        final String[] infoPost = new String[1];
        String posts = "-" + competition.getPairIdAndPostid().first +
                "_" + competition.getPairIdAndPostid().second;
        VKRequest request = new VKRequest("wall.getById", VKParameters.from("access_token", token, "posts", posts));
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
                super.onError(error);
            }

        });
    }

    public void loadTextFromGroup(final Competition competition){
        Pair<String, String> pairIdAndPostid = VkUtil.getGroupAndPostIds(competition.getLink());
        String posts = "-" + pairIdAndPostid.first +
                "_" + pairIdAndPostid.second;
        VKRequest request = new VKRequest("wall.getById", VKParameters.from("access_token", token, "posts", posts));
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
                competition.setAction(text);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

        });
    }
}
