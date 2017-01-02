package looklook.lyb.com.mylooklook.api;


import looklook.lyb.com.mylooklook.bean.news.NewsList;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 10400 on 2017/1/1.
 */

public interface TopNews {
    @GET("http://c.m.163.com/nc/article/headline/T1348647909107/{id}-20.html")
    Observable<NewsList> getNews(@Path("id") int id);
    @GET("http://c.m.163.com/nc/article/{id}/full.html")
    Observable<String> getNewsDetail(@Path("id") String id);
}
