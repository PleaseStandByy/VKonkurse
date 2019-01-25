package com.example.pleasestop.vkonkurse;


import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;
import com.example.pleasestop.vkonkurse.model.IsMemberResult;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/comp/get_active")
    Observable<CompetitionsList<Competition>> loadAllCompetition(@Query("vk_uid") String vkUId);

    @GET("/comp/get_not_active")
    Observable<CompetitionsList<Competition>> loadNotActiveCompetitions(@Query("vk_uid") String vkUId);

    @GET("/comp/get_active")
    Observable<CompetitionsList<Competition>> loadCompetitionAfterId(@Query("vk_uid") String vkUId, @Query("id") Integer id);

    // example : /comp/ask_for_participation?id=1&vk_uid=1&subscribed=false
    @GET("/comp/ask_for_participation")
    Observable<IsMemberResult> checkResolution(@Query("id") Integer  id,
                                               @Query("vk_uid") String vkUid,
                                               @Query("subscribed") boolean isMember);


    @GET("/comp/participation_done")
    Observable<JsonObject> participationDone(@Query("id") String id, @Query("vk_uid") String vkId);


    /* запросы в вк */


    @GET("https://api.vk.com/method/wall.getById")
    Observable<JsonObject> getWall(@Query("access_token") String token,
                                   @Query("v") String sdkVersion,
                                   @Query("posts") String posts);

    @GET("https://api.vk.com/method/wall.getById")
    Observable<JsonObject> isMember(@Query("access_token") String token,
                                   @Query("group_id") String groupId,
                                   @Query("user_id") String userId,
                                    @Query("extended") Integer extended);

    @GET("https://api.vk.com/method/groups.join")
    Observable<JsonObject> joinToGroup(@Query("access_token") String token,
                                       @Query("v") String sdkVersion,
                                       @Query("group_id") String id);

    @GET("https://api.vk.com/method/utils.resolveScreenName")
    Observable<JsonObject> getIdFromScreenName(@Query("access_token") String token,
                                       @Query("v") String sdkVersion,
                                       @Query("screen_name") String id);

    @GET("https://api.vk.com/method/likes.add")
    Observable<JsonObject> setLike(@Query("access_token") String token,
                                     @Query("v") String sdkVersion,
                                     @Query("type") String type,
                                     @Query("owner_id") String ownerId,
                                     @Query("item_id") Integer itemId);

    @GET("https://api.vk.com/method/likes.delete")
    Observable<JsonObject> removeLike(@Query("access_token") String token,
                                   @Query("v") String sdkVersion,
                                   @Query("type") String type,
                                   @Query("owner_id") String ownerId,
                                   @Query("item_id") Integer itemId);

    @GET("https://api.vk.com/method/groups.isMember")
    Observable<JsonObject> isMember(@Query("access_token") String token,
                                      @Query("v") String sdkVersion,
                                      @Query("group_id") String groupId,
                                      @Query("user_id") String userId,
                                      @Query("extended") Integer extended);
}
