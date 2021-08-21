package uz.instat.jokerup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import java.util.*

class AnimatedView(context: Context, attrs: AttributeSet?) : AppCompatImageView(
    context, attrs
) {
    private val myHandler: Handler
    private val FRAME_RATE = 1
    private var cardX: Float = 0f
    private var cardY: Float = 0f
    private var cardHeight: Float = 0f
    private var cardWidth: Float = 0f
    private val random: Random
    private var cardPixelSpeed: Float = 3f
    private var index: Int = 0
    private val runnable: Runnable

    private var cardList: List<Bitmap> = listOf()
    private var eventListener: IEventListener? = null

    init {
        random = Random()
        myHandler = Handler(Looper.getMainLooper())
        runnable = Runnable { this.invalidate() }
    }

    fun setCardList(cardList: List<Bitmap>) {
        this.cardList = cardList
        index = random.nextInt(cardList.size)
        cardHeight = cardList[index].height.toFloat()
        cardWidth = cardList[index].width.toFloat()
        cardY = -cardHeight
        cardX = random.nextInt(600).toFloat()
        cardPixelSpeed = 3f
        myHandler.postDelayed(runnable, FRAME_RATE.toLong())
    }

    fun setEventListener(eventListener: IEventListener) {
        this.eventListener = eventListener
    }

    override fun onDraw(c: Canvas) {
        if (cardList.isNotEmpty()) {
            val bitmap = cardList[index]
            c.drawBitmap(bitmap, cardX, cardY, null)
            if (cardY - cardHeight > this.height) {
                if (index != 1) {
                    eventListener?.decrementLife()
                } else {
                    //
                }
                cardY = -cardHeight
                cardX = random.nextInt((this.width - cardWidth).toInt()).toFloat()
                if (cardList.isNotEmpty())
                    index = random.nextInt(cardList.size)
            } else {
                cardY += cardPixelSpeed
            }

            myHandler.postDelayed(runnable, FRAME_RATE.toLong())
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            val withinX = x > cardX && x < cardX + cardWidth
            val withinY = y > cardY && y < cardY + cardHeight
            if (withinX && withinY) {
                if (index != 1) {
                    eventListener?.incrementRating()
                    cardPixelSpeed += 0.2f
                } else {
                    eventListener?.decrementLife()
                    cardPixelSpeed -= 0.1f
                }
                cardY = -cardHeight
                cardX = random.nextInt((this.width - cardWidth).toInt()).toFloat()
                if (cardList.isNotEmpty())
                    index = random.nextInt(cardList.size)
            }
        }
        return true
    }


    fun reset() {
        cardList = fullCardList
        cardY = -cardHeight
        cardX = random.nextInt((this.width - cardWidth).toInt()).toFloat()
        index = random.nextInt(cardList.size)
        cardPixelSpeed = 3f
        myHandler.postDelayed(runnable, FRAME_RATE.toLong())
    }

    private var fullCardList: List<Bitmap> = emptyList()

    fun finish() {
        fullCardList = cardList
        cardList = emptyList()
        myHandler.postDelayed(runnable, FRAME_RATE.toLong())
    }

}