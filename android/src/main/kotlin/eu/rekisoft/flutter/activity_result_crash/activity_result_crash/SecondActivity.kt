package eu.rekisoft.flutter.activity_result_crash.activity_result_crash

import android.app.Activity
import android.os.Bundle

class SecondActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Second activity created and finishing now")
        finish()
    }
}