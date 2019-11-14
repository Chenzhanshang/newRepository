package com.example.camera_system;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.camera_system.utils.BaseActivity;
import com.iflytek.aiui.AIUIEvent;
import com.starway.starrobot.aiuiability.AIUIAbility;
import com.starway.starrobot.aiuiability.NLPListener;
import com.starway.starrobot.aiuiability.SpeechHelper;
import com.starway.starrobot.commonability.HardwareServer;
import com.starway.starrobot.commonability.hardware.EmojiAction;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NLPListener {
    private Button chat_button,consult_button,register_button;
    Intent intent,inten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 隐藏标题栏
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.hide();
        }

        chat_button=findViewById(R.id.consume);
        consult_button=findViewById(R.id.question);
        register_button=findViewById(R.id.register);
        chat_button.setOnClickListener(this);
        consult_button.setOnClickListener(this);
        register_button.setOnClickListener(this);
        inten = this.getIntent();
        if(inten != null && inten.hasExtra("type"))
        {
            HardwareServer.getInstance().doEmojiAction(EmojiAction.MODE.EYE_OPERATE_HAPPY);

        }


        //初始化AIUI
        AIUIAbility.getInstance().initAIUIAbility(this);

        //添加监听器并启动语音识别
        AIUIAbility.getInstance().addNLPListener(this);
        AIUIAbility.getInstance().start();

        //初始化语音合成
        SpeechHelper.getInstance().initSpeech(this);


        // 超时跳转到休眠界面
        BaseActivity.setOnHandlerListener(new BaseActivity.HandlerListener() {
            @Override
            public void heandleMessage(Message msg) {
                Intent intent = new Intent(MainActivity.this, SleepActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View view){

        switch (view.getId()){
            case R.id.consume:
                this.toEntertainmentActivity();
                break;
            case R.id.question:
                this.toConsultationActivity();
                break;
            case R.id.register:
                this.toFaceToNotCardActivity();
                break;

            default:
                break;

        }
    }

    @Override
    public void onAiuiResponse(String s) {
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


            if (TextUtils.equals("entertainment_chat", intent)) {
                this.toEntertainmentActivity();
            }else if (TextUtils.equals("visit_book", intent)) {
                this.toFaceToNotCardActivity();
            }else if (TextUtils.equals("problem", intent)) {
                this.toConsultationActivity();
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
        Log.d(MainActivity.class.getSimpleName(),"error");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AIUIAbility.getInstance().stop();

    }

    private void toEntertainmentActivity(){
        intent = new Intent(MainActivity.this, EntertainmentActivity.class);
        startActivity(intent);
    }

    private void toConsultationActivity(){
        intent=new Intent(MainActivity.this, ConsultationActivity.class);
        startActivity(intent);

    }

    private void toFaceToNotCardActivity(){

        intent=new Intent(MainActivity.this, FaceToNotCardActivity.class);
        startActivity(intent);

    }

}
