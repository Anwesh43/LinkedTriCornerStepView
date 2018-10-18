package ui.anwesome.com.tricornerstepview

/**
 * Created by anweshmishra on 18/10/18.
 */

import android.app.Activity
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
    val ballR : Float = gap / 8
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class TCSNode(var i : Int, val state : State = State()) {
        private var next : TCSNode? = null
        private var prev : TCSNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = TCSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawTCSNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : TCSNode {
            var curr : TCSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class TriCornerStep(var i : Int) {
        private var root : TCSNode = TCSNode(0)
        private var curr : TCSNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update{i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : TriCornerStepView) {

        private val tcs : TriCornerStep = TriCornerStep(0)

        private var animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            tcs.draw(canvas, paint)
            animator.animate {
                tcs.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            tcs.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : TriCornerStepView {
            val view : TriCornerStepView = TriCornerStepView(activity)
            activity.setContentView(view)
            return view
        }
    }
}