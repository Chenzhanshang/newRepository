package com.example.camera_system.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.camera_system.R;
import com.example.camera_system.utils.image.CircleImageView;

public class RobotAnwserView {

    /**
     * 展示机器人回答的内容
     * @param context 当前环境上下文
     * @param anwserContent 回答的文本内容
     * @param contentView 展示的父类容器
     */
    public void answer(Context context, String anwserContent, LinearLayout contentView) {

        // ======================================创建机器人回答行LinearLayout========================================================
        //创建一个LinearLayout布局
        LinearLayout jqrRowLayout = new LinearLayout(context);
        //创建布局的LayoutParams
        LinearLayout.LayoutParams jqrRowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        jqrRowLayout.setLayoutParams(jqrRowParams);
        // 将headRowLayout放到jqrLayout中
        contentView.addView(jqrRowLayout);

        // ======================================创建机器人头像LinearLayout========================================================
        //创建一个LinearLayout布局
        LinearLayout jqrHeadLayout = new LinearLayout(context);
        //创建布局的LayoutParams
        LinearLayout.LayoutParams jqrHeadParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.11f);
        jqrHeadLayout.setLayoutParams(jqrHeadParams);
        // 将jqrHeadLayout放到jqrLayout中
        jqrRowLayout.addView(jqrHeadLayout);


        // ======================================创建机器人头像========================================================
        CircleImageView headImg = new CircleImageView(context);
        //创建布局的LayoutParams
        LinearLayout.LayoutParams headImgParams = new LinearLayout.LayoutParams(200, 200);
        headImg.setLayoutParams(headImgParams);
        headImg.setImageResource(R.drawable.jqr);
        headImg.setScaleType(ImageView.ScaleType.FIT_XY);
        // 将jqrHeadLayout放到jqrLayout中
        jqrHeadLayout.addView(headImg);



        // ======================================创建机器人回答内容LinearLayout========================================================
        //创建一个LinearLayout布局
        LinearLayout jqrAnwserLayout = new LinearLayout(context);
        //创建布局的LayoutParams
        LinearLayout.LayoutParams jqrAnwserParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.89f);
        jqrAnwserParams.gravity = Gravity.LEFT;
        jqrAnwserLayout.setLayoutParams(jqrAnwserParams);
        // 将jqrHeadLayout放到jqrLayout中
        jqrRowLayout.addView(jqrAnwserLayout);


        // ======================================创建机器人回答内容TextView========================================================
        TextView jqrAnwserText = new TextView(context);
        jqrAnwserText.setMinHeight(120);
        jqrAnwserText.setPadding(5, 5, 5, 5);
        jqrAnwserText.setTextSize(18);
        jqrAnwserText.setText(anwserContent);
        jqrAnwserText.setBackgroundResource(R.drawable.textview_border);
        //创建布局的LayoutParams属性
        LinearLayout.LayoutParams jqrAnwserTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        jqrAnwserTextParams.topMargin = 100;
        jqrAnwserText.setLayoutParams(jqrAnwserTextParams);
        jqrAnwserLayout.addView(jqrAnwserText);
    }
}
