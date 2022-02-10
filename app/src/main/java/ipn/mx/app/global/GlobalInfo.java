package ipn.mx.app.global;

import java.util.ArrayList;

public class GlobalInfo {

    public static final int NOTIFICATION_EMOTION_ID = 888;
    public static final int NOTIFICATION_CONN_ID = 999;
    public final int TIMEOUT_REQUEST = 2000;
    public final int MAX_INTENTS = 5;
    private static boolean enableNotifyConnHeadset = false;
    public static ArrayList<Integer> notificationsDisplayed = new ArrayList<>();


    public static boolean isEnableNotifyConnHeadset() {
        return enableNotifyConnHeadset;

    }

    public static void setEnableNotifyConnHeadset(boolean notifyConnHeadset) {
        GlobalInfo.enableNotifyConnHeadset = notifyConnHeadset;
    }

    public static ArrayList<Integer> getNotificationsDisplayed() {
        return notificationsDisplayed;
    }

}
