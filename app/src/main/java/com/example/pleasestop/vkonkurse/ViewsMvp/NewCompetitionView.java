package com.example.pleasestop.vkonkurse.ViewsMvp;


import android.view.View;

import com.example.pleasestop.vkonkurse.model.Competition;

public interface NewCompetitionView extends MyMvpView {

    void finishCompitition(View v, Competition competition);
}
