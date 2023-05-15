package com.barnavailability

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// Change version numbers in build.gradle
// Build signed apk then copy from C:\Users\simon\AndroidStudioProjects\Barn Availability\app\release

/**
 * MainActivity sets the content view activity_main, a fragment container that contains
 * overviewFragment.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
