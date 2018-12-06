package com.example.pleasestop.vkonkurse.ViewsMvp;


import android.view.View;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.pleasestop.vkonkurse.model.Competition;

public interface NewCompetitionView extends MyMvpView {

    @StateStrategyType(SkipStrategy.class)
    void finishCompitition(View v, Competition competition, boolean closing);

    @StateStrategyType(SkipStrategy.class)
    void removeItem(Competition competition);
}
