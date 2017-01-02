package looklook.lyb.com.mylooklook.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import looklook.lyb.com.mylooklook.R;
import looklook.lyb.com.mylooklook.bean.zhihu.ZhihuStory;
import looklook.lyb.com.mylooklook.config.Config;
import looklook.lyb.com.mylooklook.presenter.IZhihuStoryPresenter;
import looklook.lyb.com.mylooklook.presenter.implPresenter.ZhihuStoryPresenterImpl;
import looklook.lyb.com.mylooklook.presenter.implView.IZhihuStory;
import looklook.lyb.com.mylooklook.util.DensityUtil;
import looklook.lyb.com.mylooklook.util.WebUtil;
import looklook.lyb.com.mylooklook.widget.ElasticDragDismissFrameLayout;
import looklook.lyb.com.mylooklook.widget.ParallaxScrimageView;
import looklook.lyb.com.mylooklook.widget.TranslateYTextView;

/**
 * Created by 10400 on 2017/1/1.
 */

public class ZhihuDescribeActivitiy extends AppCompatActivity implements IZhihuStory{
    @BindView(R.id.shot)
    ParallaxScrimageView mShot;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.wv_zhihu)
    WebView wvZhihu;
    @BindView(R.id.nest)
    NestedScrollView mNest;
    @BindView(R.id.title)
    TranslateYTextView mTranslateYTextView;
    private NestedScrollView.OnScrollChangeListener scrollListener;

    boolean isEmpty;
    String mBody;
    String[] scc;
    String mImageUrl;

    @BindView(R.id.draggable_frame)
    ElasticDragDismissFrameLayout mDraggableFrame;

    int[] mDeviceInfo;
    int width;
    int heigh;

    private String id;
    private String title;
    private String url;
    private IZhihuStoryPresenter mIZhihuStoryPresenter;
    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;
    private Handler mHandler=new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhihudescribe);
        ButterKnife.bind(this);
        mDeviceInfo = DensityUtil.getDeviceInfo(this);
        width = mDeviceInfo[0];
        heigh = width * 3 / 4;
        setSupportActionBar(mToolbar);
        initlistenr();
        initData();
        initView();
        getData();
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this);
    }

    private void initView() {
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNest.smoothScrollTo(0,0);
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandImageAndFinish();
            }
        });
        mTranslateYTextView.setText(title);
        WebSettings settings = wvZhihu.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        //settings.setUseWideViewPort(true);造成文字太小
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath() + "/webViewCache");
        settings.setAppCacheEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wvZhihu.setWebChromeClient(new WebChromeClient());
    }

    private void expandImageAndFinish() {
        if (mShot.getOffset() != 0f) {
            Animator expandImage = ObjectAnimator.ofFloat(mShot, ParallaxScrimageView.OFFSET,
                    0f);
            expandImage.setDuration(80);
            expandImage.setInterpolator(new AccelerateInterpolator());
            expandImage.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    }else {
                        finish();
                    }
                }
            });
            expandImage.start();
        } else {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            }else {
                finish();
            }
        }
    }

    private void initlistenr() {
        scrollListener = new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY < 168) {
                    mShot.setOffset(-oldScrollY);
                    mTranslateYTextView.setOffset(-oldScrollY);
                }

            }
        };
    }
    protected void initData() {
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        mImageUrl = getIntent().getStringExtra("image");
        mIZhihuStoryPresenter = new ZhihuStoryPresenterImpl(this);
        mNest.setOnScrollChangeListener(scrollListener);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mDraggableFrame.addListener(chromeFader);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mDraggableFrame.removeListener(chromeFader);
    }
    @Override
    public void showError(String error) {
        Log.e("TAG", "showError: "+error);
        Snackbar.make(wvZhihu, getString(R.string.snack_infor), Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        }).show();
    }

    private void getData() {
        mIZhihuStoryPresenter.getZhihuStory(id);
    }

    @Override
    public void showZhihuStory(ZhihuStory zhihuStory) {
        Glide.with(this)
                .load(zhihuStory.getImage())
                .centerCrop()
                .override(width,heigh)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mShot);
        url = zhihuStory.getShare_url();
        isEmpty= TextUtils.isEmpty(zhihuStory.getBody());
        mBody=zhihuStory.getBody();
        scc=zhihuStory.getCss();
        if (isEmpty) {
            wvZhihu.loadUrl(url);
        } else {
            String data = WebUtil.buildHtmlWithCss(mBody, scc, Config.isNight);
            //错误代码，不能显示
//            wvZhihu.loadDataWithBaseURL(WebUtil.BASE_URL, data, WebUtil.MIME_TYPE, WebUtil.ENCODING, WebUtil.FAIL_URL);
            wvZhihu.loadDataWithBaseURL(null, data, WebUtil.MIME_TYPE, WebUtil.ENCODING, null);
            //另外一种显示方法
//            try {
//                wvZhihu.loadData(URLEncoder.encode(data,"utf-8"),WebUtil.MIME_TYPE,WebUtil.ENCODING);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
        }


    }

    @OnClick(R.id.shot)
    public void onClick(){
        mNest.smoothScrollTo(0,0);
    }
}
