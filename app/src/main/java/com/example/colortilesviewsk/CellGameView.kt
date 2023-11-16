package com.example.colortilesviewsk

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class CellGameView @JvmOverloads constructor(
    context: android.content.Context,
    attrs: android.util.AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {
    var items: List<List<Cell>> = emptyList()
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var onCellClick: (Cell) -> Unit = {}

    private var cellWidth = 0f
    private var cellHeight = 0f

    private val gestureDetector = GestureDetector(context, CellGestureListener { onCellClick(it) })

    private val activePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = context.getColor(R.color.bright)
        isAntiAlias = true
        strokeWidth = 2f
    }

    private val disabledPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = context.getColor(R.color.dark)
        isAntiAlias = true
        strokeWidth = 2f
    }

    private val strokePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = context.getColor(R.color.black)
        isAntiAlias = true
        strokeWidth = 2f
    }

    private var animationStartTime = 0L
    private val animationDuration = 200
    private val fps = 120

    private val maxAlpha = 255

    private var animationAlpha = maxAlpha
    private var animationX: Int? = null
    private var animationY: Int? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        return gestureDetector.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        cellWidth = width.toFloat() / (items.firstOrNull()?.size ?: 1)
        cellHeight = height.toFloat() / items.size

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)

        val elapsedTime = System.currentTimeMillis() - animationStartTime

        for (row in items) {
            for (cell in row) {
                val paint = if (cell.isActive) activePaint else disabledPaint
                paint.alpha = if (cell.x == animationX || cell.y == animationY) animationAlpha else maxAlpha
                canvas.drawRect(
                    cell.x * cellWidth,
                    cell.y * cellHeight,
                    (cell.x + 1) * cellWidth,
                    (cell.y + 1) * cellHeight,
                    paint
                )
                canvas.drawRect(
                    cell.x * cellWidth,
                    cell.y * cellHeight,
                    (cell.x + 1) * cellWidth,
                    (cell.y + 1) * cellHeight,
                    strokePaint
                )
            }
        }

        animationAlpha = (maxAlpha * elapsedTime / animationDuration).toInt()
        animationAlpha = animationAlpha.coerceIn(0, maxAlpha)

        if (elapsedTime < animationDuration) {
            postInvalidateDelayed(1000L / fps)
        }
    }

    inner class CellGestureListener(val onClick: (Cell) -> Unit) : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent): Boolean {
            val x = (e.x / cellWidth).toInt()
            val y = (e.y / cellHeight).toInt()
            onClick(items[y][x])
            animationX = x
            animationY = y
            animationAlpha = 0
            animationStartTime = System.currentTimeMillis()
            return true
        }

        override fun onShowPress(p0: MotionEvent) {}

        override fun onSingleTapUp(p0: MotionEvent): Boolean = false

        override fun onScroll(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean = false

        override fun onLongPress(p0: MotionEvent) {}

        override fun onFling(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean = false
    }
}