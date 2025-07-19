package in.ecomexpress.sruti.ui.dashboard.performance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.HashMap;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.databinding.ActivityPerformanceBinding;
import in.ecomexpress.sruti.ui.base.BaseActivity;

public class PerformanceActivity extends BaseActivity<ActivityPerformanceBinding, PerformanceViewModel> implements IPerformanceNavigator {

    @Inject
    PerformanceViewModel performanceViewModel;
    ProgressDialog dialog;
    ActivityPerformanceBinding activityPerformanceBinding;
    Context context = this;
    private String webUrl;
    //  String htmlFile="<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><title>Ecom Express</title><base href=\"/\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" /><link rel=\"stylesheet\"\thref=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\"><script\tsrc=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script><script\tsrc=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script></head><body>\t<div class=\"container\">\t\t<h4 style=\"color: red; text-decoration: underline;\">Performance</h4>\t\t<table class=\"table table-bordered table-striped\">\t\t\t<thead style=\"background-color: #FFC2B3\">\t\t\t\t\t\t<th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Product</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Yesterday</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Last_7Days</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">This_Season</th>\t\t\t</thead>\t\t\t<tbody>\t\t\t<tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">Outscanned</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">Successful %</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">DC Rank</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">State Rank</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">Country Rank</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr>            </tbody>\t\t</table>\t\t<h4 style=\"color:red;text-decoration: underline;\">NPS</h4>\t\t<table class=\"table table-bordered table-striped\">\t\t<thead style=\"background-color: #FFC2B3\">\t\t\t<th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Product</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Yesterday</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">Last_7Days</th><th style=\"font-size:13px;font-weight:700;vertical-align:middle;text-align: center;\">This_Season</th>\t\t\t</thead>\t\t\t<tbody>\t\t\t<tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">Outscanned</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">Successful %</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">DC Rank</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr><tr><td style=\"font-weight:bold;font-size:11px;text-align: center;\">State Rank</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td><td style=\"font-weight:bold;font-size:11px;text-align: center;\">0</td></tr>\t\t\t</tbody>\t\t</table>\t</div></body></html>";
    private String htmlFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performanceViewModel.setNavigator(this);
        this.activityPerformanceBinding = getViewDataBinding();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorStatusbar));
        }


        dialog = new ProgressDialog(context);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(true);
        if (isNetworkConnected()) {
            performanceViewModel.callApi(PerformanceActivity.this);
        } else {
            dialog.dismiss();
            showToast(getResources().getString(R.string.no_network_error));
        }
        // startWebView(String htmlString);

    }

    @Override
    protected String getScreenName() {
        return "Performance Screen";
    }


    @Override
    public PerformanceViewModel getViewModel() {
        return performanceViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_performance;
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, PerformanceActivity.class);
    }


    @Override
    public void backClick() {
        finish();
    }


    @Override
    public void startPerformanceWebView(String htmlString) {
/*        activityPerformanceBinding.performanceWebciew.setWebViewClient(new WebViewClient() {


            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);

               *//* dialog = new ProgressDialog(context);
                dialog.show();
                dialog.setCancelable(false);
                dialog.setMessage("Loading...");
                dialog.setIndeterminate(true);*//*
                // ClosePopup.setVisibility(View.GONE);
            }

            public void onPageFinished(WebView view, String url) {


                super.onPageFinished(view, url);
                dialog.dismiss();
                //ClosePopup.setVisibility(View.VISIBLE);
            }


            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {


                Toast.makeText(PerformanceActivity.this, "The Requested Page Does Not Exist", Toast.LENGTH_LONG).show();
                // webview.loadUrl("http://arunimmanuel.blogspot.in");
               *//* dialog.dismiss();*//*
                // ClosePopup.setVisibility(View.VISIBLE);

                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });*/
        activityPerformanceBinding.performanceWebciew.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                Logger.i("Performance", "progress:[" + newProgress + "]");
                super.onProgressChanged(view, newProgress);
            }
        });
        activityPerformanceBinding.performanceWebciew.clearSslPreferences();//Authorization
//        activityPerformanceBinding.performanceWebciew.
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Android", getViewModel().getAuthToken());
        WebSettings webSettings = activityPerformanceBinding.performanceWebciew.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("user-agent-string");
        // activityPerformanceBinding.performanceWebciew.loadUrl(webUrl/*"https://test.ecomexpress.in:8020"*/, headers);
        activityPerformanceBinding.performanceWebciew.loadData(htmlString, "text/html", "utf-8");

        activityPerformanceBinding.performanceWebciew.getSettings().setLoadWithOverviewMode(true);
        activityPerformanceBinding.performanceWebciew.getSettings().setUseWideViewPort(true);
        dialog.dismiss();

    }

    @Override
    public void errorHandler(String s) {
        showToast(s);
        dialog.dismiss();
    }

    @Override
    public void showHandleError(boolean status) {
        dialog.dismiss();
        if (status)
            showToast(getString(R.string.http_500_msg));
        else
            showToast(getString(R.string.server_down_msg));
    }

    @Override
    public void doLogout(String message) {

        try {
            showToast(getString(R.string.session_expire));
            performanceViewModel.logoutLocal();
        } catch (Exception e) {
            showToast(e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void clearStack() {
       /* Intent intent = new Intent(PerformanceActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
*/
    }

    @Override
    public void showException(Exception e) {
        showToast(e.getMessage());
    }

}