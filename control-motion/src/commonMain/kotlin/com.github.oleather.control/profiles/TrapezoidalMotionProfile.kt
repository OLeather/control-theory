package com.github.oleather.control.profiles

import com.github.oleather.control.MotionConstraint
import com.github.oleather.control.State

public class TrapezoidalMotionProfile(
    initialState: State,
    finalState: State,
    initialMotionConstraint: MotionConstraint,
    finalMotionConstraint: MotionConstraint
) : MotionProfile() {
    private val am = initialMotionConstraint.maxFirstDerivative
    private val dm = finalMotionConstraint.maxFirstDerivative
    private val vm = initialMotionConstraint.maxFirstDerivative
    private val vi = initialState.firstDerivative
    private val vf = finalState.firstDerivative
    private val pi = initialState.state
    private val pt = finalState.state - initialState.state
    private val ta = (vm - vi) / am
    private val xds = (vm - vf) / -dm
    private val yce = pt + pdb(xds)
    private val tc = (yce + vm * ta - pa(ta)) / vm
    private val h = tc - xds
    private val td = 2 * vf / dm + h

    override fun getStateAtTime(t: Double): State = when {
        t <= ta -> State(pa(t), dpa(t), ddpa())
        t <= tc -> State(pc(t), dpc(), ddpc())
        else -> State(pd(t), dpd(t), ddpd())
    }

    private fun pa(t: Double) = am * (t * t) / 2 + vi * t + pi
    private fun dpa(t: Double) = am * t + vi
    private fun ddpa() = am
    private fun pc(t: Double) = vm * (t - ta) + pa(ta)
    private fun dpc() = vm
    private fun ddpc() = 0.0
    private fun pdb(t: Double) = -dm * (t * t) / 2 + vf * t
    private fun dpdb(t: Double) = -dm * t + vf
    private fun ddpdb() = -dm
    private fun pd(t: Double) = pdb(t - h) + pt
    private fun dpd(t: Double) = dpdb(t - h)
    private fun ddpd() = ddpdb()

    public fun totalTime(): Double = td
}