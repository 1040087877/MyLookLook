package looklook.lyb.com.mylooklook;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import looklook.lyb.com.mylooklook.activity.BaseActivity;

/**
 * Created by 10400 on 2016/12/29.
 */

public class MyApplication extends Application{
    public final static String TAG = "BaseApplication";
    public final static boolean DEBUG = true;
    private static MyApplication myApplication;
    private static int mainTid;

    private static List<BaseActivity> activities;

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static Context getApplication( ){
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        activities=new LinkedList<>();
        mainTid=android.os.Process.myTid();
    }

    /**
     * 获取application
     *
     * @return
     */
    public static Application getContext( ){
        return myApplication;
    }

    /**
     * 添加一个Activity
     *
     * @param activity
     */
    public void addActivity(BaseActivity activity){
        activities.add(activity);
    }
    /**
     * 结束一个Activity
     *
     * @param activity
     */
    public void removeActivity(BaseActivity activity){
        activities.remove(activity);
    }

    /**
     * 结束当前所有Activity
     */
    public static void clearActivities(){
        ListIterator<BaseActivity> iterator = activities.listIterator();
        BaseActivity activity;
        while (iterator.hasNext()){
            activity = iterator.next();
            if(activity!=null){
                activity.finish();
            }
        }
    }

    /**
     * 结束当前所有Activity
     */
    public static void quiteApplication(){
        clearActivities();
        System.exit(0);
    }
}
