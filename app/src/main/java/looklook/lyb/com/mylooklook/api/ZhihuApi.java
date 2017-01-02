package looklook.lyb.com.mylooklook.api;

import looklook.lyb.com.mylooklook.bean.image.ImageResponse;
import looklook.lyb.com.mylooklook.bean.zhihu.ZhihuDaily;
import looklook.lyb.com.mylooklook.bean.zhihu.ZhihuStory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 10400 on 2016/12/29.
 */

public interface ZhihuApi {
    //最新消息
    @GET("/api/4/news/latest")
    Observable<ZhihuDaily> getLastDaily();

    //过往消息
    @GET("/api/4/news/before/{date}")
    Observable<ZhihuDaily> getTheDaily(@Path("date")String date);
    //消息内容获取与离线下载
    @GET("/api/4/news/{id}")
    Observable<ZhihuStory> getZhihuStory(@Path("id")String id);
    //图片
    @GET("http://lab.zuimeia.com/wallpaper/category/1/?page_size=1")
    Observable<ImageResponse> getImage();
}
