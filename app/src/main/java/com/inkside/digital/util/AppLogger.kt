package com.inkside.digital.util

import android.util.Log
import com.inkside.digital.BuildConfig

/**
 * AppLogger — wrapper Log yang hanya aktif di debug build.
 * Di release, log di-skip (no-op) untuk:
 * - Mengurangi overhead I/O
 * - Mencegah info leakage via logcat
 * - Mengurangi APK size (string log)
 */
object AppLogger {
    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.d(tag, msg)
    }

    fun e(tag: String, msg: String, tr: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.e(tag, msg, tr)
    }

    fun w(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.w(tag, msg)
    }

    fun i(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.i(tag, msg)
    }
}
