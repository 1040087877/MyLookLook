package looklook.lyb.com.mylooklook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import looklook.lyb.com.mylooklook.R;
import looklook.lyb.com.mylooklook.activity.ZhihuDescribeActivitiy;
import looklook.lyb.com.mylooklook.bean.zhihu.ZhihuDailyItem;
import looklook.lyb.com.mylooklook.config.Config;
import looklook.lyb.com.mylooklook.util.DBUtils;
import looklook.lyb.com.mylooklook.util.DensityUtil;
import looklook.lyb.com.mylooklook.util.DribbbleTarget;
import looklook.lyb.com.mylooklook.widget.BadgedFourThreeImageView;

/**
 * Created by 10400 on 2016/12/30.
 */

public class ZhihuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_LOADING_MORE = -1;
    private static final int NOMAL_ITEM = 1;
    boolean showLoadingMore;
    float width;
    int widthPx;
    int heighPx;
    ArrayList<ZhihuDailyItem> zhihuDailyItems=new ArrayList<>();
    private Context mContext;
    private String mImageUrl;

    public ZhihuAdapter(Context context) {
        mContext = context;
        width=mContext.getResources().getDimension(R.dimen.image_width);
        widthPx= DensityUtil.dip2px(mContext,width);
        heighPx=widthPx*3/4;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case NOMAL_ITEM:
                return new ZhihuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.zhihu_layout_item,parent,false));
            case TYPE_LOADING_MORE:
                return new LoadingMoreHolder(LayoutInflater.from(mContext).inflate(R.layout.infinite_loading,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch(type){
            case NOMAL_ITEM:
                bindViewHolderNormal((ZhihuViewHolder)holder,position);
                break;
            case TYPE_LOADING_MORE:
                bindLoadingViewHold((LoadingMoreHolder)holder,position);
                break;

            default:

            break;
        }
    }

    private void bindLoadingViewHold(LoadingMoreHolder holder, int position) {
        holder.progressBar.setVisibility(showLoadingMore==true?View.VISIBLE:View.INVISIBLE);
    }

    private void bindViewHolderNormal(final ZhihuViewHolder holder, int position) {
        final ZhihuDailyItem zhihuDailyItem = zhihuDailyItems.get(holder.getAdapterPosition());
        if(DBUtils.getDB(mContext).isRead(Config.ZHIHU,zhihuDailyItem.getId(),1)){
            holder.mTextView.setTextColor(Color.GRAY);
        }else {
            holder.mTextView.setTextColor(Color.BLACK);
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDescribeActivity(holder,zhihuDailyItem);
            }
        });
        holder.mTextView.setText(zhihuDailyItem.getTitle());
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDescribeActivity(holder,zhihuDailyItem);
            }
        });
        Glide.with(mContext)
                .load(zhihuDailyItems.get(position).getImages()[0])
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if(!zhihuDailyItem.hasFadeIn){
                            holder.imageView.setHasTransientState(true);
                        }
                        return false;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.SOURCE)
        .centerCrop().override(widthPx,heighPx)
        .into(new DribbbleTarget(holder.imageView,false));

    }

    private void goDescribeActivity(ZhihuViewHolder holder, ZhihuDailyItem zhihuDailyItem) {
        DBUtils.getDB(mContext).insertHasRead(Config.ZHIHU,zhihuDailyItem.getId(),1);
        holder.mTextView.setTextColor(Color.GRAY);

        Intent intent=new Intent(mContext, ZhihuDescribeActivitiy.class);
        intent.putExtra("id",zhihuDailyItem.getId());
        intent.putExtra("title",zhihuDailyItem.getTitle());
        intent.putExtra("image",zhihuDailyItem.getImages()[0]);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return zhihuDailyItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position<getDataItemCount() && getDataItemCount()>0){
            return NOMAL_ITEM;
        }
        return TYPE_LOADING_MORE;
    }

    private int getDataItemCount() {
        return zhihuDailyItems.size();
    }

    public void addItems(ArrayList<ZhihuDailyItem> list){
        int n = list.size();
        zhihuDailyItems.addAll(list);
        notifyDataSetChanged();
    }

    public void loadingfinish() {
        if(!showLoadingMore)return;
        int loadingPos=getLoadinggMoreItemPositin();
        showLoadingMore=false;
        notifyItemRemoved(loadingPos);
    }

    public void clearData(){
        zhihuDailyItems.clear();
        notifyDataSetChanged();
    }

    private int getLoadinggMoreItemPositin(){
        return showLoadingMore?getItemCount()-1:RecyclerView.NO_POSITION;
    }

    public void loadingStart() {
        if(showLoadingMore)return;
        showLoadingMore=true;
        notifyItemInserted(getLoadinggMoreItemPositin());
    }

    public static class LoadingMoreHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;
        public LoadingMoreHolder(View itemView) {
            super(itemView);
            progressBar= (ProgressBar) itemView;
        }
    }

    class ZhihuViewHolder extends RecyclerView.ViewHolder{
        final TextView mTextView;
        final LinearLayout mLinearLayout;
        BadgedFourThreeImageView imageView;
        public ZhihuViewHolder(View itemView) {
            super(itemView);
            imageView = (BadgedFourThreeImageView) itemView.findViewById(R.id.item_image_id);
            mTextView= (TextView) itemView.findViewById(R.id.item_text_id);
            mLinearLayout= (LinearLayout) itemView.findViewById(R.id.zhihu_item_layout);

        }
    }
}
