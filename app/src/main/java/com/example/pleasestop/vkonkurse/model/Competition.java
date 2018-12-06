package com.example.pleasestop.vkonkurse.model;

import android.util.Log;
import android.util.Pair;

import com.example.pleasestop.vkonkurse.Repository;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.pleasestop.vkonkurse.Repository.TAG;

public class Competition {

    public Competition(){}

    public Boolean isClose;

    public Boolean isLoading;

    Disposable disposable;

    ConcurrentLinkedQueue<VkRequestTask> vkRequestTasks;

    CopyOnWriteArraySet<String> vkRequestTaskIds;

    private List<String> listSponsorGroupId;

    private List<String> imageLinks;

    private String text;

    private String winner;

    private Integer participants;

    @SerializedName("max_participants")
    private Integer maxParticipants;

    private String link;

    private Integer id;

    private String expires;

    private String action;

    private Pair<String, String> pairIdAndPostid;

    public List<String> getListSponsorGroupId() {
        return listSponsorGroupId;
    }

    public void setListSponsorGroupId(List<String> listSponsorGroupId) {
        this.listSponsorGroupId = listSponsorGroupId;
    }

    public ConcurrentLinkedQueue<VkRequestTask> getVkRequestTasks() {
        return vkRequestTasks;
    }

    public void setVkRequestTasks(ConcurrentLinkedQueue<VkRequestTask> vkRequestTasks) {
        this.vkRequestTasks = vkRequestTasks;
    }

    public CopyOnWriteArraySet<String> getVkRequestTaskIds() {
        return vkRequestTaskIds;
    }

    public void setVkRequestTaskIds(CopyOnWriteArraySet<String> vkRequestTaskIds) {
        this.vkRequestTaskIds = vkRequestTaskIds;
    }

    public List<String> getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(List<String> imageLinks) {
        this.imageLinks = imageLinks;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Pair<String, String> getPairIdAndPostid() {
        return pairIdAndPostid;
    }

    public void setPairIdAndPostid(Pair<String, String> pairIdAndPostid) {
        this.pairIdAndPostid = pairIdAndPostid;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Integer getParticipants() {
        return participants;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public void runVkRequests(final Repository repository){
        if (getVkRequestTasks() == null)
            setVkRequestTasks(new ConcurrentLinkedQueue<VkRequestTask>());
        if (getVkRequestTaskIds() == null)
            setVkRequestTaskIds(new CopyOnWriteArraySet<String>());
        if(getVkRequestTasks().peek() != null) {
            io.reactivex.Observable.just(getVkRequestTasks().poll())
                    .delay(1, TimeUnit.SECONDS)
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Consumer<VkRequestTask>() {
                        @Override
                        public void accept(VkRequestTask task) throws Exception {
                            task.run(repository);
                            getVkRequestTaskIds().remove(task.getIdTask());
                            runVkRequests(repository);
                            disposable.dispose();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.i(TAG, "accept: ");
                        }
                    });
        } else {
            disposable = io.reactivex.Observable.just(1)
                    .delay(1, TimeUnit.SECONDS)
                    .observeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            runVkRequests(repository);
                        }
                    });
        }
    }
}
