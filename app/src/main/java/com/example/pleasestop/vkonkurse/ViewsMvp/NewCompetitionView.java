package com.example.pleasestop.vkonkurse.ViewsMvp;


import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.pleasestop.vkonkurse.model.Competition;

import java.util.List;

public interface NewCompetitionView extends MyMvpView {

    @StateStrategyType(SkipStrategy.class)
    void startService();

    @StateStrategyType(SkipStrategy.class)
    void stopService();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void addList(List<Competition> list);
}
