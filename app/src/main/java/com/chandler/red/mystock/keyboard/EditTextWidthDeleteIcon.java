package com.chandler.red.mystock.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.chandler.red.mystock.R;


public class EditTextWidthDeleteIcon extends android.support.v7.widget.AppCompatEditText implements View.OnTouchListener {
    private int drawableLeftWidth, drawableTopWidth, drawableRightWidth, drawableBottomWidth;
    private int drawableLeftHeight, drawableTopHeight, drawableRightHeight, drawableBottomHeight;
    private static final String TAG = EditTextWidthDeleteIcon.class.getSimpleName();
    private int mWidth, mHeight;
    private boolean isAliganCenter = true;
    private boolean mShowDeleteIcon;
    private Drawable mDrawableDelete;
    private int mDeleteIconWidth;
    private int mDeleteIconHeight;

    public EditTextWidthDeleteIcon(Context context) {
        this(context, null);
    }

    public EditTextWidthDeleteIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public EditTextWidthDeleteIcon(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDrawableDelete = getResources().getDrawable(R.drawable.selector_register_icon_close);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditTextWidthDeleteIcon);
        drawableLeftWidth = typedArray.getDimensionPixelSize(R.styleable.EditTextWidthDeleteIcon_drawableLeftWidth, 0);
        drawableTopWidth = typedArray.getDimensionPixelSize(R.styleable.EditTextWidthDeleteIcon_drawableTopWidth, 0);
        drawableRightWidth = typedArray.getDimensionPixelSize(R.styleable.EditTextWidthDeleteIcon_drawableRightWidth, 0);
        drawableBottomWidth = typedArray.getDimensionPixelSize(R.styleable.EditTextWidthDeleteIcon_drawableBottomWidth, 0);
        drawableLeftHeight = typedArray.getDimensionPixelSize(R.styleable.EditTextWidthDeleteIcon_drawableLeftHeight, 0);
        drawableTopHeight = typedArray.getDimensionPixelSize(R.styleable.EditTextWidthDeleteIcon_drawableTopHeight, 0);
        drawableRightHeight = typedArray.getDimensionPixelSize(R.styleable.EditTextWidthDeleteIcon_drawableRightHeight, 0);
        drawableBottomHeight = typedArray.getDimensionPixelSize(R.styleable.EditTextWidthDeleteIcon_drawableBottomHeight, 0);
        isAliganCenter = typedArray.getBoolean(R.styleable.EditTextWidthDeleteIcon_isAliganCenter, true);
        mShowDeleteIcon = typedArray.getBoolean(R.styleable.EditTextWidthDeleteIcon_showDeleteIcon, false);
        mDeleteIconWidth = typedArray.getDimensionPixelSize(R.styleable.EditTextWidthDeleteIcon_deleteIconWidth, 0);
        mDeleteIconHeight = typedArray.getDimensionPixelSize(R.styleable.EditTextWidthDeleteIcon_deleteIconHeight, 0);
        typedArray.recycle();
        setOnTouchListener(this);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (text.length() > 0 && mShowDeleteIcon) {
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mDrawableDelete, getCompoundDrawables()[3]);
        } else {
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mShowDeleteIcon ? null : getCompoundDrawables()[2], getCompoundDrawables()[3]);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableLeft = drawables[0];
        Drawable drawableTop = drawables[1];
        Drawable drawableRight = drawables[2];
        Drawable drawableBottom = drawables[3];
        if (drawableLeft != null) {
            setDrawable(drawableLeft, 0, drawableLeftWidth, drawableLeftHeight);
        }
        if (drawableTop != null) {
            setDrawable(drawableTop, 1, drawableTopWidth, drawableTopHeight);
        }
        if (mShowDeleteIcon) {
            setDrawable(mDrawableDelete, 2, mDeleteIconWidth, mDeleteIconHeight);
        } else if (drawableRight != null) {
            setDrawable(drawableRight, 2, drawableRightWidth, drawableRightHeight);
        }
        if (drawableBottom != null) {
            setDrawable(drawableBottom, 3, drawableBottomWidth, drawableBottomHeight);
        }
        this.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);

    }

    private void setDrawable(Drawable drawable, int tag, int drawableWidth, int drawableHeight) {
        //获取图片实际长宽
        int width = drawableWidth == 0 ? drawable.getIntrinsicWidth() : drawableWidth;
        int height = drawableHeight == 0 ? drawable.getIntrinsicHeight() : drawableHeight;
        int left = 0, top = 0, right = 0, bottom = 0;
        switch (tag) {
            case 0:
            case 2:
                left = 0;
                top = isAliganCenter ? 0 : -getLineCount() * getLineHeight() / 2 + getLineHeight() / 2;
                right = width;
                bottom = top + height;
                break;
            case 1:
                left = isAliganCenter ? 0 : -mWidth / 2 + width / 2;
                top = 0;
                right = left + width;
                bottom = top + height;
                break;
        }
        drawable.setBounds(left, top, right, bottom);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            if (mShowDeleteIcon) {
                if (event.getX() <= (getWidth() - getPaddingRight())
                        && event.getX() >= (getWidth() - getPaddingRight() - mDrawableDelete.getBounds().width())) {
                    getEditableText().clear();
                }
            }
            showKeyBoard(event);
        }
        return false;
    }

    public void showKeyBoard(MotionEvent event) {

    }
}