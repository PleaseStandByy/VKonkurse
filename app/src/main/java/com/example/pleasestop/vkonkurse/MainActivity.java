package com.example.pleasestop.vkonkurse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pleasestop.vkonkurse.Fragments.NewCompetitionFragments;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.Nullable;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.buttonAutorization)
    Button buttonAutorization;
    public static final String TAG ="Tag";

    @Inject
    Repository repository;
    @Inject
    SharedPreferences preferences;

    @BindView(R.id.navigation)
    TabLayout tabLayout;
    /**
     * temp
     */
    String[] scopes = new String[] {VKScope.FRIENDS, VKScope.WALL, VKScope.GROUPS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MyApp.getNetComponent().inject(this);
        String token = preferences.getString(Constans.TOKEN, "");
        if(!token.equals("")){
            createFragment();
        }

        tabLayout.addTab(tabLayout.newTab().setText("Активные"));
        tabLayout.addTab(tabLayout.newTab().setText("Завершённые"));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Fragment fragment = new NewCompetitionFragments();

                /*if (tab.getPosition() == 0) {
                    fragment = new ProjectInitiativesFragment();
                } else  {
                    fragment = new StartProjectFragment();
                }*/

                FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, fragment);

                transaction.commit();

                tabLayout.getTabAt(tab.getPosition()).select();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResult(VKAccessToken res) {
                preferences.edit().putString(Constans.USER_ID,res.userId).commit();
                preferences.edit().putString(Constans.TOKEN,res.accessToken).commit();
                createFragment();
                Toast.makeText(getApplicationContext(), "norm", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @OnClick(R.id.buttonAutorization)
    public void autorization(){
        VKSdk.login(this, scopes);
    }


    private void createFragment(){
        tabLayout.setVisibility(View.VISIBLE);
        repository.userID = preferences.getString(Constans.USER_ID, "");
        repository.token = preferences.getString(Constans.TOKEN, "");
        NewCompetitionFragments fragment = new NewCompetitionFragments();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        buttonAutorization.setVisibility(View.GONE);
    }
}
