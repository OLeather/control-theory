package com.github.oleather.models.profiles

import com.github.oleather.models.State

public abstract class MotionProfile {
    public abstract fun getStateAtTime(t: Double): State?
}