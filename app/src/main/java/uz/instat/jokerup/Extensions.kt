package uz.instat.jokerup

import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.getDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(requireContext(), id)
