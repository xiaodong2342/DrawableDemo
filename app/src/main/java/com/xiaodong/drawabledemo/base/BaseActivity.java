package com.xiaodong.drawabledemo.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xiaodong.drawabledemo.R;
import com.xiaodong.drawabledemo.networkrequest.NetUtil;
import com.xiaodong.drawabledemo.networkrequest.NetworkChanged;
import com.xiaodong.drawabledemo.view.UnifyProgressDialog;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String LOG_TAG = "BaseActivity";
    private BaseActivity mTopActivity;
    private List<BaseActivity> activitys = new LinkedList<BaseActivity>();
    private UnifyProgressDialog unifyProgressDialog;
    public boolean isShowKeyBoard;

    protected boolean isOnTop = false;
    //    private long mPreClickTime;
    private NetworkChangeReceiver networkChangeReceiver;
    private IntentFilter networkChangeFilter;

    private int netMobile;

    private static Toast toast;
    private String oldMessage = "";
    private long toastTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(setViewId());

        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeFilter = new IntentFilter();
        networkChangeFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        findViews();
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnTop = true;
        mTopActivity = this;
        activitys.add(this);
        registerReceiver(networkChangeReceiver, networkChangeFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnTop = false;
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activitys.remove(this);
    }

    protected abstract int setViewId();

    protected abstract void findViews();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    /**
     * 重写getResources()方法，让APP的字体不受系统设置字体大小影响
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    public void hideSoftInput() {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                isShowKeyBoard = !manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public BaseActivity getTopActivity() {
        return mTopActivity;
    }

    public List<BaseActivity> getActivitys() {
        return activitys;
    }

    public void exitActivities() {
        for (BaseActivity baseActivity : activitys) {
            baseActivity.finish();
        }
    }

    /*
    @Override
    public void onBackPressed() {
        if (this instanceof HomeActivity) {// 主页
            if (System.currentTimeMillis() - mPreClickTime > 2000) {// 两次点击的间隔大于2s中
                Toast.makeText(getApplicationContext(), R.string.press_one_more_time_exit, Toast.LENGTH_SHORT).show();
                mPreClickTime = System.currentTimeMillis();
            } else {
                // 完全退出
                exitActivities();
            }
        } else {
            super.onBackPressed();// finish
        }
    }
    */

    protected void showToast(String message) {
//        MyToast.makeText(this, message);
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        if (message != null && !message.equals("")) {
            if (System.currentTimeMillis() - toastTimeMillis > 2000) {
                oldMessage = message;
                toastTimeMillis = System.currentTimeMillis();
                toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                if (!oldMessage.equals(message)) {
                    oldMessage = message;
                    toastTimeMillis = System.currentTimeMillis();
                    toast.cancel();
                    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }

    protected void showProgressDialog() {
        showProgressDialog(null);
    }

    protected void showProgressDialog(boolean cancelable) {
        showProgressDialog(cancelable, null);
    }

    protected void showProgressDialog(String msg) {
        showProgressDialog(true, msg);
    }

    protected void showProgressDialog(boolean cancelable, String msg) {
        /*if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置样式为圆形样式
//            progressDialog.setTitle("友情提示"); // 设置进度条的标题信息
            progressDialog.setMessage("数据加载中，请稍后..."); // 设置进度条的提示信息
            progressDialog.setIndeterminate(false); // 设置进度条是否为不明确
            progressDialog.setCancelable(true); // 设置进度条是否按返回键取消

            if (!TextUtils.isEmpty(msg)) {
                progressDialog.setMessage(msg);
            }
            progressDialog.setCancelable(cancelable);
            progressDialog.show();
        }*/

        if (unifyProgressDialog == null) {
            unifyProgressDialog = new UnifyProgressDialog(this, R.style.UnifyProgressDialog, cancelable, msg);
        } else {
            unifyProgressDialog.setCancelable(cancelable);
            if (msg == null) {
                unifyProgressDialog.setTextMessage("数据加载中...");
            } else {
                unifyProgressDialog.setTextMessage(msg);
            }
        }

//        if (AppConstants.DEVICE_BEAN != null) {
//            if (AppConstants.DEVICE_BEAN.getDeviceModel().equals(AppConstants.MODEL_HUAWEI_XINGMANG)) {
//                unifyProgressDialog.setProgressBarImage(R.drawable.xingmang_unify_progress_dialog);
//            } else {
//                unifyProgressDialog.setProgressBarImage(R.drawable.unify_progress_dialog);
//            }
//        }

        unifyProgressDialog.show();
    }

    protected void showProgressDialog(int cancel, String msg, View.OnClickListener onClickListener) {
        if (cancel >= 0) {
            showProgressDialog(false, msg);
        } else {
            showProgressDialog(true, msg);
        }
        unifyProgressDialog.layout_cancel.setVisibility(View.VISIBLE);
        unifyProgressDialog.layout_cancel.setOnClickListener(onClickListener);
    }

    protected void dismissProgressDialog() {
        /*if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;*/

        if (unifyProgressDialog != null) {
            if (unifyProgressDialog.layout_cancel.getVisibility() != View.GONE) {
                unifyProgressDialog.layout_cancel.setVisibility(View.GONE);
            }
            unifyProgressDialog.dismiss();
        }
        unifyProgressDialog = null;
    }

    protected boolean isProgressDialogShowing() {
//        return (progressDialog != null && progressDialog.isShowing());
        return (unifyProgressDialog != null && unifyProgressDialog.isShowing());
    }

    protected ProgressDialog getProgressDialog() {
//        return progressDialog;
        return unifyProgressDialog;
    }

    //获取当前activity的名字
    protected String getRunningActivityName() {
        String activityString = this.toString();
        return activityString.substring(activityString.lastIndexOf(".") + 1, activityString.indexOf("@"));
    }

    NetworkChanged networkChanged;

    protected void listenNetworkStatus(NetworkChanged networkChanged) {
        this.networkChanged = networkChanged;
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_MOBILE:
                    case ConnectivityManager.TYPE_WIFI:
                        showToast("网络已连接");
                        if (networkChanged != null) {
                            networkChanged.NetworkStatus(true);
                        }
                        break;
                }
            } else {
                showToast("无网络连接");
                if (networkChanged != null) {
                    networkChanged.NetworkStatus(false);
                }
            }
        }
    }

    /**
     * 初始化时判断有没有网络 
     */
    public boolean isNetWorkOk() {

        this.netMobile = NetUtil.getNetWorkState(BaseActivity.this);

        if (netMobile == 1) {// 连接wifi
            return true;
        } else if (netMobile == 0) {// 连接移动数据
            return true;
        } else if (netMobile == -1) {// 当前没有网络
            return false;
        }
        return false;
    }
}
