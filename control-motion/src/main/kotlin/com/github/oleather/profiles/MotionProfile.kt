package com.github.oleather.profiles

import com.github.oleather.State

abstract class MotionProfile{
    abstract fun getStateAtTime(t : Double) : State
}