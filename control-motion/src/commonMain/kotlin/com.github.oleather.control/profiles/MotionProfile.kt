package com.github.oleather.control.profiles

import com.github.oleather.control.State

public abstract class MotionProfile {
    public abstract fun getStateAtTime(t: Double): State?
}