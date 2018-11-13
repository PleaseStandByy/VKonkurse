package com.example.pleasestop.vkonkurse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pleasestop.vkonkurse.Fragments.NewCompetitionFragments;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;


import org.json.JSONException;
import org.json.JSONObject;

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
//        buttonAutorization.setVisibility(View.GONE);
//        setLike("post","45135634","7030");
        VKSdk.login(this, scopes);
    }

    public  Integer setLike(String type, String owner_id, String item_id ) {
        final Integer[] likes = new Integer[1];
        VKRequest request = new VKRequest("likes.add", VKParameters.from("access_token", "742073ecb45f5553365432538e2c8e9889c060215a2523483d411be3752a93a00e2f0ddd734916b42da50",
                "type", type, "owner_id", "-" + owner_id, "item_id", item_id));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject json = response.json.getJSONObject("response");
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject)jsonParser.parse(json.toString());
                    likes[0] = jsonObject.get("likes").getAsInt();
                    Log.i("jopa", likes[0].toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
//                showError(error.errorMessage);
                super.onError(error);
            }

        });

        Log.i("jopa", "opa");
        return likes[0];
    }

    private void createFragment(){
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
