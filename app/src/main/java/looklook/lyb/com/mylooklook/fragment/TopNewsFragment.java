package looklook.lyb.com.mylooklook.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import looklook.lyb.com.mylooklook.R;
import looklook.lyb.com.mylooklook.adapter.TopNewsAdapter;
import looklook.lyb.com.mylooklook.bean.news.NewsList;
import looklook.lyb.com.mylooklook.presenter.implPresenter.TopNewsPrensenterImpl;
import looklook.lyb.com.mylooklook.presenter.implView.ITopNewsFragment;
import looklook.lyb.com.mylooklook.view.GridItemDividerDecoration;
import looklook.lyb.com.mylooklook.widget.WrapContentLinearLayoutManager;

/**
 * Created by 10400 on 2017/1/1.
 */

public class TopNewsFragment extends BaseFragment implements ITopNewsFragment{

    @BindView(R.id.recycle_topnews)
    RecyclerView recycle;
    @BindView(R.id.prograss)
    ProgressBar progress;
    private TopNewsPrensenterImpl mTopNewsPrensenterImpl;
    boolean loading;
    boolean connected = true;
    TopNewsAdapter mTopNewsAdapter;

    LinearLayoutManager mLinearLayoutManager;
    RecyclerView.OnScrollListener loadingMoreListener;

    int currentIndex;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.topnews_fragment_layout,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initalDate();
        initialView();
    }

    private void initialView() {
        initialListtener();
        mLinearLayoutManager=new WrapContentLinearLayoutManager(getContext());
        recycle.setLayoutManager(mLinearLayoutManager);
        recycle.setHasFixedSize(true);
        recycle.addItemDecoration(new GridItemDividerDecoration(getContext(),R.dimen.divider_height,R.color.divider));
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(mTopNewsAdapter);
        recycle.addOnScrollListener(loadingMoreListener);
        if(connected){
            loadDate();
        }

    }

    private void initialListtener() {
        loadingMoreListener=new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    int visibleItemCount=mLinearLayoutManager.getChildCount();
                    int totalItemCount=mLinearLayoutManager.getItemCount();
                    int pastVisiblesItems=mLinearLayoutManager.findFirstVisibleItemPosition();
                    if(!loading && (visibleItemCount+pastVisiblesItems)>=totalItemCount){
                        loading=true;
                        loadMoreDate();
                    }
                }
            }
        };
    }

    private void loadMoreDate() {
        mTopNewsAdapter.loadingStart();
        currentIndex+=20;
        mTopNewsPrensenterImpl.getNewsList(currentIndex);
    }

    private void loadDate() {
        if(mTopNewsAdapter.getItemCount()>0){
            mTopNewsAdapter.clearData();
        }
        currentIndex=0;
        mTopNewsPrensenterImpl.getNewsList(currentIndex);
    }

    private void initalDate() {
        mTopNewsPrensenterImpl = new TopNewsPrensenterImpl(this);
        mTopNewsAdapter = new TopNewsAdapter(getContext());
    }


    @Override
    public void upListItem(NewsList newsList) {
        loading=false;
        progress.setVisibility(View.INVISIBLE);
        mTopNewsAdapter.addItems(newsList.getNewsList());
    }

    @Override
    public void showProgressDialog() {
        if(currentIndex==0){
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidProgressDialog() {
        progress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        if (recycle != null) {
            Snackbar.make(recycle, "请检查网络", Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTopNewsPrensenterImpl.getNewsList(currentIndex);
                }
            }).show();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTopNewsPrensenterImpl.unsubcrible();
    }
}
