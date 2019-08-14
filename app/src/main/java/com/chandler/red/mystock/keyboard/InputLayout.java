package com.chandler.red.mystock.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.chandler.red.mystock.R;

import java.util.List;

import static com.chandler.red.mystock.keyboard.MultiKeyboardView.grayBackgroundArrays;


/**
 * 键盘管理类
 * Created by  on 2018/2/2.
 */

public class InputLayout extends RelativeLayout implements OnStockAmountListener, KeyboardView.OnKeyboardActionListener {
    private KeyboardEditText editText;
    //确定或隐藏按钮点击监听
    private OnOKOrHiddenKeyClickListener mOnOKOrHiddenKeyClickListener;
    //股票数量按键点击监听
    private OnStockAmountListener onStockAmountListener;
    private LinearLayout rlKeyboardStock;
    //当前正在显示的键盘
    private Keyboard mCurrentKeyboard;
    //数字键盘
    public Keyboard keyboardNumber;
    //带小数点的数字键盘
    public Keyboard keyboardDeciaml;
    //股票键盘
    public Keyboard keyboardStockCode;
    //全键盘
    public Keyboard keyboardQwer;
    //股票数量键盘
    public Keyboard keyboardStockAmount;
    //可以切换成英文的数字键盘
    public Keyboard keyboardNumberAll;
    //股票代码键盘
    public Keyboard mKeyboardStockCode;
    //当前键盘类型
    private int mCurrentKeyboardType;
    //数字键盘布局
    private MultiKeyboardView keyboardViewNumber;
    //全键盘布局
    private MultiKeyboardView keyboardViewQwer;
    //股票键盘布局
    private MultiKeyboardView keyboardViewStock;
    //股票代码键盘
    private MultiKeyboardView mKeyboardViewStockCode;
    //是否大写
    private boolean isCap;


    public InputLayout(Context context) {
        this(context, null);
    }

