package com.songbao.sampo_b.widgets;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

public class PriceTextWatcher implements TextWatcher {

    private AfterCallback afterCallback;
    private WeakReference<EditText> weakReference;

    public PriceTextWatcher(EditText et_price, AfterCallback callback) {
        this.afterCallback = callback;
        this.weakReference = new WeakReference<>(et_price);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        EditText et_price = weakReference.get();
        if (et_price == null) return;
        String input = s.toString();
        // 以“.”开头
        if (input.startsWith(".")) {
            input = "0" + input;
            et_price.setText(input);
            et_price.setSelection(input.length());
            return;
        }
        if (input.contains(".")) {
            // 存在两个以上“.”
            String beStr = input.substring(0, input.lastIndexOf("."));
            if (beStr.contains(".")) {
                input = beStr;
                et_price.setText(input);
                et_price.setSelection(input.length());
                return;
            }
            // 取“.”后两位数
            if (input.length() - 1 - input.indexOf(".") > 2) {
                input = input.substring(0, input.indexOf(".") + 3);
                et_price.setText(input);
                et_price.setSelection(input.length());
                return;
            }
        }
        // 以“0”开头
        if (input.startsWith("0") && input.trim().length() > 1) {
            if (!input.substring(1, 2).equals(".")) {
                input = input.substring(1, input.length());
                et_price.setText(input);
                et_price.setSelection(input.length());
                return;
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (this.afterCallback != null) {
            this.afterCallback.afterTextChanged(s);
        }
    }

    public interface AfterCallback {

        void afterTextChanged(Editable s);

    }
}
