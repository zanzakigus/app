package ipn.mx.app.global;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import ipn.mx.app.R;

public class GlobalInfo {

    public static final int NOTIFICATION_EMOTION_ID = 888;
    public static final int NOTIFICATION_CONN_ID = 999;
    public final int TIMEOUT_REQUEST = 2000;
    public final int MAX_INTENTS = 5;
    private static boolean enableNotifyConnHeadset = false;
    private static int clasifyTimeDelay = 60000;
    private static int clasifyTimeDelayCounter = clasifyTimeDelay/1000;
    public static ArrayList<Integer> notificationsDisplayed = new ArrayList<>();



    public static boolean isEnableNotifyConnHeadset() {
        return enableNotifyConnHeadset;

    }

    public static void setEnableNotifyConnHeadset(boolean notifyConnHeadset, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getResources().getString(R.string.shared_key), Context.MODE_PRIVATE);
        GlobalInfo.enableNotifyConnHeadset = notifyConnHeadset;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(context.getResources().getString(R.string.logged_notication_conn_enable), notifyConnHeadset ? "True":null);
        editor.apply();
    }

    public static void setIniEnableNotifyConnHeadset(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getResources().getString(R.string.shared_key), Context.MODE_PRIVATE);
        GlobalInfo.enableNotifyConnHeadset =
        sharedpreferences.getString(context.getResources().getString(R.string.logged_notication_conn_enable), null)!=null;
    }

    public static ArrayList<Integer> getNotificationsDisplayed() {
        return notificationsDisplayed;
    }

    public static int getClasifyTimeDelay() {
        return clasifyTimeDelay;
    }

    public static void setClasifyTimeDelay(int clasifyTimeDelay) {
        GlobalInfo.clasifyTimeDelay = clasifyTimeDelay;
    }

    public static int getClasifyTimeDelayCounter() {
        return clasifyTimeDelayCounter;
    }

    public static void setClasifyTimeDelayCounter(int clasifyTimeDelayCounter) {
        GlobalInfo.clasifyTimeDelayCounter = clasifyTimeDelayCounter;
    }
}
