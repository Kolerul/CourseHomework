package com.example.chernykhhomework.ui.util

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.animation.doOnEnd
import com.example.chernykhhomework.R


fun View.animationAppearanceFromZeroAlpha(duration: Long, endAction: () -> Unit) {
    this.alpha = 0f

    val animator = ObjectAnimator.ofFloat(
        this, View.ALPHA, 1f
    ).apply {
        this.duration = duration
        doOnEnd {
            endAction.invoke()
        }
    }
    animator.start()
}

fun View.animationBlinking() {
    val animation = AnimationUtils.loadAnimation(rootView.context, R.anim.blinking_animation)
    this.startAnimation(animation)
}