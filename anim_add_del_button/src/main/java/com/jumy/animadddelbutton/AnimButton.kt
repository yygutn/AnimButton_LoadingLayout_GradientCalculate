package com.jumy.animadddelbutton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

/**
 * Created by Jumy on 17/1/9 11:33.
 * Copyright (c) 2016, yygutn@gmail.com All Rights Reserved.
 */
class AnimButton : View {
    companion object {
        val TAG = "AnimButton"
    }

    //控件paddingLeft paddingTop + paint的width
    private var mLeft: Int = 0
    private var mTop: Int = 0
    //宽高
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    //加减圆的Path的Region
    private lateinit var mAddRegion: Region
    private lateinit var mDelRegion: Region
    private lateinit var mAddPath: Path
    private lateinit var mDelPath: Path

    //加按钮
    private lateinit var mAddPaint: Paint
    //加按钮是否开启fill模式，默认是stroke(xml)(false)
    private var isAddFillMode = false
    //加按钮背景色&加号颜色
    private var mAddEnableBgColor = 0
    private var mAddEnableFgColor = 0
    //加按钮不可用时候背景色&加号颜色
    private var mAddDisableBgColor = 0
    private var mAddDisableFgColor = 0

    //减按钮
    private lateinit var mDelPaint: Paint
    //减按钮是否开启fill模式，默认是stroke(xml)(false)
    private var isDelFillMode = false
    //减按钮背景色&减号颜色
    private var mDelEnableBgColor = 0
    private var mDelEnableFgColor = 0
    //减按钮不可用时候背景色&减号颜色
    private var mDelDisableBgColor = 0
    private var mDelDisableFgColor = 0


    //最大数量和当前数量
    private var mMaxCount = 10
    private var mCount = 0

    //圆半径
    private var mRadius = 0F
    //圆宽度
    private var mCircleWidth = 0F
    //线宽度
    private var mLineWidth = 0F

    //两圆之间的距离
    private var mGapBetweenCircle = 0f

    //绘制数量的textSize
    private var mTextSize = 0f
    private lateinit var mTextPaint: Paint
    private lateinit var mFontMetrics: Paint.FontMetrics

    //动画的基准值  动画：减0~1  加 1~0
    //普通状态下显示都是0
    private lateinit var mAnimAdd: ValueAnimator
    private lateinit var mAnimDel: ValueAnimator
    private var mAnimFraction = 0f

    //展开 加入购物车动画
    private lateinit var mAnimExpandHint: ValueAnimator
    private lateinit var mAnimReduceHint: ValueAnimator


    //是否处于HintMode模式下，count==0时，且第一段收缩动画做完了，是true
    private var isHintMode = false

    //提示语收缩动画 0-1 展开1-0
    //普通模式是1，只在isHintMode=true有效
    private var mAnimExpandHintFraction = 0f

    //展开动画结束后显示文字
    private var isShowHintText = false

    //数量为0，hint文字 字体颜色&背景色(xml)大小
    private lateinit var mHintPaint: Paint
    private var mHintBgColor = 0
    private var mHintTextSize = 0
    private var mHintText = ""
    private var mHintTextColor = 0

    //圆角值(xml)
    private var mHintBgRoundValue = 0

