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
import android.widget.Toast;

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

import static com.suudupa.coronavirustracker.utility.Resources.AND_OP;
import static com.suudupa.coronavirustracker.utility.Resources.CASES;
import static com.suudupa.coronavirustracker.utility.Resources.DATA_URL;
import static com.suudupa.coronavirustracker.utility.Resources.DATE;
import static com.suudupa.coronavirustracker.utility.Resources.DEATHS;
import static com.suudupa.coronavirustracker.utility.Resources.ENGLISH;
import static com.suudupa.coronavirustracker.utility.Resources.FILE_FORMAT;
import static com.suudupa.coronavirustracker.utility.Resources.GLOBAL;
import static com.suudupa.coronavirustracker.utility.Resources.IMAGE;
import static com.suudupa.coronavirustracker.utility.Resources.KEYWORD_1;
import static com.suudupa.coronavirustracker.utility.Resources.KEYWORD_2;
import static com.suudupa.coronavirustracker.utility.Resources.KEYWORD_3;
import static com.suudupa.coronavirustracker.utility.Resources.MIN_ARTICLES;
import static com.suudupa.coronavirustracker.utility.Resources.NEW_CASES;
import static com.suudupa.coronavirustracker.utility.Resources.NEW_DEATHS;
import static com.suudupa.coronavirustracker.utility.Resources.NO_CONNECTION;
import static com.suudupa.coronavirustracker.utility.Resources.NO_CONNECTION_ACTION;
import static com.suudupa.coronavirustracker.utility.Resources.OR_OP;
import static com.suudupa.coronavirustracker.utility.Resources.PAGE_SIZE;
import static com.suudupa.coronavirustracker.utility.Resources.RECOVERED;
import static com.suudupa.coronavirustracker.utility.Resources.SORT_BY;
import static com.suudupa.coronavirustracker.utility.Resources.SOURCE;
import static com.suudupa.coronavirustracker.utility.Resources.TIMESTAMP_KEY;
import static com.suudupa.coronavirustracker.utility.Resources.TITLE;
import static com.suudupa.coronavirustracker.utility.Resources.URL;
import static com.suudupa.coronavirustracker.utility.Utils.getRandomApiKey;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    public static List<String> regions = new ArrayList<>();
    public JSONObject jsonResponse;

    private SwipeRefreshLayout swipeRefresh;
    private Spinner regionList;
    private ArrayList<Region> regionItems = new ArrayList<>();
    private TextView timestampTextView;
    private TextView casesTextView;
    private TextView newCasesTextView;
    private TextView deathsTextView;
    private TextView newDeathsTextView;
    private TextView recoveredTextView;
    private TextView topHeadlinesTextView;
    private TextView noResultMsgTextView;
    private RecyclerView recyclerView;
    private ArticleListAdapter articleListAdapter;
    private List<Article> articles = new ArrayList<>();
    private RelativeLayout errorLayout;
    private RelativeLayout noArticleLayout;
    private Button btnRetry;
    private Button noArticleBtnRetry;
    private Snackbar noConnectionSnackBar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;

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
    }

    private void initializeView() {
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        regionList = findViewById(R.id.regionListSpinner);
        timestampTextView = findViewById(R.id.timestampTextView);
        casesTextView = findViewById(R.id.casesTextView);
        newCasesTextView = findViewById(R.id.newCasesTextView);
        deathsTextView = findViewById(R.id.deathsTextView);
        newDeathsTextView = findViewById(R.id.newDeathsTextView);
        recoveredTextView = findViewById(R.id.recoveredTextView);
        topHeadlinesTextView = findViewById(R.id.topHeadlinesTextView);
        noResultMsgTextView = findViewById(R.id.noResultMessage);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        errorLayout = findViewById(R.id.errorLayout);
        noArticleLayout = findViewById(R.id.noResultLayout);
        btnRetry = findViewById(R.id.btnRetry);
        noArticleBtnRetry = findViewById(R.id.noResultBtnRetry);
    }


    private void setupDrawer() {
        drawer = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.toolbarTitle);
        }
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

    private void executeJsonResponse(String region) {
        swipeRefresh.setRefreshing(true);
        new JsonResponse().execute(DATA_URL, this, region);
    }

    @Override
    public void onRefresh() {
        executeJsonResponse(getSelectedRegion());
    }

    public void buildRegionList(String selectedRegion) {
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
        populateSpinner(selectedRegion);
    }

    private void populateSpinner(String selectedRegion) {
        final RegionListAdapter regionListAdapter = new RegionListAdapter(this, regionItems);
        regionList.setAdapter(regionListAdapter);
        regionListAdapter.notifyDataSetChanged();
        int position = regions.indexOf(selectedRegion);
        regionList.setSelection(position);
        regionList.setTag(position);
        regionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!regionList.getTag().equals(position)) {
                    regionList.setTag(position);
                    String selectedRegion = parent.getItemAtPosition(position).toString();
                    try {
                        swipeRefresh.setRefreshing(true);
                        loadData(selectedRegion);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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
    }

    private void loadArticles(final String region) {

        final Call<ArticleList> articleListCall;
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String q;
        boolean isGlobal = false;

        if (region.equals(GLOBAL)) {
            q = KEYWORD_1 + OR_OP + KEYWORD_2 + OR_OP + KEYWORD_3;
            isGlobal = true;
        } else {
            q = "(" + KEYWORD_1 + OR_OP + KEYWORD_2 + OR_OP + KEYWORD_3 + ")" + AND_OP + region;
        }

        articleListCall = callApi(apiInterface, q, isGlobal);
        topHeadlinesTextView.setText(getResources().getString(R.string.headlinesTitle, region));
        articleListCall.enqueue(new Callback<ArticleList>() {

            @Override
            public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {

                if (response.isSuccessful() && response.body().getArticles() != null) {

                    noArticleLayout.setVisibility(View.GONE);
                    dismissNoConnectionMsg();
                    articles.clear();

                    articles = response.body().getArticles();
                    if (articles.size() < MIN_ARTICLES) {
                        if (region.equals(GLOBAL)) {
                            getArticlesOffline(GLOBAL, true);
                            return;
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.noResultToast, region, GLOBAL), Toast.LENGTH_LONG).show();
                            loadArticles(GLOBAL);
                            return;
                        }
                    }

                    if (region.equals(GLOBAL) || region.equals(getFavoriteRegion())) {
                        try {
                            Utils.writeObject(getApplicationContext(), region.toLowerCase() + FILE_FORMAT, articles);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    setArticleListAdapter();
                    articleSelectedListener();

                } else {
                    getArticlesOffline(region, true);
                }
            }

            @Override
            public void onFailure(Call<ArticleList> call, Throwable t) {
                getArticlesOffline(region, false);
            }
        });
    }

    private Call<ArticleList> callApi(ApiInterface apiInterface, String query, boolean isGlobal) {
        if (isGlobal) {
            return apiInterface.getWorldArticles(query, Utils.getDate(), ENGLISH, SORT_BY, PAGE_SIZE, getRandomApiKey());
        } else {
            return apiInterface.getRegionArticles(query, Utils.getDate(), ENGLISH, SORT_BY, PAGE_SIZE, getRandomApiKey());
        }
    }

    private void setArticleListAdapter() {
        articleListAdapter = new ArticleListAdapter(articles, MainActivity.this);
        recyclerView.setAdapter(articleListAdapter);
        articleListAdapter.notifyDataSetChanged();
    }

    private void getArticlesOffline(String region, boolean isConnected) {
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
            if(!isConnected) {
                showNoConnectionMsg();
            }
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