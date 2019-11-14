package com.example.camera_system;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.ActionBar;

import com.example.camera_system.utils.BaseActivity;
import com.example.camera_system.utils.PeopleAskView;
import com.example.camera_system.utils.RobotAnwserView;
import com.iflytek.aiui.AIUIEvent;
import com.starway.starrobot.aiuiability.AIUIAbility;
import com.starway.starrobot.aiuiability.NLPListener;
import com.starway.starrobot.aiuiability.SpeechHelper;
import com.starway.starrobot.commonability.HardwareServer;
import com.starway.starrobot.commonability.hardware.EmojiAction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 娱乐闲聊
 */

public class EntertainmentActivity extends BaseActivity implements NLPListener {
    private LinearLayout contentView;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment);
        // 隐藏标题栏
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.hide();
        }


        // 获取返回按钮对象
        ImageView back = findViewById(R.id.back);

        // 获取内容布局
        contentView = findViewById(R.id.ContentView);

        // 监听返回监听按钮点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                // 跳转到首页页面
                Intent intent = new Intent(EntertainmentActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        // 超时跳转到休眠界面
        BaseActivity.setOnHandlerListener(new BaseActivity.HandlerListener() {
            @Override
            public void heandleMessage(Message msg) {
                timer.cancel();
                Intent intent = new Intent(EntertainmentActivity.this, SleepActivity.class);
                startActivity(intent);
            }
        });


        //添加监听器并启动语音识别
        AIUIAbility.getInstance().addNLPListener(this);

        //初始化语音合成
        SpeechHelper.getInstance().initSpeech(this);

        // 设置默认滚到到最底部
        final ScrollView scrollView = findViewById(R.id.sv_en);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


        //设置表情
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                HardwareServer.getInstance().doEmojiAction(EmojiAction.MODE.EYE_OPERATE_HAPPY);
            }
        },0,1000);




    }

    @Override
    public void onAiuiResponse(String s) {
        this.voiceListener();


        if (TextUtils.isEmpty(s)) {
            return;
        }
        try {
            JSONObject object = new JSONObject(s);
            if (null == object) {
                return;
            }
            JSONObject intentObject = object.optJSONObject("intent");
            if (null == intentObject) {
                return;
            }
            if(!intentObject.has("service")){
                return;
            }
            if(intentObject.has("answer")){
                JSONObject answerObject = intentObject.optJSONObject("answer");//进行解析

                if( !(answerObject.has("emotion") && answerObject.getString("emotion").equals("default")) ) {

                    String answerText = answerObject.getString("text");//进行解析

                    // 人问问题
                    new PeopleAskView().peopleAsk(this, intentObject.getString("text"), contentView);


                    // 设置默认滚到到最底部
                    final ScrollView scrollView = findViewById(R.id.sv_en);
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });


                    // 机器人回答
                    new RobotAnwserView().answer(this, answerText, contentView);

                    // 设置默认滚到到最底部
                    final ScrollView scrollView1 = findViewById(R.id.sv_en);
                    Handler handler1 = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });






                    SpeechHelper.getInstance().speak(answerText);//说出输入的文本的文字
                }
                else {
                    return;
                }
            }

            if(TextUtils.equals("OS8047208306.my_order", intentObject.getString("service"))){
                JSONArray semanticArr = intentObject.optJSONArray("semantic");
                if (null == semanticArr || semanticArr.length() == 0) {
                    return;
                }
                JSONObject semantic = semanticArr.optJSONObject(0);
                if (null == semantic) {
                    return;
                }

                String intent = semantic.optString("intent", "");


                if (TextUtils.equals("back", intent)) {
                    timer.cancel();

                    Intent i  = new Intent(EntertainmentActivity.this,MainActivity.class);
                    startActivity(i);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAiuiWakeUp() {

    }

    @Override
    public void onAiuiSleep() {

    }

    @Override
    public void onAiuiEvent(AIUIEvent aiuiEvent) {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AIUIAbility.getInstance().stop();
    }
}
