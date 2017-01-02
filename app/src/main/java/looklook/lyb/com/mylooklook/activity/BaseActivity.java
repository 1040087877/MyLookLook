package looklook.lyb.com.mylooklook.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import looklook.lyb.com.mylooklook.MyApplication;
import looklook.lyb.com.mylooklook.util.UIUtils;

/**
 * Created by 10400 on 2016/12/29.
 */
public class BaseActivity extends AppCompatActivity{
    public static BaseActivity activity;
    protected final String TAG =getClass().getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;
        ((MyApplication)UIUtils.getContext()).addActivity(this);
        init();
    }

    private void init() {
        initData();
        initEvents();
    }
    /***
     * 初始化事件（监听事件等事件绑定）
     */
    protected void initEvents() {

    }
    /**
     * 绑定数据
     */
    protected void initData() {

    }

    /**
     * activity退出时将activity移出栈
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MyApplication)UIUtils.getContext()).removeActivity(this);
    }
}