    //点击回调
    private var mOnAddDelListener: IOnClickListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (context == null) {
            throw NullPointerException("context can not be null!")
        }
        init(context, attrs, defStyleAttr)
    }

    fun getCount(): Int {
        return mCount
    }

    /**
     * 设置当前数量
     */
    fun setCount(count: Int): AnimButton {
        mCount = count
        //先暂停动画
        pauseAllAnim()
        //复用机制处理
        if (mCount == 0) {
            //0不显示 数字和-号
            mAnimFraction = 1f
        } else {
            mAnimFraction = 0f
        }
        initHintSetting()
        return this
    }

    //暂停所有动画
    fun pauseAllAnim() {
        if (mAnimAdd.isRunning) {
            mAnimAdd.cancel()
        }
        if (mAnimDel.isRunning) {
            mAnimDel.cancel()
        }
        if (mAnimExpandHint.isRunning) {
            mAnimExpandHint.cancel()
        }
        if (mAnimReduceHint.isRunning) {
            mAnimReduceHint.cancel()
        }
    }

    fun getCustomListener(): IOnClickListener? = mOnAddDelListener

    fun getMaxCount(): Int {
        return mMaxCount
    }

    fun setMaxCount(count: Int): AnimButton {
        mMaxCount = count
        return this
    }

    //设置加减监听器
    fun setOnClickListener(listener: IOnClickListener): AnimButton {
        mOnAddDelListener = listener
        return this
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        Log.w(TAG, "init")
        //初始值
        initDefaultValue()
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimShopButton, defStyleAttr, 0)
        val indexCount = typedArray.indexCount
        for (i in 0..indexCount - 1) {
            val index = typedArray.getIndex(i)
            when (index) {
                R.styleable.AnimShopButton_gapBetweenCircle -> mGapBetweenCircle = typedArray.getDimension(index, mGapBetweenCircle)
                R.styleable.AnimShopButton_isAddFillMode -> isAddFillMode = typedArray.getBoolean(index, isAddFillMode)
                R.styleable.AnimShopButton_addEnableBgColor -> mAddEnableBgColor = typedArray.getColor(index, mAddEnableBgColor)
                R.styleable.AnimShopButton_addEnableFgColor -> mAddEnableFgColor = typedArray.getColor(index, mAddEnableFgColor)
                R.styleable.AnimShopButton_addDisableBgColor -> mAddDisableBgColor = typedArray.getColor(index, mAddDisableBgColor)
                R.styleable.AnimShopButton_addDisableFgColor -> mAddDisableFgColor = typedArray.getColor(index, mAddDisableFgColor)
                R.styleable.AnimShopButton_isDelFillMode -> isDelFillMode = typedArray.getBoolean(index, isDelFillMode)
                R.styleable.AnimShopButton_delEnableBgColor -> mDelEnableBgColor = typedArray.getColor(index, mDelEnableBgColor)
                R.styleable.AnimShopButton_delEnableFgColor -> mDelEnableFgColor = typedArray.getColor(index, mDelEnableFgColor)
                R.styleable.AnimShopButton_delDisableBgColor -> mDelDisableBgColor = typedArray.getColor(index, mDelDisableBgColor)
                R.styleable.AnimShopButton_delDisableFgColor -> mDelDisableFgColor = typedArray.getColor(index, mDelDisableFgColor)
                R.styleable.AnimShopButton_maxCount -> mMaxCount = typedArray.getInteger(index, mMaxCount)
                R.styleable.AnimShopButton_count -> mCount = typedArray.getInteger(index, mCount)
                R.styleable.AnimShopButton_radius -> mRadius = typedArray.getDimension(index, mRadius)
                R.styleable.AnimShopButton_circleStrokeWidth -> mCircleWidth = typedArray.getDimension(index, mCircleWidth)
                R.styleable.AnimShopButton_lineWidth -> mLineWidth = typedArray.getDimension(index, mLineWidth)
                R.styleable.AnimShopButton_numTextSize -> mTextSize = typedArray.getDimension(index, mTextSize)
                R.styleable.AnimShopButton_hintText -> mHintText = typedArray.getString(index)
                R.styleable.AnimShopButton_hintBgColor -> mHintBgColor = typedArray.getColor(index, mHintBgColor)
                R.styleable.AnimShopButton_hintFgColor -> mHintTextColor = typedArray.getColor(index, mHintTextColor)
                R.styleable.AnimShopButton_hintTextSize -> mHintTextSize = typedArray.getDimensionPixelSize(index, mHintTextSize)
                R.styleable.AnimShopButton_hintBgRoundValue -> mHintBgRoundValue = typedArray.getDimensionPixelSize(index, mHintBgRoundValue)
            }
        }
        typedArray.recycle()

        mAddRegion = Region()
        mDelRegion = Region()
        mAddPath = Path()
        mDelPath = Path()

        mAddPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        if (isAddFillMode) {
            mAddPaint.style = Paint.Style.FILL
        } else {
            mAddPaint.style = Paint.Style.STROKE
        }
        mDelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        if (isDelFillMode) {
            mDelPaint.style = Paint.Style.FILL
        } else {
            mDelPaint.style = Paint.Style.STROKE
        }

        mHintPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHintPaint.style = Paint.Style.FILL
        mHintPaint.textSize = mHintTextSize.toFloat()

        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.textSize = mTextSize
        mFontMetrics = mTextPaint.fontMetrics

        //动画 +
        mAnimAdd = ValueAnimator.ofFloat(1f, 0f)
        mAnimAdd.addUpdateListener { animation ->
            mAnimFraction = animation.animatedValue as Float
            invalidate()
        }
