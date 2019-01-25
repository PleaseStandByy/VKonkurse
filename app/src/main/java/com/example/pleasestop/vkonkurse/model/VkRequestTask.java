package com.example.pleasestop.vkonkurse.model;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.pleasestop.vkonkurse.R;
import com.example.pleasestop.vkonkurse.Repository;


public class VkRequestTask {

    private static final String TAG_SET_LIKE = "setLike";
    private static final String TAG_JOIN_TO_GROUP = "joinToGroup";
    private static final String TAG_JOIN_TO_GROUP_SDK = "joinToGroupSdk";
    private static final String TAG_JOIN_TO_SPONSOR_GROUP = "joinToSponsorGroup";
    private static final String TAG_JOIN_TO_SPONSOR_GROUP_SDK = "joinToSponsorGroupSdk";
    private static final String TAG_PARTICIPATION_DONE = "participationDone";


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

    public void createJoinToGroupSdk(String group_id){
        destructObject();
        this.group_id = group_id;
        tagVkRequest = TAG_JOIN_TO_GROUP_SDK;
        idTask = group_id;
    }

    public void createJoinToSponsorGroup(Competition competition){
        destructObject();
        this.competition = competition;
        tagVkRequest = TAG_JOIN_TO_SPONSOR_GROUP;
        idTask = competition.getLink();
    }

    public void createJoinToSponsorGroupSdk(Competition competition){
        destructObject();
        this.competition = competition;
        tagVkRequest = TAG_JOIN_TO_SPONSOR_GROUP_SDK;
        idTask = competition.getLink();
    }

    public void createparticipationDone(Competition competition){
        destructObject();
        this.competition = competition;
        tagVkRequest = TAG_PARTICIPATION_DONE;
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
                    repository.joinToGroup(group_id, 0);
                    break;
                case TAG_SET_LIKE:
                    repository.setLike(owner_id, item_id);
                    break;
                case TAG_JOIN_TO_SPONSOR_GROUP:
                    repository.joinToSponsorGroup(competition, false);
                    break;
                case TAG_JOIN_TO_SPONSOR_GROUP_SDK:
                    repository.joinToSponsorGroup(competition, true);
                    break;
                case TAG_JOIN_TO_GROUP_SDK:
                    repository.joinToGroupSdk(group_id);
                    break;
                case TAG_PARTICIPATION_DONE:
                    repository.participationDone(competition, repository.userID);
//                    competition.layout.findViewById(R.id.progress_mini).setVisibility(View.GONE);
//                    ((TextView)competition.layout.findViewById(R.id.text_view_run)).setText("Учавствовать");
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
