package in.ecomexpress.sruti.ui.dashboard.switchnumber;


import in.ecomexpress.sruti.model.masterdata.Post_option;
import in.ecomexpress.sruti.utils.common_files.Constants;

/**
 * Created by shivangis on 12/10/2018.
 */

public class SwitchNumberItemViewModel {

    public Post_option postoption;
    public ItemListener itemListener;
    String getPstnformat;
    String setPstnFormat;

    public SwitchNumberItemViewModel(Post_option postoption, ItemListener itemListener) {
        this.postoption = postoption;
        this.itemListener = itemListener;
    }

    public String PstnFormat() {
        try {
            getPstnformat = postoption.getPstn_format();
            if (getPstnformat != null) {
                if (getPstnformat.contains(Constants.pstn_pin)) {
                    setPstnFormat = getPstnformat.replaceAll(",@@PIN@@#", "");
                } else if (getPstnformat.contains(Constants.pstn_awb)) {
                    setPstnFormat = getPstnformat.replaceAll(",@@AWB@@#", "");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return setPstnFormat;
    }
    public String PstnProvider() {
        return postoption.getPstn_provider();
    }

    public String IsLocal() {
        return postoption.getIsLocal();
    }

    public String Additional() {
        return postoption.getAdditional_text();
    }

    public void onItemClick() {
        itemListener.onItemClick(postoption);
    }
}
