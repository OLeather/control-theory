package com.github.oleather

import com.github.oleather.profiles.TrapezoidalMotionProfile
import hep.dataforge.meta.invoke
import kscience.plotly.Plotly
import kscience.plotly.makeFile
import kscience.plotly.models.Dash
import kscience.plotly.models.ScatterMode
import kscience.plotly.models.Shape
import kscience.plotly.models.TextPosition
import kscience.plotly.scatter

fun main() {
    val profile = TrapezoidalMotionProfile(
        State(0.0, 0.0, 0.0),
        State(10.0, 0.0, 0.0),
        MotionConstraint(0.0, 2.0, 1.0),
        MotionConstraint(0.0, 2.0, 1.0)
    )

    val dt = 0.01;
    val steps: Int = (profile.totalTime() / dt).toInt()
    println(profile.totalTime())
    val t = Array(steps){ i ->(i*dt)}
    val p = Array(steps){ i ->(profile.getStateAtTime(i*dt).state)}
    val v = Array(steps){ i ->(profile.getStateAtTime(i*dt).firstDerivative)}
    val a = Array(steps){ i ->(profile.getStateAtTime(i*dt).secondDerivative)}

    t.forEach { println(it) }

    val plot = Plotly.plot {
        scatter {
            x.set(t)
            y.set(p)
            mode = ScatterMode.markers
            name = "Position"
            textposition = TextPosition.`top center`
            textfont {
                family = "Raleway, sans-serif"
            }
            line {
                width = 1
                dash = Dash.solid
            }
        }
        scatter {
            x.set(t)
            y.set(v)
            mode = ScatterMode.markers
            name = "Velocity"
            textposition = TextPosition.`top center`
            textfont {
                family = "Raleway, sans-serif"
            }
            line {
                width = 1
                dash = Dash.solid
            }
        }
        scatter {
            x.set(t)
            y.set(a)
            mode = ScatterMode.markers
            name = "Acceleration"
            textposition = TextPosition.`top center`
            textfont {
                family = "Raleway, sans-serif"
            }
            line {
                width = 1
                dash = Dash.solid

            }
        }

        layout {
            title = "Trapezoidal Motion Profile"
            legend {
                y = 0.5
                font {
                    family = "Arial, sans-serif"
                    size = 20
                    color("grey")
                }
            }
        }
    }

    plot.makeFile()
}