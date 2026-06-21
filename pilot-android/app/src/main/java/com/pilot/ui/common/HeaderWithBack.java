package com.pilot.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pilot.R;

/**
 * Reusable header sa back arrow i naslovom.
 */
public class HeaderWithBack extends LinearLayout {

    private ImageButton btnBack;
    private TextView tvTitle;

    public HeaderWithBack(Context context) {
        super(context);
        init(context);
    }

    public HeaderWithBack(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeaderWithBack(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.header_with_back, this, true);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTitle(int stringResId) {
        tvTitle.setText(stringResId);
    }

    public void setOnBackClickListener(OnClickListener listener) {
        btnBack.setOnClickListener(listener);
    }

    public ImageButton getBackButton() {
        return btnBack;
    }

    public TextView getTitleView() {
        return tvTitle;
    }
}

