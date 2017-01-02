package looklook.lyb.com.mylooklook.api;


import java.io.File;
import java.io.IOException;

import looklook.lyb.com.mylooklook.MyApplication;
import looklook.lyb.com.mylooklook.util.NetWorkUtil;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 10400 on 2016/12/29.
 */

public class ApiManage {

    //意图过滤器
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponnse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(MyApplication.getContext())) {
                int maxAge = 60;//在线缓存在1分钟内获取
                return originalResponnse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + maxAge).build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; //离线时缓存四周
                return originalResponnse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public,only-if-cached, max-stale=" + maxStale).build();
            }
        }
    };

    public static ApiManage apiManage;
    public TopNews topNews;
    private static File httpCacheDirectory = new File(MyApplication.getContext().getCacheDir(), "zhihuCache");
    private static int cacheSize = 10 * 1024 * 1024;// 10 MiB

    private static Cache cache = new Cache(httpCacheDirectory,
            cacheSize);

    private static OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .cache(cache)
            .build();
    public ZhihuApi zhihuApi;
    public Object zhihuMonitor=new Object();

    public static ApiManage getInstence(){
        if(apiManage==null){
            synchronized (ApiManage.class){
                if(apiManage==null){
                    apiManage=new ApiManage();
                }
            }
        }
        return apiManage;
    }

    public ZhihuApi getZhihuApiService(){
        if(zhihuApi==null){
            synchronized (zhihuMonitor){
                if(zhihuApi==null){
                    zhihuApi=new Retrofit.Builder()
                            .baseUrl("http://news-at.zhihu.com")
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(ZhihuApi.class);
                }
            }
        }
        return zhihuApi;
    }

    public TopNews getTopNewsService(){
        if(topNews==null){
            synchronized (zhihuMonitor){
                if(topNews==null){
                    topNews=new Retrofit.Builder()
                            .baseUrl("http://c.m.163.com")
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(TopNews.class);
                }
            }
        }
        return topNews;
    }

}
