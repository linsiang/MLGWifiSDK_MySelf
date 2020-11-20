package com.cf.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cf.R;
import com.cf.cf685.Video;
import com.cf.adapter.BmpAdapter;
import com.cf.pojo.BmpList;
import com.cf.util.BmpUtil;
import com.cf.util.MlgUtil;
import com.cf.wifi.DevGroup;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.cf.util.MlgUtil.ShowLog;
import static com.cf.util.MlgUtil.ShowMessage;
import static com.cf.util.MlgUtil.hideKeyBoard;

//import androidx.appcompat.app.AlertDialog;

////////////////////Video.jar -->libs////////////////

/////////////////////////////////////////////////////
public class VideoSDK extends baseAvtivity {

    public String picpath;
    int screen_height = 0, screen_width = 0;
    public Video VideoDecoder;
    private boolean running = false;
    public ImageView mypictureView;
    private int videoPort = 40003;
    private EditText EditPort;
    private AlertDialog alertDialog1; //信息框
    public SurfaceView mSurfaceView;
    private ImageButton power_on_off;
    boolean isScale = true;
    private SharedPreferences sharedPreferences;
    public String CurrentJPGFile;
    private ListView video_bmp_listview;
    List<BmpList> list;
    List<String> listPath;
    ArrayAdapter<BmpList> bmpadapter;
    private ViewGroup.LayoutParams mrLayoutParams; //默认布局
    private boolean isbackPlay = false;
    private final BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        final String SYSTEM_REASON = "reason";
        final String SYSTEM_HOME_KEY = "homekey";
        final String SYSTEM_HOME_KEY_LONG = "recentapps";

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    Log.e("iMVR", "onReceive!");
                    unregisterReceiver(mHomeKeyEventReceiver);
                    StopVideo();
                    Log.e("IMVR", "this is the turn back of page======================== ");
                    Intent intentM = new Intent();
                    intentM.setClass(VideoSDK.this, SetRouter.class);
                    startActivity(intentM);
                    finish();
                } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
                    //表示长按home键,显示最近使用的程序列表
                    Log.e("iMVR", "Home!");
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10) {
                ShowMessage("There is no JPG picture in the TF Card!", VideoSDK.this);
                Log.e("iMVR", "============msg.what==10=================");
            }
            if (msg.what == 11) {
                ShowMessage("The current picture was deleted!", VideoSDK.this);
                Log.e("iMVR", "Delete CurrentJPGFile:" + CurrentJPGFile);
                Log.e("iMVR", "============msg.what==11=================");
            }
            if (msg.what == 12) {
                ShowMessage("All the files are deleted in the TF Card!", VideoSDK.this);
                Log.e("iMVR", "============msg.what==12=================");
                //DeleteAllJPG();
                //image_screenshot.setVisibility(View.INVISIBLE);
                //mSurfaceView.setVisibility(View.VISIBLE);
                //delAllFile(g_jpgpath);
            }
            if (msg.what == 13) {
                Log.e("iMVR", "msg.what==13:-->" + isScale);
                //	image_screenshot.setVisibility(View.INVISIBLE);
                //	mSurfaceView.setVisibility(View.VISIBLE);
            }
            if (msg.what == 6) {  //按键抓拍，设备插卡时候返回此值
                String filename = msg.getData().getString("filename");
                boolean newfile = msg.getData().getBoolean("newfile");
                Log.e("iMVR", "filename->6:" + filename);
                CurrentJPGFile = filename;
                Bitmap b = BitmapFactory.decodeFile(filename); //测试用途 ，显示抓到的图片！
                mypictureView.setImageBitmap(b);
                Log.e("iMVR", "============msg.what==6=================");
            }
            if (msg.what == 5) {  //水分有份显示，oil 油分值，water水分值
                int oil = msg.getData().getInt("oil");
                int water = msg.getData().getInt("water");
                // Toast.makeText(getApplicationContext(), filename, Toast.LENGTH_SHORT).show();
                Log.e("iMVR", "oil :" + oil + "water :" + water);
                // Toast.makeText(VideoSDK.this, "油分"+oil+"水分"+water, Toast.LENGTH_SHORT).show();
            }

            if (msg.what == 4) { //按键抓拍
                String filename = msg.getData().getString("filename");//抓到的图片文件名字！
                Log.e("iMVR", "filename:" + filename);
                String jpgfilepath = picpath + filename;
                Log.e("iMVR", "jpgfile:" + jpgfilepath);
                permissionActivity.verifyStoragePermissions(VideoSDK.this);
                Bitmap b = BitmapFactory.decodeFile(jpgfilepath); //测试用途 ，显示抓到的图片！
                mypictureView.setImageBitmap(b);
                ShowLog("============msg.what==4=================");
                BmpList bm = new BmpList(b, filename);
                list.add(bm);
                bmpadapter.notifyDataSetChanged();
            }
            if (msg.what == 1) { //锁屏
                Log.e("iMVR", "diagnosos--->lock ->msg.what==1");
                //  Toast.makeText(getApplicationContext(), "lock", Toast.LENGTH_SHORT).show();
            }
            if (msg.what == 2) { //解除锁屏
                Log.e("iMVR", "diagnosos--->unlock");
                // Toast.makeText(getApplicationContext(), "unlock", Toast.LENGTH_SHORT).show();
                ShowLog("============msg.what==2=================");
            }
            if (msg.what == 3) { //没连接上设备！
                Log.e("iMVR", "net is error!");
                ShowLog("============msg.what==3=================");

            }
            if (msg.what == 20) {  //左边按键
                ShowLog("============msg.what==20=================");
                ShowMessage("按键左", VideoSDK.this);
            }
            if (msg.what == 21) { //右边按键
                ShowLog("============msg.what==21=================");
                ShowMessage("按键右", VideoSDK.this);
            }
            if (msg.what == 22) { //删除按键
                ShowLog("============msg.what==22=================");
                ShowMessage("按键下", VideoSDK.this);
            }
            if (msg.what == 23) { //重置ap
                ShowLog("============msg.what==23=================");
                ShowMessage("--重置ap模式--", VideoSDK.this);
            }
        }
    };

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screen_width = metric.widthPixels;
        screen_height = metric.heightPixels;
        setContentView(R.layout.cameracv1920);
        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        mSurfaceView = findViewById(R.id.surfaceView1);
        mSurfaceView.setFocusable(true);
        mSurfaceView.setFocusableInTouchMode(true);
        mSurfaceView.setVisibility(View.VISIBLE);
        Button videobuttonCapture = findViewById(R.id.face_capturebtn);
        mypictureView = findViewById(R.id.mypicture);
        mypictureView.setVisibility(View.VISIBLE);
        Button videobuttonStart = findViewById(R.id.face_startbtn);
        Button videobuttonStop = findViewById(R.id.face_stopbtn);
        EditPort = findViewById(R.id.EditPort);
        EditPort.setHint("setPort");
        Button setPort = findViewById(R.id.SetPort);
        Button getDevPort = findViewById(R.id.GetPort);
        Button getAppPort = findViewById(R.id.GetAppPort);
        power_on_off = findViewById(R.id.show_power);
        ImageButton video_return_main = findViewById(R.id.video_return_main);
        video_bmp_listview = findViewById(R.id.video_bmp_list);
        permissionActivity.verifyStoragePermissions(VideoSDK.this);  //先获取到读取图片的权限
        list = new ArrayList<>();
        listPath = BmpUtil.getFilesAllName(MlgUtil.getSDPath() + "/10000/");
        for (String s : listPath) {
            BmpList bmps = new BmpList();
            bmps.setBitmap(BitmapFactory.decodeFile(s));
            bmps.setBmpText(s.substring(s.lastIndexOf("/") + 1));
            list.add(bmps);
        }
        bmpadapter = new BmpAdapter(getApplicationContext(), R.layout.bmp_items, list);
        video_bmp_listview.setAdapter(bmpadapter);
        video_bmp_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BmpList bmp = list.get(position);
                TastyToast.makeText(getApplicationContext(), "你点击了" + bmp.getBmpText(), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            }
        });
        sharedPreferences = getSharedPreferences("tcfcameraN", MODE_PRIVATE);
        mSurfaceView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isbackPlay) {
                    mSurfaceView.setLayoutParams(mrLayoutParams);
                    isbackPlay = false;
                } else {

                    mrLayoutParams = mSurfaceView.getLayoutParams();
                    mSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    isbackPlay = true;
                }
                return true;
            }
            return false;
        });


        video_return_main.setOnClickListener(v -> {
            if (running) {
                StopVideo();
            }
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_out);
            finish();
        });

        getAppPort.setOnClickListener(view -> ShowMessage("APP Port->" + videoPort, VideoSDK.this));
        setPort.setOnClickListener(view -> {
            if ("".equals(EditPort.getText().toString().trim())) {
                TastyToast.makeText(this, "不能输入空值", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            } else {
                int portNum = Integer.parseInt(EditPort.getText().toString());
                if (portNum < 20000 || portNum > 50000) {
                    TastyToast.makeText(this, "请输入20000到50000的数字", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    EditPort.setText("");
                } else {
                    videoPort = portNum;
                    VideoDecoder.SetVideoPort(videoPort);
                    saveVideoPort(videoPort);
                    EditPort.setText("");
                    EditPort.setHint("port");
                    EditPort.clearFocus();
                    hideKeyBoard(VideoSDK.this);
                    TastyToast.makeText(this, "set App port: " + videoPort, TastyToast.LENGTH_SHORT, TastyToast.INFO);
                    StopVideo();
                }
            }
        });
        getDevPort.setOnClickListener(view -> {
            if (VideoDecoder.FindDevice() == 1) {
                if (VideoDecoder.GetDevVideoPort() != videoPort && VideoDecoder.GetDevVideoPort() != -2) {
                    TastyToast.makeText(this, "error port: " + VideoDecoder.GetDevVideoPort(), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                } else {
                    TastyToast.makeText(this, "dev port: " + VideoDecoder.GetDevVideoPort(), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                }
            } else {
                TastyToast.makeText(this, "you should check your net", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        });
        //
        videobuttonCapture.setOnClickListener(v -> {
            if (running)
                VideoDecoder.ManualCapture(); //手工抓图
        });
        //
        videobuttonStart.setOnClickListener(v -> StartVideo());
        //
        videobuttonStop.setOnClickListener(v -> {
            Log.e("iMVR", "stop video");
            StopVideo();
        });
        picpath = MlgUtil.getSDPath() + "/10000/"; //抓图的文件夹
        VideoDecoder = new Video(mSurfaceView, handler, picpath, "192.168.1.1");  //ip写死的，不要修改

///////////////////////////////////////////////////////////////
///////////适用于手机热点 模式 或者路由器模式!!!//////////////////////
////////////////////////////////////////////////////////////////
        int g_wifimode = sharedPreferences.getInt("wifimode", 0);
        if (g_wifimode != 0) {
            VideoDecoder.SetDevType(g_wifimode);
        }
        videoPort = GetVideoPortFromProp();
        VideoDecoder.SetVideoPort(videoPort);
        ///////////////////////////////////////////////////////////////////////
        StartCodec();
    }


    public Hashtable<Integer, String> getDevMap(DevGroup listdev, Hashtable<Integer, String> map) {
        if (listdev.devid0 != 0) {
            map.put(listdev.devid0, listdev.devid0ip);
        }
        if (listdev.devid1 != 0) {
            map.put(listdev.devid1, listdev.devid1ip);
        }
        if (listdev.devid2 != 0) {
            map.put(listdev.devid2, listdev.devid2ip);

        }
        if (listdev.devid3 != 0) {
            map.put(listdev.devid3, listdev.devid3ip);

        }
        if (listdev.devid4 != 0) {
            map.put(listdev.devid4, listdev.devid4ip);
        }
        return map;
    }

    String[] items = null;
    String[] devips = null;
    Hashtable<Integer, String> map = new Hashtable<>();
    int i = 0;

    public void showTheUsefulDev(View view) {
        StopVideo();
        if (1 == VideoDecoder.FindDevice()) {
            DevGroup listdev = VideoDecoder.ListAllDev();
            MlgUtil.showDevGrop(listdev);
            map = getDevMap(listdev, map);
            items = new String[map.size()];  //new String[map.size()-1]
            devips = new String[map.size()];
            i = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                map.forEach((key, value) -> {
                    Log.e("iMVR", "id:" + key);
                    Log.e("iMVR", "ip:" + value);
                    if (!"".equals(value) && (key != 0)) {
                        items[i] = addBeforeZero(key);
                        devips[i] = value;
                        i++;
                    }
                });
            }
            map.clear();
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
                            if (tempwhich[0] != -1) {
                                if (items[tempwhich[0]] != null && devips[tempwhich[0]] != null) {
                                    EditPort.setHint(items[tempwhich[0]].substring(8));  //截取id的后几位
                                    VideoDecoder.SetDevIP(devips[tempwhich[0]]);
                                    try {
                                        VideoDecoder.SetDevIP(devips[tempwhich[0]]);
                                        Thread.sleep(1000);
                                        StartVideo();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                TastyToast.makeText(getApplicationContext(), "你选择了" + items[tempwhich[0]], TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            alertDialog.show();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setTitle("未搜索到设备");
            sweetAlertDialog.setCanceledOnTouchOutside(false);
            sweetAlertDialog.show();
        }

    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            unregisterReceiver(mHomeKeyEventReceiver);
            StopVideo();
            Log.e("IMVR ", "-----------------------------------turn off codec-----------------");
            Intent intent = new Intent();
            intent.setClass(VideoSDK.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void saveVideoPort(int savePort) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("videoPort", savePort);
        edit.apply();
    }

    private int GetVideoPortFromProp() {
        return sharedPreferences.getInt("videoPort", videoPort);
    }

    private int gotoFindDevice() {
        int findDevice_ok = 0;
        for (int i = 0; i < 4; i++) {
            if (VideoDecoder.FindDevice() == 1) {
                Log.e("iMVR", "Find device ok!");
                findDevice_ok = 1;
                break;
            }
        }
        return findDevice_ok;
    }

    private void StartVideo() {
        if (gotoFindDevice() == 1) {
            // MlgUtil.ShowMessage("find device ok..", VideoSDK.this);
            TastyToast.makeText(this, "startting Video...", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
        }
        if (!running) {
            VideoDecoder.StartKeyThread();  //按键触发
            VideoDecoder.StartVideoThread();//视频解码输出
            running = true;
            power_on_off.setBackgroundResource(R.drawable.power_on);  //开启了视频之后切换图片为power_on
        }
    }

    public void StopVideo() {
        if (running) {
            VideoDecoder.StopKeyThread(); //停止按键触发线程
            VideoDecoder.StopVideo(); //停止解码线程
            //      StopCodec();
            running = false;
            TastyToast.makeText(this, "stopped Video", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            power_on_off.setBackgroundResource(R.drawable.power_off);
        }
    }

    private void StopCodec() {
        VideoDecoder.ReleaseCodec();
    }

    private void StartCodec() {
        CodecClient m_codecclient = new CodecClient();
        Thread codecThread = new Thread(m_codecclient);
        codecThread.start();
    }

    private class CodecClient implements Runnable {
        public void run() {
            new Thread(new ThreadCodec()).start();
        }
    }

    private class ThreadCodec implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(700);//bug xxxxxxx
                VideoDecoder.InitCodec();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String addBeforeZero(int num) {
        if (num == 0) {
            return "";
        } else {
            String beforeNum = (num >= 10 ? (num >= 100 ? (num >= 1000 ? (num >= 10000 ? "" : "0") : "00") : "000") : "0000");
            return "mlg_M999" + beforeNum + num;
        }
    }
}