package com.example.pleasestop.vkonkurse.Modules;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.pleasestop.vkonkurse.Repository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class RepositoryModule {

    @Provides
    @Singleton
    Repository providesRepository() {
        return new Repository();
    }

    @Provides
    @Singleton
    SharedPreferences providesSecretSharedPreferences(Application application) {
        return application.getApplicationContext()
                .getSharedPreferences("secret", Context.MODE_PRIVATE);

    }
}

