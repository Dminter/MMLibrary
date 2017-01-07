package com.zncm.library.utils;

import android.view.View;

public class DoubleClickImp {
    public interface OnDoubleClickListener {
        public void OnSingleClick(View v);

        public void OnDoubleClick(View v);
    }

    private static long lastClickTime = 0;

    public static boolean isDoubleClick() {
        long time = System.currentTimeMillis();
        long diff = time - lastClickTime;
        lastClickTime = time;
        if (diff > 0 && diff < 300) {
            return true;
        }
        return false;
    }

    public static void registerDoubleClickListener(View view, final OnDoubleClickListener listener) {
        if (listener == null) return;
        if (view == null) return;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDoubleClick()) {
                    listener.OnDoubleClick(v);
                } else {
                    listener.OnSingleClick(v);
                }

            }
//            private static final int DOUBLE_CLICK_TIME = 350;        //双击间隔时间350毫秒
//            private boolean waitDouble = true;
//            @SuppressLint("HandlerLeak")
//            private Handler handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    listener.OnSingleClick((View) msg.obj);
//                }
//
//            };
//
//            //等待双击
//            public void onClick(final View v) {
//                if (waitDouble) {
//                    waitDouble = false;        //与执行双击事件
//                    new Thread() {
//
//                        public void run() {
//                            try {
//                                Thread.sleep(DOUBLE_CLICK_TIME);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }    //等待双击时间，否则执行单击事件
//                            if (!waitDouble) {
//                                //如果过了等待事件还是预执行双击状态，则视为单击
//                                waitDouble = true;
//                                Message msg = handler.obtainMessage();
//                                msg.obj = v;
//                                handler.sendMessage(msg);
//                            }
//                        }
//
//                    }.start();
//                } else {
//                    waitDouble = true;
//                    listener.OnDoubleClick(v);    //执行双击
//                }
//            }
        });
    }


}
