package com.suudupa.coronavirustracker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.suudupa.coronavirustracker.R;
import com.suudupa.coronavirustracker.adapter.ArticleListAdapter;
import com.suudupa.coronavirustracker.adapter.RegionListAdapter;
import com.suudupa.coronavirustracker.api.ApiClient;
import com.suudupa.coronavirustracker.api.ApiInterface;
import com.suudupa.coronavirustracker.asyncTask.JsonResponse;
import com.suudupa.coronavirustracker.model.Article;
import com.suudupa.coronavirustracker.model.ArticleList;
import com.suudupa.coronavirustracker.model.Region;
import com.suudupa.coronavirustracker.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suudupa.coronavirustracker.utility.Resources.*;
import static com.suudupa.coronavirustracker.utility.Utils.getRandomApiKey;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    public static List<String> regions = new ArrayList<>();
    public JSONObject jsonResponse;
    private TextView casesTextView;
    private TextView newCasesTextView;
    private TextView deathsTextView;
    private TextView newDeathsTextView;
    private TextView recoveredTextView;
    private TextView timestampTextView;
    private TextView topHeadlinesTextView;
    private TextView noResultMsgTextView;
    private SwipeRefreshLayout swipeRefresh;
    private SharedPreferences sharedPreferences;
    private Spinner regionList;
    private RecyclerView recyclerView;
    private ArticleListAdapter articleListAdapter;
    private ArrayList<Region> regionItems = new ArrayList<>();
    private List<Article> articles = new ArrayList<>();
    private DrawerLayout drawer;
    private NavigationView navigationView;
    Snackbar noConnectionSnackBar;
    private RelativeLayout errorLayout;
    private RelativeLayout noArticleLayout;
    private Button btnRetry;
    private Button noArticleBtnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();
        setupDrawer();
        navigationView.getMenu().getItem(0).setChecked(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        executeJsonResponse(getFavoriteRegion());

        regionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = parent.getItemAtPosition(position).toString();
                try {
                    swipeRefresh.setRefreshing(true);
                    loadData(selectedRegion);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initializeView() {
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        casesTextView = findViewById(R.id.casesTextView);
        newCasesTextView = findViewById(R.id.newCasesTextView);
        deathsTextView = findViewById(R.id.deathsTextView);
        newDeathsTextView = findViewById(R.id.newDeathsTextView);
        recoveredTextView = findViewById(R.id.recoveredTextView);
        timestampTextView = findViewById(R.id.timestampTextView);
        topHeadlinesTextView = findViewById(R.id.topHeadlinesTextView);
        noResultMsgTextView = findViewById(R.id.noResultMessage);
        regionList = findViewById(R.id.regionListSpinner);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        errorLayout = findViewById(R.id.errorLayout);
        btnRetry = findViewById(R.id.btnRetry);
        noArticleLayout = findViewById(R.id.noResultLayout);
        noArticleBtnRetry = findViewById(R.id.noResultBtnRetry);
    }

    private void setupDrawer() {
        drawer = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsScreen) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private String getFavoriteRegion() {
        return sharedPreferences.getString(getString(R.string.favoriteRegionKey), GLOBAL);
    }

    private String getSelectedRegion() {
        String selectedRegion;
        try {
            selectedRegion = regionList.getSelectedItem().toString();
        } catch (NullPointerException e) {
            selectedRegion = getFavoriteRegion();
        }
        return selectedRegion;
    }

    private String getFavoriteLanguage() {
        return sharedPreferences.getString(getString(R.string.languageKey), "");
    }

    private void executeJsonResponse(String region) {
        swipeRefresh.setRefreshing(true);
        new JsonResponse().execute(DATA_URL, this, region);
    }

    public void buildRegionList() {
        JSONArray jsonNames = jsonResponse.names();
        regions.clear();
        regionItems.clear();
        for (int i = 0; i < jsonNames.length() - 1; i++) {
            try {
                String region = jsonNames.getString(i);
                String cases = Utils.formatNumber(jsonResponse.getJSONObject(region).getString(CASES));
                regions.add(region);
                regionItems.add(new Region(region, cases));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setupSpinner();
    }

    public void setupSpinner() {
        RegionListAdapter regionListAdapter = new RegionListAdapter(this, regionItems);
        regionList.setAdapter(regionListAdapter);
        regionListAdapter.notifyDataSetChanged();
    }

    public void loadData(String region) throws JSONException {
        errorLayout.setVisibility(View.GONE);
        retrieveData(region);
        loadArticles(region);
        swipeRefresh.setRefreshing(false);
    }

    private void retrieveData(String name) throws JSONException {
        JSONObject region = jsonResponse.getJSONObject(name);
        casesTextView.setText(Utils.formatNumber(region.getString(CASES)));
        newCasesTextView.setText(Utils.formatNumber(region.getString(NEW_CASES), "+"));
        deathsTextView.setText(Utils.formatNumber(region.getString(DEATHS)));
        newDeathsTextView.setText(Utils.formatNumber(region.getString(NEW_DEATHS), "+"));
        recoveredTextView.setText(Utils.formatNumber(region.getString(RECOVERED)));
        String lastUpdated = Utils.convertUnixTimestamp(jsonResponse.getString(TIMESTAMP_KEY));
        timestampTextView.setText(getResources().getString(R.string.timestampTitle, lastUpdated));
        regionList.setSelection(regions.indexOf(name));
    }

    private void loadArticles(final String region) {

        final Call<ArticleList> articleListCall;
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String q;

        if (region.equals(GLOBAL)) {
            q = Utils.urlEncode(KEYWORD_1 + OR_OP + KEYWORD_2);
        } else {
            q = Utils.urlEncode(KEYWORD_1 + AND_OP + region);
        }

        articleListCall = callApi(apiInterface, q);
        topHeadlinesTextView.setText(getResources().getString(R.string.headlinesTitle, region));
        articleListCall.enqueue(new Callback<ArticleList>() {

            @Override
            public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {

                if (response.isSuccessful() && response.body().getArticles() != null) {

                    noArticleLayout.setVisibility(View.GONE);
                    dismissNoConnectionMsg();
                    articles.clear();

                    articles = response.body().getArticles();
                    if (region.equals(GLOBAL) || region.equals(getFavoriteRegion())) {
                        try {
                            Utils.writeObject(getApplicationContext(), region.toLowerCase() + FILE_FORMAT, articles);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (articles.size() < MIN_ARTICLES) {
                        loadArticles(GLOBAL);
                    }

                    setArticleListAdapter();
                    swipeRefresh.setRefreshing(false);
                    articleSelectedListener();

                } else {
                    getArticlesOffline(region);
                }
            }

            @Override
            public void onFailure(Call<ArticleList> call, Throwable t) {
                getArticlesOffline(region);
            }
        });
    }

    @Override
    public void onRefresh() {
        executeJsonResponse(getSelectedRegion());
    }

    private void setArticleListAdapter() {
        articleListAdapter = new ArticleListAdapter(articles, MainActivity.this);
        recyclerView.setAdapter(articleListAdapter);
        articleListAdapter.notifyDataSetChanged();
    }

    private void getArticlesOffline(String region) {
        boolean notFound = false;
        articles.clear();

        if (region.equals(GLOBAL) || region.equals(getFavoriteRegion())) {
            try {
                articles = (List<Article>) Utils.readObject(getApplicationContext(), region.toLowerCase() + FILE_FORMAT);
            } catch (IOException | ClassNotFoundException e) {
                notFound = true;
            }
        }

        if (notFound || articles == null || articles.isEmpty()) {
            showArticleError(region);
        } else {
            noArticleLayout.setVisibility(View.GONE);
            setArticleListAdapter();
            swipeRefresh.setRefreshing(false);
            showNoConnectionMsg();
            articleSelectedListener();
        }
    }

    private void articleSelectedListener() {

        articleListAdapter.setOnItemClickListener(new ArticleListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                Article article = articles.get(position);

                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra(URL, article.getUrl());
                intent.putExtra(TITLE, article.getTitle());
                intent.putExtra(IMAGE, article.getUrlToImage());
                intent.putExtra(DATE, article.getPublishedAt());
                intent.putExtra(SOURCE, article.getSource().getName());
                intent.putExtra(AUTHOR, article.getAuthor());

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, android.R.anim.fade_out);
            }
        });
    }

    public void showError() {
        makeLayoutVisible(errorLayout);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeJsonResponse(getSelectedRegion());
            }
        });
    }

    private void showArticleError(String region) {
        setArticleListAdapter();
        noResultMsgTextView.setText(getResources().getString(R.string.noResultText, region));
        makeLayoutVisible(noArticleLayout);
        noArticleBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefresh.setRefreshing(true);
                loadArticles(getSelectedRegion());
            }
        });
    }

    private void makeLayoutVisible(RelativeLayout relativeLayout) {
        swipeRefresh.setRefreshing(false);
        if (relativeLayout.getVisibility() == View.GONE) {
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

    private Call<ArticleList> callApi(ApiInterface apiInterface, String query) {
        if (getFavoriteLanguage().length() == 0) {
            return apiInterface.getLatestArticles(query, Utils.getDate(), SORT_BY, getRandomApiKey());
        } else {
            return apiInterface.getLatestArticles(query, Utils.getDate(), getFavoriteLanguage(), SORT_BY, getRandomApiKey());
        }
    }

    public void showNoConnectionMsg() {
        noConnectionSnackBar = Snackbar.make(findViewById(android.R.id.content), NO_CONNECTION, Snackbar.LENGTH_INDEFINITE);
        noConnectionSnackBar.setAction(NO_CONNECTION_ACTION, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noConnectionSnackBar.dismiss();
            }
        })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    public void dismissNoConnectionMsg() {
        if (noConnectionSnackBar != null && noConnectionSnackBar.isShown()) {
            noConnectionSnackBar.dismiss();
        }
    }
}