    public InputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        inflate(getContext(), R.layout.input, this);
        keyboardViewNumber = findViewById(R.id.keyboard_number);
        keyboardViewQwer = findViewById(R.id.keyboard_qwer);
        keyboardViewStock = findViewById(R.id.keyboard_stock);
        mKeyboardViewStockCode = findViewById(R.id.keyboard_stock_code);
        rlKeyboardStock = findViewById(R.id.rl_keyboard_stock);
    }

    /**
     * 键盘是否正在显示
     *
     * @return true 显示 false 隐藏
     */
    private boolean isShow() {
        return rlKeyboardStock.getVisibility() == VISIBLE || keyboardViewQwer.getVisibility() == VISIBLE ||
                keyboardViewNumber.getVisibility() == VISIBLE;
    }

    /**
     * 隐藏键盘
     */
    private void hide() {
        rlKeyboardStock.setVisibility(GONE);
        keyboardViewNumber.setVisibility(GONE);
        keyboardViewQwer.setVisibility(GONE);
    }


    /**
     * 设置键盘的监听事件。
     */
    public void showSoftKeyboard(KeyboardEditText editText, int keyboardType) {
        if (editText == null) {
            return;
        }
        if (this.editText == null) {
            this.editText = editText;
            initKeyboardView(keyboardType);
        }
        addEditText(editText, keyboardType);
    }

    //页面中edittext数量大于1个时，剩余edittext调用这个方法
    private void addEditText(final KeyboardEditText editText, final int keyboardType) {
        editText.setOnEditTextTouchListenr(new KeyboardEditText.onEditTextTouchListenr() {
            @Override
            public void onTouch(KeyboardEditText v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (v != InputLayout.this.editText || !isShow()) {
                        InputLayout.this.editText = v;
                        initKeyboardView(keyboardType);
                    } else {
                        showCurrentKeyboard();
                    }
                }
            }
        });
    }

    /**
     * 自定义键盘显示
     */
    private void showCurrentKeyboard() {
        initKeyboardView(mCurrentKeyboardType);
    }

    /**
     * 初始化键盘布局
     *
     * @param keyboardType 键盘类型
     */
    private void initKeyboardView(int keyboardType) {
        isCap = true;
        mCurrentKeyboardType = keyboardType;
        hide();
        setVisibility(VISIBLE);
        switch (keyboardType) {
            case Constants.KEYBOARD_TYPE_NUMBER:
                keyboardNumber = new Keyboard(getContext(), R.xml.keyboard_number);
                keyboardViewNumber.setKeyboard(keyboardNumber);
                keyboardViewNumber.setVisibility(VISIBLE);
                setmCurrentKeyboard(keyboardNumber);
                keyboardViewNumber.setOnKeyboardActionListener(this);
                break;
            case Constants.KEYBOARD_TYPE_DECIMAL:
                keyboardDeciaml = new Keyboard(getContext(), R.xml.keyboard_number_decimal);
                setmCurrentKeyboard(keyboardDeciaml);
                keyboardViewNumber.setKeyboard(keyboardDeciaml);
                keyboardViewNumber.setVisibility(VISIBLE);
                keyboardViewNumber.setOnKeyboardActionListener(this);
                break;
            case Constants.KEYBOARD_TYPE_STOCK_CODE:
                keyboardStockCode = new Keyboard(getContext(), R.xml.keyboard_stock_code);
                keyboardQwer = new Keyboard(getContext(), R.xml.keyboard_qwer);
                mKeyboardStockCode = new Keyboard(getContext(), R.xml.keyboard_stock);
                setmCurrentKeyboard(keyboardStockCode);
                keyboardViewStock.setKeyboard(keyboardStockCode);
                keyboardViewQwer.setKeyboard(keyboardQwer);
                mKeyboardViewStockCode.setKeyboard(mKeyboardStockCode);
                rlKeyboardStock.setVisibility(VISIBLE);
                keyboardViewStock.setOnKeyboardActionListener(this);
                keyboardViewQwer.setOnKeyboardActionListener(this);
                mKeyboardViewStockCode.setOnKeyboardActionListener(this);
                break;
            case Constants.KEYBOARD_TYPE_STOCK_AMOUNT:
                keyboardStockAmount = new Keyboard(getContext(), R.xml.keyboard_stock_amount);
                setmCurrentKeyboard(keyboardStockAmount);
                keyboardViewNumber.setKeyboard(keyboardStockAmount);
                keyboardViewNumber.setVisibility(VISIBLE);
                keyboardViewNumber.setOnKeyboardActionListener(this);
                break;
            case Constants.KEYBOARD_TYPE_NUMBER_ALL:
                keyboardNumberAll = new Keyboard(getContext(), R.xml.keyboard_number_all);
                keyboardQwer = new Keyboard(getContext(), R.xml.keyboard_qwer);
                keyboardQwer.setShifted(isCap);
                setmCurrentKeyboard(keyboardNumberAll);
                keyboardViewNumber.setVisibility(VISIBLE);
                keyboardViewNumber.setEnabled(true);
                keyboardViewNumber.setOnKeyboardActionListener(this);
                keyboardViewQwer.setOnKeyboardActionListener(this);
                keyboardViewQwer.setKeyboard(keyboardQwer);
                keyboardViewNumber.setKeyboard(keyboardNumberAll);
                break;
            case Constants.KEYBOARD_TYPE_QWER:
                keyboardQwer = new Keyboard(getContext(), R.xml.keyboard_qwer);
                setmCurrentKeyboard(keyboardQwer);
                keyboardNumberAll = new Keyboard(getContext(), R.xml.keyboard_number_all);
                keyboardViewNumber.setKeyboard(keyboardNumberAll);
                keyboardViewQwer.setKeyboard(keyboardQwer);
                keyboardViewQwer.setVisibility(VISIBLE);
                keyboardViewNumber.setOnKeyboardActionListener(this);
                keyboardViewQwer.setOnKeyboardActionListener(this);
                break;
            default:
                keyboardQwer = new Keyboard(getContext(), R.xml.keyboard_qwer);
                setmCurrentKeyboard(keyboardQwer);
                keyboardNumberAll = new Keyboard(getContext(), R.xml.keyboard_number_all);
                keyboardViewNumber.setKeyboard(keyboardNumberAll);
                keyboardViewQwer.setKeyboard(keyboardQwer);
                keyboardViewQwer.setVisibility(VISIBLE);
                keyboardViewNumber.setOnKeyboardActionListener(this);
                keyboardViewQwer.setOnKeyboardActionListener(this);
                break;
        }
    }

    public void setmCurrentKeyboard(Keyboard mCurrentKeyboard) {
        this.mCurrentKeyboard = mCurrentKeyboard;
    }

    /**
     * 股票代码键盘用线性布局覆盖最左边解决左右行数不对等的问题，同时设置线性布局长为股票键盘的/5，高度和股票键盘一致
     */
    private void initStockKeyboard() {
        keyboardViewStock.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = mKeyboardViewStockCode.getLayoutParams();
                params.height = keyboardViewStock.getHeight();
                mKeyboardViewStockCode.setLayoutParams(params);
            }
        });
    }

    /**
     * 股票按钮点击监听回调
     *
     * @param primaryCode 股票数量按键
     */
    @Override
    public void onStockAmountKeyPressed(int primaryCode) {
        if (onStockAmountListener != null) {
            onStockAmountListener.onStockAmountKeyPressed(primaryCode);
        }
    }

    public void setOnStockAmountListener(OnStockAmountListener onStockAmountListener) {
        this.onStockAmountListener = onStockAmountListener;
    }

    /**
     * @param onOKOrHiddenKeyClickListener 确定或取消按钮点击监听
     */
    public void setOnOKOrHiddenKeyClickListener(OnOKOrHiddenKeyClickListener onOKOrHiddenKeyClickListener) {
        mOnOKOrHiddenKeyClickListener = onOKOrHiddenKeyClickListener;
    }

    @Override
    public void onPress(int primaryCode) {
        if (mCurrentKeyboard != null && mCurrentKeyboard.equals(keyboardQwer)) {
            if (primaryCode == Constants.KEYCODE_ONE_TWO_THREE || primaryCode == Constants.KEYCODE_DOWN ||
                    primaryCode == Constants.KEYCODE_DELETE || primaryCode == Constants.KEYCODE_SHIFT ||
                    primaryCode == Constants.KEYCODE_OK || primaryCode == 32) {
                keyboardViewQwer.setPreviewEnabled(false);
            } else if (keyboardViewQwer.isPreviewEnabled()) {
                keyboardViewQwer.setPreviewEnabled(true);
            }
        }
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        // 处理按键的点击事件
        // 点击了删除按键
        if (primaryCode == Constants.KEYCODE_DELETE) {
            if (editText != null) {
                int selectionStart = editText.getSelectionStart();
                int selectionEnd = editText.getSelectionEnd();
                if (selectionStart == selectionEnd) {
                    selectionStart = selectionEnd - 1;
                }
                editText.getEditableText().delete(selectionStart < 0 ? 0 : selectionStart, selectionEnd);
            }
        } else if (primaryCode == Constants.KEYCODE_SHIFT) {
//            changeKey();
        } else if (primaryCode == Constants.KEYCODE_ABC) {
            keyboardViewQwer.setVisibility(VISIBLE);
            keyboardViewNumber.setVisibility(GONE);
            rlKeyboardStock.setVisibility(GONE);
            setmCurrentKeyboard(keyboardQwer);
        } else if (primaryCode == Constants.KEYCODE_ONE_TWO_THREE) {
            keyboardViewQwer.setVisibility(GONE);
            if (mCurrentKeyboardType == Constants.KEYBOARD_TYPE_STOCK_CODE) {
                initStockKeyboard();
                rlKeyboardStock.setVisibility(VISIBLE);
                setmCurrentKeyboard(keyboardStockCode);
                mCurrentKeyboardType = Constants.KEYBOARD_TYPE_STOCK_CODE;
            } else {
                keyboardViewNumber.setVisibility(VISIBLE);
                setmCurrentKeyboard(keyboardNumberAll);
                mCurrentKeyboardType = Constants.KEYBOARD_TYPE_NUMBER_ALL;
            }
        } else if (primaryCode == Constants.KEYCODE_DOWN) {
            hide();
        } else if (primaryCode == Constants.KEYCODE_SIX_ZERO_ZERO) {
            editText.getEditableText().insert(editText.getSelectionStart(), "600");
        } else if (primaryCode == Constants.KEYCODE_THREE_ZERO_ZERO) {
            editText.getEditableText().insert(editText.getSelectionStart(), "300");
        } else if (primaryCode == Constants.KEYCODE_ZERO_ZERO_TWO) {
            editText.getEditableText().insert(editText.getSelectionStart(), "002");
        } else if (primaryCode == Constants.KEYCODE_ZERO_ZERO_ZERO) {
            editText.getEditableText().insert(editText.getSelectionStart(), "000");
        } else if (primaryCode == Constants.KEYCODE_SIX_ZERO_ONE) {
            editText.getEditableText().insert(editText.getSelectionStart(), "601");
        } else if (!needGrayBackground(primaryCode) && primaryCode != Constants.KEYCODE_OK && primaryCode != Constants.KEYCODE_EMPTY) {
            if (editText != null) {
                final KeyboardEditText editText = this.editText;
                Editable editable = editText.getEditableText();
                int selectionStart = editText.getSelectionStart();
                int selectionEnd = editText.getSelectionEnd();
                String letter = Character.toString((char) primaryCode);
                if(isLetter(letter)){
                    letter = letter.toUpperCase();
                }
                editable.replace(selectionStart, selectionEnd, letter);
                if (selectionStart != selectionEnd) {
                    editText.setSelection(selectionStart + letter.length());
                }
            }
        }
        //清除键
        if (primaryCode == Constants.KEYCODE_CLEAR) {
            if (editText != null) {
                editText.getEditableText().clear();
            }
        }
        //确认键
        if (primaryCode == Constants.KEYCODE_OK) {
            if (editText != null) {
                editText.clearFocus();
            }
            if (null != mOnOKOrHiddenKeyClickListener) {
                boolean isHide = mOnOKOrHiddenKeyClickListener.onOKKeyClick();
                if (isHide) {
                    hide();
                }
            } else {
                hide();
            }
        }
        //隐藏键
        if (primaryCode == Constants.KEYCODE_HIDE) {
            if (mOnOKOrHiddenKeyClickListener != null) {
                mOnOKOrHiddenKeyClickListener.onHiddenKeyClick();
            } else {
                hide();
            }
        }
        if (onStockAmountListener != null && isStockAmountKeyPressed(primaryCode)) {
            onStockAmountListener.onStockAmountKeyPressed(primaryCode);
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    //股票数量按键是否触发
    private boolean isStockAmountKeyPressed(int primaryCode) {
        return primaryCode == Constants.KEYCODE_ALL || primaryCode == Constants.KEYCODE_HALF ||
                primaryCode == Constants.KEYCODE_THIRD || primaryCode == Constants.KEYCODE_QUARTER ||
                primaryCode == Constants.KEYCODE_ADD_HUNDRED || primaryCode == Constants.KEYCODE_MINUS_HUNDRED;
    }

    //按键背景是否需要变成灰色
    private boolean needGrayBackground(int code) {
        for (int grayBackgroundArray : grayBackgroundArrays) {
            if (code == grayBackgroundArray) {
                return true;
            }
        }
        return false;
    }

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Keyboard.Key> keylist = keyboardQwer.getKeys();
        isCap = !isCap;
        keyboardQwer.setShifted(isCap);
        if (!isCap) {// 大写切小写
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        } else {// 小写切大写
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
        keyboardViewQwer.invalidateAllKeys();
    }

    /**
     * 是否是字母
     *
     * @param str 参数
     * @return true 是字母 false 不是
     */
    private boolean isLetter(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        return wordstr.contains(str.toLowerCase());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
