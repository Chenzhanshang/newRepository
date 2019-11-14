package com.example.camera_system;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.camera_system.utils.BaseActivity;
import com.example.camera_system.utils.database.UserDataBaseHelper;
import com.iflytek.aiui.AIUIEvent;
import com.starway.starrobot.aiuiability.NLPListener;
import com.starway.starrobot.aiuiability.SpeechHelper;
import com.starway.starrobot.commonability.HardwareServer;
import com.starway.starrobot.commonability.camera.CameraHelper;
import com.starway.starrobot.commonability.hardware.EmojiAction;
import com.starway.starrobot.mscability.StarMscAbility;
import com.starway.starrobot.mscability.facedetect.FaceVerifyHelper;
import com.starway.starrobot.mscability.facegroup.FaceGroupHelper;

import java.util.Timer;
import java.util.TimerTask;

public class FaceToNotCardActivity extends AppCompatActivity implements CameraHelper.GetBitmapListener,View.OnClickListener, NLPListener {

    private static final String TAG = FaceToNotCardActivity.class.getSimpleName();
    // webapi 接口地址
    private static final String WEBWFV_URL = "https://api.xfyun.cn/v1/service/v1/image_identify" +
            "/face_verification";


    private Timer timer;
    private Timer timer1;
    private Integer sign;
    private String name;

    // 创建数据库操作对象
    private UserDataBaseHelper helper;
    // 获取数据库
    private SQLiteDatabase db;

    // 相机实例
    private CameraHelper cameraHelper;


    // 人脸组实例
    private FaceGroupHelper faceGroupHelper;
    // 人脸识别实例
    private FaceVerifyHelper faceVerifyHelper;
    private Context mContext;
    private Bitmap mBitmap;
    private HandlerThread handlerThread;
    private Handler mHandler;
    private SharedPreferences sharedPreferences;
    private FrameLayout frameLayout;

    private Button toRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facetonotcard);

        // 隐藏标题栏
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.hide();
        }

        mContext = FaceToNotCardActivity.this;
        sharedPreferences = getSharedPreferences("faceInformation", Context.MODE_PRIVATE);
        handlerThread = new HandlerThread("faceThead");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        frameLayout = findViewById(R.id.face);
        cameraHelper = new CameraHelper(mContext);

        helper = new UserDataBaseHelper(this, "user1.db", null, 1);
        db =  helper.getWritableDatabase();

        //获取注册按钮
        toRegister = findViewById(R.id.toRegister);
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer =null;
                timer1.cancel();
                Intent intent = new Intent(FaceToNotCardActivity.this, CardActivity.class);
                startActivity(intent);
            }
        });


        // 获取返回按钮对象
        ImageView back = findViewById(R.id.back);


        // 监听返回监听按钮点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer = null;
                timer1.cancel();
                // 跳转到首页页面
                Intent intent = new Intent(FaceToNotCardActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        // 超时跳转到休眠界面
        BaseActivity.setOnHandlerListener(new BaseActivity.HandlerListener() {
            @Override
            public void heandleMessage(Message msg) {
                timer = null;
                timer1.cancel();
                Intent intent = new Intent(FaceToNotCardActivity.this, SleepActivity.class);
                startActivity(intent);
            }
        });

        SpeechHelper.getInstance().speak("请正视摄像头进行人脸登录");//说出输入的文本的文字

        //初始化语音合成
        SpeechHelper.getInstance().initSpeech(this);

        // 拍照或拿预览帧监听
        cameraHelper.setGetBitmapListener(this);

        faceGroupHelper = new FaceGroupHelper(mContext);
        faceVerifyHelper = new FaceVerifyHelper(mContext);
        faceVerifyHelper.setFaceVerifyResultCallback(new StarMscAbility.onResultCallback() {
            @Override
            public void onResult(boolean b, String s) {
                Log.d(TAG, "name = " + s);
                if(sign >=2){
                    timer = null;
                    timer1.cancel();
                    Intent intent = new Intent(FaceToNotCardActivity.this, CardActivity.class);
                    startActivity(intent);
                }
                if (b) {
                    if(!s.equals(""))
                    {

                        String[] selectionArgs = new String[1];
                        selectionArgs[0] = s;
                        Cursor cursor = db.query("t_user", null, "idCard=?", selectionArgs, null, null, "id DESC");
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
                            name = cursor.getString(1);
                            cursor.moveToNext();
                        }
                        Toast.makeText(mContext,"欢迎：" + name,Toast.LENGTH_SHORT).show();
                        SpeechHelper.getInstance().speak("欢迎" + name);//说出输入的文本的文字
                        sign = 2;
                        timer =null;
                        timer1.cancel();
                        Intent intent = new Intent(FaceToNotCardActivity.this, VisitorActivity.class);
                        intent.putExtra("idCard",s);
                        startActivity(intent);
                    }
                    else {
                        sign ++;
                        Toast.makeText(mContext,"请正视摄像头！",Toast.LENGTH_SHORT).show();
                        if(sign<=2)
                            SpeechHelper.getInstance().speak("请正视摄像头！" );//说出输入的文本的文字
                        else{
                            SpeechHelper.getInstance().speak("人脸识别失败，请注册！" );//说出输入的文本的文字
                        }
                    }
                    System.out.println(s);
                }
            }
        });


        // 一直都是打开相机的状态
        cameraHelper.openCamera(frameLayout,0);//需要预览界面的调用
        //相机打开监听回调
        cameraHelper.setOnCameraPrepareListener(new CameraHelper.OnCameraPrepareListener() {
            @Override
            public void prepare() {
                Log.d(TAG, "相机已经准备好");
            }
        });

        sign = 0;
        timer = new Timer();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(timer == null)
                    return;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(timer == null)
                                return;
                            cameraHelper.takePreView();
                            System.out.println("正在拍照");
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (null != mBitmap) {
                                        faceVerifyHelper.sendGroupVerify(mBitmap);
                                        Log.d(TAG, "onlineFaceDetect**************");
                                    }
                                }
                            },500);
                        }
                    },0,4000);
                }
        },4000);


        //设置表情
        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                HardwareServer.getInstance().doEmojiAction(EmojiAction.MODE.EYE_OPERATE_CAMERA);
            }
        },0,1000);



    }

    @Override
    public void onClick(View v) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quitSafely();
        timer = null;
        timer1.cancel();
    }
}
