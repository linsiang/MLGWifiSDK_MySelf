package com.cf.mlg;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.cf.R;
import com.cf.util.MlgUtil;
import com.cf.wifi.TcpSender;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SetRouter extends baseAvtivity {
    private static final String TAG = "NewMainActivity--->";
    public Button GetRouterWifi;
    public Button SaveRouterWifi;
    private RadioGroup groupDevice;  //groupDevice;
    private RadioGroup groupSensor;//groupSensor;
    protected RadioGroup groupModel; //groupModel;
    private RadioButton ApButton;
    private RadioButton RouterButton;
    private ImageView imageView1;
    private Button setAp;
    public EditText ssid;
    private Button returnSet;
    public EditText pwd;
    private Button RouterBar;
    private Button APBar;
    private ImageButton goVideosdk;
    private ImageButton select_wifi;
    String devip = "192.168.1.1";
    int validwifi = -1;
    String m_ssid = null, m_passwd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_router);
        APBar = findViewById(R.id.constact_AP);
        APBar.setPressed(false);
        APBar.setEnabled(true);
        RouterBar = findViewById(R.id.constact_ROUTER);
        RouterBar.setPressed(true);
        RouterBar.setEnabled(false);
        GetRouterWifi = findViewById(R.id.btn_getwifi);
        SaveRouterWifi = findViewById(R.id.btn_savewifi);
        groupDevice = findViewById(R.id.group_device);
        groupModel = findViewById(R.id.group_model);
        groupSensor = findViewById(R.id.group_sensor);
        ssid = findViewById(R.id.ssid_edit_account);
        pwd = findViewById(R.id.pwd_edit_account);
        returnSet = findViewById(R.id.returnSetMain);
        imageView1 = findViewById(R.id.gotoSystemWifi1);
        RouterButton = findViewById(R.id.ROUTER);
        ApButton = findViewById(R.id.AP);
        setAp = findViewById(R.id.setAP);
        goVideosdk = findViewById(R.id.videoSDK);
        select_wifi = findViewById(R.id.select_wifi);
        ReadCameraConfig();
        ssid.setText(g_wifissid);
        pwd.setText(g_wifipwd);

        if (g_cameramode == 0)
            groupSensor.check(R.id.setSensor_50);
        else if (g_cameramode == 1)
            groupSensor.check(R.id.setSensor_200);
        else if (g_cameramode == 2)
            groupSensor.check(R.id.setSensor_100);
        if (g_wifimode == 0)
            groupModel.check(R.id.AP);
        else if (g_wifimode == 1) {
            groupModel.check(R.id.ROUTER);
            String[] arr = getRouterSSID();
            ssid.setText(arr[0]);
            pwd.setText(arr[1]);
        } else if (g_wifimode == 2)
            groupModel.check(R.id.HOTSPOT);

        if (g_cameratype == 0)
            groupDevice.check(R.id.setBM558);
        if (g_cameratype == 1)
            groupDevice.check(R.id.setBM999);
        if (g_cameratype == 2)
            groupDevice.check(R.id.setBM189);
        imageView1.setOnClickListener(v -> {
            Intent intent = new Intent("android.settings.WIFI_SETTINGS");
            startActivity(intent);
        });

        returnSet.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        APBar.setOnClickListener(v -> {
            APBar.setEnabled(false);
            RouterBar.setEnabled(true);
            RouterBar.setPressed(false);
            startActivity(new Intent(SetRouter.this, SetAp.class));

            finish();

        });
        groupSensor.setOnCheckedChangeListener((group, checkedId) -> {
            if (R.id.setSensor_50 == checkedId) {
                Log.e("iMVR", "x50");
                g_cameramode = 0;
                SaveCameraConfig();
            }
            if (R.id.setSensor_200 == checkedId) {
                Log.e("iMVR", "x200");
                g_cameramode = 1;
                SaveCameraConfig();
            }
            if (R.id.setSensor_100 == checkedId) {
                Log.e("iMVR", "x100");
                g_cameramode = 2;
                SaveCameraConfig();
            }
        });
        goVideosdk.setOnClickListener(v -> {
            startActivity(new Intent(this, VideoSDK.class));
            finish();
        });

        setAp.setOnClickListener(v -> {
            startActivity(new Intent(SetRouter.this, SetAp.class));
            finish();
        });

        groupDevice.setOnCheckedChangeListener((group, checkedId) -> {
            if (R.id.setBM558 == checkedId) {
                g_cameratype = 0;
                Log.e("iMVR", "bm558");
                SaveCameraConfig();
            }
            if (R.id.setBM999 == checkedId) {
                g_cameratype = 1;
                Log.e("iMVR", "bm999");
                SaveCameraConfig();
            }
            if (R.id.setBM189 == checkedId) {
                Log.e("iMVR", "bm189a");
                g_cameratype = 2;
                SaveCameraConfig();
            }
        });

        groupModel.setOnCheckedChangeListener((group, checkedId) -> {
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
        });

        GetRouterWifi.setOnClickListener(v -> {
            GetWifiMsg(ssid,pwd);
        });
        SaveRouterWifi.setOnClickListener(v -> {
            SetRouterWifi(ssid,pwd);
        });
        select_wifi.setOnClickListener(v -> {
          String[] str = SelectWifiName(ssid,pwd);
          ssid.setText(str[0]);
          pwd.setText(str[1]);
            Log.e(TAG, "onCreate: pwd:=========="+read_ssid_pwd(str[0]));
        });
    }

    public String[] SelectWifiName(EditText editText,EditText pwd) {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CHANGE_NETWORK_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE
            };
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
        switch (isOPenGps(this)) {
            case 1:
                break;
            case 2: {

                List<ScanResult> wifiList = getWifiList();
                if (wifiList == null) {
                    //  TastyToast.makeText(this,"未找到WiFi",TastyToast.LENGTH_SHORT,TastyToast.ERROR);
                    return new String[]{editText.getText().toString(),""};
                }
                String[] items = new String[wifiList.size()];
                //  showTv.setText(sb);
                byte j = 0;
                for (ScanResult scanResult : wifiList) {
                    Log.e(TAG, "onCreate: " + scanResult.SSID);
                    if (scanResult.SSID == null || "".equals(scanResult.SSID.trim())) {
                        Log.e(TAG, "onCreate: 出现了null值");
                    } else {
                        Log.e(TAG, "onCreate: 这里应该不会有null了吧");
                        items[j] = scanResult.SSID;
                        j++;
                    }
                }
                byte[] tempwhich = {-1};
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this)
                        .setTitle("请选择")//默认为-1表示默认不选中WiFi
                        .setCancelable(false) //
                        .setSingleChoiceItems(items, -1, (dialog, which) -> {
                            tempwhich[0] = (byte) which;
                        })
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("selet item-which", "onClick: " + tempwhich[0]);
                                //把选中的WiFi名称填入到ssid输入框
                                if (tempwhich[0] != -1) {
                                    editText.setText(items[tempwhich[0]]);
                                    pwd.setText(read_ssid_pwd(editText.getText().toString()));
                                    TastyToast.makeText(getApplicationContext(), "你选择了" + items[tempwhich[0]], TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                alertDialog.show();
            }
            break;
            case 3: {
                gotoSetGps();
            }
            break;
        }
        return new String[]{editText.getText().toString(),pwd.getText().toString()};
    }
    //获取wifi 列表
    public ArrayList<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        //判断wifi 是否开启
        if (wifiManager.isWifiEnabled()) {
            Log.e(TAG, " wifi 打开");
            List<ScanResult> scanWifiList = wifiManager.getScanResults();
            if (scanWifiList != null) {
                ArrayList<ScanResult> list3 = new ArrayList<ScanResult>();
                for (int i = 0; i < scanWifiList.size(); i++) {
                    if (!"".equals(scanWifiList.get(i).SSID.trim())) {
                        list3.add(scanWifiList.get(i));
                    }
                }
                return list3;
            } else {
                Log.e(TAG, "可能没打开gps或者附近没有WiFi");
                TastyToast.makeText(this, "没打开gps或者附近没有WiFi", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        } else {
            Log.e(TAG, " wifi 关闭");
            TastyToast.makeText(this, "系统WiFi未打开。。。", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        }
        return null;
    }


    public void GetWifiMsg(EditText editText1,EditText editText2)
    {
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
            //   String Device = "Find Device-> ip:" + devip + "WifiMode:-->" + workmode;
            //  MlgUtil.ShowMessage(Device, SetRouter.this);
            TastyToast.makeText(this, "devIP  :" + devip + "  \n" + workmode + "模式", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            if (getwifi(editText1,editText2) == 1) {
                SaveCameraConfig();
            }
        } else {
            //  MlgUtil.ShowMessage("no Device!", NewMainActivity.this);
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("no device!")
                    .show();
        }
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


    public void SetRouterWifi(EditText e_ssid,EditText e_pwd)
    {
        Log.e("iMVR", "btn_wifi_confirm");
        //保存设置
        if (FindDevice() == 1) {
            ShiwConfirmDiagNew(e_ssid,e_pwd);
        } else {
            // MlgUtil.ShowMessage("no Device", NewMainActivity.this);
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("no device!")
                    .show();
        }
    }





    public int getwifi(EditText editssid,EditText editpwd) {
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
                editssid.setText(m_ssid.trim());
                editpwd.setText(m_passwd.trim());
            } else {
                editpwd.setText(m_passwd.trim());
                editssid.setText(m_ssid.trim());
            }
        } else {
            editssid.setText(null);
            editpwd.setText(null);
            //MlgUtil.ShowMessage("check your net connect", SetRouter.this);
            TastyToast.makeText(this, "check your net connect", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        }
        return validwifi;
    }

    private int g_cameratype = 0;
    private int g_cameramode = 0;
    private String g_wifiip = null;
    private String g_wifissid = null;
    private String g_wifipwd = null;
    private int g_wifichannel = 6;
    protected int g_wifimode = 0;
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

    public String[] getRouterSSID() {
        String[] arr = new String[2];
        SharedPreferences sharedPreferences = getSharedPreferences("SSID_PWD", MODE_PRIVATE);
        String ssid = sharedPreferences.getString("ssid", "TP-LINK_76E1");
        String pwd = sharedPreferences.getString("pwd", "aa123456");
        arr[0] = ssid;
        arr[1] = pwd;
        return arr;
    }

    public void write_ssid_pwd(String key_ssid, String value_pwd) {
        SharedPreferences.Editor shard = getSharedPreferences("ssid_pwd", MODE_PRIVATE).edit();
        shard.putString(key_ssid, value_pwd);  //以键值对的方式存储wifi名称和密码
        shard.apply();
    }

    public String read_ssid_pwd(String key_ssid) {
        SharedPreferences sharedPreferences = getSharedPreferences("ssid_pwd", MODE_PRIVATE);
        Log.e(TAG, "read_ssid_pwd: 这里是读取配置文件"+ sharedPreferences.getString(key_ssid, "null123"));
        return sharedPreferences.getString(key_ssid, "null123");
    }

    void ShiwConfirmDiagNew(EditText editssid1, EditText editpwd2) {
        SweetAlertDialog sw = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        sw.setTitle("修改路由模式");
        sw.setContentText("确定要修改为路由模式吗？");
        sw.setConfirmText("确定");
        sw.setConfirmClickListener(SweetAlertDialog -> {
            g_wifissid = editssid1.getText().toString().trim();
            g_wifipwd = editpwd2.getText().toString().trim();
            if (g_wifimode == 0) {
                if (g_wifipwd.getBytes().length != 8) {
                    MlgUtil.ShowMessage("请设置八位长度的密码", SetRouter.this);
                    return;
                }
            }
            int mode = 0;
            if (g_wifimode != 0) {  //ap 0;  else 3;
                mode = 3;
            }
            if (g_wifimode == 0) {
                // MlgUtil.ShowMessage("暂不支持AP模式的修改", NewMainActivity.this);
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, cn.pedant.SweetAlert.SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText("此页面暂不支持AP模式的修改");
                sweetAlertDialog.show();
                sw.cancel();
            } else {
                //判断是不是Router模式
                if (RouterButton.isChecked()) {
                    saveRouterSSID(g_wifissid, g_wifipwd);
                    write_ssid_pwd(g_wifissid,g_wifipwd);
                }
                if (TcpSender.setwifiinfo(mode, g_wifichannel, g_wifissid, g_wifipwd) == 0) {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, cn.pedant.SweetAlert.SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setTitleText("设置成功");
                    sweetAlertDialog.setCanceledOnTouchOutside(false);
                    sweetAlertDialog.show();
                    // MlgUtil.ShowMessage("save successful", NewMainActivity.this);
                } else {
                    //  MlgUtil.ShowMessage("set failed", NewMainActivity.this);
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, cn.pedant.SweetAlert.SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitleText("设置失败");
                    sweetAlertDialog.setCanceledOnTouchOutside(false);
                    sweetAlertDialog.show();
                }
                sw.cancel();
            }
        });
        sw.setCancelButton("取消", SweetAlertDialog -> {
            sw.cancel();
        });
        sw.show();
    }

    //重写案件程序，按下返回案件后返回到主界面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    void ShowConfirmDialog() {
        AlertDialog.Builder normalDia = new AlertDialog.Builder(SetRouter.this);
        normalDia.setIcon(R.drawable.bar_news);
        normalDia.setTitle("");
        normalDia.setMessage("Save change?");
        normalDia.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            //  @Override
            public void onClick(DialogInterface dialog, int which) {
                g_wifissid = ssid.getText().toString().trim();
                g_wifipwd = pwd.getText().toString().trim();
                if (g_wifimode == 0) {
                    if (g_wifipwd.getBytes().length != 8) {
                        MlgUtil.ShowMessage("请设置八位长度的密码", SetRouter.this);
                        return;
                    }
                }
                int mode = 0;
                if (g_wifimode != 0) {  //ap 0;  else 3;
                    mode = 3;
                }
                if (g_wifimode == 0) {
                    MlgUtil.ShowMessage("暂不支持AP模式的修改", SetRouter.this);
                } else {
                    //判断是不是Router模式
                    if (RouterButton.isChecked()) {
                        saveRouterSSID(g_wifissid, g_wifipwd);
                        write_ssid_pwd(g_wifissid,g_wifipwd);
                    }
                    if (TcpSender.setwifiinfo(mode, g_wifichannel, g_wifissid, g_wifipwd) == 0) {
                        MlgUtil.ShowMessage("save successful", SetRouter.this);
                    } else {
                        MlgUtil.ShowMessage("set failed", SetRouter.this);
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

    public int isOPenGps(Activity activity) {

        LocationManager lm;//【位置管理】
        lm = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                TastyToast.makeText(this, "没有开启定位权限", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                return 1;
            } else {
                return 2;
            }
        } else {
            //Toast.makeText(activity, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            return 3;
        }
    }

    void gotoSetGps() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText("gps未打开，是否去打开系统的gps位置信息？");
        sweetAlertDialog.setConfirmText("确定");
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.setConfirmClickListener(v -> {
            sweetAlertDialog.cancel();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        });
        sweetAlertDialog.setCancelButton("取消", sweetAlertDialog1 -> {
            sweetAlertDialog.cancel();
        });
        sweetAlertDialog.show();
    }



}