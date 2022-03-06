package com.common.wxalipaydemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * TODO 类描述
 *
 * @author HB.fee
 * @date 2022-03-06
 */
public class HookLayoutInflater implements LayoutInflater.Factory, LayoutInflater.Factory2 {
    private LayoutInflater.Factory factory1;
    private LayoutInflater.Factory2 factory2;

    public void hookLayoutInflater(LayoutInflater outSideLayoutInflater) {
        if (outSideLayoutInflater != null) {
            this.factory1 = outSideLayoutInflater.getFactory();

            this.factory2 = outSideLayoutInflater.getFactory2();

            outSideLayoutInflater.setFactory(this);
            outSideLayoutInflater.setFactory2(this);
        }
    }


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (factory2 != null) {
            View v = factory2.onCreateView(parent, name, context, attrs);
            if (v instanceof TextView) {
                ((TextView) v).setText("大在在在");
            }
            return v;
        }
        return null;
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if (factory1 != null) {
            View v = factory1.onCreateView(name, context, attrs);
            if (v instanceof TextView) {
                ((TextView) v).setText("ffffff");
            }
            return v;
        }
        return null;
    }
}
