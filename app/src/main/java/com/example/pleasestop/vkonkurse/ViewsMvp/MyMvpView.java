package com.example.pleasestop.vkonkurse.ViewsMvp;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.pleasestop.vkonkurse.model.Competition;

import java.util.List;

public interface MyMvpView extends MvpView {

    void showError(String error);

    void loading(boolean visible);

    void updateList();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void addList(List<Competition> list);
}
