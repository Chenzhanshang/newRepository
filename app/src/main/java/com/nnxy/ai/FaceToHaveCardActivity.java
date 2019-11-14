package com.example.camera_system;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.camera_system.entity.User;
import com.example.camera_system.face.FaceAlignmentHelper;
import com.example.camera_system.face.FileUtil;
import com.example.camera_system.utils.BaseActivity;
import com.example.camera_system.utils.database.UserDataBaseHelper;
import com.iflytek.aiui.AIUIEvent;
import com.starway.starrobot.aiuiability.AIUIAbility;
import com.starway.starrobot.aiuiability.NLPListener;
import com.starway.starrobot.aiuiability.SpeechHelper;
import com.starway.starrobot.commonability.HardwareServer;
import com.starway.starrobot.commonability.camera.CameraHelper;
import com.starway.starrobot.commonability.hardware.EmojiAction;
import com.starway.starrobot.mscability.StarMscAbility;
import com.starway.starrobot.mscability.facedetect.FaceRegisterHelper;
import com.starway.starrobot.mscability.facedetect.FaceVerifyHelper;
import com.starway.starrobot.mscability.facegroup.FaceGroupHelper;
import com.starway.starrobot.mscability.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.camera_system.MyApplication.API_KEY;
import static com.example.camera_system.MyApplication.APPID;

public class FaceToHaveCardActivity extends AppCompatActivity implements CameraHelper.GetBitmapListener, NLPListener {
    private static final String TAG = FaceToHaveCardActivity.class.getSimpleName();
    // webapi 接口地址
    private static final String WEBWFV_URL = "https://api.xfyun.cn/v1/service/v1/image_identify" +
            "/face_verification";

    // 图片地址
    private static final String FILE_PATH3 =
            Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    "aiLab/camera/my.jpg";


    private Timer timer;
    private Intent intent;
    private User user = null;
    // 创建数据库操作对象
    UserDataBaseHelper helper;
    // 获取数据库
    SQLiteDatabase db;

