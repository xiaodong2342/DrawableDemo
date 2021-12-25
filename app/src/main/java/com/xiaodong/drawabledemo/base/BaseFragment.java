package com.xiaodong.drawabledemo.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xiaodong.drawabledemo.R;
import com.xiaodong.drawabledemo.view.UnifyProgressDialog;


public abstract class BaseFragment extends Fragment {

    //	private ProgressDialog progressDialog;
    private UnifyProgressDialog unifyProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setViewId(), container, false);
        findViews(view);
        initView();
        initData();
        initListener();
        return view;
    }

    protected abstract int setViewId();

    protected abstract void findViews(View view);

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        showProgressDialog(null);
    }

    public void showProgressDialog(boolean cancelable) {
        showProgressDialog(cancelable, null);
    }

    public void showProgressDialog(String msg) {
        showProgressDialog(true, msg);
    }

    public void showProgressDialog(boolean cancelable, String msg) {
        if (unifyProgressDialog == null) {
            unifyProgressDialog = new UnifyProgressDialog(getActivity(), R.style.UnifyProgressDialog, cancelable, msg);
        } else {
            unifyProgressDialog.setCancelable(cancelable);
            if (msg == null) {
                unifyProgressDialog.setTextMessage("数据加载中...");
            } else {
                unifyProgressDialog.setTextMessage(msg);
            }
        }
        unifyProgressDialog.show();
    }

    public void showProgressDialog(int cancel, String msg, View.OnClickListener onClickListener) {
        if (cancel >= 0) {
            showProgressDialog(false, msg);
        } else {
            showProgressDialog(true, msg);
        }
        unifyProgressDialog.layout_cancel.setVisibility(View.VISIBLE);
        unifyProgressDialog.layout_cancel.setOnClickListener(onClickListener);
    }

    public void dismissProgressDialog() {
        if (unifyProgressDialog != null) {
            unifyProgressDialog.dismiss();
            if (unifyProgressDialog.layout_cancel.getVisibility() != View.GONE) {
                unifyProgressDialog.layout_cancel.setVisibility(View.GONE);
            }
        }
        unifyProgressDialog = null;
    }

    public boolean isProgressDialogShowing() {
        return (unifyProgressDialog != null && unifyProgressDialog.isShowing());
    }

    public ProgressDialog getProgressDialog() {
        return unifyProgressDialog;
    }
}
