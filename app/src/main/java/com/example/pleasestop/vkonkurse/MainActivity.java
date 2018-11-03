package com.example.pleasestop.vkonkurse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pleasestop.vkonkurse.Fragments.NewCompitationFragments;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.Nullable;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.buttonAutorization)
    Button buttonAutorization;

    /**
     * temp
     */
    String[] scopes = new String[] {VKScope.FRIENDS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResult(VKAccessToken res) {
                NewCompitationFragments fragment = new NewCompitationFragments();

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
                buttonAutorization.setVisibility(View.GONE);
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

    @Nullable
    @OnClick(R.id.buttonAutorization)
    public void autorization(){
        VKSdk.login(this, scopes);
    }
}
