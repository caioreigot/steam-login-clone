package br.com.github.caioreigot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        changeToLogin()
    }

    fun changeToLogin() {
        val intent = Intent(this, LoginActivity::class.java)

        Handler(Looper.getMainLooper()).postDelayed({
            intent.change(intent)
        }, 2000)
    }

    fun Intent.change(passedIntent: Intent) {
        startActivity(passedIntent)
        finish()
    }
}