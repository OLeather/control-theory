package com.github.oleather.visualization

import com.github.oleather.models.profiles.NMotionProfile
import space.kscience.plotly.Plotly
import space.kscience.plotly.layout
import space.kscience.plotly.makeFile
import space.kscience.plotly.models.ScatterMode
import space.kscience.plotly.scatter

public fun main(){
//    val profile1 = NMotionProfile(1, arrayOf(0.0), arrayOf(50.0), arrayOf(10.0), arrayOf(50.0))
//    val profile2 = NMotionProfile(2, arrayOf(0.0, 0.0), arrayOf(0.0, 50.0), arrayOf(10.0, 60.0), arrayOf(10.0, 50.0))
//    val profile3 = NMotionProfile(3, arrayOf(0.0, 0.0, 0.0), arrayOf(0.0, 0.0, 10.0), arrayOf(20.0, 15.0, 20.0), arrayOf(20.0, 20.0, 50.0))
    val profile4 = NMotionProfile(4, arrayOf(0.0, 0.0, 0.0, 0.0), arrayOf(0.0, 0.0, 0.0, 50.0), arrayOf(100.0, 50.0, 50.0, 100.0), arrayOf(100.0, 50.0, 50.0, 100.0))
//    val profile5 = NMotionProfile(5, arrayOf(0.0, 0.0, 0.0, 0.0, 0.0), arrayOf(0.0, 0.0, 0.0, 0.0, 50.0), arrayOf(100.0, 50.0, 50.0, 100.0, 500.0), arrayOf(100.0, 50.0, 50.0, 100.0, 500.0))
//    val profile6 = NMotionProfile(6, arrayOf(2.0, -5.0, 10.0, 0.0, 0.0, 0.0), arrayOf(0.0, -5.0, 10.0, 0.0, 0.0, 2.0e5), arrayOf(100.0, 50.0, 50.0, 100.0, 500.0, 8000.0), arrayOf(100.0, 50.0, 50.0, 100.0, 500.0, 8000.0))
//    val profile7 = NMotionProfile(6, arrayOf(2.0, -5.0, 10.0, 0.0, 0.0, 0.0), arrayOf(0.0, -5.0, 10.0, 0.0, 0.0, 0.0, 2.0e100), arrayOf(100.0, 50.0, 50.0, 100.0, 500.0, 8000.0,  2.0e5), arrayOf(100.0, 50.0, 50.0, 100.0, 500.0, 8000.0,  2.0e5))
    val profile = profile4

    val module = profile.profileModule
    val dt = 0.01
    val steps = (profile.totalTime() / dt).toInt()
    val t = Array(steps) { i -> i * dt }
    val p = Array(steps) { i -> module.getValue(i*dt)}
    var v = Array(steps) { i -> module.getDerivative(i*dt, n=1) }
    var p1 = Array(steps) { i -> module.getDerivative(i*dt, n=2) }
    var v1 = Array(steps) { i -> module.getDerivative(i*dt, n=3) }
    var p2 = Array(steps) { i -> 0.0 }
    var v2 = Array(steps) { i -> 0.0 }
    var v3 = Array(steps) { i -> 0.0 }
    var maxVal = 0.0
    for(n in v){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }
    v = Array(v2.size){i -> v[i]?.times((maxVal/maxVal)) }
    maxVal = 0.0
    for(n in p1){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }
    p1 = Array(v2.size){i -> p1[i]?.times((maxVal/maxVal)) }
    maxVal = 0.0
    for(n in v1){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }
    v1 = Array(v2.size){i -> v1[i]?.times((maxVal/maxVal)) }
    maxVal = 0.0
    for(n in p2){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }

    p2 = Array(v2.size){i -> p2[i]?.times((maxVal/maxVal)) }
    maxVal = 0.0
    for(n in v2){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }
    v2 = Array(v2.size){i -> v2[i]?.times((maxVal/maxVal)) }


    maxVal = 0.0
    for(n in v3){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }
    v3 = Array(v2.size){i -> v3[i]?.times((maxVal/maxVal)) }

    val plot = Plotly.plot {
        scatter {
            x.set(t)
            y.set(p)
            mode = ScatterMode.lines
            name = "Position"
        }

        scatter {
            x.set(t)
            y.set(v)
            mode = ScatterMode.lines
            name = "Velocity"
        }

        scatter {
            x.set(t)
            y.set(p1)
            mode = ScatterMode.lines
            name = "Acceleration"
        }

        scatter {
            x.set(t)
            y.set(v1)
            mode = ScatterMode.lines
            name = "Jerk"
        }

        scatter {
            x.set(t)
            y.set(p2)
            mode = ScatterMode.lines
            name = "Snap"
        }

        scatter {
            x.set(t)
            y.set(v2)
            mode = ScatterMode.lines
            name = "Crackle"
        }


        scatter {
            x.set(t)
            y.set(v3)
            mode = ScatterMode.lines
            name = "Pop"
        }

        layout {
            title = "6th Degree Motion Profile"
        }
    }
    plot.makeFile()
}