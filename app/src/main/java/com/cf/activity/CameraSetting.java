package com.cf.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.cf.R;
import com.cf.util.MlgUtil;
import com.cf.wifi.TcpSender;
import java.io.UnsupportedEncodingException;
public class CameraSetting extends baseAvtivity {
    public Button btn_to_back;
    public Button btn_to_main;
    public Button btn_wifi_confirm;
    public Button btn_wifi_searchdevice;
    private RadioGroup myRadioGroupproduct;
    private RadioGroup myRadioGroupmode;
    private RadioGroup myRadioGroupwifi;
    private RadioButton ApButton;
    private RadioButton RouterButton;
    public EditText ssid;
    public EditText pwd;
    public EditText channel;
    String devip = "192.168.1.1";
    int validwifi = -1;
    String m_ssid = null, m_passwd = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.camera_settingtcf);

        btn_to_back = findViewById(R.id.btn_to_back);
        btn_to_main = findViewById(R.id.btn_to_main);

        btn_wifi_confirm = findViewById(R.id.btn_wifi_confirm);
        btn_wifi_searchdevice = findViewById(R.id.btn_wifi_searchdevice);

        myRadioGroupproduct = findViewById(R.id.myRadioGroupproduct);
        myRadioGroupwifi = findViewById(R.id.myRadioGroupwifi);
        myRadioGroupmode = findViewById(R.id.myRadioGroupmode);

        ApButton = findViewById(R.id.AP);
        RouterButton = findViewById(R.id.ROUTER);
        ssid = findViewById(R.id.ssid);
        pwd = findViewById(R.id.pwd);
        channel = findViewById(R.id.channel);
        ReadCameraConfig();
        ssid.setText(g_wifissid);
        pwd.setText(g_wifipwd);
        channel.setText(String.valueOf(g_wifichannel));
        if (g_cameramode == 0)
            myRadioGroupmode.check(R.id.x50);
        else if (g_cameramode == 1)
            myRadioGroupmode.check(R.id.x200);
        if (g_wifimode == 0)
            myRadioGroupwifi.check(R.id.AP);
        else if (g_wifimode == 1) {
            myRadioGroupwifi.check(R.id.ROUTER);
            String[] arr = getRouterSSID();
            ssid.setText(arr[0]);
            pwd.setText(arr[1]);
        } else if (g_wifimode == 2)
            myRadioGroupwifi.check(R.id.HOTSPOT);

        if (g_cameratype == 0)
            myRadioGroupproduct.check(R.id.bm558);
        if (g_cameratype == 1)
            myRadioGroupproduct.check(R.id.bm999);
        if (g_cameratype == 2)
            myRadioGroupproduct.check(R.id.bm189a);

        myRadioGroupmode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.x50 == checkedId) {
                    Log.e("iMVR", "x50");
                    g_cameramode = 0;
                    SaveCameraConfig();
                }
                if (R.id.x200 == checkedId) {
                    Log.e("iMVR", "x200");
                    g_cameramode = 1;
                    SaveCameraConfig();
                }
            }
        });

        myRadioGroupproduct.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.bm558 == checkedId) {
                    g_cameratype = 0;
                    Log.e("iMVR", "bm558");
                    SaveCameraConfig();
                }
                if (R.id.bm999 == checkedId) {
                    g_cameratype = 1;
                    Log.e("iMVR", "bm999");
                    SaveCameraConfig();
                }
                if (R.id.bm189a == checkedId) {
                    Log.e("iMVR", "bm189a");
                    g_cameratype = 2;
                    SaveCameraConfig();
                }
            }
        });

        myRadioGroupwifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.AP == checkedId) {
                    Log.e("iMVR", "AP");
                    g_wifimode = 0;
                    SaveCameraConfig();
                }
                if (R.id.ROUTER == checkedId) {
                    Log.e("iMVR", "ROUTER");
                    String[] arr = getRouterSSID();
                    ssid.setText(arr[0]);
                    pwd.setText(arr[1]);
                    g_wifimode = 1;
                    SaveCameraConfig();
                }
                if (R.id.HOTSPOT == checkedId) {
                    Log.e("iMVR", "HOTSPOT");
                    g_wifimode = 2;
                    SaveCameraConfig();
                }

            }
        });

        btn_to_back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Log.e("iMVR", "btn_to_back");
                Intent intent = new Intent();
                intent.setClass(CameraSetting.this, SetRouter.class);
                startActivity(intent);
                finish();
            }
        });
        btn_to_main.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Log.e("iMVR", "btn_to_main");
                Intent intent = new Intent();
                intent.setClass(CameraSetting.this, SetRouter.class);
                startActivity(intent);
                finish();
            }
        });


        btn_wifi_confirm.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Log.e("iMVR", "btn_wifi_confirm");
                //保存设置
                    ShowConfirmDialog();
            }
        });
        btn_wifi_searchdevice.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Log.e("iMVR", "btn_wifi_searchdevice=============");
                String workmode;
                byte findDevice_ok = 0;
                for (int i = 0; i < 5; i++) {
                    if (FindDevice() == 1) {
                        findDevice_ok = 1;
                        break;
                    }
                }
                if (findDevice_ok == 1) {
                    //	Log.e("iMVR","Find device ok");
                    devip.trim();
                    if (devip.contains("192.168.43")) {
                        g_wifimode = 2;
                        myRadioGroupwifi.check(R.id.HOTSPOT);
                        workmode = "Hotspot";
                    } else {
                        if (devip.equals("192.168.1.1")) {
                            g_wifimode = 0;
                            myRadioGroupwifi.check(R.id.AP);
                            workmode = "AP";
                        } else {
                            g_wifimode = 1;
                            myRadioGroupwifi.check(R.id.ROUTER);
                            workmode = "Router";
                        }
                    }
                    String Device = "Find Device-> ip:" + devip + "WifiMode:-->" + workmode;
                    MlgUtil.ShowMessage(Device, CameraSetting.this);
                    if (getwifi() == 1) {
                        SaveCameraConfig();
                    }
                } else {
                    MlgUtil.ShowMessage("no Device!", CameraSetting.this);
                }
            }
        });
    }

    private void ShowConfirmDialog() {
        AlertDialog.Builder normalDia = new AlertDialog.Builder(CameraSetting.this);
        normalDia.setIcon(R.drawable.icon);
        normalDia.setTitle("");
        normalDia.setMessage("Save change?");
        normalDia.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            //  @Override
            public void onClick(DialogInterface dialog, int which) {

                g_wifissid = ssid.getText().toString().trim();
                g_wifipwd = pwd.getText().toString().trim();
                String wifichannel = channel.getText().toString().trim();
                g_wifichannel = Integer.parseInt(wifichannel);
                if (g_wifimode == 0) {
                    if (g_wifipwd.getBytes().length != 8) {
                        MlgUtil.ShowMessage("请设置八位长度的密码", CameraSetting.this);
                        return;
                    }
                }
                if (g_wifichannel < 1 || g_wifichannel > 10) {
                    MlgUtil.ShowMessage("please set cahnnel 1-->10", CameraSetting.this);
                    return;
                }
                int mode = 0;
                if (g_wifimode != 0) {  //ap 0;  else 3;
                    mode = 3;
                }
                if (g_wifimode == 0) {
                    MlgUtil.ShowMessage("暂不支持AP模式的修改", CameraSetting.this);
                } else {
                    //判断是不是Router模式
                    if (RouterButton.isChecked()) {
                        saveRouterSSID(g_wifissid, g_wifipwd);
                    }
                    if (TcpSender.setwifiinfo(mode, g_wifichannel, g_wifissid, g_wifipwd) == 0) {
                        MlgUtil.ShowMessage("save successful", CameraSetting.this);
                    } else {
                        MlgUtil.ShowMessage("set failed", CameraSetting.this);
                    }
                }
            }
        });
        normalDia.setNegativeButton("No", new DialogInterface.OnClickListener() {
            // @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        normalDia.create().show();
    }

    public int FindDevice() {
        int findevice = 0;
        byte[] ip = new byte[16];
        if (g_wifimode == 2)
            TcpSender.SetDeviceType(2); //hotspot mode
        else
            TcpSender.SetDeviceType(0); //ap  or router mode!

        findevice = TcpSender.SearchDevice(ip);
        if (findevice == 1) {
            try {
                devip = new String(ip, "GB2312");  //以gb2312的格式进行解码，返回的是字符串
                devip = devip.trim();   //去除头尾空格
                Log.e("iMVR", "--------devip----------------:" + devip);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return findevice;
    }

    public int getwifi() {
        byte[] t_ssid = new byte[32];
        byte[] t_pwd = new byte[20];
        int ap = 1, routerorhotspot = 4;
        int mode = 0;
        if (g_wifimode == 0) {
            mode = ap;
        } else {
            mode = routerorhotspot;
        }
        validwifi = TcpSender.getwifiinfo(mode);

        if (validwifi == 100) {
            TcpSender.getwifissid(t_ssid);
            TcpSender.getwifipwd(t_pwd);
            try {
                m_ssid = new String(t_ssid, "GB2312");
                m_passwd = new String(t_pwd, "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (g_wifimode == 0) {
                ssid.setText(R.string.default_ap_ssid);
                pwd.setText(R.string.default_ap_pwd);
            } else {
                pwd.setText(m_passwd.trim());
                ssid.setText(m_ssid.trim());
            }
        } else {
            pwd.setText(null);
            ssid.setText(null);
            MlgUtil.ShowMessage("check your net connect", CameraSetting.this);
        }
        return validwifi;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            Log.e("iMVR", "stop video");
            Intent intent = new Intent();
            intent.setClass(CameraSetting.this, SetRouter.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }



    private int g_cameratype = 0;
    private int g_cameramode = 0;
    private String g_wifiip = null;
    private String g_wifissid = null;
    private String g_wifipwd = null;

    private int g_wifichannel = 6;
    private int g_wifimode = 0;
    private int g_wififinddevice = 0;

    private void ReadCameraConfig() {
        SharedPreferences sharedata = getSharedPreferences("tcfcameraN", 0);
        g_cameratype = sharedata.getInt("cameratype", 0);
        g_cameramode = sharedata.getInt("cameramode", 0);
        g_wifiip = sharedata.getString("wifiip", "192.168.1.1");
        g_wifissid = sharedata.getString("wifissid", "mlgxxx");
        g_wifipwd = sharedata.getString("wifipwd", "88888888");
        g_wifichannel = sharedata.getInt("wifichannel", 6);
        g_wifimode = sharedata.getInt("wifimode", 0);
        g_wififinddevice = sharedata.getInt("wififinddevice", 0);
    }

    void SaveCameraConfig() {
        SharedPreferences.Editor sharedata = getSharedPreferences("tcfcameraN", 0).edit();
        sharedata.putInt("cameratype", g_cameratype);
        sharedata.putInt("cameramode", g_cameramode);
        sharedata.putString("wifiip", g_wifiip);
        sharedata.putString("wifissid", g_wifissid);
        sharedata.putString("wifipwd", g_wifipwd);
        sharedata.putInt("wifichannel", g_wifichannel);
        sharedata.putInt("wifimode", g_wifimode);
        sharedata.putInt("wififinddevice", g_wififinddevice);
        sharedata.apply();
    }

    void saveRouterSSID(String SSID, String pwd) {
        SharedPreferences sharedPreferences = getSharedPreferences("SSID_PWD", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ssid", SSID);
        editor.putString("pwd", pwd);
        editor.apply();
    }

    private String[] getRouterSSID() {
        String[] arr = new String[2];
        SharedPreferences sharedPreferences = getSharedPreferences("SSID_PWD", MODE_PRIVATE);
        String ssid = sharedPreferences.getString("ssid", "TP-LINK_2FEA");
        String pwd = sharedPreferences.getString("pwd", "aa888888");
        arr[0] = ssid;
        arr[1] = pwd;
        return arr;
    }
}
