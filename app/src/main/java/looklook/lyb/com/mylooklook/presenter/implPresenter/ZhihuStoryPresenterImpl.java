package looklook.lyb.com.mylooklook.presenter.implPresenter;


import looklook.lyb.com.mylooklook.api.ApiManage;
import looklook.lyb.com.mylooklook.bean.zhihu.ZhihuStory;
import looklook.lyb.com.mylooklook.presenter.IZhihuStoryPresenter;
import looklook.lyb.com.mylooklook.presenter.implView.IZhihuStory;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class ZhihuStoryPresenterImpl extends BasePresenterlmpl implements IZhihuStoryPresenter {

    private IZhihuStory mIZhihuStory;

    public ZhihuStoryPresenterImpl(IZhihuStory zhihuStory) {
        if (zhihuStory == null)
            throw new IllegalArgumentException("zhihuStory must not be null");
        mIZhihuStory = zhihuStory;
    }

    @Override
    public void getZhihuStory(String id) {
        Subscription s = ApiManage.getInstence().getZhihuApiService().getZhihuStory(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuStory>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mIZhihuStory.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuStory zhihuStory) {
                        mIZhihuStory.showZhihuStory(zhihuStory);
                    }
                });
        addSubscription(s);
    }

    @Override
    public void getGuokrArticle(String id) {

    }

//    @Override
//    public void getGuokrArticle(String id) {
//        Subscription s = ApiManage.getInstence().getZhihuApiService().getGuokrArticle(id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<GuokrArticle>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mIZhihuStory.showError(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(GuokrArticle guokrArticle) {
//                        mIZhihuStory.showGuokrArticle(guokrArticle);
//                    }
//                });
//        addSubscription(s);
//    }
}
