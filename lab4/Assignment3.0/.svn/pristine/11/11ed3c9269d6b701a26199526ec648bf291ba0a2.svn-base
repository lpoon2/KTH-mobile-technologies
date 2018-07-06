package com.example.lpoon2.assignment30;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by lpoon2 on 10/20/2017.
 */

public class RepoDetail extends Fragment {
    private static final String REPO_URL = "param1";
    private String url = "";

    public RepoDetail() {
    }

    public static RepoDetail newInstance(String repo_url){
        RepoDetail fragment = new RepoDetail();
        Bundle args = new Bundle();
        args.putString(REPO_URL, repo_url);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(REPO_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.repo_detail, container, false);
        WebView web   = (WebView) rootView.findViewById(R.id.web);
        web.loadUrl(url);

        // Enable Javascript
        WebSettings webSettings = web.getSettings();
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        web.setWebViewClient(new WebViewClient());

        return rootView;
    }


}
