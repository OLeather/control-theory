package com.github.oleather

import com.github.oleather.profiles.TrapezoidalMotionProfile
import hep.dataforge.meta.invoke
import kscience.plotly.Plotly
import kscience.plotly.makeFile
import kscience.plotly.models.ScatterMode
import kscience.plotly.scatter

fun main() {
    val profile = TrapezoidalMotionProfile(
        State(0.0, 0.0, 0.0),
        State(10.0, 0.0, 0.0),
        MotionConstraint(0.0, 2.0, 1.0),
        MotionConstraint(0.0, 2.0, 1.0)
    )

    val dt = 0.01;
    val steps = (profile.totalTime() / dt).toInt()
    val t = Array(steps) { i -> i * dt }
    val p = Array(steps) { i -> profile.getStateAtTime(i * dt).state }
    val v = Array(steps) { i -> profile.getStateAtTime(i * dt).firstDerivative }
    val a = Array(steps) { i -> profile.getStateAtTime(i * dt).secondDerivative }
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
            y.set(a)
            mode = ScatterMode.lines
        }

        layout {
            title = "Trapezoidal Motion Profile"
        }
    }
    plot.makeFile()
}