    //人脸Id
    private String authId;
    private FrameLayout frameLayout;
    private ImageView imageView;
    // 相机实例
    private CameraHelper cameraHelper;
    // 人脸组实例
    private FaceGroupHelper faceGroupHelper;
    // 人脸注册实例
    private FaceRegisterHelper faceRegisterHelper;
    // 人脸识别实例
    private FaceVerifyHelper faceVerifyHelper;
    private Context mContext;
    private Bitmap mBitmap;
    private HandlerThread handlerThread;
    private Handler mHandler;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facetohavecard);

        intent = this.getIntent();
        user = (User) intent.getExtras().get("user");

        helper = new UserDataBaseHelper(this, "user1.db", null, 1);
        db =  helper.getWritableDatabase();

        mContext = FaceToHaveCardActivity.this;
        sharedPreferences = getSharedPreferences("faceInformation", Context.MODE_PRIVATE);
        handlerThread = new HandlerThread("faceThead");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        frameLayout = findViewById(R.id.surface_frame);

        // 隐藏标题栏
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.hide();
        }

        // 获取上一步按钮对象
        ImageButton next = findViewById(R.id.next);
        //上一步
        ImageButton reback = findViewById(R.id.reback);

        reback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                Intent intent = new Intent(FaceToHaveCardActivity.this, CardActivity.class);//办理业务界面
                startActivity(intent);
            }
        });

        // 监听下一步监听按钮点击事件
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                faceAligement();
            }
        });


        // 获取返回按钮对象
        ImageView back = findViewById(R.id.back);

        // 监听返回监听按钮点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                // 跳转到首页页面
                Intent intent1 = new Intent(FaceToHaveCardActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });


        // 获取拍照按钮对象
        ImageButton take_photo = findViewById(R.id.take_photo);

        // 监听拍照按钮点击事件
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHelper.takePicture();

            }
        });


        // 超时跳转到休眠界面
        BaseActivity.setOnHandlerListener(new BaseActivity.HandlerListener() {
            @Override
            public void heandleMessage(Message msg) {
                timer.cancel();
                Intent intent = new Intent(FaceToHaveCardActivity.this, SleepActivity.class);
                startActivity(intent);
            }
        });


        imageView = findViewById(R.id.image);
        cameraHelper = new CameraHelper(mContext);
        //相机打开监听回调
        cameraHelper.setOnCameraPrepareListener(new CameraHelper.OnCameraPrepareListener() {
            @Override
            public void prepare() {
                Log.d(TAG, "相机已经准备好");
            }
        });


        //初始化AIUI
        AIUIAbility.getInstance().initAIUIAbility(this);

        //添加监听器并启动语音识别
        AIUIAbility.getInstance().addNLPListener(this);
        AIUIAbility.getInstance().start();

        //初始化语音合成
        SpeechHelper.getInstance().initSpeech(this);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                HardwareServer.getInstance().doEmojiAction(EmojiAction.MODE.EYE_OPERATE_CAMERA);
            }
        },0,1000);

        // 拍照或拿预览帧监听
        cameraHelper.setGetBitmapListener(this);
        faceGroupHelper = new FaceGroupHelper(mContext);
        faceRegisterHelper = new FaceRegisterHelper(mContext);
        faceVerifyHelper = new FaceVerifyHelper(mContext);
        faceVerifyHelper.setFaceVerifyResultCallback(new StarMscAbility.onResultCallback() {
            @Override
            public void onResult(boolean b, String s) {
                Log.d(TAG, "name = " + s);
                if (b) {
                    String name = s;
                    System.out.println(s);
                    Toast.makeText(mContext,name,Toast.LENGTH_SHORT).show();
                }
            }
        });


        // 一直都是打开相机的状态
        cameraHelper.openCamera(frameLayout,0);//需要预览界面的调用
        //这个是用来测试的，以后用于人脸唤醒


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        saveBitmap(FILE_PATH3, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AIUIAbility.getInstance().stop();
        handlerThread.quitSafely();
    }
    //-----------------------------------------------------------------------------------
    //基本函数实现
    //人脸比对
    private void faceAligement() {
        FaceAlignmentHelper.getInstance().init(APPID, API_KEY);

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                // FileUtil.Bitmap2Bytes(mBitmap)
                boolean result = FaceAlignmentHelper.getInstance().faceAlignment(WEBWFV_URL,
                        FileUtil.Bitmap2Bytes((Bitmap) intent.getExtras().get("headImg")) , FileUtil.Bitmap2Bytes(mBitmap));

                if (result) {
                    Toast.makeText(mContext, "人脸比对通过", Toast.LENGTH_SHORT).show();


                    //加组（上传）
                    authId=user.getIdCard();//身份证信息
                    if (null != mBitmap && !(StringUtils.isEmpty(authId))) {
                        faceRegisterHelper.startRegister(authId, mBitmap,
                                new StarMscAbility.onResultCallback() {
                                    @Override
                                    public void onResult(boolean b, String s) {
                                        if (b) {
                                            Log.d(TAG, "注册成功了--" + s);
                                            SpeechHelper.getInstance().speak("人脸比对通过,注册成功！");//说出输入的文本的文字
                                            HardwareServer.getInstance().doEmojiAction(EmojiAction.MODE.EYE_OPERATE_LOVE);

                                            // 添加
                                            ContentValues values = new ContentValues();
                                            values.put("name", user.getName());
                                            values.put("sex", user.getSex());
                                            values.put("address", user.getAddress());
                                            values.put("birthday", user.getBirthday());
                                            values.put("nationality", user.getNationality());
                                            values.put("idCard", user.getIdCard());
                                            long num = db.insert("t_user", "id", values);


                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(authId, "id");
                                            editor.apply();

                                            Intent intent1 = new Intent(FaceToHaveCardActivity.this, VisitorActivity.class);
                                            intent1.putExtra("idCard",user.getIdCard());
                                            startActivity(intent1);
                                        } else {
                                            Log.d(TAG, "注册失败了--" + s);
                                            SpeechHelper.getInstance().speak("注册失败，用户已存在，请返回登录！");//说出输入的文本的文字
                                        }
                                    }
                                });
                    }
                }
                else{
                    Toast.makeText(mContext, "身份识别未通过！", Toast.LENGTH_SHORT).show();
                    SpeechHelper.getInstance().speak("身份识别未通过！");//说出输入的文本的文字
                }

            }
        });

    }



    //把图片保存成jpg格式
    private boolean saveBitmap(String path, Bitmap bmp) {
        //校验图片路径，若没有则新建保存路径
        File dirFile = new File(path);
        //判断目录并创建
        if (!dirFile.getParentFile().exists()) {
            dirFile.getParentFile().mkdirs();
        }
        try {
            FileOutputStream os = new FileOutputStream(path);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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


                if (TextUtils.equals("back", intent)) {

                    Intent i  = new Intent(FaceToHaveCardActivity.this,MainActivity.class);
                    startActivity(i);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}


