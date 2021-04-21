package com.github.oleather.visualization

import com.github.oleather.core.geometry.Circle
import com.github.oleather.core.geometry.Line
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.shape.toPathList
import hep.dataforge.meta.invoke
import kscience.plotly.Plotly
import kscience.plotly.makeFile
import kscience.plotly.models.Axis
import kscience.plotly.models.Marker
import kscience.plotly.models.ScatterMode
import kscience.plotly.scatter

public fun main(){
    val circle = Circle(5.0, Point(0.0, 0.0))
    val circle1 = Circle(5.0, Point(9.5, 0.0))
    val line = Line(Point(-4.0, -5.0), Point(5.0, 5.0))
    val line1 = Line(Point(-4.0, 5.0), Point(5.0, -6.0))
    val points = circle.intersection(circle1)!!
    val point1 = points.first
    val point2 = points.second

    val intersectionXs = arrayOf(point1.x, point2.x, line.intersection(line1)!!.x)
    val intersectionYs = arrayOf(point1.y, point2.y, line.intersection(line1)!!.y)

    var lineXs = arrayOf(line.getPoint(-4.0).x, line.getPoint(5.0).x)
    var lineYs = arrayOf(line.getPoint(-4.0).y, line.getPoint(5.0).y)

    val plot = Plotly.plot {
        scatter {
            x.set(intersectionXs)
            y.set(intersectionYs)
            mode = ScatterMode.markers
            marker = Marker{
                size = 5
            }
            name = "Part1"
        }
        scatter {
            x.set(lineXs)
            y.set(lineYs)
            mode = ScatterMode.lines
            name = "Part1"
        }
        scatter {
            x.set(lineXs)
            y.set(lineYs)
            mode = ScatterMode.lines
            name = "Part1"
        }
        layout {
            title = "Model"
            width = 800
            height = 800
            xaxis = Axis {
                range = -10.0..10.0
                zeroline = false
            }
            yaxis = Axis {
                range = -10.0..10.0
                zeroline = false
            }
        }
    }

    plot.makeFile()
}
