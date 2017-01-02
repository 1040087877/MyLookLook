package looklook.lyb.com.mylooklook.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by 10400 on 2016/12/30.
 */

public class GridItemDividerDecoration extends RecyclerView.ItemDecoration{
    private final float dividerSize;
    private final Paint paint;

    public GridItemDividerDecoration(float dividerSize, @ColorInt int dividerColor) {
        this.dividerSize = dividerSize;
        paint =new Paint();
        paint.setColor(dividerColor);
        paint.setStyle(Paint.Style.FILL);
    }

    public GridItemDividerDecoration(@NonNull Context context, @DimenRes int dividerSizeResid, @ColorRes int dividerColorResId) {
        this(context.getResources().getDimensionPixelSize(dividerSizeResid), ContextCompat.getColor(context,dividerColorResId));
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        if(parent.isAnimating()){
            return;
        }
        int childConut=parent.getChildCount();
        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        for (int i=0;i<childConut;i++){
            View child = parent.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(child);
            if(requiresDivider(viewHolder)){
                int right = lm.getDecoratedRight(child);
                int bottom=lm.getDecoratedBottom(child);
                canvas.drawRect(lm.getDecoratedLeft(child),
                        bottom-dividerSize,right,bottom,paint);
                canvas.drawRect(right-dividerSize,lm.getDecoratedTop(child),right,bottom-dividerSize,paint);

            }
        }

    }

    private boolean requiresDivider(RecyclerView.ViewHolder viewHolder) {
            return true;
    }
}
