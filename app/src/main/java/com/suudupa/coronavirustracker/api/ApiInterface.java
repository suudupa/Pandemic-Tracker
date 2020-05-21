package com.suudupa.coronavirustracker.api;

import com.suudupa.coronavirustracker.model.ArticleList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("everything")
    Call<ArticleList> getWorldArticles(

            @Query("qInTitle") String keywords,
            @Query("from") String from,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("pageSize") int pageSize,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<ArticleList> getRegionArticles(

            @Query("q") String keywords,
            @Query("from") String from,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("pageSize") int pageSize,
            @Query("apiKey") String apiKey
    );
}