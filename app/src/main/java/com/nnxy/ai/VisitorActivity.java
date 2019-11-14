package com.example.camera_system;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.example.camera_system.entity.User;
import com.example.camera_system.utils.BaseActivity;
import com.example.camera_system.utils.database.UserDataBaseHelper;
import com.printsdk.cmd.PrintCmd;
import com.starway.starrobot.aiuiability.SpeechHelper;
import com.starway.starrobot.commonability.HardwareServer;
import com.starway.starrobot.commonability.RobotType;
import com.starway.starrobot.commonability.StarCommonAbility;
import com.starway.starrobot.commonability.hardware.EmojiAction;
import com.starway.starrobot.commonability.hardware.base.BaseHardware;
import com.starway.starrobot.commonability.hardware.print.PrinterLines;
import com.starway.starrobot.logability.log.PartCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class VisitorActivity extends BaseActivity {


    private final List<PrinterLines> printLines = new ArrayList();
    private String idCard;
    private Intent inten;
    private User u = new User() ;
    private User user = new User() ;

    private TextView name;
    private TextView sex;
    private TextView nationality ;
    private TextView birthday;
    private TextView address;
    private TextView idCardT;
    private RadioGroup radioGroup;
    private EditText phone;
    private EditText opinion;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    private TextView dateTextView;
    private String mDate;


    private String doWhat;


    // 创建数据库操作对象
    private UserDataBaseHelper helper;
    // 获取数据库
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);

        inten = this.getIntent();
        idCard = (String) inten.getExtras().get("idCard");
        user = (User) inten.getExtras().get("user");

        helper = new UserDataBaseHelper(this, "user1.db", null, 1);
        db =  helper.getWritableDatabase();

        name = findViewById(R.id.name);
        sex = findViewById(R.id.sex);
        nationality = findViewById(R.id.nationality);
        birthday = findViewById(R.id.birthday);
        address = findViewById(R.id.address);
        idCardT = findViewById(R.id.idCard);
        opinion = findViewById(R.id.opinion);
        phone = findViewById(R.id.phone);
        dateTextView = findViewById(R.id.date);

        mDate = simpleDateFormat.format(new Date()).toString();
        dateTextView.setText(mDate);


        opinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceListener();
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceListener();
            }
        });


        // 超时跳转到休眠界面
        BaseActivity.setOnHandlerListener(new BaseActivity.HandlerListener() {
            @Override
            public void heandleMessage(Message msg) {
                Intent intent = new Intent(VisitorActivity.this, SleepActivity.class);
                startActivity(intent);
            }
        });

        //初始化语音合成
        SpeechHelper.getInstance().initSpeech(this);

        radioGroup = findViewById(R.id.rg);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.access: doWhat = "参观访问";
                        break;
                    case R.id.talk: doWhat = "参与会议";
                        break;
                    case R.id.communication: doWhat = "沟通洽谈";
                        break;
                    case R.id.person: doWhat = "拜访个人";
                        break;
                    default:
                        break;
                }
            }
        });

        // 隐藏标题栏
        ActionBar bar = getSupportActionBar();
        if(null!=bar)

        {
            bar.hide();
        }

        // 获取返回按钮对象
        ImageView back = findViewById(R.id.back);

        // 监听返回监听按钮点击事件
        back.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View view){
                // 跳转到首页页面
                Intent intent1 = new Intent(VisitorActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });


        //基础能力初始化
        StarCommonAbility.getInstance().initAbility(this,RobotType.TYPE_TEACHING, new StarCommonAbility.onResultCallback() {
        @Override
        public void onResult(boolean b, String s) {
            if(b){
                switch (s){
                    case PartCode.HARDWARE_PARTCODE.CODE_PRINTER:
                        initPrinter();
                        break;
                }

            }
            else{
                Log.e("hardware init failed",s);
            }
        }
    });

        if(idCard.equals("card"))
        {
            name.setText(user.getName());
            idCardT.setText(user.getIdCard());
            address.setText(user.getAddress());
            birthday.setText(user.getBirthday());
            sex.setText(user.getSex());
            nationality.setText(user.getNationality());
        }
        else{

        String[] selectionArgs = new String[1];
        if(idCard!=null && !(idCard.equals("")))
            selectionArgs[0] = idCard;
        else
            selectionArgs[0] = " ";


        Cursor cursor = db.query("t_user", null, "idCard=?", selectionArgs, null, null, "id DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String address = cursor.getString(2);
            String sex = cursor.getString(3);
            String idCard = cursor.getString(4);
            String birthday = cursor.getString(5);
            String nationality = cursor.getString(6);

            u.setId(Integer.parseInt(id));
            u.setIdCard(idCard);
            u.setSex(sex);
            u.setAddress(address);
            u.setNationality(nationality);
            u.setBirthday(birthday);
            u.setName(name);

            System.out.println(u);


            cursor.moveToNext();
        }

        name.setText(u.getName());
        idCardT.setText(u.getIdCard());
        address.setText(u.getAddress());
        birthday.setText(u.getBirthday());
        sex.setText(u.getSex());
        nationality.setText(u.getNationality());

        }

    }


    //基本硬件初始化函数（打印机）
    private void initPrinter(){
        ImageButton print_button = findViewById(R.id.print_button);
        print_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceListener();
                print();
                HardwareServer.getInstance().doEmojiAction(EmojiAction.MODE.EYE_OPERATE_BLINK);
                printLines.clear();
            }
        });
    }

    //打印函数
    private void print(){


        PrinterLines line7 = new PrinterLines();
        line7.setText("访客登记");
        line7.setAlignment(1);//对齐：0：左对齐 1：居中 2：右对齐
        line7.setFeedLines(2);//空行数量
        line7.setPaintBold(1);//加粗方式 0：不加粗 1：加粗
        line7.setPaintSize(2,2);//设置画笔大小 从大小0开始 文字宽度 文字高度
        printLines.add(line7);

        PrinterLines line1 = new PrinterLines();
        TextView name=findViewById(R.id.name);
        line1.setText("姓    名："+name.getText().toString());
        line1.setAlignment(0);//对齐：0：左对齐 1：居中 2：右对齐
        line1.setFeedLines(2);//空行数量
        line1.setPaintBold(0);//加粗方式 0：不加粗 1：加粗
        line1.setPaintSize(0,0);//设置画笔大小 从大小0开始 文字宽度 文字高度
        printLines.add(line1);

        PrinterLines line2 = new PrinterLines();
        TextView sex=findViewById(R.id.sex);
        TextView nation=findViewById(R.id.nationality );
        String str="性    别："+sex.getText().toString()+"   "+"民族："+nation.getText().toString();
        line2.setText(str);
        line2.setAlignment(0);//对齐：0：左对齐 1：居中 2：右对齐
        line2.setFeedLines(2);//空行数量
        line2.setPaintBold(0);//加粗方式 0：不加粗 1：加粗
        line2.setPaintSize(0,0);//设置画笔大小 从大小0开始 文字宽度 文字高度
        printLines.add(line2);

        PrinterLines line3 = new PrinterLines();
        TextView birthday=findViewById(R.id.birthday);
        line3.setText("出    生："+birthday.getText().toString());
        line3.setAlignment(0);//对齐：0：左对齐 1：居中 2：右对齐
        line3.setFeedLines(2);//空行数量
        line3.setPaintBold(0);//加粗方式 0：不加粗 1：加粗
        line3.setPaintSize(0,0);//设置画笔大小 从大小0开始 文字宽度 文字高度
        printLines.add(line3);

        PrinterLines line4 = new PrinterLines();
        TextView address=findViewById(R.id.address);
        line4.setText("地    址：" + address.getText().toString());
        line4.setAlignment(0);//对齐：0：左对齐 1：居中 2：右对齐
        line4.setFeedLines(2);//空行数量
        line4.setPaintBold(0);//加粗方式 0：不加粗 1：加粗
        line4.setPaintSize(0,0);//设置画笔大小 从大小0开始 文字宽度 文字高度
        printLines.add(line4);

        PrinterLines line6 = new PrinterLines();
        TextView id_num=findViewById(R.id.idCard);
        line6.setText("身份证号："+id_num.getText().toString());
        line6.setAlignment(0);//对齐：0：左对齐 1：居中 2：右对齐
        line6.setFeedLines(2);//空行数量
        line6.setPaintBold(0);//加粗方式 0：不加粗 1：加粗
        line6.setPaintSize(0,0);//设置画笔大小 从大小0开始 文字宽度 文字高度
        printLines.add(line6);

        PrinterLines line10 = new PrinterLines();
        line10.setText("联系电话：" + phone.getText().toString());
        line10.setAlignment(0);//对齐：0：左对齐 1：居中 2：右对齐
        line10.setFeedLines(2);//空行数量
        line10.setPaintBold(0);//加粗方式 0：不加粗 1：加粗
        line10.setPaintSize(0,0);//设置画笔大小 从大小0开始 文字宽度 文字高度
        printLines.add(line10);




        PrinterLines line8 = new PrinterLines();
        line8.setText("来访事项：" + doWhat);
        line8.setAlignment(0);//对齐：0：左对齐 1：居中 2：右对齐
        line8.setFeedLines(2);//空行数量
        line8.setPaintBold(0);//加粗方式 0：不加粗 1：加粗
        line8.setPaintSize(0,0);//设置画笔大小 从大小0开始 文字宽度 文字高度
        printLines.add(line8);

        if( !TextUtils.isEmpty(opinion.getText().toString()) ){
            PrinterLines line11 = new PrinterLines();
            line11.setText("留    言：" + opinion.getText().toString());
            line11.setAlignment(0);//对齐：0：左对齐 1：居中 2：右对齐
            line11.setFeedLines(2);//空行数量
            line11.setPaintBold(0);//加粗方式 0：不加粗 1：加粗
            line11.setPaintSize(0,0);//设置画笔大小 从大小0开始 文字宽度 文字高度
            printLines.add(line11);
        }


        PrinterLines line12 = new PrinterLines();
        line12.setText("来访时间：" + mDate);
        line12.setAlignment(0);//对齐：0：左对齐 1：居中 2：右对齐
        line12.setFeedLines(2);//空行数量
        line12.setPaintBold(0);//加粗方式 0：不加粗 1：加粗
        line12.setPaintSize(0,0);//设置画笔大小 从大小0开始 文字宽度 文字高度
        printLines.add(line12);


        PrinterLines line5 = new PrinterLines();
        line5.setAlignment(1);
        line5.addParam(PrintCmd.PrintQrcode("http://nnxy.iflysse.com/Login_nnxy.aspx", 25, 6, 1));
        line5.setFeedLines(3);
        printLines.add(line5);

        HardwareServer.getInstance().print(printLines, new BaseHardware.onResultCallback() {
            @Override
            public void onResult(boolean b, String s) {
                if(b){
                    Log.d("print", "打印成功");
                }
                else{
                    Log.e("print error", s);
                }
            }
        });
    }
}


