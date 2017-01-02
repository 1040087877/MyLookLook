package looklook.lyb.com.mylooklook.presenter.implPresenter;

import looklook.lyb.com.mylooklook.api.ApiManage;
import looklook.lyb.com.mylooklook.bean.news.NewsList;
import looklook.lyb.com.mylooklook.presenter.INewTopPresenter;
import looklook.lyb.com.mylooklook.presenter.implView.ITopNewsFragment;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 10400 on 2017/1/1.
 */

public class TopNewsPrensenterImpl extends BasePresenterlmpl implements INewTopPresenter{

    ITopNewsFragment mITopNewsFragment;

    public TopNewsPrensenterImpl(ITopNewsFragment ITopNewsFragment) {
        mITopNewsFragment = ITopNewsFragment;
    }

    @Override
    public void getNewsList(int t) {
        mITopNewsFragment.showProgressDialog();
        Subscription subscription= ApiManage.getInstence().getTopNewsService()
                .getNews(t)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsList>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mITopNewsFragment.hidProgressDialog();
                        mITopNewsFragment.showError(e.toString());
                    }

                    @Override
                    public void onNext(NewsList newsList) {
                        mITopNewsFragment.hidProgressDialog();
                        mITopNewsFragment.upListItem(newsList);
                    }
                });
        addSubscription(subscription);

    }
}
