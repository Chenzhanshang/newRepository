package com.example.camera_system.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.camera_system.R;
import com.example.camera_system.utils.image.CircleImageView;


public class PeopleAskView {


    /***
     * 人问问题视图
     * @param context 上下文
     * @param askContent 问题内容
     * @param contentView 要展示内容的布局对象
     */
    public void peopleAsk(Context context, String askContent, LinearLayout contentView) {
        // #########################################以下是人问问题的布局代码###############################################

        // ======================================创建人问问题的行LinearLayout========================================================
        //创建一个LinearLayout布局
        LinearLayout userRowLayout = new LinearLayout(context);
        //创建布局的LayoutParams
        LinearLayout.LayoutParams userRowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        userRowParams.topMargin = 20;
        userRowLayout.setLayoutParams(userRowParams);
        // 将headRowLayout放到jqrLayout中
        contentView.addView(userRowLayout);

        // ======================================创建人问问题的LinearLayout====================================================
        //创建一个LinearLayout布局
        LinearLayout userAskLayout = new LinearLayout(context);
        userAskLayout.setGravity(Gravity.RIGHT);
        //创建布局的LayoutParams
        LinearLayout.LayoutParams userAskParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.89f);
        userAskLayout.setLayoutParams(userAskParams);
        // 将headRowLayout放到jqrLayout中
        userRowLayout.addView(userAskLayout);

        // ======================================创建人提问内容TextView========================================================
        TextView userAskText = new TextView(context);
        userAskText.setMinHeight(60);
        userAskText.setPadding(5, 5, 5, 5);
        userAskText.setTextSize(18);
        userAskText.setText(askContent);
        userAskText.setBackgroundResource(R.drawable.people_textview_border);
        //创建布局的LayoutParams属性
        LinearLayout.LayoutParams userAskTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        userAskTextParams.topMargin = 100;
        userAskText.setLayoutParams(userAskTextParams);
        userAskLayout.addView(userAskText);


        // ======================================创建人头像LinearLayout====================================================
        //创建一个LinearLayout布局
        LinearLayout userHeadLayout = new LinearLayout(context);
        //创建布局的LayoutParams
        LinearLayout.LayoutParams userHeadParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.11f);
        userHeadParams.gravity = Gravity.RIGHT;
        userHeadLayout.setLayoutParams(userHeadParams);
        // 将headRowLayout放到jqrLayout中
        userRowLayout.addView(userHeadLayout);


        // ======================================创建人头像布局========================================================
        CircleImageView userHeadImg = new CircleImageView(context);
        //创建布局的LayoutParams
        LinearLayout.LayoutParams userHeadImgParams = new LinearLayout.LayoutParams(200, 200);
        userHeadImg.setLayoutParams(userHeadImgParams);
        userHeadImg.setImageResource(R.drawable.people);
        userHeadImg.setScaleType(ImageView.ScaleType.FIT_XY);
        // 将userHeadImg放到userHeadLayout中
        userHeadLayout.addView(userHeadImg);
    }


}
