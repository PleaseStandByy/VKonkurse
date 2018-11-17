package com.example.pleasestop.vkonkurse.model;

import android.util.Log;

import com.example.pleasestop.vkonkurse.Repository;


public class VkRequestTask {

    private static final String TAG_SET_LIKE = "setLike";
    private static final String TAG_JOIN_TO_GROUP = "joinToGroup";
    private static final String TAG_JOIN_TO_SPONSOR_GROUP = "joinToSponsorGroup";


    private String idTask;

    private String tagVkRequest;

    private String typeVkRequest;

    private String owner_id;

    private String item_id;

    private String group_id;

    private Competition competition;

    public void createSetLike(String type, String owner_id, String item_id){
        destructObject();
        typeVkRequest = type;
        this.owner_id = owner_id;
        this.item_id = item_id;
        tagVkRequest = TAG_SET_LIKE;
        idTask = owner_id + item_id;
    }

    public void createJoinToGroup(String group_id){
        destructObject();
        this.group_id = group_id;
        tagVkRequest = TAG_JOIN_TO_GROUP;
        idTask = group_id;
    }

    public void createJoinToSponsorGroup(Competition competition){
        destructObject();
        this.competition = competition;
        tagVkRequest = TAG_JOIN_TO_SPONSOR_GROUP;
        idTask = competition.getLink();
    }

    private void destructObject(){
        tagVkRequest = null;
        typeVkRequest = null;
        owner_id = null;
        item_id = null;
        group_id = null;
    }

    public void run(Repository repository){
        try {
            switch (tagVkRequest){
                case TAG_JOIN_TO_GROUP:
                    repository.joinToGroup(group_id);
                    break;
                case TAG_SET_LIKE:
                    repository.setLike(typeVkRequest, owner_id, item_id);
                    break;
                case TAG_JOIN_TO_SPONSOR_GROUP:
                    repository.joinToSponsorGroup(competition);
                    break;
            }
        } catch (Exception e) {
            Log.i("jopa", e.getMessage());
        }
    }

    public String getIdTask(){
        return idTask;
    }
}
