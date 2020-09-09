package com.stl.autolineradiogroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

/**
 * project :
 * package : com.stl.autolineradiogroup
 *
 * @author : taolin
 * @date : 2020-08-14
 * desc    : 可换行的RadioGroup，增加RTL（自右向左布局处理） 处理
 */
public class AutoLineRadioGroup extends RadioGroup {
    public AutoLineRadioGroup(Context context) {
        super(context);
    }

    public AutoLineRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取当前布局的宽高信息
        int widthMeasureMode     = MeasureSpec.getMode(widthMeasureSpec);
        int widthMeasureSize     = MeasureSpec.getSize(widthMeasureSpec);
        int heightMeasureMode   = MeasureSpec.getMode(heightMeasureSpec);
        int heightMeasureSize   = MeasureSpec.getSize(heightMeasureSpec);


        measureChildren(widthMeasureSpec,heightMeasureSpec);

        //measure
        int totalWidth  = 0;
        //总高度
        int totalHeight = 0;
        //行最大高度
        int lineMaxHeight = 0;
        //最大宽度
        int maxWidth    = 0;
        int childCount = 0;
        //当前行宽
        int currentLineWidth = 0;
        childCount = getChildCount();
        for (int i = 0; i<childCount; i++){
            View child = getChildAt(i);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();

            //获取当前child宽度
            int currentChildWidth = child.getMeasuredWidth() + params.getMarginStart() + params.getMarginEnd();
            //获取当前child 高度
            int currentChildHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            if (child.getVisibility() == GONE){
                if (i == childCount - 1){//最后一个时，取当前child 高度 和 之前行最大高度 的较大值
                    totalHeight += Math.max(currentChildHeight,lineMaxHeight);
                }
                continue;
            }
            // 超过了指定宽度
            if (currentLineWidth + currentChildWidth + getPaddingStart() + getPaddingEnd() > widthMeasureSize){
                maxWidth = Math.max(currentChildWidth,maxWidth);
                //清空当前行宽，赋值为最新宽度
                currentLineWidth = currentChildWidth;
                //将此行最大高度添加到总高度
                totalHeight +=  lineMaxHeight;
                //两者比较取较大值
                lineMaxHeight = Math.max(currentChildHeight,lineMaxHeight);
            }
            else{//没超过指定宽度，不换行
                //累加宽度
                currentLineWidth += currentChildWidth;
                //两者比较取较大值
                lineMaxHeight = Math.max(currentChildHeight,lineMaxHeight);
            }
            if (i == childCount - 1){//最后一个时，取当前child 高度 和 之前行最大高度 的较大值
                totalHeight += Math.max(currentChildHeight,lineMaxHeight);
            }

        }
        //整体计算宽高
        maxWidth += getPaddingStart() + getPaddingEnd();
        totalHeight += getPaddingTop() + getPaddingBottom();
        //设置宽高
        setMeasuredDimension(
                widthMeasureMode == MeasureSpec.EXACTLY  ? widthMeasureSize  : maxWidth,
                heightMeasureMode == MeasureSpec.EXACTLY ? heightMeasureSize : totalHeight
        );

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();

        //定义左边距、上边距。用于child 放置
        int layoutLeft = getPaddingStart();
        int layoutTop   = getPaddingTop();
        int lineMaxHeight   = 0;
        int totalHeight = 0;

        int mWidth = getPaddingStart() + getMeasuredWidth() + getPaddingEnd();

        for (int i = 0; i < childCount; i ++){
            View child = getChildAt(i);
            //获取 当前 child 布局信息
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            if (child.getVisibility() == GONE){
                continue;
            }
            //获取当前child 高度
            int currentChildHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            int left   = 0;
            int right  = 0;
            int top    = 0;
            int bottom = 0;
            // 子控件宽度 超出 父控件 ，换行
            if (layoutLeft + params.getMarginStart() + child.getMeasuredWidth() + params.getMarginEnd() > (r - l)) {
                //重置横向边距
                layoutLeft = getPaddingStart();
                //更新纵向边距 总行高
                layoutTop += lineMaxHeight;
                //获取行最高
                lineMaxHeight = Math.max(currentChildHeight, lineMaxHeight);
            } else {
                //获取行最高
                lineMaxHeight = Math.max(currentChildHeight, lineMaxHeight);
            }
            left = layoutLeft + params.getMarginStart();
            right = left + child.getMeasuredWidth();
            top = layoutTop + params.topMargin;
            bottom = top + child.getMeasuredHeight();

            //子view布局layout
            //注意当系统语言为阿拉伯语、希伯来语时 布局方向为从右向左，需要到对标准布局方向从左向右进行镜像处理。
            if(getLayoutDirection() == LAYOUT_DIRECTION_RTL){
                //镜像变换，从右向左layout
                child.layout(Math.abs(mWidth-right),top,Math.abs(mWidth-left),bottom);
            }
            else{
                child.layout(left,top,right,bottom);
            }

            //更新左边距
            layoutLeft += params.getMarginStart() + child.getMeasuredWidth() + params.getMarginEnd();
        }
    }

    /**
     * measure width
     * @param mode measure mode
     * @param size measure width
     * @return final width
     */
    private int measureWidth(int mode,int size) {
        return 0;
    }
    /**
     * measure height
     * @param mode measure mode
     * @param size measure width
     * @return final height
     */
    private int measureHeight(int mode,int size) {
        return 0;
    }


}
