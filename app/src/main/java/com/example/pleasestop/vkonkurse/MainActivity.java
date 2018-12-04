package com.example.pleasestop.vkonkurse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pleasestop.vkonkurse.Fragments.CloseCompetitionFragment;
import com.example.pleasestop.vkonkurse.Fragments.NewCompetitionFragments;
import com.example.pleasestop.vkonkurse.Fragments.SettingsFragment;
import com.example.pleasestop.vkonkurse.Utils.Constans;
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
    View buttonAutorization;
    @BindView(R.id.main_info_layout)
    LinearLayout layoutStartInfo;
    public static final String TAG ="Tag";

    @Inject
    Repository repository;
    @Inject
    SharedPreferences preferences;

    @BindView(R.id.navigation)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar appBar;
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

        tabLayout.addTab(tabLayout.newTab().setText("Конкурсы"));
        tabLayout.addTab(tabLayout.newTab().setText("Победители"));
        tabLayout.addTab(tabLayout.newTab().setText("Настройки"));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent));
//        setCustomFont();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Fragment fragment = new NewCompetitionFragments();

                switch (tab.getPosition()){
                    case 0 : fragment = new NewCompetitionFragments(); break;
                    case 1 : fragment = new CloseCompetitionFragment(); break;
                    case 2 : fragment = new SettingsFragment(); break;
                }

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
        appBar.setVisibility(View.VISIBLE);
        repository.userID = preferences.getString(Constans.USER_ID, "");
        repository.token = preferences.getString(Constans.TOKEN, "");
        NewCompetitionFragments fragment = new NewCompetitionFragments();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        layoutStartInfo.setVisibility(View.GONE);
//        buttonAutorization.setVisibility(View.GONE);
    }

    public void setCustomFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "@font/acomic.ttf"));
                }
            }
        }
    }
}