//        mAnimAdd.addUpdateListener {  }
        mAnimAdd.duration = 350

        //提示语收缩动画0-1
        mAnimReduceHint = ValueAnimator.ofFloat(0f, 1f)
        mAnimReduceHint.addUpdateListener { animation ->
            mAnimExpandHintFraction = animation.animatedValue as Float
            invalidate()
        }
        mAnimReduceHint.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (mCount >= 1) {
                    //底色也不显示
                    isHintMode = false
                }
                if (mCount >= 1) {
                    Log.w(TAG, "现在还是>=1，开始收缩动画")
                    if (!mAnimAdd.isRunning) {
                        mAnimAdd.start()
                    }
                }
            }

            override fun onAnimationStart(animation: Animator) {
                if (mCount == 1) {
                    //先不显示文字
                    isShowHintText = false
                }
            }
        })
        mAnimReduceHint.duration = 350

        //动画 -
        mAnimDel = ValueAnimator.ofFloat(0f, 1f)
        mAnimDel.addUpdateListener { animation ->
            mAnimFraction = animation.animatedValue as Float
            invalidate()
        }
        //1-0的动画
        mAnimDel.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (mCount == 0) {
                    Log.w(TAG, "现在还是0，onAnimationEnd called with: animation = [$animation]")
                    if (!mAnimExpandHint.isRunning) {
                        mAnimExpandHint.start()
                    }
                }
            }
        })
        mAnimDel.duration = 350

        //提示语展开动画
        //分析这个动画，最初是个圆。 就是left 不断减小
        mAnimExpandHint = ValueAnimator.ofFloat(1f, 0f)
        mAnimExpandHint.addUpdateListener { animation ->
            mAnimExpandHintFraction = animation.animatedValue as Float
            invalidate()
        }
        mAnimExpandHint.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mCount == 0) {
                    isShowHintText = true
                }
            }

            override fun onAnimationStart(animation: Animator?) {
                if (mCount == 0) {
                    isHintMode = true
                }
            }
        })
        mAnimExpandHint.duration = 350
    }

    //设置初始值
    private fun initDefaultValue() {
        Log.w(TAG, "initDefaultValue")
        mGapBetweenCircle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, resources.displayMetrics)

        isAddFillMode = true
        mAddEnableBgColor = Color.parseColor("#FFDC5B")
        mAddEnableFgColor = Color.BLACK
        mAddDisableBgColor = Color.parseColor("#979797")
        mAddDisableFgColor = Color.BLACK

        isDelFillMode = false
        mDelEnableBgColor = Color.parseColor("#979797")
        mDelEnableFgColor = Color.parseColor("#979797")
        mDelDisableBgColor = Color.parseColor("#979797")
        mDelDisableFgColor = Color.parseColor("#979797")

        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12.5f, resources.displayMetrics)
        mCircleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
        mLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics)
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14.5f, resources.displayMetrics)

        mHintText = "加入购物车"
        mHintBgColor = mAddEnableBgColor
        mHintTextColor = mAddEnableFgColor
        mHintTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()
        mHintBgRoundValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.w(TAG, "onMeasure")
        val wMode = View.MeasureSpec.getMode(widthMeasureSpec)
        var wSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val hMode = View.MeasureSpec.getMode(heightMeasureSpec)
        var hSize = View.MeasureSpec.getSize(heightMeasureSpec)
        when (wMode) {
            MeasureSpec.EXACTLY -> {

            }
            MeasureSpec.AT_MOST -> {
                //不超过父控件给的范围，自由发挥
                val computeSize = (mCircleWidth * 2 + paddingLeft + mRadius * 2 + mGapBetweenCircle + mRadius * 2 + paddingRight + mCircleWidth * 2).toInt()
                wSize = if (computeSize < wSize) computeSize else wSize
            }
            MeasureSpec.UNSPECIFIED -> {
                //自由发挥
                val computeSize = (mCircleWidth * 2 + paddingLeft + mRadius * 2 + mGapBetweenCircle + mRadius * 2 + paddingRight + mCircleWidth * 2).toInt()
                wSize = computeSize
            }
        }
        when (hMode) {
            MeasureSpec.EXACTLY -> {

            }
            MeasureSpec.AT_MOST -> {
                //不超过父控件给的范围，自由发挥
                val computeSize = (paddingTop + mRadius * 2 + paddingBottom + mCircleWidth * 2)
                hSize = if (computeSize < hSize) computeSize.toInt() else hSize
            }
            MeasureSpec.UNSPECIFIED -> {
                //自由发挥
                val computeSize = (paddingTop + mRadius * 2 + paddingBottom + mCircleWidth * 2)
                hSize = computeSize.toInt()
            }
        }

        setMeasuredDimension(wSize, hSize)

        //复用走这里，所以初始化UI显示参数
        mAnimFraction = 0f
        initHintSetting()
    }

    private fun initHintSetting() {
        Log.w(TAG, "initHintSetting")
        if (mCount == 0) {
            isHintMode = true
            isShowHintText = true
            mAnimExpandHintFraction = 0f
        } else {
            isHintMode = false
            isShowHintText = false
            mAnimExpandHintFraction = 1f
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.w(TAG, "onSizeChanged")
        super.onSizeChanged(w, h, oldw, oldh)
        mLeft = (paddingLeft + mCircleWidth).toInt()
        mTop = (paddingTop + mCircleWidth).toInt()
        mWidth = w
        mHeight = h
    }

    override fun onDraw(canvas: Canvas) {
        Log.w(TAG, "onDraw")
        if (isHintMode) {
            //add hint  展开动画
            mHintPaint.color = mHintBgColor
            val rectF = RectF(mLeft + (mWidth - mRadius * 2) * mAnimExpandHintFraction,
                    mTop.toFloat(), mWidth - mCircleWidth, mHeight - mCircleWidth)
            canvas.drawRoundRect(rectF, mHintBgRoundValue.toFloat(), mHintBgRoundValue.toFloat(), mHintPaint)
            if (isShowHintText) {
                //前景文字
                mHintPaint.color = mHintTextColor
                //计算BaseLine绘制的起点X坐标
                val baseX = (mWidth / 2 - mHintPaint.measureText(mHintText) / 2) / 1
                //计算baseLine绘制起点Y坐标
                val baseY = (mHeight / 2 - (mHintPaint.descent() + mHintPaint.ascent()) / 2) / 1
                canvas.drawText(mHintText, baseX, baseY, mHintPaint)
            }
        } else {
            //动画 mAnimFraction: 减0-1，加1-0
            //动画位移Max
            val animOffsetMax = mRadius * 2 + mGapBetweenCircle
            //透明度动画基准
            val animAlphaMax = 255
            //旋转动画基准
            val animRotateMax = 360

            //左边
            //背景  圆
            if (mCount > 0) {
                mDelPaint.color = mDelEnableBgColor
            } else {
                mDelPaint.color = mDelDisableBgColor
            }
            mDelPaint.alpha = animAlphaMax * (1 - mAnimFraction).toInt()

            mDelPaint.strokeWidth = mCircleWidth
            mDelPath.reset()

            //改变圆心的X坐标，实现位移
            mDelPath.addCircle(animOffsetMax * mAnimFraction + mLeft + mRadius, mTop + mRadius, mRadius, Path.Direction.CW)
            mDelRegion.setPath(mDelPath, Region(mLeft, mTop, mWidth - paddingRight, mHeight - paddingBottom))
            canvas.drawPath(mDelPath, mDelPaint)

            //前景 -
            if (mCount > 0) {
                mDelPaint.color = mDelEnableFgColor
            } else {
                mDelPaint.color = mDelDisableFgColor
            }
            mDelPaint.strokeWidth = mLineWidth
            //旋转动画
            canvas.save()
            canvas.translate(animOffsetMax * mAnimFraction + mLeft + mRadius, mTop + mRadius)
            canvas.rotate(animRotateMax * (1 - mAnimFraction) / 1)
            canvas.drawLine(-mRadius / 2, 0f, +mRadius / 2, 0f, mDelPaint)
            canvas.restore()

            //数量
            canvas.save()
            //平移动画
            canvas.translate(mAnimFraction * (mGapBetweenCircle / 2 - mTextPaint.measureText("$mCount") / 2 + mRadius), 0f)
            //旋转动画
            canvas.rotate(360 * mAnimFraction, mGapBetweenCircle / 2 + mLeft + mRadius * 2, mTop + mRadius)
            //透明度动画
            mTextPaint.alpha = (255 * (1 - mAnimFraction)).toInt()
            //没有动画的普通写法, x left, y baseLine
            canvas.drawText("$mCount", mGapBetweenCircle / 2 - mTextPaint.measureText("$mCount") / 2 + mLeft + mRadius * 2,
                    mTop + mRadius - (mFontMetrics.top + mFontMetrics.bottom) / 2, mTextPaint)
            canvas.restore()

            //右边
            //背景 圆
            if (mCount < mMaxCount) {
                mAddPaint.color = mAddEnableBgColor
            } else {
                mAddPaint.color = mAddDisableBgColor
            }
            mAddPaint.strokeWidth = mCircleWidth
            val left = mLeft + mRadius * 2 + mGapBetweenCircle
            mAddPath.reset()
            mAddPath.addCircle(left + mRadius, mTop + mRadius, mRadius, Path.Direction.CW)
            mAddRegion.setPath(mAddPath, Region(mLeft, mTop, mWidth - paddingRight, mHeight - paddingBottom))
            canvas.drawPath(mAddPath, mAddPaint)
            //前景 +
            if (mCount < mMaxCount) {
                mAddPaint.color = mAddEnableFgColor
            } else {
                mAddPaint.color = mAddDisableFgColor
            }
            mAddPaint.strokeWidth = mLineWidth
            canvas.drawLine(left + mRadius / 2, mTop + mRadius, left + mRadius / 2 + mRadius, mTop + mRadius, mAddPaint)
            canvas.drawLine(left + mRadius, mTop + mRadius / 2, left + mRadius, mTop + mRadius / 2 + mRadius, mAddPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.w(TAG, "onTouchEvent")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //hint文字模式
                if (isHintMode) {
                    onAddClick()
                    return true
                } else {
                    if (mAddRegion.contains(event.x.toInt(), event.y.toInt())) {
                        onAddClick()
                        return true
                    } else if (mDelRegion.contains(event.x.toInt(), event.y.toInt())) {
                        onDelClick()
                        return true
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun onDelClick() {
        if (mCount > 0) {
            mCount--
            onCountDelSuccess()
            if (mOnAddDelListener != null) {
                mOnAddDelListener?.onDelSuccess(mCount)
            }
        } else {
            if (mOnAddDelListener != null) {
                mOnAddDelListener?.onDelFailed(mCount, IOnClickListener.FailType.COUNT_MIN)
            }
        }
    }

    private fun onAddClick() {
        if (mCount < mMaxCount) {
            mCount++
            onCountAddSuccess()
            if (mOnAddDelListener != null) {
                mOnAddDelListener?.onAddSuccess(mCount)
            }
        } else {
            if (mOnAddDelListener != null) {
                mOnAddDelListener?.onAddFailed(mCount, IOnClickListener.FailType.COUNT_MAX)
            }
        }
    }

    /**
     * 数量增加成功后，使用者回调以执行动画。
     */
    fun onCountAddSuccess() {
        if (mCount === 1) {
            pauseAllAnim()
            mAnimReduceHint.start()
        } else {
            mAnimFraction = 0f
            invalidate()
        }
    }

    /**
     * 数量减少成功后，使用者回调以执行动画。
     */
    fun onCountDelSuccess() {
        if (mCount === 0) {
            pauseAllAnim()
            mAnimDel.start()
        } else {
            mAnimFraction = 0f
            invalidate()
        }
    }
}