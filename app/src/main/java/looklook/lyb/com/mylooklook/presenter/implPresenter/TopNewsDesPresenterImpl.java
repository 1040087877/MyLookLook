package looklook.lyb.com.mylooklook.presenter.implPresenter;

import looklook.lyb.com.mylooklook.bean.news.NewsDetailBean;
import looklook.lyb.com.mylooklook.presenter.INewTopDescriblePresenter;
import looklook.lyb.com.mylooklook.presenter.implView.ITopNewsDesFragment;
import looklook.lyb.com.mylooklook.util.NewsJsonUtils;
import looklook.lyb.com.mylooklook.util.OkHttpUtils;
import looklook.lyb.com.mylooklook.util.Urls;

/**
 * Created by 10400 on 2017/1/1.
 */

public class TopNewsDesPresenterImpl extends BasePresenterlmpl
        implements INewTopDescriblePresenter {

    private ITopNewsDesFragment mITopDesNewsFragment;

    public TopNewsDesPresenterImpl(ITopNewsDesFragment topNewsDesFragment) {
        if(topNewsDesFragment==null){
            throw new IllegalArgumentException(" must not be null");
        }
        mITopDesNewsFragment = topNewsDesFragment;
    }

    //拼接字符串
    private String getDetailUrl(String docId) {
        StringBuffer sb=new StringBuffer(Urls.NEW_DETAIL);
        sb.append(docId).append(Urls.END_DETAIL_URL);
        return sb.toString();
    }

    @Override
    public void getDescrible(final String docId) {
        mITopDesNewsFragment.showProgressDialog();
        String url=getDetailUrl(docId);
        OkHttpUtils.ResultCallback<String> loadNewsCallback=new OkHttpUtils.ResultCallback<String>(){

            @Override
            public void onSuccess(String response) {
                NewsDetailBean newsDetailBean=NewsJsonUtils.readJsonNewsDetailBeans(response,docId);
                mITopDesNewsFragment.upListItem(newsDetailBean);
            }

            @Override
            public void onFailure(Exception e) {
                mITopDesNewsFragment.showError(e.toString());
            }
        };
        OkHttpUtils.get(url,loadNewsCallback);
    }
}
