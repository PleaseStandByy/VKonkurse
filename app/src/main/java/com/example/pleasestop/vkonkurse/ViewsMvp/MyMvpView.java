package com.example.pleasestop.vkonkurse.ViewsMvp;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.pleasestop.vkonkurse.model.Competition;

import java.util.List;

public interface MyMvpView extends MvpView {

    @StateStrategyType(SkipStrategy.class)
    void showMessage(String error, String type);

    void loading(boolean visible);

    void updateList();

    void addList(List<Competition> list);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void clearData();
}
