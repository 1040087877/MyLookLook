package looklook.lyb.com.mylooklook.presenter.implPresenter;

import looklook.lyb.com.mylooklook.presenter.implView.BasePresenter;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by 10400 on 2016/12/29.
 */

public class BasePresenterlmpl implements BasePresenter {
    private CompositeSubscription mCompositeSubscription;
    protected void addSubscription(Subscription s){
        if(this.mCompositeSubscription==null){
            this.mCompositeSubscription=new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }


    //解绑
    @Override
    public void unsubcrible() {
        if(this.mCompositeSubscription!=null){
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
