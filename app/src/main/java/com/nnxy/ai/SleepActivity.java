package com.example.camera_system;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;

import com.example.camera_system.utils.BaseActivity;
import com.iflytek.aiui.AIUIEvent;
import com.starway.starrobot.aiuiability.NLPListener;
import com.starway.starrobot.aiuiability.SpeechHelper;
import com.starway.starrobot.commonability.WakeUpActionHelper;
import com.starway.starrobot.commonability.camera.CameraHelper;
import com.starway.starrobot.mscability.StarMscAbility;
import com.starway.starrobot.mscability.facedetect.FaceVerifyHelper;

import java.util.Timer;
import java.util.TimerTask;

public class SleepActivity extends BaseActivity implements NLPListener,CameraHelper.GetBitmapListener {
    private WakeUpActionHelper wakeUpActionHelper;

    // 相机实例
    private CameraHelper cameraHelper;

    // 人脸识别实例
    private FaceVerifyHelper faceVerifyHelper;

    private Bitmap mBitmap;
    private Timer timer;


    ImageView wakeup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        // 隐藏标题栏
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.hide();
        }

        cameraHelper = new CameraHelper(this);

        // 拍照或拿预览帧监听
        cameraHelper.setGetBitmapListener(this);
        // 一直都是打开相机的状态
        cameraHelper.openCamera(0);

        //初始化语音合成
        SpeechHelper.getInstance().initSpeech(this);

        wakeup=findViewById(R.id.Wakeup);
        wakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraHelper.closeCamera();
                timer.cancel();
                Intent intent = new Intent(SleepActivity.this,MainActivity.class);
                intent.putExtra("type","sleep");
                startActivity(intent);

            }
        });

        faceVerifyHelper = new FaceVerifyHelper(this);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                cameraHelper.takePreView();

                faceVerifyHelper.startOnlineDetect(mBitmap, new StarMscAbility.onResultCallback() {
                    @Override
                    public void onResult(boolean b, String s) {
                        if (b) {
                            timer.cancel();
                            cameraHelper.closeCamera();
                            SpeechHelper.getInstance().speak("欢迎您。");
                            Intent intent = new Intent(SleepActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        },0,1000);





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                voiceWakeup();

            }
        },3000);

    }

    private void voiceWakeup(){
        wakeUpActionHelper = new WakeUpActionHelper(this);
        wakeUpActionHelper.setOnWakeUpActionListener(new WakeUpActionHelper.OnWakeUpActionListener() {
            @Override
            public boolean getAngle(final int i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(i<180 && i>0){
                            SpeechHelper.getInstance().speak("欢迎您。");
                            cameraHelper.closeCamera();
                            timer.cancel();
                            Intent intent1 = new Intent(SleepActivity.this, MainActivity.class);
                            startActivity(intent1);

                        }
                        else
                        {
                            SpeechHelper.getInstance().speak("请走过来，正对我！");
                        }

                    }
                });
                return false;
            }

            @Override
            public void onRotateEnd(int i) {

            }

            @Override
            public void onForwardEnd() {

            }
        });
        wakeUpActionHelper.registerWakeUpActionReceiver();
    }

    @Override
    public void onAiuiResponse(String s) {

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
    public void getBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
