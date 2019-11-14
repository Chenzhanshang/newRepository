package com.example.camera_system;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.hardware.idscanner.IDCardInfo;
import com.example.camera_system.entity.User;
import com.example.camera_system.utils.BaseActivity;
import com.iflytek.aiui.AIUIEvent;
import com.starway.starrobot.aiuiability.AIUIAbility;
import com.starway.starrobot.aiuiability.NLPListener;
import com.starway.starrobot.aiuiability.SpeechHelper;
import com.starway.starrobot.commonability.HardwareServer;
import com.starway.starrobot.commonability.RobotType;
import com.starway.starrobot.commonability.StarCommonAbility;
import com.starway.starrobot.commonability.hardware.EmojiAction;
import com.starway.starrobot.commonability.hardware.base.BaseHardware;
import com.starway.starrobot.logability.log.PartCode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class CardActivity extends AppCompatActivity implements NLPListener {

    private boolean sign = false;
    Timer timer = new Timer();
    private User user = new User();
    private User u = new User();

    private Bitmap headImg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        //初始化AIUI
        AIUIAbility.getInstance().initAIUIAbility(this);

        //添加监听器并启动语音识别
        AIUIAbility.getInstance().addNLPListener(this);
        AIUIAbility.getInstance().start();

        //初始化语音合成
        SpeechHelper.getInstance().initSpeech(this);

        //基础能力初始化
        StarCommonAbility.getInstance().initAbility(this, RobotType.TYPE_TEACHING, new StarCommonAbility.onResultCallback() {
            @Override
            public void onResult(boolean b, String s) {
                if (b) {
                    switch (s) {
                        case PartCode.HARDWARE_PARTCODE.CODE_ID_SCANNER:
                            initIdScanner();
                            break;
                    }
                } else {
                    Log.e("hardware init failed", s);
                }
            }
        });
        // 隐藏标题栏
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.hide();
        }

        //设置表情
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                HardwareServer.getInstance().doEmojiAction(EmojiAction.MODE.EYE_OPERATE_WAIT);
            }
        },0,1000);



        // 获取返回按钮对象
        ImageView back = findViewById(R.id.back);


        // 监听返回监听按钮点击事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到首页页面
                Intent intent = new Intent(CardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 返回上一步按钮
        Button reback = findViewById(R.id.reback);
        // 返回上一步按钮事件
        reback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 跳转到休眠页面
                Intent intent = new Intent(CardActivity.this, FaceToNotCardActivity.class);
                startActivity(intent);
            }
        });

        Button noCard = findViewById(R.id.nocard);
        noCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到输入信息页面
                Intent intent = new Intent(CardActivity.this, WriteActivity.class);
                startActivity(intent);
            }
        });

        Button toVisit = findViewById(R.id.tovisit);
        toVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到访客登记页面
                Intent intent = new Intent(CardActivity.this, VisitorActivity.class);
                intent.putExtra("user",user);
                intent.putExtra("idCard","card");

                startActivity(intent);
            }
        });

        // 下一步按钮
        Button next = findViewById(R.id.next);
        // 下一步按钮事件
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(sign){
                    // 跳转到下一步
                    Intent intent = new Intent(CardActivity.this, FaceToHaveCardActivity.class);
                    intent.putExtra("user",  user);
                    intent.putExtra("headImg",headImg);
                    startActivity(intent);
                }
                else {
                    SpeechHelper.getInstance().speak("请将身份证放置身份证阅读器处！");//说出输入的文本的文字
                    Toast.makeText(CardActivity.this,"请将身份证放置身份证阅读器处！",Toast.LENGTH_SHORT).show();

                }

            }
        });

        // 超时跳转到主界面
        BaseActivity.setOnHandlerListener(new BaseActivity.HandlerListener() {
            @Override
            public void heandleMessage(Message msg) {
                Intent intent = new Intent(CardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    //读取身份证的信息初始化
    private void initIdScanner () {
        HardwareServer.getInstance().startIdScanWithListener(new BaseHardware.idScanCallback() {
            @Override
            public void onIDscanEnd(final IDCardInfo idCardInfo, final byte[] bytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//主线程问题
                        SpeechHelper.getInstance().speak("身份证识别成功！");//说出输入的文本的文字
                        Toast.makeText(CardActivity.this,"身份证识别成功！",Toast.LENGTH_SHORT).show();
                        //设置为已读取身份证状态
                        sign = true;

                        TextView nameView = (TextView) findViewById(R.id.name);
                        TextView idNubmview = (TextView) findViewById(R.id.idCard);
                        TextView birthdayView = (TextView) findViewById(R.id.birthday);
                        TextView sex = (TextView) findViewById(R.id.sex);
                        TextView nation = (TextView) findViewById(R.id.nationality);
                        TextView address = (TextView) findViewById(R.id.address);
                        ImageView headImgView = (ImageView) findViewById(R.id.id_image);
                        nameView.setText(idCardInfo.getName());
                        idNubmview.setText(idCardInfo.getIdcardno());
                        String str = idCardInfo.getBirthday();
                        birthdayView.setText(str.substring(0, 4) + "年" + str.substring(4, 6) + "月" + str.substring(6, 8) + "日");//str=str.Remove(i,str.Length-i)
                        sex.setText(idCardInfo.getSex());
                        nation.setText(idCardInfo.getNation());
                        address.setText(idCardInfo.getAddress());
                        headImg = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        headImgView.setImageBitmap(headImg);


                        user.setIdCard(idCardInfo.getIdcardno());
                        user.setSex(idCardInfo.getSex());
                        user.setAddress(idCardInfo.getAddress());
                        user.setNationality(idCardInfo.getNation());
                        user.setBirthday(str.substring(0, 4) + "年" + str.substring(4, 6) + "月" + str.substring(6, 8) + "日");
                        user.setName(idCardInfo.getName());

                        System.out.println(user);
                    }
                });

            }

            @Override
            public void onIDscanFailed(int i, String s) {

            }
        });
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

                JSONArray slotArr = semantic.optJSONArray("slots");
                JSONObject slot1 = slotArr.optJSONObject(0);


                if (TextUtils.equals("no_Idcard", intent)) {
                    Intent i  = new Intent(CardActivity.this,WriteActivity.class);
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


