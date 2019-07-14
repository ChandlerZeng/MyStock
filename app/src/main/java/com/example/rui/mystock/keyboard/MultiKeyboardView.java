package com.example.rui.mystock.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.example.rui.mystock.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 自定义键盘布局
 * Created by  on 2018/2/1.
 */
public class MultiKeyboardView extends KeyboardView {
    private Rect mIconRect = new Rect();
    public static int[] grayBackgroundArrays = {Constants.KEYCODE_CLEAR, Constants.KEYCODE_HIDE,
            Constants.KEYCODE_ALL, Constants.KEYCODE_QUARTER, Constants.KEYCODE_HALF,
            Constants.KEYCODE_THIRD, Constants.KEYCODE_ADD_HUNDRED, Constants.KEYCODE_DELETE,
            Constants.KEYCODE_MINUS_HUNDRED, Constants.KEYCODE_SHIFT, Constants.KEYCODE_ONE_TWO_THREE,
            Constants.KEYCODE_DOWN, Constants.KEYCODE_SIX_ZERO_ZERO, Constants.KEYCODE_SIX_ZERO_ONE,
            Constants.KEYCODE_ZERO_ZERO_ZERO, Constants.KEYCODE_ZERO_ZERO_TWO, Constants.KEYCODE_THREE_ZERO_ZERO};
    // 0-9 数字的 Character 值
    private final List<Character> keyCodes = Arrays.asList('0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9');
    private int labelTextSize;
    private int specialTextSize;
    private int mKeyboardType;

