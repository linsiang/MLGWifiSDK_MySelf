package com.cf.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cf.R;
import com.cf.util.MlgUtil;

public class SetAp extends SetRouter {
    private Button returnSet;
    public Button GetApWifi;
    public Button SaveApWifi;
    public EditText channel;
    private ImageView imageView;
    private Button setButton;
    private Button RouterBar;
    private Button APBar;
    private ImageView videoSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ap);
        GetApWifi = findViewById(R.id.ApGetWifi);
        returnSet = findViewById(R.id.returnSet);
        SaveApWifi = findViewById(R.id.saveApWifi);
        channel = findViewById(R.id.channel);
        imageView = findViewById(R.id.login_picture);
        setButton = findViewById(R.id.setAP);
        setButton.setText("设置路由");
        videoSDK = findViewById(R.id.videoSDK);
        videoSDK.setOnClickListener(v -> {
            startActivity(new Intent(this, VideoSDK.class));
            finish();
        });
        setButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SetRouter.class));
            finish();
        });
        returnSet.setOnClickListener(v -> {
            startActivity(new Intent(this, SetRouter.class));
            finish();
        });

        APBar = findViewById(R.id.constact_AP);
        APBar.setPressed(true);
        APBar.setEnabled(false);
        RouterBar = findViewById(R.id.constact_ROUTER);
        RouterBar.setPressed(false);
        RouterBar.setEnabled(true);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent("android.settings.WIFI_SETTINGS");
            startActivity(intent);
        });

        RouterBar.setOnClickListener(v -> {
            RouterBar.setEnabled(false);
            APBar.setEnabled(true);
            APBar.setPressed(false);
            startActivity(new Intent(SetAp.this, SetRouter.class));
            finish();
        });

        SaveApWifi.setOnClickListener(v -> {
            Log.e("iMVR", "btn_wifi_confirm");
            //保存设置
            if (FindDevice() == 1) {
                ShowConfirmDialog();
            } else {
                MlgUtil.ShowMessage("no Device", SetAp.this);
            }

        });
        GetApWifi.setOnClickListener(v -> {
            Log.e("iMVR", "btn_wifi_searchdevice=============");
            String workmode;
            if (FindDevice() == 1) {
                //	Log.e("iMVR","Find device ok");
                devip.trim();
                if (devip.contains("192.168.43")) {
                    g_wifimode = 2;
                    groupModel.check(R.id.HOTSPOT);
                    workmode = "Hotspot";
                } else {
                    if (devip.equals("192.168.1.1")) {
                        g_wifimode = 0;
                        groupModel.check(R.id.AP);
                        workmode = "AP";
                    } else {
                        g_wifimode = 1;
                        groupModel.check(R.id.ROUTER);
                        workmode = "Router";
                    }
                }
                String Device = "Find Device-> ip:" + devip + "WifiMode:-->" + workmode;
                MlgUtil.ShowMessage(Device, SetAp.this);
                if (getwifi(ssid,pwd) == 1) {
                    SaveCameraConfig();
                }
            } else {
                MlgUtil.ShowMessage("no Device!", SetAp.this);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK || keyCode==KeyEvent.KEYCODE_HOME)
        {
            startActivity(new Intent(this, SetRouter.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }



}