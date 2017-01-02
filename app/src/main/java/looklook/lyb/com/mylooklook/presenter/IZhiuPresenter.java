package looklook.lyb.com.mylooklook.presenter;

/**
 * Created by 10400 on 2016/12/30.
 */

import looklook.lyb.com.mylooklook.presenter.implView.BasePresenter;

public interface IZhiuPresenter extends BasePresenter{
    void getLastZhihuNews();
    void getTheDaily(String date);
    void getLastFromCache();
}
