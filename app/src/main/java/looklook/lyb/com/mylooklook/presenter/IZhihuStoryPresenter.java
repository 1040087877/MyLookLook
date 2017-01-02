package looklook.lyb.com.mylooklook.presenter;


import looklook.lyb.com.mylooklook.presenter.implView.BasePresenter;

public interface IZhihuStoryPresenter extends BasePresenter {
    void getZhihuStory(String id);

    void getGuokrArticle(String id);
}
