package net.rayan.eventpipelinesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private val eventPipeline = EventPipeline.create("Background Pipeline")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eventPipeline.queueEvent(Runnable {
            Log.d(TAG, "test 0")
        })

        var i = 1
        val start = System.currentTimeMillis()
        while (true) {
            if (i > 10)
                break
            eventPipeline.queueEvent(Runnable {
                Log.d(TAG, "test $i")
                ++i
            })
            if (i == 5) {
                eventPipeline.sleep()
            }
            if (System.currentTimeMillis() - start > 1000)
                eventPipeline.wake()
            sleep(10)
        }
    }
}
