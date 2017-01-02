package looklook.lyb.com.mylooklook.util;

import android.content.Context;
import android.content.Intent;

import looklook.lyb.com.mylooklook.MyApplication;
import looklook.lyb.com.mylooklook.activity.BaseActivity;

/**
 * Created by 10400 on 2016/12/29.
 */

public class UIUtils {
    public static Context getContext(){
        return MyApplication.getApplication();
    }

    /**
     * 页面跳转
     */
    public static void startActivity(Intent intent) {
        // 如果不在activity里去打开activity  需要指定任务栈  需要设置标签
        if(BaseActivity.activity==null){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }else {
            BaseActivity.activity.startActivity(intent);
        }
    }

    /**
     * 分享
     *
     * @param context
     * @param content 分享内容
     * @param uri     分享图片uri
     */
    public static void share(Context context,String content,String uri){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        if(uri!=null){
            shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
            shareIntent.setType("image/*");
            //当用户选择短信时使用sms_body取得文字
            shareIntent.putExtra("sms_body",content);
        }else {
            //如果不是图片 则发送文字
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT,content);
        context.startActivity(Intent.createChooser(shareIntent,"分享"));
    }
}
