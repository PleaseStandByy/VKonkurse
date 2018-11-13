package com.example.pleasestop.vkonkurse.ViewsMvp;

import com.arellomobile.mvp.MvpView;

public interface MyMvpView extends MvpView {

    void showError(String error);

    void loading(boolean visible);
}
