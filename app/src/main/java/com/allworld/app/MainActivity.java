package com.allworld.app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private static final String HOME_URL =
        "https://all-world-app-source-code.docc-samisoliman.workers.dev/";
    private FrameLayout root;
    private View splash;
    private boolean splashDismissed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(3, 5, 22));
        getWindow().setNavigationBarColor(Color.rgb(3, 5, 22));

        root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(3, 5, 22));

        WebView web = new WebView(this);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideSplash();
            }
        });
        root.addView(web, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));

        splash = createSplash();
        root.addView(splash, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));
        setContentView(root);

        // Splash is native UI embedded in this APK; it does not download assets.
        web.loadUrl(HOME_URL);

        // Avoid leaving the splash forever if the site is unreachable.
        root.postDelayed(this::hideSplash, 12000);
    }

    private View createSplash() {
        LinearLayout panel = new LinearLayout(this);
        panel.setGravity(Gravity.CENTER);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(28, 28, 28, 28);
        panel.setBackgroundColor(Color.rgb(3, 5, 22));

        TextView earth = new TextView(this);
        earth.setText("🌍");
        earth.setTextSize(76);
        earth.setGravity(Gravity.CENTER);
        panel.addView(earth, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(this);
        title.setText("All World");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.topMargin = 12;
        panel.addView(title, titleParams);

        TextView subtitle = new TextView(this);
        subtitle.setText("Deine Welt. Alles verbunden.");
        subtitle.setTextColor(Color.rgb(180, 195, 230));
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subParams.topMargin = 8;
        panel.addView(subtitle, subParams);

        return panel;
    }

    private void hideSplash() {
        if (splashDismissed || splash == null) return;
        splashDismissed = true;
        AlphaAnimation fade = new AlphaAnimation(1f, 0f);
        fade.setDuration(220);
        splash.startAnimation(fade);
        splash.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // WebView is kept as the main app surface; default behavior exits safely.
        super.onBackPressed();
    }
}
