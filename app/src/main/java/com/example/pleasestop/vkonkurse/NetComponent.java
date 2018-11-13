package com.example.pleasestop.vkonkurse;

import com.example.pleasestop.vkonkurse.Fragments.CloseCompetitionFragment;
import com.example.pleasestop.vkonkurse.Fragments.NewCompetitionFragments;
import com.example.pleasestop.vkonkurse.Modules.AppModule;
import com.example.pleasestop.vkonkurse.Modules.NetModule;
import com.example.pleasestop.vkonkurse.Modules.RepositoryModule;
import com.example.pleasestop.vkonkurse.presenters.NewCompetitionPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        RepositoryModule.class,
        AppModule.class,
        NetModule.class})
public interface NetComponent {

    void inject(MainActivity mainActivity);

    void inject(NewCompetitionFragments newCompetitionFragments);

    void inject(CloseCompetitionFragment closeCompetitionFragment);

    void inject(Repository repository);

    void inject(MyForeGroundService myForeGroundService);

    void inject(NewCompetitionPresenter newCompetitionPresenter);
}
