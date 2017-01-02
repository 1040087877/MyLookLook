package looklook.lyb.com.mylooklook.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import looklook.lyb.com.mylooklook.R;
import looklook.lyb.com.mylooklook.adapter.ZhihuAdapter;
import looklook.lyb.com.mylooklook.bean.zhihu.ZhihuDaily;
import looklook.lyb.com.mylooklook.presenter.implPresenter.ZhihuPresenterImpl;
import looklook.lyb.com.mylooklook.presenter.implView.IZhihuFragment;
import looklook.lyb.com.mylooklook.view.GridItemDividerDecoration;
import looklook.lyb.com.mylooklook.widget.WrapContentLinearLayoutManager;

/**
 * Created by 10400 on 2016/12/30.
 */

public class ZhihuFragment extends BaseFragment implements IZhihuFragment{
    View view=null;
    private boolean mConnected;
    @BindView(R.id.prograss)
    ProgressBar progress;
    @BindView(R.id.recycle_zhihu)
    RecyclerView recycle;

    TextView noConnectionText;
    boolean monitoringConnectivity;
    private ZhihuPresenterImpl mZhihuPresenter;
    private WrapContentLinearLayoutManager mLinearLayoutManager;
    private ZhihuAdapter mZhihuAdapter;
    RecyclerView.OnScrollListener loadingMoreListener;
    private boolean loading;
    private ConnectivityManager.NetworkCallback connectivityCallback;
    private String currentLoadDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        view=inflater.inflate(R.layout.zhihu_fragment_layout,container,false);
        checkConnectivity(view);
        ButterKnife.bind(this,view);
        return view;
    }

    //检查网络
    private void checkConnectivity(View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        mConnected =  activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if(!mConnected && progress!=null){
            progress.setVisibility(View.INVISIBLE);
            if(noConnectionText==null){
                ViewStub stub_text= (ViewStub) view.findViewById(R.id.stub_no_connection_text);
                noConnectionText= (TextView) stub_text.inflate();
            }

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().addCapability(NetworkCapabilities
                .NET_CAPABILITY_INTERNET).build(),connectivityCallback);
                monitoringConnectivity=true;
            }
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialDate();
        initialView();

    }

    private void initialView() {
        initialListener();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            mLinearLayoutManager = new WrapContentLinearLayoutManager(getContext());
        }else {
            mLinearLayoutManager = (WrapContentLinearLayoutManager) new LinearLayoutManager(getContext());
        }
        recycle.setLayoutManager(mLinearLayoutManager);
        recycle.setHasFixedSize(true);
        recycle.addItemDecoration(new GridItemDividerDecoration(getContext(),R.dimen.divider_height,R.color.divider));
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(mZhihuAdapter);
        recycle.addOnScrollListener(loadingMoreListener);

        if(mConnected){
            loadDate();
        }
    }


    private void initialListener() {
        loadingMoreListener=new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){//向下滚动
                    int visibleItemCount=mLinearLayoutManager.getChildCount();
                    int totalItemCount=mLinearLayoutManager.getItemCount();
                    int pastVisblesItems=mLinearLayoutManager.findFirstVisibleItemPosition();
                    if(!loading && (visibleItemCount+pastVisblesItems)>=totalItemCount){
                        loading=true;
                        loadMoreDate();
                    }
                }
            }
        };

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            connectivityCallback=new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    mConnected=true;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noConnectionText.setVisibility(View.GONE);
                            loadDate();
                        }

                    });
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    mConnected=false;
                }

            };
            Log.e("TAG", "initialListener: "+connectivityCallback );
        }
    }



    private void initialDate() {
        mZhihuPresenter = new ZhihuPresenterImpl(getContext(), this);
        mZhihuAdapter = new ZhihuAdapter(getContext());
    }


    //单纯获取数据,没有加载到局部
    private void loadDate() {
        if(mZhihuAdapter.getItemCount()>0){
            mZhihuAdapter.clearData();
        }
        currentLoadDate="0";
        mZhihuPresenter.getLastZhihuNews();

    }

    //把得到的数据填充到布局
    @Override
    public void updateList(ZhihuDaily zhihuDaily) {
        if(loading){
            loading=false;
            mZhihuAdapter.loadingfinish();
        }
        currentLoadDate=zhihuDaily.getDate();
        mZhihuAdapter.addItems(zhihuDaily.getStories());
        //滑动到底部
        if(!recycle.canScrollVertically(View.SCROLL_INDICATOR_BOTTOM)){
            loadMoreDate();
        }
    }

    private void loadMoreDate() {
        mZhihuAdapter.loadingStart();
        mZhihuPresenter.getTheDaily(currentLoadDate);
    }

    @Override
    public void showProgressDialog() {
        if(progress!=null){
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidProgressDialog() {
        if(progress!=null){
            progress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showError(String error) {
        if(recycle!=null){
            Snackbar.make(recycle,"请检查网络",Snackbar.LENGTH_SHORT).setAction("重新", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentLoadDate.equals("0")){
                        mZhihuPresenter.getLastZhihuNews();
                    }else {
                        mZhihuPresenter.getTheDaily(currentLoadDate);
                    }
                }
            }).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
