package com.cf.activity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.cf.R;
import com.cf.cf685.Video;
import com.sdsmdg.tastytoast.TastyToast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends SetRouter {

    private ImageButton main_wifi;
    private ImageButton main_settings;
    private ImageButton main_language;
    private ImageButton main_video;
    private ImageButton logoutSystem;
    private Button textbutton;
    private ImageView light_flag;
    private TextView text_flag;
    private boolean flag_device = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        main_wifi = findViewById(R.id.main_wifi);
        main_settings = findViewById(R.id.main_setting);
        main_language = findViewById(R.id.main_language);
        main_video = findViewById(R.id.main_camera);
        logoutSystem = findViewById(R.id.logoutSystem);
        textbutton = findViewById(R.id.testbutton);
        light_flag = findViewById(R.id.connect_light_flag);
        text_flag = findViewById(R.id.connect_text_flag);
        new Thread(() -> {
            if (FindDevice() == 1) {
                text_flag.setText("已连接设备");
                text_flag.setTextColor(getResources().getColor(R.color.main_green_color));
                light_flag.setBackgroundResource(R.drawable.light_on);
            }
            while (flag_device) {
                try {
                    Thread.sleep(10000);
                    if (FindDevice() == 1) {
                        text_flag.setText("已连接设备");
                        text_flag.setTextColor(getResources().getColor(R.color.main_green_color));
                        light_flag.setBackgroundResource(R.drawable.light_on);
                    } else {
                        text_flag.setText("未连接设备");
                        text_flag.setTextColor(getResources().getColor(R.color.gray_btn_bg_pressed_color));
                        light_flag.setBackgroundResource(R.drawable.light_off);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        textbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetDeBugDialog();
            }
        });
        InitLanguage();
        logoutSystem.setOnClickListener(v -> {
            SweetAlertDialog sw = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            sw.setTitleText("退出程序!");
            sw.setContentText("是否要退出本程序？");
            sw.setCanceledOnTouchOutside(false);
            sw.setCancelButton("取消", sweetAlertDialog -> {
                sw.dismiss();//点击取消的操作
            });
            sw.setConfirmText("确定").setConfirmClickListener(sweetAlertDialog -> {
                sw.dismiss();
                finish(); //点击确定的操作
            });
            sw.show();

        });
        main_wifi.setOnClickListener(v -> {
            // startActivity(new Intent("android.settings.WIFI_SETTINGS"));
            startActivity(new Intent("android.settings.WIFI_SETTINGS"), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            //  overridePendingTransition(R.anim.pop_enter, R.anim.pop_out);
        });
        main_settings.setOnClickListener(v -> {
            //    startActivity(new Intent(this,NewMainActivity.class));
            startActivity(new Intent(this, SetRouter.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            //  overridePendingTransition(R.anim.pop_enter, R.anim.pop_out);
            finish();
        });
        main_language.setOnClickListener(v -> {
            if (ReadLanguageConfig() == 1) {
                SaveLanguageConfig(0);
            } else {
                SaveLanguageConfig(1);
            }
            setLanguage(); //切换语言
            setContentView(R.layout.activity_main);
            // startActivity(new Intent(this,MainActivity.class),ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("isSetLanguage", 1);
            intent.putExtra("language", ReadLanguageConfig());
            startActivity(intent);
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_out);
            finish();
        });
        main_video.setOnClickListener(v -> {
            startActivity(new Intent(this, VideoSDK.class));
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_out);
            finish();
        });
    }

    private void InitLanguage() {
        int isSetlanguage = getIntent().getIntExtra("isSetLanguage", -1);
        int nowlanguage = getIntent().getIntExtra("language", -1);
        Log.e("fuck you", "InitLanguage: " + isSetlanguage);
        Log.e("fuck you", "InitLanguage: " + nowlanguage);
        if (isSetlanguage != -1) {
            if (nowlanguage == 1) {
                main_language.setBackgroundResource(R.drawable.language_ch);
                TastyToast.makeText(getApplicationContext(), "当前语言设置为中文", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            }
            if (nowlanguage == 0) {
                main_language.setBackgroundResource(R.drawable.language_en);
                TastyToast.makeText(getApplicationContext(), "Now was set to english", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            }
        }
    }


    private void showSetDeBugDialog() {

        AlertDialog.Builder setDeBugDialog = new AlertDialog.Builder(this);
        //获取界面
        View dialogView = LayoutInflater.from(this).inflate(R.layout.system_admin_psw_alert_dialog, null);
        //将界面填充到AlertDiaLog容器
        setDeBugDialog.setView(dialogView);
        //初始化控件
        final EditText pwd1 = (EditText) dialogView.findViewById(R.id.pwd_edit_account);
        final EditText ssid1 = dialogView.findViewById(R.id.ssid_edit_account);
        Button okButton = (Button) dialogView.findViewById(R.id.sap_alert_dialog_ok);
        final RadioGroup main_radio_group = dialogView.findViewById(R.id.main_group_model);
        //取消点击外部消失弹窗
        setDeBugDialog.setCancelable(false);
        //创建AlertDiaLog
        setDeBugDialog.create();
        //AlertDiaLog显示
        final AlertDialog customAlert = setDeBugDialog.show();
        Window window = customAlert.getWindow();
        //  window.setBackgroundDrawableResource(R.drawable.border);
        window.setLayout(this.getResources().getDisplayMetrics().widthPixels * 6 / 10, this.getResources().getDisplayMetrics().heightPixels);
        //   getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置Dialog背景透明
        //   getWindow().setDimAmount(0f);//设置Dialog窗口后面的透明度
        String[] arr0 = getRouterSSID();
        ssid1.setText(arr0[0]);
        pwd1.setText(arr0[1]);
        //设置自定义界面的点击事件逻辑
        dialogView.findViewById(R.id.select_wifi1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] str = SelectWifiName(ssid1, pwd1);
                ssid1.setText(str[0]);
                pwd1.setText(str[1]);
              /*  ssid1.setText(str);
                pwd1.setText(read_ssid_pwd(str));
                Log.e("asasdasddsa", "onClick: ========="+read_ssid_pwd(str));*/
            }
        });
        main_radio_group.setOnCheckedChangeListener((group, checkedId) -> {
            if (R.id.main_AP == checkedId) {
                g_wifimode = 0;
                ssid1.setText("mlg_XXXX");
                pwd1.setText("88888888");
                SaveCameraConfig();
            } else if (R.id.main_ROUTER == checkedId) {
                Log.e("iMVR", "ROUTER");
                String[] arr = getRouterSSID();
                ssid1.setText(arr[0]);
                pwd1.setText(arr[1]);
                g_wifimode = 1;
                SaveCameraConfig();
            } else if (R.id.HOTSPOT == checkedId) {
                g_wifimode = 2;
                SaveCameraConfig();
            }
        });

        dialogView.findViewById(R.id.system_admin_psw_alert_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customAlert.dismiss();
            }
        });

        //保存wifi设置
        okButton.setOnClickListener(view -> {
            SetRouterWifi(ssid1, pwd1);
            write_ssid_pwd(ssid1.getText().toString(), pwd1.getText().toString());
            saveRouterSSID(ssid1.getText().toString(), pwd1.getText().toString());
        });
        //获取wifi信息
        dialogView.findViewById(R.id.sap_alert_dialog_cancel).setOnClickListener(view -> {
            //获取wifi
            GetWifiMsg(ssid1, pwd1);
            SharedPreferences sharedata = getSharedPreferences("tcfcameraN", 0);
            int wifimode = sharedata.getInt("wifimode", 0);
            switch (wifimode) {
                case 0:
                    main_radio_group.check(R.id.main_AP);
                    break;
                case 1:
                    main_radio_group.check(R.id.main_ROUTER);
                    break;
                case 2:
                    main_radio_group.check(R.id.main_HOTSPOT);
                    break;
            }
        });
    }
}