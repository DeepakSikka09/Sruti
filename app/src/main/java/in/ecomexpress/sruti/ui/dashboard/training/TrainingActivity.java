package in.ecomexpress.sruti.ui.dashboard.training;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityTrainingBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.ui.base.BaseActivity;

public class TrainingActivity extends BaseActivity<ActivityTrainingBinding, TrainingViewModel> implements ITrainingNavigator {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    TrainingViewModel trainingViewModel;
    ActivityTrainingBinding activityTrainingBinding;
    WebView webView;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = findViewById(R.id.training_webview);
        trainingViewModel.setNavigator(this);
        activityTrainingBinding = getViewDataBinding();
        if (isNetworkConnected()) {
            trainingViewModel.getTrainingUrlAPI(this);// Here We are caling the API
        }else {
            showSnackbar(getResources().getString(R.string.no_network_found));
        }


        trainingViewModel.getTrainingResponse().observe(this, trainingResponse -> {
            if (trainingResponse != null) {
                if (trainingResponse.isSuccess()) {

                    if (trainingResponse.getRedirectURL().trim().equalsIgnoreCase("")) {
                        showSnackbar(String.valueOf(R.string.try_after_some_time));
                    } else {
                        startTrainingWebView(trainingResponse.getRedirectURL());
                    }

                } else {


                    showSnackbar(trainingResponse.getErrorCode());
                }
            }
        });

        // Get the click time from the intent
        long clickTime = getIntent().getLongExtra("clickTime", 0);
        long openTime = System.currentTimeMillis();
        long duration = openTime - clickTime;

        // Log the time taken to open this screen
        logScreenOpenTime(duration);
    }

    private void logScreenOpenTime(long duration) {
        Bundle bundle = new Bundle();
        bundle.putString("screen_name", getScreenName());
        bundle.putLong("open_time", duration);
        FirebaseAnalytics.getInstance(this).logEvent("screen_open_time", bundle);
    }

    @Override
    protected String getScreenName() {
        return "Training Screen";
    }


    public static Intent getStartIntent(Context context) {
        return new Intent(context, TrainingActivity.class);
    }

    @Override
    public TrainingViewModel getViewModel() {

        trainingViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(TrainingViewModel.class);
        return trainingViewModel;


    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_training;
    }

    @Override
    public void showError(String description) {
        showSnackbar(description);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void startTrainingWebView(String url) {
        try {
            activityTrainingBinding.trainingWebview.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setLoadWithOverviewMode(true);


            // Enable cookies
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setAcceptThirdPartyCookies(webView, true);
            }

            webView.setWebViewClient(new WebViewClient());
            webView.setWebChromeClient(new MyChrome(this));

            activityTrainingBinding.trainingWebview.loadUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }


    @Override
    public Activity getActivityContext() {
        return this;
    }



    @Override
    public void onBackClick() {
        try {
            if (activityTrainingBinding.trainingWebview.canGoBack()) {
                activityTrainingBinding.trainingWebview.goBack();

            }  else {
                super.onBackPressed(); // Allow activity to handle back button press
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        private final Activity activity;

        public MyChrome(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onHideCustomView() {
            ((FrameLayout) activity.getWindow().getDecorView()).removeView(mCustomView);
            mCustomView = null;
            activity.getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
            activity.setRequestedOrientation(mOriginalOrientation);
            mCustomViewCallback.onCustomViewHidden();
            mCustomViewCallback = null;
        }

        @Override
        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (mCustomView != null) {
                onHideCustomView();
                return;
            }
            mCustomView = paramView;
            mOriginalSystemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            mOriginalOrientation = activity.getRequestedOrientation();
            mCustomViewCallback = paramCustomViewCallback;

            // Add the custom view to the decor view
            ((FrameLayout) activity.getWindow().getDecorView()).addView(
                    mCustomView,
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    )
            );

            // Hide the system UI and make the activity full-screen
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );

            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

}