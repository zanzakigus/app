package ipn.mx.app.global;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import ipn.mx.app.R;

public class GlobalInfo {

    public static final int NOTIFICATION_EMOTION_ID = 888;
    public static final int NOTIFICATION_CONN_ID = 999;
    public static final int TIPO_NEGATIVO = 0;
    public static final int TIPO_POSITIVO = 1;
    public static final int TIPO_ALL = -1;
    public static ArrayList<Integer> notificationsDisplayed = new ArrayList<>();
    private static boolean enableNotifyConnHeadset = false;
    private static int clasifyTimeDelay = 60000;
    private static int trainSectionTime = 90;
    private static int trainSectionTimeCounter = 90;
    private static int clasifyTimeDelayCounter = clasifyTimeDelay / 1000;
    public final int TIMEOUT_REQUEST = 2000;
    public final int MAX_INTENTS = 5;

    public static boolean isEnableNotifyConnHeadset() {
        return enableNotifyConnHeadset;

    }

    public static void setEnableNotifyConnHeadset(boolean notifyConnHeadset, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getResources().getString(R.string.shared_key), Context.MODE_PRIVATE);
        GlobalInfo.enableNotifyConnHeadset = notifyConnHeadset;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(context.getResources().getString(R.string.logged_notication_conn_enable), notifyConnHeadset ? "True" : null);
        editor.apply();
    }

    public static void setIniEnableNotifyConnHeadset(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(context.getResources().getString(R.string.shared_key), Context.MODE_PRIVATE);
        GlobalInfo.enableNotifyConnHeadset =
                sharedpreferences.getString(context.getResources().getString(R.string.logged_notication_conn_enable), null) != null;
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

    public static int getTrainSectionTime() {
        return trainSectionTime;
    }

    public static int getTrainSectionTimeCounter() {
        return trainSectionTimeCounter;
    }

    public static void setTrainSectionTimeCounter(int trainSectionTimeCounter) {
        GlobalInfo.trainSectionTimeCounter = trainSectionTimeCounter;
    }

    public static int getClasifyTimeDelayCounter() {
        return clasifyTimeDelayCounter;
    }

    public static void setClasifyTimeDelayCounter(int clasifyTimeDelayCounter) {
        GlobalInfo.clasifyTimeDelayCounter = clasifyTimeDelayCounter;
    }
}
