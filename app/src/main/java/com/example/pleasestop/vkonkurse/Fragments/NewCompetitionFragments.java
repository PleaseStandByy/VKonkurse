package com.example.pleasestop.vkonkurse.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.pleasestop.vkonkurse.Adapters.MyAdapter;
import com.example.pleasestop.vkonkurse.MyApp;
import com.example.pleasestop.vkonkurse.model.Competition;
import com.example.pleasestop.vkonkurse.presenters.NewCompetitionPresenter;
import com.example.pleasestop.vkonkurse.R;
import com.example.pleasestop.vkonkurse.Repository;
import com.example.pleasestop.vkonkurse.ViewsMvp.NewCompetitionView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Все новые конкурсы будут появляться здесь
 */
public class NewCompetitionFragments extends MvpAppCompatFragment implements NewCompetitionView, SwipeRefreshLayout.OnRefreshListener {
    MyAdapter<Competition, NewCompetitionPresenter> adapter;

    @Inject
    Repository repository;

    @Inject
    SharedPreferences preferences;

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "NewCompetitionPresenter")
    NewCompetitionPresenter presenter;

    @ProvidePresenter(type = PresenterType.GLOBAL, tag = "NewCompetitionPresenter")
    NewCompetitionPresenter getPresenter(){
        return new NewCompetitionPresenter();
    }

    @BindView(R.id.error_message)
    TextView errorMessege;
    @BindView(R.id.loading_progress)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    RecyclerView list;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_compitations_fragment, container, false);
        MyApp.getNetComponent().inject(this);
        ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        adapter = new MyAdapter<>(R.layout.item_new_compitation, presenter);
        list.setAdapter(adapter);
        swipe.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void addList(List<Competition> list) {
        adapter.addAll(list);
    }

    @Override
    public void showError(String error) {
        errorMessege.setVisibility(View.VISIBLE);
        errorMessege.setText(error);
    }

    @Override
    public void loading(boolean visible) {
        errorMessege.setVisibility(View.GONE);
        if(visible){
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            swipe.setRefreshing(false);
        }
    }

    @Override
    public void updateList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        presenter.clearData();
        presenter.loadNewCompetitions(0);
    }

    @Override
    public void finishCompitition(View v, Competition competition) {
        final LinearLayout layout = ((LinearLayout)v);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.findViewById(R.id.progress_mini).setVisibility(View.GONE);
                ((Button)layout.findViewById(R.id.buttonAutorization)).setText("типа Отменитб");
            }
        });
        adapter.remove(competition);
        adapter.notifyDataSetChanged();
    }
}
