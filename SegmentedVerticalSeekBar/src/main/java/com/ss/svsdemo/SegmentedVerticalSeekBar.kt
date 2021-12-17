package com.ss.svsdemo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

class SegmentedVerticalSeekBar : View {
    private var mMinValue = DEFAULT_MIN_VALUE

    private var mMaxValue = DEFAULT_MAX_VALUE

    var step = 10

    val paint = Paint()
    val mPath = Path()
    private var mCornerRadius = 10f

    private var mCurrentValue = 0
    private var mEnabled = true
    var mIsAllRadius = false
    var mClockwise = false
    var mPyramidViewEnable = false

    var mTouchDisabled = false
    private var mProgressSweep = 0f
    private var mProgressPaint: Paint? = null
    private var scrWidth = 0
    private var scrHeight = 0
    private var mOnValuesChangeListener: OnValuesChangeListener? = null
    private var progressColor = 0
    private var backColor = 0
    private var firstRun = true

    private var delimiterSize = DEFAULT_DELIMITER_SIZE

    private var delimiterColor = DEFAULT_DELIMITER_COLOR

    private var gapSize = DEFAULT_GAP_SIZE

    private val segmentHeight: Int
        get() = height / max - delimiterSize

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
            mCurrentValue = a.getInteger(R.styleable.SegmentedVerticalSeekBar_currentValue, mCurrentValue)
            mCornerRadius = a.getDimension(R.styleable.SegmentedVerticalSeekBar_cornerRadius, mCornerRadius) as Float
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
        if(mIsAllRadius){
            drawContainerRectangles(canvas)
            drawFilledRectangles(canvas)
        }else{
            drawBackground(canvas)
            drawForeground(canvas)
            drawProgress(canvas)
            drawDelimiter(canvas)
        }


