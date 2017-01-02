package looklook.lyb.com.mylooklook.presenter.implPresenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import looklook.lyb.com.mylooklook.api.ApiManage;
import looklook.lyb.com.mylooklook.bean.zhihu.ZhihuDaily;
import looklook.lyb.com.mylooklook.bean.zhihu.ZhihuDailyItem;
import looklook.lyb.com.mylooklook.config.Config;
import looklook.lyb.com.mylooklook.presenter.IZhiuPresenter;
import looklook.lyb.com.mylooklook.presenter.implView.IZhihuFragment;
import looklook.lyb.com.mylooklook.util.CacheUtil;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 10400 on 2016/12/30.
 */

public class ZhihuPresenterImpl extends BasePresenterlmpl implements IZhiuPresenter {
    private IZhihuFragment mZhihuFragment;
    private CacheUtil mCacheUtil;
    private Gson gson = new Gson();

    public ZhihuPresenterImpl(Context context, IZhihuFragment zhihuFragment) {
        mZhihuFragment = zhihuFragment;
        mCacheUtil = CacheUtil.get(context);
    }


    @Override
    public void getLastZhihuNews() {
        mZhihuFragment.showProgressDialog();
        //最新消息
        Subscription subscription = ApiManage.getInstence().getZhihuApiService().getLastDaily()
                .map(new Func1<ZhihuDaily, ZhihuDaily>() {
                    @Override
                    public ZhihuDaily call(ZhihuDaily zhihuDaily) {
                        //日期
                        String date = zhihuDaily.getDate();
                        //取出每一个新闻
                        for (ZhihuDailyItem zhihuDailyItem : zhihuDaily.getStories()) {
                            //给每个条目设置时间
                            zhihuDailyItem.setDate(date);
                        }
                        return zhihuDaily;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuDaily>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mZhihuFragment.hidProgressDialog();
                        mZhihuFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuDaily zhihuDaily) {
                        //获取到数据
                        mZhihuFragment.hidProgressDialog();
                        mCacheUtil.put(Config.ZHIHU, gson.toJson(zhihuDaily));
                        mZhihuFragment.updateList(zhihuDaily);
                    }
                });
        //绑定
        addSubscription(subscription);
    }

    @Override
    public void getTheDaily(String date) {
        Subscription subscription = ApiManage.getInstence().getZhihuApiService().getTheDaily(date).map(new Func1<ZhihuDaily, ZhihuDaily>() {

            @Override
            public ZhihuDaily call(ZhihuDaily zhihuDaily) {
                String date = zhihuDaily.getDate();
                for (ZhihuDailyItem zhihuDailyItem : zhihuDaily.getStories()) {
                    zhihuDailyItem.setDate(date);
                }
                return zhihuDaily;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuDaily>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mZhihuFragment.hidProgressDialog();
                        mZhihuFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuDaily zhihuDaily) {
                        mZhihuFragment.hidProgressDialog();
                        mZhihuFragment.updateList(zhihuDaily);
                        Log.e("TAG", "onNext: "+ zhihuDaily);
                    }
                });
        addSubscription(subscription);
    }

    //获取缓存数据
    @Override
    public void getLastFromCache() {
        if (mCacheUtil.getAsJSONObject(Config.ZHIHU) != null) {
            ZhihuDaily zhihuDaily = gson.fromJson(mCacheUtil.getAsJSONObject(Config.ZHIHU).toString(), ZhihuDaily.class);
            mZhihuFragment.updateList(zhihuDaily);
        }
    }
}
