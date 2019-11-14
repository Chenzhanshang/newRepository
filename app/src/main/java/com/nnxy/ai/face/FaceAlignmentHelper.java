package com.example.camera_system.face;

import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 人脸1:1的工具类
 */
public class FaceAlignmentHelper {
    private static final String TAG = FaceAlignmentHelper.class.getSimpleName();
    private String mAppid = "";
    private String mAppKey = "";
    private double threshold = 0.8;

    private static FaceAlignmentHelper sInstance = null;

    public static FaceAlignmentHelper getInstance() {
        if (null == sInstance) {
            synchronized (FaceAlignmentHelper.class) {
                if (null == sInstance) {
                    sInstance = new FaceAlignmentHelper();
                }
            }
        }
        return sInstance;
    }

    private FaceAlignmentHelper() {
    }

    public void init(String appid, String appKey) {
        mAppid = appid;
        mAppKey = appKey;
    }

    /**
     * 组装http请求头
     *
     * @throws JSONException
     */
    private Map<String, String> buildHttpHeader(String appid, String appkey) throws UnsupportedEncodingException,
            JSONException {
        String curTime = System.currentTimeMillis() / 1000L + "";
        JSONObject param = new JSONObject();
        param.put("get_image", true);
        String params = param.toString();
        String paramBase64 = new String(Base64.encodeBase64(params.getBytes("UTF-8")));

        //String checkSum = DigestUtils.md5Hex(MyApplication.API_KEY + curTime + paramBase64);
        String checkSum =
                new String(Hex.encodeHex(DigestUtils.md5(appkey + curTime + paramBase64)));
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        header.put("X-Param", paramBase64);
        header.put("X-CurTime", curTime);
        header.put("X-CheckSum", checkSum);
        header.put("X-Appid", appid);
        return header;
    }
    public boolean faceAlignment(String webUrl, byte[] imageByteArray1, byte[] imageByteArray2) {
        boolean result = false;
        try {
            Map<String, String> header = buildHttpHeader(mAppid, mAppKey);
            //图片1和图片二Base64编码之后需要urlencode
            String imageBase641 = new String(Base64.encodeBase64(imageByteArray1), "UTF-8");
            String imageBase642 = new String(Base64.encodeBase64(imageByteArray2), "UTF-8");
            String res = HttpUtil.doPost1(webUrl, header,
                    "first_image=" + URLEncoder.encode(imageBase641, "UTF-8") + "&" +
                            "second_image=" + URLEncoder.encode(imageBase642, "UTF-8"));
            Log.d(TAG, "人脸比对接口调用结果：" + res);
            JSONObject object = new JSONObject(res);
            Log.d(TAG, "人脸比对接口调用结果：" + object.getString("code") + " 得分：" + object.getDouble("data"
            ));

            if (object.getString("code").equals("0") && object.getDouble("data"
            ) > threshold) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
