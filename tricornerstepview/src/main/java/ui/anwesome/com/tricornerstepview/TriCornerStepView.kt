package ui.anwesome.com.tricornerstepview

/**
 * Created by anweshmishra on 18/10/18.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color

val nodes : Int = 5
val corners : Int = 3

fun Canvas.drawTCSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val r : Float = gap / 3
    val ballR : Float = gap / 12
    val sk : Float = 1f / corners
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = Math.min(w, h) / 60
    paint.color = Color.parseColor("#673AB7")
    val deg : Double = (2 * Math.PI) / corners
    save()
    translate(gap + i * gap, h/2)
    for (j in 0..corners - 1) {
        val sc : Float = Math.min(sk, Math.max(0f, scale - sk * j)) * corners
        val x : Float = r * sc * Math.cos(j * deg).toFloat()
        val y: Float = r * sc * Math.sin(j * deg).toFloat()
        save()
        translate(x, y)
        drawCircle(0f, 0f, ballR, paint)
        restore()
    }
    restore()
}

class TriCornerStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += (0.1f/ corners) * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }
}