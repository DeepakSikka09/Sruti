package in.ecomexpress.sruti.ui.dashboard.signature;

public class MobileItem {
    private String mobileNo;
    private boolean isSelected;

    public MobileItem(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