        if (firstRun) {
            firstRun = false
            value = mCurrentValue
        }
    }

    private fun drawContainerRectangles(canvas: Canvas) {
        val mSegmentHeight = segmentHeight

        var leftX = 0
        var rightX = width
        var topY = height
        var botY = topY - mSegmentHeight

        val containerRectanglePaint = buildRectanglePaint(backColor)
        if(mClockwise){
            for (i in 0 until max) {
                if(mPyramidViewEnable){
                    if(i>0){
                        leftX += gapSize
                        rightX -= gapSize
                    }
                }

                drawRoundedRect(canvas, leftX.toFloat(), topY.toFloat(), rightX.toFloat(), botY.toFloat(),
                    containerRectanglePaint)
                topY -= mSegmentHeight + delimiterSize
                botY = topY - mSegmentHeight
            }
        }else{
            var lastLeftX = ((max - 1) * gapSize)
            var lastrightX = (rightX - ((max - 1) * gapSize))

            for (i in 0 until max) {
                if(mPyramidViewEnable){
                    if(i>0){
                        lastLeftX -= gapSize
                        lastrightX += gapSize
                    }
                }
                drawRoundedRect(canvas, lastLeftX.toFloat(), topY.toFloat(), lastrightX.toFloat(), botY.toFloat(),
                    containerRectanglePaint)
                topY -= mSegmentHeight + delimiterSize
                botY = topY - mSegmentHeight
            }
        }
    }


    private fun drawFilledRectangles(canvas: Canvas) {
        val mSegmentHeight = segmentHeight

        var leftX = 0
        var rightX = width
        var topY = height
        var botY = topY - mSegmentHeight

        val fillRectanglePaint = buildRectanglePaint(progressColor)

        if(mClockwise){
            for (i in 0 until mCurrentValue) {
                if(mPyramidViewEnable){
                    if(i>0){
                        leftX += gapSize
                        rightX -= gapSize
                    }
                }
                drawRoundedRect(canvas, leftX.toFloat(), topY.toFloat(), rightX.toFloat(), botY.toFloat(), fillRectanglePaint)
                topY -= mSegmentHeight + delimiterSize
                botY = topY - mSegmentHeight
            }
        }else{
            var lastLeftX = ((max - 1) * gapSize)
            var lastrightX = (rightX - ((max - 1) * gapSize))

            for (i in 0 until mCurrentValue) {
                if(mPyramidViewEnable){
                    if(i>0){
                        lastLeftX -= gapSize
                        lastrightX += gapSize
                    }
                }
                drawRoundedRect(canvas, lastLeftX.toFloat(), topY.toFloat(), lastrightX.toFloat(), botY.toFloat(), fillRectanglePaint)
                topY -= mSegmentHeight + delimiterSize
                botY = topY - mSegmentHeight
            }
        }
    }

    private fun drawRoundedRect(canvas: Canvas,
        left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        canvas.drawRoundRect(RectF(left, top, right, bottom), cornerRadius.toFloat(), cornerRadius.toFloat(), paint)
    }

    private fun buildRectanglePaint(@ColorInt color: Int): Paint {
        paint.color = color
        paint.style = Paint.Style.FILL
        return paint
    }

    private fun drawBackground(canvas: Canvas){
        mPath.addRoundRect(
            RectF(0f, 0f, scrWidth.toFloat(), scrHeight.toFloat()),
            mCornerRadius.toFloat(),
            mCornerRadius.toFloat(),
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

    private fun drawDelimiter(canvas: Canvas){
        // delimiters
        paint.color = delimiterColor
        if (mMaxValue > 1) {
            val segmentHeight = scrHeight.toFloat() / mMaxValue
            for (i in 1 until mMaxValue) {
                val yPos = segmentHeight * i
                canvas.drawRect(
                    0F,
                    yPos,
                    scrWidth.toFloat(),
                    yPos + delimiterSize,
                    paint
                )
            }
        }
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
                    if (mOnValuesChangeListener != null) mOnValuesChangeListener!!.onStartTrackingTouch(
                        this
                    )
                    if (!mTouchDisabled) {
                        updateOnTouch(event)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!mTouchDisabled) {
                        updateOnTouch(event)
                        if (mOnValuesChangeListener != null) mOnValuesChangeListener!!.onStopTrackingTouch(
                            this
                        )
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mOnValuesChangeListener != null) mOnValuesChangeListener!!.onStopTrackingTouch(
                        this
                    )
                    isPressed = false
                    this.parent.requestDisallowInterceptTouchEvent(false)
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (mOnValuesChangeListener != null) mOnValuesChangeListener!!.onStopTrackingTouch(
                        this
                    )
                    isPressed = false
                    this.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            return true
        }
        return false
    }

    private fun updateOnTouch(event: MotionEvent) {
        isPressed = true
        val mTouch = convertTouchEventPoint(event.y)
        val progress = Math.round(mTouch).toInt()
        updateProgress(progress)
    }

    private fun convertTouchEventPoint(yPos: Float): Double {
        val wReturn: Float
        if (yPos > scrHeight * 2) {
            wReturn = (scrHeight * 2).toFloat()
            return wReturn.toDouble()
        } else if (yPos < 0) {
            wReturn = 0f
        } else {
            wReturn = yPos
        }
        return wReturn.toDouble()
    }

    private fun updateProgress(progress: Int) {
        var mProgress = progress
        mProgressSweep = mProgress.toFloat()
        mProgress = if (mProgress > scrHeight) scrHeight else mProgress
        mProgress = if (mProgress < 0) 0 else mProgress

        //convert progress to min-max range
        mCurrentValue = mProgress * (mMaxValue - mMinValue) / scrHeight + mMinValue
        //reverse value because progress is descending
        mCurrentValue = mMaxValue + mMinValue - mCurrentValue
        //if value is not max or min, apply step
        if (mCurrentValue != mMaxValue && mCurrentValue != mMinValue) {
            mCurrentValue = mCurrentValue - mCurrentValue % step + mMinValue % step
        }
        if (mOnValuesChangeListener != null) {
            mOnValuesChangeListener!!
                .onProgressChanged(this, mCurrentValue)
        }
        invalidate()
    }

    private fun updateProgressByValue(value: Int) {
        mCurrentValue = value
        mCurrentValue = if (mCurrentValue > mMaxValue) mMaxValue else mCurrentValue
        mCurrentValue = if (mCurrentValue < mMinValue) mMinValue else mCurrentValue

        //convert min-max range to progress
        mProgressSweep = ((mCurrentValue - mMinValue) * scrHeight / (mMaxValue - mMinValue)).toFloat()
        //reverse value because progress is descending
        mProgressSweep = scrHeight - mProgressSweep
        if (mOnValuesChangeListener != null) {
            mOnValuesChangeListener!!
                .onProgressChanged(this, mCurrentValue)
        }
        invalidate()
    }

    interface OnValuesChangeListener {
        fun onProgressChanged(segmentedPointsSeekBar: SegmentedVerticalSeekBar?, progress: Int)
        fun onStartTrackingTouch(segmentedPointsSeekBar: SegmentedVerticalSeekBar?)
        fun onStopTrackingTouch(segmentedPointsSeekBar: SegmentedVerticalSeekBar?)
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
        const val DEFAULT_DELIMITER_SIZE = 10
        const val DEFAULT_GAP_SIZE = 20
        const val DEFAULT_MAX_VALUE = 5
        const val DEFAULT_MIN_VALUE = 0
    }
}