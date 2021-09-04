/*
 * Created by Lukas (LukaLike) on Sat, Sep 4, '21.
 * Copyright (c) 2021. All rights reserved.
 */

package com.example.exoplayer.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferences @Inject constructor(@ApplicationContext val context: Context) {

    companion object {
        private const val PREFERENCE_NAME = "MY_APP_PREF"
        private const val PREF_AUTO_PLAY = "PREF_AUTO_PLAY"
        private const val PREF_WINDOW = "PREF_WINDOW"
        private const val PREF_POSITION = "PREF_POSITION"
    }

    private val pref: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun setAutoPlay(autoPlay: Boolean) {
        editor.putBoolean(PREF_AUTO_PLAY, autoPlay)
        editor.apply()
    }

    fun getAutoPlay(): Boolean = pref.getBoolean(PREF_AUTO_PLAY, false)

    fun setWindow(window: Int) {
        editor.putInt(PREF_WINDOW, window)
        editor.apply()
    }

    fun getWindow(): Int = pref.getInt(PREF_WINDOW, 0)

    fun setPosition(position: Long) {
        editor.putLong(PREF_POSITION, position)
        editor.apply()
    }

    fun getPosition(): Long = pref.getLong(PREF_POSITION, 0)

}