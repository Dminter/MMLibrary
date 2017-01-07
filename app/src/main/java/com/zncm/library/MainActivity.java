package com.zncm.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Lib;
import com.zncm.library.data.LocLib;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.data.SpConstant;
import com.zncm.library.ui.HomeTabActivity;
import com.zncm.library.ui.TabMyLibActivity;
import com.zncm.library.utils.ActionService;
import com.zncm.library.utils.DbHelper;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.NotificationMonitor;
import com.zncm.library.utils.XUtil;

import java.util.List;

import de.greenrobot.event.EventBus;


public class MainActivity extends Activity {

    Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);
        MobclickAgent.onEvent(ctx, "open_app");
        startService(new Intent(this, NotificationMonitor.class));
        startService(new Intent(this, ActionService.class));
        Tasks.executeInBackground(ctx, new BackgroundWork<Boolean>() {
            @Override
            public Boolean doInBackground() throws Exception {
                try {
                    List<LocLib> locLibs = Dbutils.getLocLib(null, 0);
                    if (locLibs.size() == 0) {
                        DbHelper.importLocCsv(ctx, null);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        }, new Completion<Boolean>() {
            @Override
            public void onSuccess(Context context, Boolean result) {
                if (result) {
                    XUtil.tShort("模板数据已成功导入~");
                    EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LOCLIB.getValue()));
                }
            }

            @Override
            public void onError(Context context, Exception e) {
            }
        });

        Integer version_code = MySp.get(SpConstant.app_version_code, Integer.class, 0);
        Integer versionNow = XUtil.getVersionCode(MainActivity.this);
        if (versionNow != null && versionNow.intValue() > version_code.intValue()) {
            MySp.put(SpConstant.app_version_code, versionNow);
            XUtil.initRss();
            XUtil.initNetLib();
        }
        intoApp();
    }

    private void intoApp() {
        Intent intent = new Intent(this, TabMyLibActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


}
