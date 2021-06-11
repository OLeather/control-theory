package com.github.oleather.models.profiles

import com.github.oleather.models.State
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

public class TrapezoidalMotionProfile(
    initialState: State,
    finalState: State,
    maxStates: State,
    minState: State
) {
    private val pi = initialState.states[0]
    private val pt = finalState.states[0]
    private var am = maxStates.states[1] * (if (pt - pi < 0) {
        -1
    } else {
        1
    })
    private var dm = minState.states[1] * (if (pt - pi < 0) {
        -1
    } else {
        1
    })
    private val vi = initialState.states[1]
    private val vf = -finalState.states[1]
    private var vm =
        min(abs(maxStates.states[0]), abs(sqrt(2 * (pt - pi) * am * dm * (dm + am)) / (dm + am))) * (if (pt - pi < 0) {
            -1
        } else {
            1
        })
    public val accelerationTime: Double = (vm - vi) / am
    private val xds = (vm - vf) / -dm
    private val yce = pt + pdb(xds)
    public val cruiseTime: Double = (yce + vm * accelerationTime - pa(accelerationTime)) / vm
    private val h = cruiseTime - xds
    public val decelerationTime: Double = 2 * vf / dm + h
    public val totalTime: Double = decelerationTime

    public fun getStateAtTime(t: Double): State = when {
        t <= accelerationTime -> State(pa(t), dpa(t), ddpa())
        t <= cruiseTime -> State(pc(t), dpc(), ddpc())
        else -> State(pd(t), dpd(t), ddpd())
    }

    private fun pa(t: Double) = am * (t * t) / 2 + vi * t + pi
    private fun dpa(t: Double) = am * t + vi
    private fun ddpa() = am
    private fun pc(t: Double) = vm * (t - accelerationTime) + pa(accelerationTime)
    private fun dpc() = vm
    private fun ddpc() = 0.0
    private fun pdb(t: Double) = -dm * (t * t) / 2 + vf * t
    private fun dpdb(t: Double) = -dm * t + vf
    private fun ddpdb() = -dm
    private fun pd(t: Double) = pdb(t - h) + pt
    private fun dpd(t: Double) = dpdb(t - h)
    private fun ddpd() = ddpdb()
}