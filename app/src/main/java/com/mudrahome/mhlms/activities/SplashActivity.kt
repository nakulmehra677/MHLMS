package com.mudrahome.mhlms.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.managers.ProfileManager


class SplashActivity : AppCompatActivity() {

    private val time = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val manager = ProfileManager()

        Handler().postDelayed({
            if (manager.checkUserExist()) {
                startActivity(Intent(this, LeadListActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, time.toLong())
    }
}
