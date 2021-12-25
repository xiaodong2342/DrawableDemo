package com.xiaodong.drawabledemo.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaodong.drawabledemo.R;

public class UnifyProgressDialog extends ProgressDialog {
    private boolean cancelable = true;
    private String text;
    private TextView textView;
    public LinearLayout layout_cancel;

    public UnifyProgressDialog(Context context) {
        super(context);
    }

    public UnifyProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public UnifyProgressDialog(Context context, int theme, boolean cancelable, String text) {
        super(context, theme);
        this.cancelable = cancelable;
        this.text = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (cancelable) {
            setCancelable(true);
            setCanceledOnTouchOutside(true);
        } else {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
        }

        setContentView(R.layout.unify_progress_dialog);
        textView = findViewById(R.id.text_loading);
        layout_cancel = findViewById(R.id.layout_cancel);
        if (text == null) {
            textView.setText("数据加载中...");
        } else {
            textView.setText(text);
        }

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    public void setCanCancelable(boolean canCancelable) {
        setCancelable(canCancelable);
        setCanceledOnTouchOutside(canCancelable);
    }

    public void setTextMessage(String text) {
        textView.setText(text);
    }
}
