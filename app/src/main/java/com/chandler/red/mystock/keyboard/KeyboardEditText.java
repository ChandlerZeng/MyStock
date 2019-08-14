package com.chandler.red.mystock.keyboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.chandler.red.mystock.R;

import java.lang.reflect.Method;


/**
 * 带自定义键盘的edittext
 * Created by  on 2018/2/9.
 */

public class KeyboardEditText extends EditTextWidthDeleteIcon {
    private int keyBoardType;
    private static AlertDialog mKeyboardDialog;
    private onEditTextTouchListenr onEditTextTouchListenr;
    private static KeyboardEditText mCurrentKeyboardEditText;
    private OnStockAmountListener mOnStockAmountListener;

    public void setOnEditTextTouchListenr(KeyboardEditText.onEditTextTouchListenr onEditTextTouchListenr) {
        this.onEditTextTouchListenr = onEditTextTouchListenr;
    }

    public KeyboardEditText(Context context) {
        this(context, null);
    }

    public KeyboardEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.KeyboardEditText);
        keyBoardType = array.getInt(R.styleable.KeyboardEditText_keyboardType, 0);
        array.recycle();
        setOnTouchListener(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public void showKeyBoard(MotionEvent event) {
        super.showKeyBoard(event);
        if (mKeyboardDialog == null || !mKeyboardDialog.isShowing() || KeyboardEditText.mCurrentKeyboardEditText != this) {
            KeyboardEditText.mCurrentKeyboardEditText = this;
            showKeyboard();
            if (onEditTextTouchListenr != null) {
                onEditTextTouchListenr.onTouch(this, event);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mKeyboardDialog != null && mKeyboardDialog.isShowing()) {
            mKeyboardDialog.dismiss();
        }
    }

    private void showKeyboard() {
        hideSystemSoftKeyboard();
        requestFocus();
        if (mKeyboardDialog != null) {
            mKeyboardDialog.dismiss();
            mKeyboardDialog = null;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.BottomDialog);
        final InputLayout inputlayout = new InputLayout(getContext());
        if (keyBoardType == Constants.KEYBOARD_TYPE_STOCK_AMOUNT && mOnStockAmountListener != null) {
            inputlayout.setOnStockAmountListener(mOnStockAmountListener);
        }
        mKeyboardDialog = builder.create();
        builder.setCancelable(true);
        mKeyboardDialog.show();
        mKeyboardDialog.setCanceledOnTouchOutside(true);
        inputlayout.showSoftKeyboard(this, keyBoardType);
        mKeyboardDialog.setContentView(inputlayout);
        Window window = mKeyboardDialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams attributes = window.getAttributes();
            //防止点击输入框导致键盘被关闭
            attributes.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
            attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setAttributes(attributes);
        }
        inputlayout.setOnOKOrHiddenKeyClickListener(new OnOKOrHiddenKeyClickListener() {
            @Override
            public boolean onOKKeyClick() {
                mKeyboardDialog.dismiss();
                return true;
            }

            @Override
            public boolean onHiddenKeyClick() {
                mKeyboardDialog.dismiss();
                return true;
            }
        });

    }

    interface onEditTextTouchListenr {
        void onTouch(KeyboardEditText v, MotionEvent event);
    }

    /**
     * 隐藏系统键盘
     */
    private void hideSystemSoftKeyboard() {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(this, false);

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            setInputType(InputType.TYPE_NULL);
        }
        // 如果软键盘已经显示，则隐藏
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK &&
                mKeyboardDialog != null && mKeyboardDialog.isShowing()) {
            mKeyboardDialog.dismiss();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        View view = ((ViewGroup) getRootView().findViewById(android.R.id.content)).getChildAt(0);
        view.setOnTouchListener(new OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        boolean returnVlaue;
                                        switch (event.getAction()) {
                                            case MotionEvent.ACTION_DOWN:
                                                returnVlaue = true;
                                                break;
                                            case MotionEvent.ACTION_UP:
                                                if (mKeyboardDialog != null && mKeyboardDialog.isShowing()) {
                                                    mKeyboardDialog.dismiss();
                                                }
                                                returnVlaue = false;
                                                break;
                                            default:
                                                returnVlaue = false;
                                        }
                                        return returnVlaue;
                                    }
                                }
        );
    }

    public void setOnStockAmountListener(OnStockAmountListener onStockAmountListener) {
        mOnStockAmountListener = onStockAmountListener;
    }
}
