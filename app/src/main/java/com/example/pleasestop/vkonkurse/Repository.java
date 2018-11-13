package com.example.pleasestop.vkonkurse;

import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.model.CompetitionsList;
import com.example.pleasestop.vkonkurse.model.IsMemberResult;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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


    public Repository (){
        MyApp.getNetComponent().inject(this);
    }

    public Observable<CompetitionsList<Competition>> loadAllCompetition(String vkUId) {
        return apiService.loadAllCompetition(vkUId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<IsMemberResult> loadResolution(Integer id, String vkUid, boolean isMember) {
        return apiService.checkResolution(id, vkUid, isMember)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

}
