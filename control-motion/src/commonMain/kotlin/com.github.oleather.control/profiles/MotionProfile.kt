package com.github.oleather.control.profiles

import com.github.oleather.com.github.oleather.control.State

abstract class MotionProfile {
    abstract fun getStateAtTime(t: Double): State

}