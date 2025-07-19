package in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.DashboardBanner;
import in.ecomexpress.sruti.ui.dashboard.dashboard_main.dashboard_item_listener.SliderCallListener;

/**
 * Created by shivangis on 10/20/2018.
 */

public class SliderAdapter extends PagerAdapter {

    private Context context;
    /* private List<String> color;
     private List<String> colorName;*/
    List<DashboardBanner> getDashboardbanner;
    String currentUrl = null;
    SliderCallListener sliderCallListener;

    public SliderAdapter(Context context, List<DashboardBanner> getDashboardbanner) {
        this.context = context;
        this.getDashboardbanner = getDashboardbanner;
    }


    public void setsliderCallListener(SliderCallListener listener) {
        this.sliderCallListener = listener;
    }

    @Override
    public int getCount() {
        return getDashboardbanner.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_dashboard_banner_slider, null);

        TextView textView = view.findViewById(R.id.textView);
        ProgressBar progress = view.findViewById(R.id.progress1);
        WebView web = view.findViewById(R.id.image);
        RelativeLayout linearLayout = view.findViewById(R.id.linearLayout);

        textView.setText(getDashboardbanner.get(position).getTitle());
        startWebView(web, progress, getDashboardbanner.get(position).getShort_url());
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);


     /*   // listen for page changes so we can track the current index
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrollStateChanged(int arg0) {
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageSelected(int currentPage) {
                //currentPage is the position that is currently displayed.
//               getPopup(color.get(currentPage));

            }

        });
*/

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show();
                sliderCallListener.stopTimer(getDashboardbanner.get(position).getLong_url());

            }
        });
        return view;
    }


    public void startWebView(final WebView webview, final ProgressBar progressBar, String url) {
        webview.loadUrl(url);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.setWebViewClient(new WebViewClient() {


            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);

            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);

            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {


                webview.loadUrl("http://arunimmanuel.blogspot.in");
                progressBar.setVisibility(View.GONE);

                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }


}