    public MultiKeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiKeyboardView,
                defStyleAttr, 0);
        labelTextSize = a.getDimensionPixelSize(R.styleable.MultiKeyboardView_labelTextSize, 14);
        specialTextSize = a.getDimensionPixelSize(R.styleable.MultiKeyboardView_specialLabelTextSize, 14);
        mKeyboardType = a.getInt(R.styleable.MultiKeyboardView_keyboardViewType, 0);
        a.recycle();
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Keyboard mCurrentKeyboard = getKeyboard();
        // 遍历所有的按键
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            // 特殊按键背景
            if (needGrayBackground(key.codes[0])) {
                //全键盘
                if (mKeyboardType == 1) {
                    if (key.codes[0] == Constants.KEYCODE_SHIFT ||
                            key.codes[0] == Constants.KEYCODE_DELETE ||
                            key.codes[0] == Constants.KEYCODE_ONE_TWO_THREE ||
                            key.codes[0] == Constants.KEYCODE_DOWN) {
                        drawKeyBackground(key, canvas, R.drawable.selector_keyboard_confirm_key_gray_corner4);
                    } else {
                        drawKeyBackground(key, canvas, R.drawable.selector_keyboard_confirm_key_corner4);
                    }
                } else {
                    drawKeyBackground(key, canvas, R.drawable.selector_keyboard_special_key_bg);
                }
                drawText(key, canvas, true);
            }
            // 删除图标
            if (key.codes[0] == Keyboard.KEYCODE_DELETE) {
                drawIconKey(key, canvas, R.drawable.common_icon_click_keyboard_delete);
            } else if (key.codes[0] == Constants.KEYCODE_OK) {//确定按钮
                if (mKeyboardType == 1) {
                    drawKeyBackground(key, canvas, R.drawable.selector_keyboard_confirm_key_corner4);
                } else {
                    drawKeyBackground(key, canvas, R.drawable.selector_keyboard_confirm_key_bg);
                }
                drawText(key, canvas, true);
            } else if (key.codes[0] == Constants.KEYCODE_SHIFT) {
                drawIconKey(key, canvas, mCurrentKeyboard.isShifted() ? R.drawable.common_icon_click_keyboard_capital_open :
                        R.drawable.common_icon_click_keyboard_capital_close);
            } else if (key.codes[0] == Constants.KEYCODE_DOWN) {
                drawIconKey(key, canvas, R.drawable.common_icon_click_keyboard_down);
            } else if (key.codes[0] == Constants.KEYCODE_ABC) {
                drawKeyBackground(key, canvas, R.drawable.selector_key_background);
                drawText(key, canvas, true);
            } else if (key.codes[0] == 32) {//空格
                drawKeyBackground(key, canvas, R.drawable.selector_key_qwer);
                drawText(key, canvas, true);
            } else if (key.codes[0] == Constants.KEYCODE_ONE_TWO_THREE ||
                    key.codes[0] == Constants.KEYCODE_CLEAR || key.codes[0] == Constants.KEYCODE_HIDE) {
                drawText(key, canvas, true);
            } else if (!needGrayBackground(key.codes[0])) {
                drawText(key, canvas, false);
            }

        }

    }

    // 绘制带有icon的按键
    private void drawIconKey(Keyboard.Key key, Canvas canvas, int drawable) {
        Drawable mDeleteDrawable = getResources().getDrawable(drawable);
        // 计算删除图标绘制的坐标
        int drawWidth, drawHeight;
        int intrinsicWidth = mDeleteDrawable.getIntrinsicWidth();
        int intrinsicHeight = mDeleteDrawable.getIntrinsicHeight();
        int mDeleteWidth = dp2Px(24);
        int mDeleteHeight = dp2Px(24);
        if (mDeleteWidth > 0 && mDeleteHeight > 0) {
            drawWidth = mDeleteWidth;
            drawHeight = mDeleteHeight;
        } else if (mDeleteWidth > 0) {
            drawWidth = mDeleteWidth;
            drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
        } else if (mDeleteHeight > 0) {
            drawHeight = mDeleteHeight;
            drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
        } else {
            drawWidth = intrinsicWidth;
            drawHeight = intrinsicHeight;
        }
        // 限制图标的大小，防止图标超出按键
        if (drawWidth > key.width) {
            drawWidth = key.width;
            drawHeight = drawWidth * intrinsicHeight / intrinsicWidth;
        }
        if (drawHeight > key.height) {
            drawHeight = key.height;
            drawWidth = drawHeight * intrinsicWidth / intrinsicHeight;
        }
        // 获取删除图标绘制的坐标
        int left = key.x + (key.width - drawWidth) / 2 + getPaddingLeft();
        int top = key.y + (key.height - drawHeight) / 2 + getPaddingTop();
        mIconRect.set(left, top, left + drawWidth, top + drawHeight);
        // 绘制删除的图标
        mDeleteDrawable.setBounds(mIconRect.left, mIconRect.top,
                mIconRect.right, mIconRect.bottom);
        mDeleteDrawable.draw(canvas);
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

    // 绘制按键灰色背景
    private void drawKeyBackground(Keyboard.Key key, Canvas canvas, int drawableResource) {
        Drawable npd = getResources().getDrawable(drawableResource);
        int[] drawableState = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            npd.setState(drawableState);
        }
        npd.setBounds(key.x + getPaddingLeft(), key.y + getPaddingTop(),
                key.x + key.width + getPaddingLeft(), key.y + getPaddingTop()
                        + key.height);
        npd.draw(canvas);
    }

    //绘制文字
    private void drawText(Keyboard.Key key, Canvas canvas, boolean special) {
        if (key.label != null) {
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setFakeBoldText(false);
            if (special) {
                textPaint.setTextSize(specialTextSize);
            } else {
                textPaint.setTextSize(labelTextSize);
            }
            if (key.codes[0] == Constants.KEYCODE_OK) {
                if (key.pressed) {
                    textPaint.setColor(Color.WHITE);
                } else {
                    textPaint.setColor(getResources().getColor(R.color.main_text_color));
                }
            } else {
                textPaint.setColor(getResources().getColor(R.color.main_text_color));
            }
            canvas.drawText(key.label.toString(), key.x + key.width / 2 + getPaddingLeft(),
                    key.y + key.height / 2 + (textPaint.getTextSize()-textPaint.descent()-6) / 2 + getPaddingTop(), textPaint);
        }
    }

    /**
     * 随机打乱数字键盘上键位的排列顺序。
     */
    public void shuffleKeyboard() {
        Keyboard keyboard = getKeyboard();
        if (keyboard != null && keyboard.getKeys() != null && keyboard.getKeys().size() > 0) {
            Collections.shuffle(keyCodes); // 随机排序数字
            // 遍历所有的按键
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            int index = 0;
            for (Keyboard.Key key : keys) {
                // 如果按键是数字
                if (isNumberKey(key.codes[0])) {
                    char code = keyCodes.get(index++);
                    key.codes[0] = code;
                    key.label = Character.toString(code);
                }
            }
            setKeyboard(keyboard);
        }
    }

    //是否是数字键
    private boolean isNumberKey(int code) {
        return code >= 48 && code <= 57;
    }

    private int dp2Px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}