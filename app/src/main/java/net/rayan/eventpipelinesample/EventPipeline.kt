package net.rayan.eventpipelinesample

import android.os.Handler
import android.os.HandlerThread
import android.util.Log

class EventPipeline private constructor(name: String) : Pipeline {

    companion object {
        fun create(name: String): EventPipeline {
            return EventPipeline(name)
        }
    }

    private val TAG = javaClass.simpleName

    private val lock = Object()
    private var start = false
    private var handlerThread: HandlerThread = HandlerThread(name).apply {
        start()
        start = true
    }
    private var handler = Handler(handlerThread.looper)

    override fun queueEvent(event: Runnable, front: Boolean) {
        if (!start) {
            Log.e(TAG, "EventPipeline has quited")
            return
        }
        if (front) {
            handler.postAtFrontOfQueue(event)
        } else {
            handler.post(event)
        }
    }

    override fun queueEvent(event: Runnable, delayed: Long) {
        if (!start) {
            Log.e(TAG, "EventPipeline has quited")
            return
        }
        handler.postDelayed(event, delayed)
    }

    override fun quit() {
        if (!start) {
            Log.e(TAG, "EventPipeline has quited")
            return
        }
        start = false
        handlerThread.interrupt()
        handlerThread.quitSafely()
    }

    override fun started(): Boolean {
        return start
    }

    override fun getName(): String {
        return handlerThread.name
    }

    override fun getHandler(): Handler {
        return handler
    }

    override fun sleep() {
        queueEvent(Runnable {
            synchronized(lock) {
                try {
                    lock.wait()
                } catch (e: InterruptedException) {
                    Log.i(TAG, e.toString())
                }
            }
        })
    }

    override fun wake() {
        synchronized(lock) {
            lock.notifyAll()
        }
    }
}