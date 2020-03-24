package com.suudupa.coronavirustracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.suudupa.coronavirustracker.R;
import com.suudupa.coronavirustracker.adapter.ArticleListAdapter;
import com.suudupa.coronavirustracker.api.ApiClient;
import com.suudupa.coronavirustracker.api.ApiInterface;
import com.suudupa.coronavirustracker.asyncTask.RetrieveGlobalData;
import com.suudupa.coronavirustracker.asyncTask.RetrieveRegionData;
import com.suudupa.coronavirustracker.model.Article;
import com.suudupa.coronavirustracker.model.ArticleList;
import com.suudupa.coronavirustracker.utility.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suudupa.coronavirustracker.utility.Resources.AND_OP;
import static com.suudupa.coronavirustracker.utility.Resources.API_KEY;
import static com.suudupa.coronavirustracker.utility.Resources.GLOBAL;
import static com.suudupa.coronavirustracker.utility.Resources.HOMEPAGE_URL;
import static com.suudupa.coronavirustracker.utility.Resources.KEYWORD_1;
import static com.suudupa.coronavirustracker.utility.Resources.KEYWORD_2;
import static com.suudupa.coronavirustracker.utility.Resources.OR_OP;
import static com.suudupa.coronavirustracker.utility.Resources.OUTBREAK_DATA;
import static com.suudupa.coronavirustracker.utility.Resources.REGION_URL;
import static com.suudupa.coronavirustracker.utility.Resources.SORT_BY;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public String numCases = "0";
    public String numDeaths = "0";
    public String numRecovered = "0";

    public TextView casesTextView;
    public TextView deathsTextView;
    public TextView recoveredTextView;
    private TextView topHeadlinesTextView;

    private Spinner regionList;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private ArticleListAdapter articleListAdapter;
    private List<Article> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();

        loadData(GLOBAL);

        regionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = parent.getItemAtPosition(position).toString();
                loadData(selectedRegion);
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
        topHeadlinesTextView = findViewById(R.id.topHeadlinesTextView);
        regionList = findViewById(R.id.regionListSpinner);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onRefresh() {
        loadData(regionList.getSelectedItem().toString());
    }

    private void loadData(String region) {
        retrieveData(region);
        loadArticles(region);
    }

    private void retrieveData(String region) {
        if (region.equals(GLOBAL)) {
            new RetrieveGlobalData().execute(HOMEPAGE_URL, OUTBREAK_DATA, this);
        } else {
            new RetrieveRegionData().execute(REGION_URL, region, this);
        }
    }

    public void loadArticles(String region) {

        swipeRefresh.setRefreshing(true);

        Call<ArticleList> articleListCall;
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        String q;
        if (region.equals(GLOBAL)) {
            q = urlEncode(KEYWORD_1 + OR_OP + KEYWORD_2);
            articleListCall = apiInterface.getLatestArticles(q, Utils.getDate(), SORT_BY, API_KEY);
        } else {
            q = urlEncode(KEYWORD_1 + AND_OP + region);
            articleListCall = apiInterface.getLatestArticles(q, Utils.getDate(), SORT_BY, API_KEY);
        }

        articleListCall.enqueue(new Callback<ArticleList>() {

            @Override
            public void onResponse(Call<ArticleList> call, Response<ArticleList> response) {

                if (response.isSuccessful() && response.body().getArticles() != null) {

                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticles();

                    articleListAdapter = new ArticleListAdapter(articles, MainActivity.this);
                    recyclerView.setAdapter(articleListAdapter);
                    articleListAdapter.notifyDataSetChanged();

                    articleSelectedListener();

                    topHeadlinesTextView.setVisibility(View.VISIBLE);
                    swipeRefresh.setRefreshing(false);
                } else {
                    topHeadlinesTextView.setVisibility(View.INVISIBLE);
                    swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ArticleList> call, Throwable t) {
                topHeadlinesTextView.setVisibility(View.INVISIBLE);
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

                ImageView imageView = view.findViewById(R.id.img);
                Article article = articles.get(position);

                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img", article.getUrlToImage());
                intent.putExtra("date", article.getPublishedAt());
                intent.putExtra("source", article.getSource().getName());
                intent.putExtra("author", article.getAuthor());


                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, android.R.anim.fade_out);


            }
        });
    }
}


