package com.github.oleather.profiles

import com.github.oleather.MotionConstraint
import com.github.oleather.State

class TrapezoidalMotionProfile(
    var initialState: State,
    var finalState: State,
    var initialMotionConstraint: MotionConstraint,
    var finalMotionConstraint: MotionConstraint
) : MotionProfile() {
    private var am: Double = 0.0
    private var dm: Double = 0.0
    private var vm: Double = 0.0
    private var vi: Double = 0.0
    private var vf: Double = 0.0
    private var pi: Double = 0.0
    private var pt: Double = 0.0
    private var ta: Double = 0.0
    private var tc: Double = 0.0
    private var td: Double = 0.0
    private var xds: Double = 0.0
    private var h: Double = 0.0

    init{
        am = initialMotionConstraint.maxFirstDerivative
        dm = finalMotionConstraint.maxFirstDerivative
        vm = initialMotionConstraint.maxFirstDerivative
        vi = initialState.firstDerivative
        vf = finalState.firstDerivative
        pi = initialState.state
        pt = finalState.state - initialState.state
        ta = (vm - vi) / am
        xds = (vm - vf) / -dm
        val yce = pt + pdb(xds)
        tc = (yce + vm * ta - pa(ta)) / vm
        val tld = 2 * vf / dm
        h = tc - xds
        td = tld + h
    }

    override fun getStateAtTime(t: Double): State {
        val pa = pa(t)
        val pc = pc(t)
        val pd = pd(t)

        val dpa = dpa(t)
        val dpc = dpc(t)
        val dpd = dpd(t)

        val ddpa = ddpa(t)
        val ddpc = ddpc(t)
        val ddpd = ddpd(t)

        return if (t <= ta) State(pa, dpa, ddpa) else if (t <= tc) State(pc, dpc, ddpc) else State(pd, dpd, ddpd)
    }

    private fun pa(t: Double): Double {
        return am * (t * t) / 2 + vi * t + pi
    }

    private fun dpa(t: Double): Double {
        return am * t + vi;
    }

    private fun ddpa(t: Double): Double {
        return am;
    }

    private fun pc(t: Double): Double {
        return vm * (t - ta) + pa(ta)
    }

    private fun dpc(t: Double): Double {
        return vm
    }

    private fun ddpc(t: Double): Double {
        return 0.0
    }

    private fun pdb(t: Double): Double {
        return -dm * (t * t) / 2 + vf * t
    }

    private fun dpdb(t: Double): Double {
        return -dm * t + vf
    }

    private fun ddpdb(t: Double): Double {
        return -dm
    }

    private fun pd(t: Double): Double {
        return pdb(t - h) + pt
    }

    private fun dpd(t: Double): Double {
        return dpdb(t - h)
    }

    private fun ddpd(t: Double): Double {
        return ddpdb(t)
    }

    fun totalTime() : Double{
        return td
    }
}