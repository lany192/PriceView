package com.lany.priceview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.NumberFormat;

public class PriceView extends View {
    /**
     * 是否开启 千分符
     */
    private boolean isThousandsUsed;
    /**
     * 文本显示占用的宽高
     */
    private int mTextWidth;
    private int mTextHeight;
    //钱文本 e.g.    12345.15
    private String mMoneyText = "0.00";
    private final String POINT = "."; //小数点
    private int mMoneyColor = Color.parseColor("#505050");

    private int mPrefixColor = Color.parseColor("#505050");
    /**
     * 前缀文本，例如¥
     */
    private String mPrefix;
    private int mPrefixSize = sp2px(12);
    private int mPrefixPadding = dp2px(4); //小数点与分的间隔

    /**
     * 整数部分大小
     */
    private int mIntegerSize = sp2px(18);
    private String mIntegerValue; //多少元

    /**
     * 小数部分大小
     */
    private int mDecimalsSize = sp2px(14);
    private String mDecimalsValue; //多少分

    private int mPointPaddingLeft = dp2px(3); //小数点与分的间隔
    private int mPointPaddingRight = dp2px(4); //小数点与分的间隔
    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mIntegerBound;
    private Rect mPrefixBound;
    private Rect mDecimalsBound;
    private Rect mPointBound;
    private Paint mPaint;
    //基线高度
    private float maxDescent;

    public PriceView(Context context) {
        this(context, null);
    }

    public PriceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PriceView, defStyle, 0);

        mMoneyText = typedArray.getString(R.styleable.PriceView_value_text);
        mMoneyColor = typedArray.getColor(R.styleable.PriceView_value_color, mMoneyColor);
        mIntegerSize = typedArray.getDimensionPixelSize(R.styleable.PriceView_integer_size, mIntegerSize);
        mDecimalsSize = typedArray.getDimensionPixelSize(R.styleable.PriceView_decimals_size, mDecimalsSize);

        mPrefix = typedArray.getString(R.styleable.PriceView_prefix_text);
        mPrefixSize = typedArray.getDimensionPixelSize(R.styleable.PriceView_prefix_size, mPrefixSize);
        mPrefixColor = typedArray.getColor(R.styleable.PriceView_prefix_color, mPrefixColor);
        mPrefixPadding = typedArray.getDimensionPixelSize(R.styleable.PriceView_prefix_padding, mPrefixPadding);

        mPointPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.PriceView_point_padding_left, mPointPaddingLeft);
        mPointPaddingRight = typedArray.getDimensionPixelSize(R.styleable.PriceView_point_padding_right, mPointPaddingRight);
        isThousandsUsed = typedArray.getBoolean(R.styleable.PriceView_integer_thousands, false);
        typedArray.recycle();

        /**
         * 获得绘制文本的宽和高
         */
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿

        mIntegerBound = new Rect();
        mDecimalsBound = new Rect();
        mPointBound = new Rect();
        mPrefixBound = new Rect();

        if (TextUtils.isEmpty(mPrefix)) {
            mPrefix = "¥";
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;

        int pointPosition = mMoneyText.indexOf(POINT);
        if (!mMoneyText.contains(POINT)) {
            pointPosition = mMoneyText.length();
        }
        //获取元的文本
        mIntegerValue = mMoneyText.substring(0, pointPosition);
        //如果使用千分符
        if (isThousandsUsed) {
            mIntegerValue = NumberFormat.getInstance().format(Long.valueOf(mIntegerValue));
        }
        //获取分的文本
        mDecimalsValue = mMoneyText.substring(pointPosition + 1, mMoneyText.length());
        //获取元小数点、的占据宽高
        mPaint.setTextSize(mIntegerSize);
        mPaint.getTextBounds(mIntegerValue, 0, mIntegerValue.length(), mIntegerBound);
        mPaint.getTextBounds(POINT, 0, POINT.length(), mPointBound);
        //获取分占据宽高
        mPaint.setTextSize(mDecimalsSize);
        mPaint.getTextBounds(mDecimalsValue, 0, mDecimalsValue.length(), mDecimalsBound);
        //获取前缀占据宽高
        mPaint.setTextSize(mPrefixSize);
        mPaint.getTextBounds(mPrefix, 0, mPrefix.length(), mPrefixBound);
        //文本占据的宽度
        mTextWidth = mIntegerBound.width() + mDecimalsBound.width() + mPrefixBound.width() + mPointBound.width()
                + mPointPaddingLeft + mPointPaddingRight + mPrefixPadding;
        // 设置宽度
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            width = specSize + getPaddingLeft() + getPaddingRight();
        } else {
            width = mTextWidth + getPaddingLeft() + getPaddingRight();
        }
        // 设置高度
        // 获取最大字号
        int maxSize = Math.max(mIntegerSize, mDecimalsSize);
        maxSize = Math.max(maxSize, mPrefixSize);
        mPaint.setTextSize(maxSize);
        // 获取基线距离
        maxDescent = mPaint.getFontMetrics().descent;
        int maxHeight = Math.max(mIntegerBound.height(), mDecimalsBound.height());
        maxHeight = Math.max(maxHeight, mPrefixBound.height());
        // 文本占据的高度 (给顶线和底线留间距)
        mTextHeight = maxHeight + (int) (maxDescent * 2 + 0.5f);

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = mTextHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制X坐标
        int drawX = (getMeasuredWidth() - mTextWidth) / 2;
        float drawY = (getMeasuredHeight() + mTextHeight) / 2 - maxDescent;

        //绘制前缀
        mPaint.setColor(mPrefixColor);
        mPaint.setTextSize(mPrefixSize);
        canvas.drawText(mPrefix, drawX, drawY, mPaint);
        //绘制元
        drawX += mPrefixBound.width() + mPrefixPadding;
        mPaint.setColor(mMoneyColor);
        mPaint.setTextSize(mIntegerSize);
        canvas.drawText(mIntegerValue, drawX, drawY, mPaint);
        //绘制小数点
        drawX += mIntegerBound.width() + mPointPaddingLeft;
        canvas.drawText(POINT, drawX, drawY, mPaint);
        //绘制分
        drawX += mPointPaddingRight;
        mPaint.setTextSize(mDecimalsSize);
        canvas.drawText(mDecimalsValue, drawX, drawY, mPaint);
    }

    public String getMoneyText() {
        return mMoneyText;
    }

    public void setMoneyText(String string) {
        if (string == null) {
            string = "";
        }
        mMoneyText = string;
        requestLayout();
        postInvalidate();
    }

    /**
     * 开启千分符号
     *
     * @param used yes or no
     */
    public void setGroupingUsed(boolean used) {
        isThousandsUsed = used;
    }

    public Paint getPaint() {
        return mPaint;
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }
}
