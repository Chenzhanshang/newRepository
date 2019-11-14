package com.example.camera_system.utils;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static Handler.Callback callback = new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message msg) {

            if(  mListener != null)
                mListener.heandleMessage(msg);
            return false;
        }
    };

    protected static Handler handler = new Handler(callback);


    private static HandlerListener mListener;

    public static void setOnHandlerListener(HandlerListener listener) {
        mListener = listener;
    }

    public static HandlerListener getListener() {
        return mListener;
    }

    // 执行跳转界面业务功能接口
    public interface HandlerListener {
        void heandleMessage(Message msg);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {    //手指下来的时候,取消之前绑定的Runnable
                handler.removeCallbacksAndMessages(null);
                break;
            }
            case MotionEvent.ACTION_UP: {    //手指离开屏幕，发送延迟消息 ，5秒后执行
                // 先清空队列，不然会影响内存
                handler.removeCallbacksAndMessages(null);
                // 再执发送新的消息
                handler.sendEmptyMessageDelayed(0, 1000 * 30);
                break;
            }
        }
        return result;
    }

    public void voiceListener(){
        // 先清空队列，不然会影响内存
        handler.removeCallbacksAndMessages(null);
        // 再执发送新的消息
        handler.sendEmptyMessageDelayed(0, 1000 * 30);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 清空消息队列
        handler.removeCallbacksAndMessages(null);
    }


    @Override
    protected void onStop() {
        super.onStop();
        // 清空消息队列
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 先清空队列，不然会影响内存
        handler.removeCallbacksAndMessages(null);
        // 再执发送新的消息
        handler.sendEmptyMessageDelayed(0, 1000 * 30);
    }


}
