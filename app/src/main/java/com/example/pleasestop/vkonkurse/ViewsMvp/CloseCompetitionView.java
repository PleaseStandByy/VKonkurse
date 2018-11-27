package com.example.pleasestop.vkonkurse.ViewsMvp;

import android.content.Intent;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface CloseCompetitionView extends MyMvpView {
    @StateStrategyType(SkipStrategy.class)
    void openWinner(Intent i);
}
