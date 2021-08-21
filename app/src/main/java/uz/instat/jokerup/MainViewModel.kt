package uz.instat.jokerup

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable != null) {
            drawable.setBounds(0, 0, 50, 50)
            val bitmap: Bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
        return null
    }

}