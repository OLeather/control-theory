package com.github.oleather.control

import com.github.oleather.control.profiles.NMotionProfile
import com.github.oleather.control.profiles.TrapezoidalMotionProfile
import com.github.oleather.control.profiles.calcIntegral
import hep.dataforge.meta.invoke
import kotlinx.coroutines.flow.combine
import kscience.kmath.functions.Polynomial
import kscience.kmath.functions.asFunction
import kscience.kmath.functions.asMathFunction
import kscience.kmath.functions.value
import kscience.kmath.operations.RealField
import kscience.kmath.operations.Ring
import kscience.plotly.Plotly
import kscience.plotly.makeFile
import kscience.plotly.models.ScatterMode
import kscience.plotly.scatter

fun main(){
    val profile1 = NMotionProfile(1, arrayOf(0.0), arrayOf(50.0), arrayOf(10.0), arrayOf(50.0))
    val profile2 = NMotionProfile(2, arrayOf(0.0, 0.0), arrayOf(0.0, 100.0), arrayOf(50.0, 50.0), arrayOf(50.0, 50.0))
    val profile3 = NMotionProfile(3, arrayOf(0.0, -10.0, -5.0), arrayOf(0.0, -10.0, 150.0), arrayOf(50.0, 50.0, 50.0), arrayOf(50.0, 50.0, 50.0))
    val profile4 = NMotionProfile(4, arrayOf(0.0, 0.0, 0.0, 0.0), arrayOf(0.0, 0.0, 0.0, 500.0), arrayOf(50.0, 50.0, 50.0, 100.0), arrayOf(50.0, 50.0, 50.0, 100.0))
    val profile = profile3

    val dt = 0.01;
    val steps = (profile.totalTime() / dt).toInt()
    val t = Array(steps) { i -> i * dt }
    val p = Array(steps) { i -> profile.accModule.getValue(i * dt) }
    val v = Array(steps) { i -> profile.accModule.getDerivative(i * dt) }
    val p1 = Array(steps) { i -> profile.cruiseModule.getValue(i * dt) }
    val v1 = Array(steps) { i -> profile.cruiseModule.getDerivative(i * dt) }
    val p2 = Array(steps) { i -> profile.decModule.getValue(i * dt) }
    val v2 = Array(steps) { i -> profile.decModule.getDerivative(i * dt) }

    val plot = Plotly.plot {
        scatter {
            x.set(t)
            y.set(p)
            mode = ScatterMode.lines
        }

        scatter {
            x.set(t)
            y.set(v)
            mode = ScatterMode.lines
        }

        scatter {
            x.set(t)
            y.set(p1)
            mode = ScatterMode.lines
        }

        scatter {
            x.set(t)
            y.set(v1)
            mode = ScatterMode.lines
        }

        scatter {
            x.set(t)
            y.set(p2)
            mode = ScatterMode.lines
        }

        scatter {
            x.set(t)
            y.set(v2)
            mode = ScatterMode.lines
        }

        layout {
            title = "S-Curve Motion Profile"
        }
    }
    plot.makeFile()
}