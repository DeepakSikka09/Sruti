package in.ecomexpress.sruti.model.masterdata;

import java.util.ArrayList;

/**
 * Created by 63091 on 28-06-2019.
 */

public class Banner_Configuration {
    public ArrayList<Dashboard_Banner> getDashboard_banner() {
        return dashboard_banner;
    }

    public void setDashboard_banner(ArrayList<Dashboard_Banner> dashboard_banner) {
        this.dashboard_banner = dashboard_banner;
    }

    private ArrayList<Dashboard_Banner> dashboard_banner;
}
