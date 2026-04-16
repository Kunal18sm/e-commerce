package com.shopzone.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * ShopZone Android App — WebView-based client.
 * 
 * This activity loads the ShopZone web application in a WebView,
 * enabling JavaScript and handling back navigation gracefully.
 */
public class MainActivity extends Activity {

    private WebView webView;

    // ========================================================
    // IMPORTANT: Change this URL to match your server address.
    // For Android Emulator accessing localhost: use 10.0.2.2
    // For physical device on same network: use your PC's IP
    // ========================================================
    private static final String SHOP_URL = "http://10.0.2.2:8080/ShopZone/products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);           // Enable JavaScript
        webSettings.setDomStorageEnabled(true);            // Enable DOM storage
        webSettings.setLoadWithOverviewMode(true);         // Fit page to screen
        webSettings.setUseWideViewPort(true);              // Use wide viewport
        webSettings.setBuiltInZoomControls(true);          // Enable zoom
        webSettings.setDisplayZoomControls(false);         // Hide zoom controls
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Set WebView client to handle navigation within the app
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // Set Chrome client for alert dialogs
        webView.setWebChromeClient(new WebChromeClient());

        // Load the ShopZone URL
        webView.loadUrl(SHOP_URL);
    }

    /**
     * Handle the back button — navigate back in WebView history
     * instead of closing the app.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
