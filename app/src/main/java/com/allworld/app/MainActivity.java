package com.allworld.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
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

        // Splash and loading animation are native UI; no image downloads are needed.
        web.loadUrl(HOME_URL);

        // Fallback if the website does not finish loading.
        root.postDelayed(this::hideSplash, 12000);
    }

    private View createSplash() {
        LinearLayout panel = new LinearLayout(this);
        panel.setGravity(Gravity.CENTER);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(28, 28, 28, 28);
        panel.setBackgroundResource(R.drawable.all_world_splash_background);

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

        LoadingDotsView loader = new LoadingDotsView(this);
        LinearLayout.LayoutParams loaderParams = new LinearLayout.LayoutParams(
            dp(52), dp(52)
        );
        loaderParams.topMargin = dp(22);
        panel.addView(loader, loaderParams);

        return panel;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void hideSplash() {
        if (splashDismissed || splash == null) return;
        splashDismissed = true;

        // Scale the complete splash toward the top-right corner, then remove it.
        splash.setPivotX(splash.getWidth());
        splash.setPivotY(0f);
        splash.animate()
            .scaleX(0.06f)
            .scaleY(0.06f)
            .translationX(dp(12))
            .translationY(-dp(12))
            .alpha(0f)
            .setDuration(520)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (splash != null) {
                        root.removeView(splash);
                        splash = null;
                    }
                }
            })
            .start();
    }

    private static final class LoadingDotsView extends View {
        private static final int DOT_COUNT = 12;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final ValueAnimator animator;
        private int frame;

        LoadingDotsView(Activity activity) {
            super(activity);
            paint.setStyle(Paint.Style.FILL);
            animator = ValueAnimator.ofInt(0, DOT_COUNT - 1);
            animator.setDuration(840);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new android.view.animation.LinearInterpolator());
            animator.addUpdateListener(valueAnimator -> {
                frame = (Integer) valueAnimator.getAnimatedValue();
                invalidate();
            });
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (!animator.isStarted()) animator.start();
        }

        @Override
        protected void onDetachedFromWindow() {
            animator.cancel();
            super.onDetachedFromWindow();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            float radius = Math.min(getWidth(), getHeight()) * 0.37f;
            float dotRadius = Math.max(dp(1.8f), Math.min(getWidth(), getHeight()) * 0.055f);

            // A bright dot travels around the ring; trailing dots fade smoothly.
            for (int i = 0; i < DOT_COUNT; i++) {
                int distance = (i - frame + DOT_COUNT) % DOT_COUNT;
                int alpha = Math.max(35, 255 - distance * 19);
                double angle = (Math.PI * 2.0 * i / DOT_COUNT) - Math.PI / 2.0;
                float x = cx + (float) Math.cos(angle) * radius;
                float y = cy + (float) Math.sin(angle) * radius;
                paint.setColor(Color.argb(alpha, 105, 190, 255));
                canvas.drawCircle(x, y, dotRadius, paint);
            }
        }

        private float dp(float value) {
            return value * getResources().getDisplayMetrics().density;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
