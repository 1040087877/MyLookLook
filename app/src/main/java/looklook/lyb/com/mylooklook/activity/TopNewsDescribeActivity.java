package looklook.lyb.com.mylooklook.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import looklook.lyb.com.mylooklook.R;
import looklook.lyb.com.mylooklook.bean.news.NewsDetailBean;
import looklook.lyb.com.mylooklook.presenter.implPresenter.TopNewsDesPresenterImpl;
import looklook.lyb.com.mylooklook.presenter.implView.ITopNewsDesFragment;
import looklook.lyb.com.mylooklook.util.ColorUtils;
import looklook.lyb.com.mylooklook.util.DensityUtil;
import looklook.lyb.com.mylooklook.util.GlideUtils;
import looklook.lyb.com.mylooklook.widget.ElasticDragDismissFrameLayout;
import looklook.lyb.com.mylooklook.widget.ParallaxScrimageView;
import looklook.lyb.com.mylooklook.widget.TranslateYTextView;

public class TopNewsDescribeActivity extends AppCompatActivity implements ITopNewsDesFragment{
    private static final float SCRIM_ADJUSTMENT = 0.075f;
    int[] mDeviceInfo;
    int width;
    int heigh;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.htNewsContent)
    HtmlTextView mHtNewsContent;
    @BindView(R.id.shot)
    ParallaxScrimageView mShot;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.draggable_frame)
    ElasticDragDismissFrameLayout mDraggableFrame;
    @BindView(R.id.nest)
    NestedScrollView mNest;
    @BindView(R.id.title)
    TranslateYTextView mTextView;
    private String id;
    private String title;
    private String mImageUrl;
    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;
    private TopNewsDesPresenterImpl mTopNewsDesPresenter;
    private NestedScrollView.OnScrollChangeListener scrollListener;
    private Transition.TransitionListener mReturnHomeListener;
    private Transition.TransitionListener mEnterTrasitionListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topnews_describe);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mDeviceInfo=DensityUtil.getDeviceInfo(this);
        width=mDeviceInfo[0];
        heigh=width*3/4;
        initData();
        initView();
        getData();
        chromeFader=new ElasticDragDismissFrameLayout.SystemChromeFader(this);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
//            getWindow().getSharedElementReturnTransition()
//                    .addListener(mReturnHomeListener);
//            getWindow().getSharedElementEnterTransition()
//                    .addListener(mEnterTrasitionListener);
        }
    }

    private void getData() {
        //获取新闻信息
        mTopNewsDesPresenter.getDescrible(id);
    }

    private void initView() {
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNest.smoothScrollTo(0,0);
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandImageAndFinish();
            }
        });
    }

    private void expandImageAndFinish() {
        if(mShot.getOffset()!=0f){
            Animator expandImage= ObjectAnimator.ofFloat(
                    mShot,ParallaxScrimageView.OFFSET,0f
            );
            expandImage.setDuration(280);
            expandImage.setInterpolator(new AccelerateInterpolator());
            expandImage.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    } else {
                        finish();
                    }
                }
            });
            expandImage.start();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
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

    private void initData() {
        id = getIntent().getStringExtra("docid");
        title = getIntent().getStringExtra("title");
        mTextView.setText(title);
        mImageUrl = getIntent().getStringExtra("image");
        scrollListener=new NestedScrollView.OnScrollChangeListener(){
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //移动图片和文字
                if(oldScrollY<168){
                    mShot.setOffset(-oldScrollY);
                    mTextView.setOffset(-oldScrollY);
                }
                Log.e("TAG", "onScrollChange: oldScrollY"+ oldScrollY+" [scrollX]+"+scrollX);
            }
        };
        Glide.with(this)
                .load(mImageUrl)
                .override(width,heigh)
                .listener(glideLoadListener)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mShot);

        mTopNewsDesPresenter=new TopNewsDesPresenterImpl(this);
        mNest.setOnScrollChangeListener(scrollListener);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
        }
    }


    private RequestListener glideLoadListener=new RequestListener<String,GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            final Bitmap bitmap=GlideUtils.getBitmap(resource);
            int twentyFourDip= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,24,TopNewsDescribeActivity.this.getResources().getDisplayMetrics());

            //获取图片的颜色
            Palette.from(bitmap)
                    .maximumColorCount(3)
                    .clearFilters()
                    .setRegion(0,0,bitmap.getWidth()-1,twentyFourDip)
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            boolean isDark;
//                            int lightness=ColorUtils.isDark(palette);
//                            if(lightness==ColorUtils.LIGHTNESS_UNKNOWN){
//                                isDark=ColorUtils.isDark(bitmap,bitmap.getWidth()/2,0);
//                            }else {
//                                isDark = lightness == ColorUtils.IS_DARK;
//                            }
                            //获取状态栏的颜色
                            int statusBarColor=getWindow().getStatusBarColor();
                            //获取图片的颜色值
                            final Palette.Swatch topColor = ColorUtils.getMostPopulousSwatch(palette);
                            Log.e("TAG", "statusBarColor: "+statusBarColor+"   topColor"+topColor );
                            getWindow().setStatusBarColor(topColor.getRgb());
//                            if(topColor!=null && (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)){
//
//                            }
                        }
                    });

//            final Palette.Swatch topColor =

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if(sta)
//            }
            return false;
        }
    };

    @Override
    public void upListItem(NewsDetailBean newsList) {
        mProgress.setVisibility(View.INVISIBLE);
        mHtNewsContent.setHtmlFromString(newsList.getBody(),
                new HtmlTextView.LocalImageGetter());
    }

    @Override
    public void showProgressDialog() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidProgressDialog() {
        mProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        Snackbar.make(mDraggableFrame, getString(R.string.snack_infor), Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        }).show();
    }


}
