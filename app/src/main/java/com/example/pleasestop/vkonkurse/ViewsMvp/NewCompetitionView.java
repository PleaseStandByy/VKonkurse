package com.example.pleasestop.vkonkurse.ViewsMvp;


import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface NewCompetitionView extends MyMvpView {

    @StateStrategyType(SkipStrategy.class)
    void startService();

    @StateStrategyType(SkipStrategy.class)
    void stopService();

}
