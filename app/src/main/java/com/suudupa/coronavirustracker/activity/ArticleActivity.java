package com.suudupa.coronavirustracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.suudupa.coronavirustracker.R;
import com.suudupa.coronavirustracker.utility.Utils;

public class ArticleActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout titleAppbar;
    private FrameLayout date_behavior;
    private ImageView imageView;
    private TextView appbar_title, appbar_subtitle, title, date, time;

    private boolean isToolbarViewHidden = false;
    private String mUrl, mImg, mTitle, mDate, mSource, mAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_layout);

        initView();

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mImg = intent.getStringExtra("img");
        mTitle = intent.getStringExtra("title");
        mDate = intent.getStringExtra("date");
        mSource = intent.getStringExtra("source");
        mAuthor = intent.getStringExtra("author");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(Utils.getRandomColorDrawable());

        Glide.with(this)
                .load(mImg)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        appbar_title.setText(mSource);
        appbar_subtitle.setText(mUrl);
        date.setText(Utils.formatDate(mDate));
        title.setText(mTitle);

        String author;
        if (mAuthor != null){
            author = " \u2022 " + mAuthor;
        } else {
            author = "";
        }

        time.setText(mSource + author + " \u2022 " + Utils.formatDateTime(mDate));

        initWebView(mUrl);
    }

    private void initView() {

        appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        date_behavior = findViewById(R.id.date_behavior);
        titleAppbar = findViewById(R.id.title_appbar);
        imageView = findViewById(R.id.backdrop);
        appbar_title = findViewById(R.id.title_on_appbar);
        appbar_subtitle = findViewById(R.id.subtitle_on_appbar);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        title = findViewById(R.id.title);
    }

    private void initWebView(String url){
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isToolbarViewHidden) {
            date_behavior.setVisibility(View.GONE);
            titleAppbar.setVisibility(View.VISIBLE);
            isToolbarViewHidden = !isToolbarViewHidden;
        }
        else if (percentage < 1f && !isToolbarViewHidden) {
            date_behavior.setVisibility(View.VISIBLE);
            titleAppbar.setVisibility(View.GONE);
            isToolbarViewHidden = !isToolbarViewHidden;
        }
    }
}