package com.ss.svs

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class SegmentedVerticalSeekBar : View {
    private var mMinValue = DEFAULT_MIN_VALUE

    private var mMaxValue = DEFAULT_MAX_VALUE

    private val paint = Paint()
    private val mPath = Path()
    private var mCornerRadius = 10f
    private var mCurrentValue = 0
    private var mEnabled = true
    private val baselineAligned = true

    private var step = 1
    private var mIsAllRadius = false
    private var mClockwise = false
    private var mPyramidViewEnable = false
    private var mTouchDisabled = false

    private var mProgressSweep = 0f
    private var mProgressPaint: Paint? = null
    private var scrWidth = 0
    private var scrHeight = 0
    private var mOnValuesChangeListener: OnValuesChangeListener? = null
    private var progressColor = 0
    private var backColor = 0
    private var firstRun = true

    private var delimiterColor = DEFAULT_DELIMITER_COLOR

    private var gapSize = DEFAULT_GAP_SIZE

    private var circleDiameter = 53f
    private var circleDiameterStep = 8f
    private val circlesDiameters: List<Float>
        get() = (0 until max).map {
            circleDiameter + it * circleDiameterStep
        }

    private var xs: List<Float> = listOf()

    private val delimiterSize: Float
        get() = (width - circlesDiameters.sum()) / (max - 1)

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        progressColor = ContextCompat.getColor(context, R.color.color_progress)
        backColor = ContextCompat.getColor(context, R.color.color_background)
        delimiterColor = ContextCompat.getColor(context, R.color.black)
        mCurrentValue = mMaxValue / 2
        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.SegmentedVerticalSeekBar, 0, 0
            )
            mMaxValue = a.getInteger(R.styleable.SegmentedVerticalSeekBar_maxValue, mMaxValue)
            mMinValue = a.getInteger(R.styleable.SegmentedVerticalSeekBar_minValue, mMinValue)
            step = a.getInteger(R.styleable.SegmentedVerticalSeekBar_step, step)
            circleDiameter = a.getDimension(R.styleable.SegmentedVerticalSeekBar_diameterStart, 20f)
            circleDiameterStep = a.getDimension(R.styleable.SegmentedVerticalSeekBar_diameterStep, 0f)
            mCurrentValue = a.getInteger(R.styleable.SegmentedVerticalSeekBar_currentValue, mCurrentValue)
            mCornerRadius = a.getDimension(R.styleable.SegmentedVerticalSeekBar_cornerRadius, mCornerRadius)
            progressColor = a.getColor(R.styleable.SegmentedVerticalSeekBar_progressColor, progressColor)
            backColor = a.getColor(R.styleable.SegmentedVerticalSeekBar_backgroundColor, backColor)
            delimiterColor = a.getColor(R.styleable.SegmentedVerticalSeekBar_delimiterColor, delimiterColor)
            mEnabled = a.getBoolean(R.styleable.SegmentedVerticalSeekBar_enabled, mEnabled)
            mTouchDisabled = a.getBoolean(R.styleable.SegmentedVerticalSeekBar_touchDisabled, mTouchDisabled)
            mIsAllRadius = a.getBoolean(R.styleable.SegmentedVerticalSeekBar_isAllRadius, mIsAllRadius)
            mClockwise = a.getBoolean(R.styleable.SegmentedVerticalSeekBar_clockwise, mClockwise)
            mPyramidViewEnable = a.getBoolean(R.styleable.SegmentedVerticalSeekBar_pyramidViewEnable, mPyramidViewEnable)
            a.recycle()
        }

        // range check
        mCurrentValue = if (mCurrentValue > mMaxValue) mMaxValue else mCurrentValue
        mCurrentValue = if (mCurrentValue < mMinValue) mMinValue else mCurrentValue
        mProgressPaint = Paint()
        mProgressPaint!!.color = progressColor
        mProgressPaint!!.isAntiAlias = true
        mProgressPaint!!.style = Paint.Style.STROKE
        scrHeight = context.resources.displayMetrics.heightPixels
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        scrWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        scrHeight = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        mProgressPaint!!.strokeWidth = scrWidth.toFloat()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        gapSize = ((width /mMaxValue)/ 2)
        if (mIsAllRadius) {
            drawCircles(canvas)
            drawFilledCircles(canvas)
        } else {
            drawBackground(canvas)
            drawForeground(canvas)
            drawProgress(canvas)
        }

        if (firstRun) {
            firstRun = false
            value = mCurrentValue
        }
    }

    private fun drawCircles(canvas: Canvas) {
        paint.color = backColor
        paint.style = Paint.Style.FILL
        xs = circlesDiameters.mapIndexed { i, d ->
            circleDiameter / 2 + i * (d / 2 + delimiterSize + circleDiameter / 2)
        }
        circlesDiameters.forEachIndexed { i, d ->
            val x = circleDiameter / 2f + i * (d / 2 + delimiterSize + circleDiameter / 2f)
            val y = if (baselineAligned) (max - i - 1) * circleDiameterStep / 2f + height /2f else height / 2f
            canvas.drawCircle(x, y, d / 2f, paint)
        }
    }

    private fun drawFilledCircles(canvas: Canvas) {
        paint.color = progressColor
        paint.style = Paint.Style.FILL
        for (i in 0 until mCurrentValue) {
            val d = circlesDiameters[i]
            val x = circleDiameter / 2f + i * (d / 2 + delimiterSize + circleDiameter / 2)
            val y = if (baselineAligned) (max - i - 1) * circleDiameterStep / 2f + height /2f else height / 2f
            canvas.drawCircle(x, y, d / 2f, paint)
        }
    }

    private fun drawBackground(canvas: Canvas){
        mPath.addRoundRect(
            RectF(0f, 0f, scrWidth.toFloat(), scrHeight.toFloat()),
            mCornerRadius,
            mCornerRadius,
            Path.Direction.CCW
        )
        canvas.clipPath(mPath, Region.Op.INTERSECT)
    }

    private fun drawForeground(canvas: Canvas){
        paint.alpha = 255
        paint.color = backColor
        paint.isAntiAlias = true
        canvas.drawRect(0f, 0f, scrWidth.toFloat(), scrHeight.toFloat(), paint)
    }

    private fun drawProgress(canvas: Canvas){
        canvas.drawLine(
            (canvas.width / 2).toFloat(),
            canvas.height.toFloat(),
            (canvas.width / 2).toFloat(),
            mProgressSweep,
            mProgressPaint!!
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mEnabled) {
            this.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (mOnValuesChangeListener != null) mOnValuesChangeListener!!.onStartTrackingTouch(this)
                    if (!mTouchDisabled) {
                        updateOnTouch(event)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!mTouchDisabled) {
                        updateOnTouch(event)
                        if (mOnValuesChangeListener != null) mOnValuesChangeListener!!.onStopTrackingTouch(this)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mOnValuesChangeListener != null) mOnValuesChangeListener!!.onStopTouch(this)
                    isPressed = false
                    this.parent.requestDisallowInterceptTouchEvent(false)
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (mOnValuesChangeListener != null) mOnValuesChangeListener!!.onStopTouch(this)
                    isPressed = false
                    this.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            return true
        }
        return false
    }

    private fun convertTouchEventPoint(yPos: Float): Float {
        return if (yPos < 0) 0f else if (yPos > scrWidth) scrWidth.toFloat() else yPos
    }

    private fun updateOnTouch(event: MotionEvent) {
        isPressed = true
        updateProgress(convertTouchEventPoint(event.x))
    }

    private fun updateProgress(progress: Float) {
        val xxs = circlesDiameters.mapIndexed { i, d ->
            xs[i] - d / 2
        }
        getUpdated(xxs, progress)
        if (mCurrentValue != mMaxValue && mCurrentValue != mMinValue) {
            mCurrentValue = mCurrentValue - mCurrentValue % step + mMinValue % step
        }
        if (mOnValuesChangeListener != null) {
            mOnValuesChangeListener!!
                .onProgressChanged(this, mCurrentValue)
        }
        invalidate()
    }

    private fun getUpdated(xs: List<Float>, progress: Float) {
        xs.forEachIndexed { index, x ->
            if (progress <= x) {
                mCurrentValue = index
                return
            }
        }
        mCurrentValue = max
        return
    }

    private fun updateProgressByValue(value: Int) {
        mCurrentValue = value
        mCurrentValue = if (mCurrentValue > mMaxValue) mMaxValue else mCurrentValue
        mCurrentValue = if (mCurrentValue < mMinValue) mMinValue else mCurrentValue

        if (mOnValuesChangeListener != null) {
            mOnValuesChangeListener!!
                .onProgressChanged(this, mCurrentValue)
        }
        invalidate()
    }

    interface OnValuesChangeListener {
        fun onProgressChanged(segmentedPointsSeekBar: SegmentedVerticalSeekBar, progress: Int)
        fun onStartTrackingTouch(segmentedPointsSeekBar: SegmentedVerticalSeekBar)
        fun onStopTrackingTouch(segmentedPointsSeekBar: SegmentedVerticalSeekBar)
        fun onStopTouch(segmentedPointsSeekBar: SegmentedVerticalSeekBar)
    }

    var value: Int
        get() = mCurrentValue
        set(progress) {
            var mProgress = progress
            mProgress = if (mProgress > mMaxValue) mMaxValue else mProgress
            mProgress = if (mProgress < mMinValue) mMinValue else mProgress
            updateProgressByValue(mProgress)
        }

    override fun isEnabled(): Boolean {
        return mEnabled
    }

    override fun setEnabled(enabled: Boolean) {
        mEnabled = enabled
    }

    var max: Int
        get() = mMaxValue
        set(mMax) {
            require(mMax > mMinValue) { "Max should not be less than zero" }
            this.mMaxValue = mMax
            if(mCurrentValue > mMax){
                updateProgressByValue(mMax)
            }
            invalidate()
        }
    var cornerRadius: Float
        get() = mCornerRadius
        set(mRadius) {
            mCornerRadius = mRadius
            invalidate()
        }

    var pyramidViewEnable: Boolean
        get() = mPyramidViewEnable
        set(flag) {
            clockwise = true
            mPyramidViewEnable = flag
            invalidate()
        }

    var isAllRadius: Boolean
        get() = mIsAllRadius
        set(flag) {
            mIsAllRadius = flag
            invalidate()
        }

    var clockwise: Boolean
        get() = mClockwise
        set(flag) {
            if(pyramidViewEnable){
                mClockwise = flag
                invalidate()
            }
        }

    var touchDisabled: Boolean
        get() = mTouchDisabled
        set(flag) {
            mTouchDisabled = flag
            invalidate()
        }

    fun setOnBoxedPointsChangeListener(onValuesChangeListener: OnValuesChangeListener?) {
        mOnValuesChangeListener = onValuesChangeListener
    }

    companion object {
        val DEFAULT_DELIMITER_COLOR = Color.parseColor("#ffffff")
        const val DEFAULT_GAP_SIZE = 20
        const val DEFAULT_MAX_VALUE = 5
        const val DEFAULT_MIN_VALUE = 0
    }
}