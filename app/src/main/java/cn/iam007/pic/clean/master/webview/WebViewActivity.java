package cn.iam007.pic.clean.master.webview;

import android.content.Intent;
import android.os.Bundle;
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

    private WebView mWebView = null;

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

                    view.loadUrl(url);
                    return true;
                } else {
                    return false;
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(DATA_URL);
            if (URLUtil.isNetworkUrl(url)) {
                mWebView.loadUrl(url);
            }
        }
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
