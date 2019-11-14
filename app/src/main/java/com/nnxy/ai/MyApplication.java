package com.example.camera_system;

import android.app.Application;

import com.starway.starrobot.aiuiability.AIUIAbility;
import com.starway.starrobot.commonability.RobotType;
import com.starway.starrobot.commonability.StarCommonAbility;
import com.starway.starrobot.commonability.hardware.EmojiHelper;
import com.starway.starrobot.commonability.hardware.GPIOHelper;
import com.starway.starrobot.logability.log.PartCode;
import com.starway.starrobot.mscability.StarMscAbility;

public class MyApplication extends Application {
    public static final String APPID = "5dad0933";
    public static final String API_KEY = "04aa498eb6725e82b5133ce7b7156f53";
    public static final String GROUP_ID = "5537813236";
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        StarMscAbility.getInstance().initWithAppid(getApplicationContext(), APPID);
        StarMscAbility.getInstance().setGroupID(GROUP_ID);
        //日志初始化

        //基础能力初始化
        StarCommonAbility.getInstance().initAbility(this,
                RobotType.TYPE_TEACHING, new StarCommonAbility.onResultCallback() {
                    @Override
                    public void onResult(boolean isSuccess, String hardCode) {
                        if (isSuccess) {
                            //硬件和业务状态初始化
                                switch (hardCode) {
                                    case PartCode.HARDWARE_PARTCODE.CODE_EMOJI:
                                        //设置初始表情
                                        EmojiHelper.doEmojiBase();
                                        break;
                                    case PartCode.HARDWARE_PARTCODE.CODE_GPIO:
                                        //默认加载的时候，将拾音方向设置为默认正前方的0度。
                                        GPIOHelper.getInstance().setMainMic(0);
                                        break;

                                default:
                                    break;
                            }
                        }
                    }
                });
        //初始化AIUI
        AIUIAbility.getInstance().initAIUIAbility(this);
    }

}
