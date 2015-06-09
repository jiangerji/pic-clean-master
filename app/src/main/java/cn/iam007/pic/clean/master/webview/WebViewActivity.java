package cn.iam007.pic.clean.master.webview;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.BaseActivity;

/**
 * Created by Administrator on 2015/6/5.
 */
public class WebViewActivity extends BaseActivity {

    public final static String DATA_URL = "DATA_URL";
    public final static String DATA_TITLE = "DATA_TITLE";

    private WebView mWebView = null;
    private View mLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        mWebView = (WebView) findViewById(R.id.webview);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    if (mLoadingProgressBar != null) {
                        mLoadingProgressBar.setVisibility(View.VISIBLE);
                        mLoadingProgressBar.startAnimation(
                                AnimationUtils.loadAnimation(WebViewActivity.this, R.anim.abc_fade_in));
                    }
                    view.loadUrl(url);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mLoadingProgressBar != null) {
                    mLoadingProgressBar.setVisibility(View.GONE);
                    mLoadingProgressBar.startAnimation(
                            AnimationUtils.loadAnimation(WebViewActivity.this, R.anim.abc_fade_out));
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(DATA_URL);
            if (URLUtil.isNetworkUrl(url)) {
                mWebView.loadUrl(url);
            }

            String title = intent.getStringExtra(DATA_TITLE);
            if (!TextUtils.isEmpty(title)){
                setTitle(title);
            }
        }

        mLoadingProgressBar = getToolbar().findViewById(R.id.toolbar_progress_bar);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
