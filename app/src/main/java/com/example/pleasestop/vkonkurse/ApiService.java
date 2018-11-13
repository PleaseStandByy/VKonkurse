package com.example.pleasestop.vkonkurse;


import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;
import com.example.pleasestop.vkonkurse.model.IsMemberResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/comp/get_active")
    Observable<CompetitionsList<Competition>> loadAllCompetition(@Query("vk_uid") String vkUId);

    @GET("/comp/get_active")
    Observable<CompetitionsList<Competition>> loadCompetitionAfterId(@Query("vk_uid") String vkUId, @Query("id") Integer id);

    // example : /comp/ask_for_participation?id=1&vk_uid=1&subscribed=false
    @GET("/comp/ask_for_participation")
    Observable<IsMemberResult> checkResolution(@Query("id") Integer  id, @Query("vk_uid") String vkUid, @Query("subscribed") boolean isMember);


}
