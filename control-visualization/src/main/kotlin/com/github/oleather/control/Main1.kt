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
    val profile4 = NMotionProfile(4, arrayOf(0.0, 0.0, 0.0, 0.0), arrayOf(0.0, 0.0, 0.0, 400.0), arrayOf(100.0, 50.0, 50.0, 100.0), arrayOf(100.0, 50.0, 50.0, 100.0))
    val profile5 = NMotionProfile(5, arrayOf(2.0, -5.0, 10.0, 0.0, 0.0), arrayOf(0.0, -5.0, 10.0, 0.0, 8000.0), arrayOf(100.0, 50.0, 50.0, 100.0, 500.0), arrayOf(100.0, 50.0, 50.0, 100.0, 500.0))
    val profile = profile5

    val module = profile.profileModule
    val dt = 0.01
    val steps = (profile.totalTime() / dt).toInt()
    val t = Array(steps) { i -> i * dt }
    val p = Array(steps) { i -> module.getValue(i*dt)}
    var v = Array(steps) { i -> module.getDerivative(i*dt, n=1) }
    var p1 = Array(steps) { i -> module.getDerivative(i*dt, n=2) }
    var v1 = Array(steps) { i -> module.getDerivative(i*dt, n=3) }
    var p2 = Array(steps) { i -> module.getDerivative(i*dt, n=4) }
    var v2 = Array(steps) { i -> module.getDerivative(i*dt, n=5) }

    var maxVal = 0.0
    for(n in v){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }
    v = Array(v2.size){i -> v[i]?.times((6000/maxVal)) }
    maxVal = 0.0
    for(n in p1){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }
    p1 = Array(v2.size){i -> p1[i]?.times((6000/maxVal)) }
    maxVal = 0.0
    for(n in v1){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }
    v1 = Array(v2.size){i -> v1[i]?.times((6000/maxVal)) }
    maxVal = 0.0
    for(n in p2){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }

    p2 = Array(v2.size){i -> p2[i]?.times((6000/maxVal)) }
    maxVal = 0.0
    for(n in v2){
        if (n != null) {
            if(n > maxVal){
                maxVal = n
            }
        }
    }

    v2 = Array(v2.size){i -> v2[i]?.times((4000/maxVal)) }

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

        layout {
            title = "5th Degree Motion Profile"
        }
    }
    plot.makeFile()
}