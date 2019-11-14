package com.example.camera_system;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.camera_system.entity.User;
import com.iflytek.aiui.AIUIEvent;
import com.starway.starrobot.aiuiability.AIUIAbility;
import com.starway.starrobot.aiuiability.NLPListener;
import com.starway.starrobot.aiuiability.SpeechHelper;

import org.json.JSONArray;
import org.json.JSONObject;

public class WriteActivity extends AppCompatActivity implements NLPListener {
    private User user = new User();
    private EditText name;
    private RadioGroup sex;
    private EditText nationality ;
    private EditText birthday;
    private EditText address;
    private EditText idCardT;
    private String s;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        //初始化AIUI
        AIUIAbility.getInstance().initAIUIAbility(this);

        //添加监听器并启动语音识别
        AIUIAbility.getInstance().addNLPListener(this);
        AIUIAbility.getInstance().start();

        //初始化语音合成
        SpeechHelper.getInstance().initSpeech(this);
        Toast.makeText(WriteActivity.this,"请输入身份信息！" ,Toast.LENGTH_SHORT).show();

        SpeechHelper.getInstance().speak("请输入身份信息");//说出输入的文本的文字

        name = findViewById(R.id.name);
        sex = findViewById(R.id.sex);
        nationality = findViewById(R.id.nationality);
        birthday = findViewById(R.id.birthday);
        address = findViewById(R.id.address);
        idCardT = findViewById(R.id.idCard);


        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.man: s = "男";
                        break;
                    case R.id.women: s = "女";
                      break;
                    default:
                        break;
                }
            }
        });






        ImageButton next = findViewById(R.id.next);
        ImageButton reback = findViewById(R.id.reback);
        ImageView back = findViewById(R.id.back);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WriteActivity.this,VisitorActivity.class);
                user.setName(name.getText().toString());
                user.setIdCard(idCardT.getText().toString());
                user.setAddress(address.getText().toString());
                user.setBirthday(birthday.getText().toString());
                user.setSex(s);
                user.setNationality(nationality.getText().toString());
                if(user.getIdCard().trim().length() == 18  ){
                    intent.putExtra("user",user);
                    intent.putExtra("idCard","card");
                    startActivity(intent);
                }else{
                    Toast.makeText(WriteActivity.this,"身份证长度必须为不包含空格的18位字符" ,Toast.LENGTH_SHORT).show();
                    SpeechHelper.getInstance().speak("身份证长度必须为不包含空格的18位字符" );//说出输入的文本的文字
                }

            }
        });
        reback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WriteActivity.this,CardActivity.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WriteActivity.this,MainActivity.class);
                startActivity(intent);
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


                if (TextUtils.equals("Ok", intent)) {

                    Intent i  = new Intent(WriteActivity.this,VisitorActivity.class);
                    i.putExtra("idCard"," ");

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
