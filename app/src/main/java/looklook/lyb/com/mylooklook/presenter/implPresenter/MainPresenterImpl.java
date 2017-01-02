package looklook.lyb.com.mylooklook.presenter.implPresenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import looklook.lyb.com.mylooklook.api.ApiManage;
import looklook.lyb.com.mylooklook.bean.image.ImageResponse;
import looklook.lyb.com.mylooklook.presenter.IMainPresenter;
import looklook.lyb.com.mylooklook.presenter.implView.IMain;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 10400 on 2016/12/29.
 */

public class MainPresenterImpl extends BasePresenterlmpl implements IMainPresenter {

    private final IMain mImain;
    private final Context mContext;

    public MainPresenterImpl(IMain main, Context context) {
        if (main == null) {
            throw new IllegalArgumentException("main must not be null");
        }
        mImain = main;
        mContext = context;
    }

    @Override
    public void getBackground() {
        ApiManage.getInstence().getZhihuApiService().getImage()
                .subscribeOn(Schedulers.io()).map(new Func1<ImageResponse, Boolean>() {
            @Override
            public Boolean call(ImageResponse imageResponse) {
                if (imageResponse.getData() != null && imageResponse.getData().getImages() != null) {
                    try {
                        URL url = new URL("http://wpstatic.zuimeia.com/" + imageResponse.getData().getImages().get(0).getImageUrl() + "?imageMogr/v2/auto-orient/thumbnail/480x320/quality/100");

                        Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(mContext.getFilesDir().getPath() + "/bg.jpg")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mImain.getPic();
                    }

                    @Override
                    public void onNext(Boolean imageReponse) {
                        mImain.getPic();
                    }
                });
    }
}
