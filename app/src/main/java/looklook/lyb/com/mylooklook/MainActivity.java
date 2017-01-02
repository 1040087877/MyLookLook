package looklook.lyb.com.mylooklook;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import looklook.lyb.com.mylooklook.activity.AboutActivity;
import looklook.lyb.com.mylooklook.activity.BaseActivity;
import looklook.lyb.com.mylooklook.fragment.TopNewsFragment;
import looklook.lyb.com.mylooklook.fragment.ZhihuFragment;
import looklook.lyb.com.mylooklook.presenter.implPresenter.MainPresenterImpl;
import looklook.lyb.com.mylooklook.presenter.implView.IMain;
import looklook.lyb.com.mylooklook.util.AnimUtils;
import looklook.lyb.com.mylooklook.util.ViewUtils;

public class MainActivity extends BaseActivity implements IMain {
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;

    private MainPresenterImpl mIMainPresenterImpl;
    MenuItem currentMenuItem;
    Fragment currentFragment;
    SimpleArrayMap<Integer, String> mTitleArryMap = new SimpleArrayMap<>();
    private SwitchCompat mThemeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mIMainPresenterImpl = new MainPresenterImpl(this, this);
        mIMainPresenterImpl.getBackground();
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        //设置toolbar动画
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            animateToolbar();
        }

        addfragmentsAndTitle();
        currentMenuItem=navView.getMenu().findItem(R.id.zhihuitem);
        if(savedInstanceState==null){
            if(currentMenuItem!=null){
                currentMenuItem.setChecked(true);
                Fragment fragment=getFragmentById(currentMenuItem.getItemId());
                String title=mTitleArryMap.get(currentMenuItem.getItemId());
                if(fragment!=null){
                    switchFragment(fragment,title);
                }
            }
        }
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(currentMenuItem!=item && currentMenuItem!=null){
                    //取消上一次的记录
                    currentMenuItem.setChecked(false);
                    int id=item.getItemId();
                    currentMenuItem=item;
                    currentMenuItem.setChecked(true);
                    switchFragment(getFragmentById(currentMenuItem.getItemId()),
                            mTitleArryMap.get(currentMenuItem.getItemId()));
                }
                drawer.closeDrawer(GravityCompat.START,true);
                return true;
            }
        });

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            drawer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    //修改Toolbar 位置
                    ViewGroup.MarginLayoutParams lpToolbar=
                            (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                    lpToolbar.topMargin+=insets.getSystemWindowInsetTop();
                    lpToolbar.rightMargin+=insets.getStableInsetRight();
                    toolbar.setLayoutParams(lpToolbar);
                    //修改内容的位置
                    mFragmentContainer.setPadding(mFragmentContainer.getPaddingLeft(),insets.getSystemWindowInsetTop()+ ViewUtils.getActionBarSize
                            (MainActivity.this),mFragmentContainer.getPaddingRight()+insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());

                    View statusBarBackground=findViewById(R.id.status_bar_background);

                    drawer.setOnApplyWindowInsetsListener(null);
                    return insets.consumeSystemWindowInsets();
                }
            });
        }

        MenuItem item=navView.getMenu().findItem(R.id.nav_theme);
        View view = MenuItemCompat.getActionView(item);
        mThemeSwitch = (SwitchCompat) view.findViewById(R.id.view_switch);
        mThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mThemeSwitch.setChecked(isChecked);
                if(isChecked){
                    setThemeColor(Color.GREEN);
                }else {
                    setThemeColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });
    }

    private void setThemeColor(int color) {
        getWindow().setStatusBarColor(color);
        toolbar.setBackgroundColor(color);
    }

    private void animateToolbar() {
        View t=toolbar.getChildAt(0);
        if(t!=null && t instanceof TextView){
            TextView title=(TextView)t;
            title.setAlpha(0f);
            title.setScaleX(0.8f);
            title.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .setStartDelay(500)
                    .setDuration(900)
                    .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this)).start();
        }
    }

    private void switchFragment(Fragment fragment, String title) {
        if(fragment!=null){
            if(currentFragment==null || !currentFragment.getClass().getName().equals(fragment.getClass().getName()) )
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment)
                        .commit();
                currentFragment=fragment;
            }
        }
    }

    private void addfragmentsAndTitle(){
        mTitleArryMap.put(R.id.zhihu_item_layout,"知乎日报");
        mTitleArryMap.put(R.id.topnewsitem,"网易头条");
        mTitleArryMap.put(R.id.meiziitem,"每日看看");
    }


    @Override
    public void getPic() {
        View headerLayout = navView.getHeaderView(0);
        LinearLayout llImage= (LinearLayout) headerLayout.findViewById(R.id.side_image);
        if(new File(getFilesDir().getPath()+"/bg.jpg").exists()){
            BitmapDrawable bitmapDrawable= new BitmapDrawable(getResources(),getFilesDir().getPath()+"/bg.jpg");
            llImage.setBackground(bitmapDrawable);
        }
    }



    private Fragment getFragmentById(int id){
        Fragment fragment=null;
        switch(id){
            case R.id.zhihuitem:
                fragment=new ZhihuFragment();
                break;
            case R.id.topnewsitem:
                fragment=new TopNewsFragment();
                break;
            case R.id.meiziitem:

                break;
            default:

            break;
        }
        return fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick=new Toolbar.OnMenuItemClickListener(){

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.menu_open:
                    drawer.openDrawer(GravityCompat.START);
                    break;
                case R.id.menu_about:
                    goAboutActivity();
                    break;
            }
            return true;
        }
    };

    private void goAboutActivity() {
        Intent intent=new Intent(this,AboutActivity.class);
        this.startActivity(intent);
    }
}
