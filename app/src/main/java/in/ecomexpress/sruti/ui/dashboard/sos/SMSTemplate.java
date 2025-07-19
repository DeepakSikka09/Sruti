package in.ecomexpress.sruti.ui.dashboard.sos;

import java.io.PrintWriter;
import java.io.StringWriter;


class SMSTemplate {
    final private static String TAG = SMSTemplate.class.getSimpleName();

    //This is agreed sms template JIRA NO. ND-1600
//    @@EMPCODE has raised a SOS Alert from https://www.google.com/maps/search/?api=1&query=@@LATITUDE,@@LONGITUDE location.
    public static String getSOSSms(String empCode, double lat, double lang, String sosTemplate) {

        try {
            sosTemplate = sosTemplate.replace("@@EMPCODE", empCode);
            sosTemplate = sosTemplate.replace("@@LATITUDE", String.valueOf(lat));
            sosTemplate = sosTemplate.replace("@@LONGITUDE", String.valueOf(lang));
//            Logger.e(TAG, "modified sos templage: " + sosTemplate);
            return sosTemplate;
        } catch (Exception e) {
//            AppLogJsonProcessor.appendErrorJSONObject(AppLogJsonProcessor.LogType.EVENT, getExceptionAsString(e), lat, lang, System.currentTimeMillis(), empCode);
            e.printStackTrace();
        }
        return "EmpCode: " + empCode
                + ",\n"
                + "Lat:" + lat
                + ",\n"
                + "Lng:" + lang;
    }

    private static String getExceptionAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
