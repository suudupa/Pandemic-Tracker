package com.suudupa.coronavirustracker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.suudupa.coronavirustracker.R;
import com.suudupa.coronavirustracker.adapter.ArticleListAdapter;
import com.suudupa.coronavirustracker.api.ApiClient;
import com.suudupa.coronavirustracker.api.ApiInterface;
import com.suudupa.coronavirustracker.asyncTask.JsonResponse;
import com.suudupa.coronavirustracker.model.Article;
import com.suudupa.coronavirustracker.model.ArticleList;
import com.suudupa.coronavirustracker.utility.Utils;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suudupa.coronavirustracker.utility.Resources.AND_OP;
import static com.suudupa.coronavirustracker.utility.Resources.AUTHOR;
import static com.suudupa.coronavirustracker.utility.Resources.CASES;
import static com.suudupa.coronavirustracker.utility.Resources.DATA_URL;
import static com.suudupa.coronavirustracker.utility.Resources.DATE;
import static com.suudupa.coronavirustracker.utility.Resources.DEATHS;
import static com.suudupa.coronavirustracker.utility.Resources.GLOBAL;
import static com.suudupa.coronavirustracker.utility.Resources.IMAGE;
import static com.suudupa.coronavirustracker.utility.Resources.KEYWORD_1;
import static com.suudupa.coronavirustracker.utility.Resources.KEYWORD_2;
import static com.suudupa.coronavirustracker.utility.Resources.MIN_ARTICLES;
import static com.suudupa.coronavirustracker.utility.Resources.OR_OP;
import static com.suudupa.coronavirustracker.utility.Resources.RECOVERED;
import static com.suudupa.coronavirustracker.utility.Resources.SORT_BY;
import static com.suudupa.coronavirustracker.utility.Resources.SOURCE;
import static com.suudupa.coronavirustracker.utility.Resources.TIMESTAMPKEY;
import static com.suudupa.coronavirustracker.utility.Resources.TIMESTAMPTEXT;
import static com.suudupa.coronavirustracker.utility.Resources.TITLE;
import static com.suudupa.coronavirustracker.utility.Resources.URL;
import static com.suudupa.coronavirustracker.utility.Utils.getRandomApiKey;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    public JSONObject jsonResponse;
    private JSONArray jsonNames;

    private TextView casesTextView;
    private TextView deathsTextView;
    private TextView recoveredTextView;
    private TextView timestampTextView;
    private TextView topHeadlinesTextView;

    private SwipeRefreshLayout swipeRefresh;
    private SharedPreferences sharedPreferences;
    private SearchableSpinner regionList;
    public static List<String> regions = new ArrayList<String>();
    private RecyclerView recyclerView;
    private ArticleListAdapter articleListAdapter;
    private List<Article> articles = new ArrayList<>();
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();
        setupDrawer();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        executeJsonResponse(getFavoriteRegion());

        regionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = parent.getItemAtPosition(position).toString();
                try {
                    loadData(selectedRegion);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeView() {
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        casesTextView = findViewById(R.id.casesTextView);
        deathsTextView = findViewById(R.id.deathsTextView);
        recoveredTextView = findViewById(R.id.recoveredTextView);
        timestampTextView = findViewById(R.id.timestampTextView);
        topHeadlinesTextView = findViewById(R.id.topHeadlinesTextView);
        regionList = findViewById(R.id.regionListSpinner);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void setupDrawer() {
        drawer = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.navigationView);
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

    private String getLanguage() {
        return sharedPreferences.getString(getString(R.string.languageKey), "");
    }

    private void executeJsonResponse(String region) {
        new JsonResponse().execute(DATA_URL, this, region);
    }

    public void buildRegionList() {
        JSONArray oldJsonNames = jsonNames;
        jsonNames = jsonResponse.names();
        if (oldJsonNames == null || (oldJsonNames.hashCode() != jsonNames.hashCode())) {
            regions.clear();
            for (int i = 0; i < jsonNames.length() - 1; i++) {
                try {
                    String region = jsonNames.getString(i);
                    regions.add(region);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setupSpinner();
        }
    }

    private void setupSpinner() {
        ArrayAdapter<String> dynamicRegionList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, regions);
        regionList.setAdapter(dynamicRegionList);
    }

    @Override
    public void onRefresh() {
        String selectedRegion = regionList.getSelectedItem().toString();
        executeJsonResponse(selectedRegion);
    }

    public void loadData(String region) throws JSONException {
        retrieveData(region);
        loadArticles(region);
    }

    private void retrieveData(String name) throws JSONException {
        JSONObject region = jsonResponse.getJSONObject(name);
        casesTextView.setText(formatNumber(region.getString(CASES)));
        deathsTextView.setText(formatNumber(region.getString(DEATHS)));
        recoveredTextView.setText(formatNumber(region.getString(RECOVERED)));
        String lastUpdated = Utils.convertUnixTimestamp(jsonResponse.getString(TIMESTAMPKEY));
        timestampTextView.setText(TIMESTAMPTEXT + lastUpdated);
        regionList.setSelection(regions.indexOf(name));
    }

    private String formatNumber (String value) {
        int number = Integer.parseInt(value);
        return String.format("%,d", number);
    }

    private void loadArticles(String region) {

        swipeRefresh.setRefreshing(true);

        final Call<ArticleList> articleListCall;
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        String q;
        if (region.equals(GLOBAL)) {
            q = urlEncode(KEYWORD_1 + OR_OP + KEYWORD_2);
            if (getLanguage().length() == 0) {
                articleListCall = apiInterface.getLatestArticles(q, Utils.getDate(), SORT_BY, getRandomApiKey());
            }
            else {
                articleListCall = apiInterface.getLatestArticles(q, Utils.getDate(), getLanguage(), SORT_BY, getRandomApiKey());
            }
        } else {
            q = urlEncode(KEYWORD_1 + AND_OP + region);
            if (getLanguage().length() == 0) {
                articleListCall = apiInterface.getLatestArticles(q, Utils.getDate(), SORT_BY, getRandomApiKey());
            }
            else {
                articleListCall = apiInterface.getLatestArticles(q, Utils.getDate(), getLanguage(), SORT_BY, getRandomApiKey());
            }
        }

        articleListCall.enqueue(new Callback<ArticleList>() {

            @Override
            public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {

                if (response.isSuccessful() && response.body().getArticles() != null) {

                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticles();

                    if (articles.size() < MIN_ARTICLES) {
                        loadArticles(GLOBAL);
                    }

                    articleListAdapter = new ArticleListAdapter(articles, MainActivity.this);
                    recyclerView.setAdapter(articleListAdapter);
                    articleListAdapter.notifyDataSetChanged();

                    articleSelectedListener();

                    topHeadlinesTextView.setVisibility(View.VISIBLE);
                    swipeRefresh.setRefreshing(false);
                }
                else {
                    topHeadlinesTextView.setVisibility(View.INVISIBLE);
                    swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ArticleList> call, Throwable t) {
                topHeadlinesTextView.setVisibility(View.INVISIBLE);
                //TODO: add error layout
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private static String urlEncode(String query) {
        try {
            return URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getCause());
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
}