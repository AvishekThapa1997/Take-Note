package com.app.takenote.ui


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import com.app.takenote.R
import com.app.takenote.utility.CustomTypeWriterListener
import kotlinx.android.synthetic.main.activity_splash.*
import kotlin.math.max

class SplashActivity : BaseActivity(), View.OnLayoutChangeListener {
    override val layoutResourceId: Int = R.layout.activity_splash
    private lateinit var anim: Animator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView.addOnLayoutChangeListener(this)
        animateText()
        textAnimation.setTypeWriterListener(object : CustomTypeWriterListener {
            override fun onTypingEnd(text: String?) {
                //textAnimation.visibility = View.GONE
                //startAnimation()
                startIntentFor(LoginActivity::class.java)
            }

            override fun onCharacterTyped(text: String?, position: Int) {
//                if(position == 10){
//                    textAnimation.hideView(View.GONE)
//                    startAnimation()
//                }
            }
        })
    }

    override fun onLayoutChange(
        v: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
//        rootView.post {
//                image.visibility = View.VISIBLE
//            lifecycleScope.launch(Dispatchers.Main) {
//                delay(1000)
//                startAnimation()
//            }
//        }
//        rootView.removeOnLayoutChangeListener(this)
    }

    private fun startAnimation() {
        val cx = revealView.width / 2
        val cy = revealView.height / 2
        val radius = max(cx.toDouble(), cy.toDouble())
        //val finalRadius = max(image.height / 2.toFloat(), image.width / 2.toFloat())
        anim = ViewAnimationUtils.createCircularReveal(
            revealView,
            cx,
            cy,
            0f,
            radius.toFloat()
        )
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                revealView.hideView(View.INVISIBLE)
                startIntentFor(LoginActivity::class.java)
            }

            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                textAnimation.hideView(View.GONE)
            }
        })
        revealView.showView()
        anim.start()
    }

    private fun animateText() {
        textAnimation.setDelay(2500)
        textAnimation.setWithMusic(false)
        textAnimation.animateText("TAKE NOTE ")
    }
}