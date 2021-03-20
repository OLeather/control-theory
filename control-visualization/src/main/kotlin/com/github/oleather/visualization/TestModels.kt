package com.github.oleather.visualization

import com.github.oleather.models.Part
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.VectorPath
import com.soywiz.korma.geom.vector.rCubicTo
import hep.dataforge.meta.invoke
import kscience.plotly.Plotly
import kscience.plotly.makeFile
import kscience.plotly.models.Axis
import kscience.plotly.models.Color
import kscience.plotly.models.Margin
import kscience.plotly.models.ScatterMode
import kscience.plotly.scatter
import kotlin.math.PI

public fun main(){
    var path = VectorPath{
        moveTo(0.0, 0.0)
        lineTo(50.0, 0.0)
        lineTo(100.0, 50.0)
        close()
    }
    println(path.getPoints())

    val part = Part(path)

    part.rotateAround(Point(50.0, 0.0), PI/8)

    val xVals = Array(part.getPoints().size){i->part.getPoints().get(i).x}
    val yVals = Array(part.getPoints().size){i->part.getPoints().get(i).y}

    val plot = Plotly.plot {
        scatter {
            x.set(xVals)
            y.set(yVals)
            mode = ScatterMode.lines
            name = "Part1"

        }
        layout {
            title = "Model"
            width = 800
            height = 800
            xaxis = Axis{
                range = -50.0..150.0
                zeroline = false
            }
            yaxis = Axis{
                range = -50.0..150.0
                zeroline = false
            }
        }
    }
    plot.makeFile()
}