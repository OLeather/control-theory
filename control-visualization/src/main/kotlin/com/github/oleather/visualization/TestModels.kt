package com.github.oleather.visualization

import com.github.oleather.models.Part
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.bezier.Bezier
import com.soywiz.korma.geom.shape.Shape2d
import com.soywiz.korma.geom.shape.ops.intersection
import com.soywiz.korma.geom.shape.toPathList
import com.soywiz.korma.geom.shape.toPolygon
import com.soywiz.korma.geom.shape.toShape2d
import com.soywiz.korma.geom.vector.VectorPath
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import space.kscience.plotly.*
import space.kscience.plotly.models.Axis
import space.kscience.plotly.models.Marker
import space.kscience.plotly.models.ScatterMode
import space.kscience.plotly.models.ShapeType

private val points: ArrayList<Point> = arrayListOf()

public fun main() {
    var path = VectorPath {
        circle(0.0, 0.0, 10.0)
    }
    println(path.getPoints())

    val shape = path.toShape2d(true)
    graphVector(path)

    val part = Part(path)

    val path1 = VectorPath{
        circle(5.0, 5.0, 10.0)
    }

    val intersect = shape.intersection(path1.toShape2d(true))
    println(shape.paths)

//    part.rotateAround(Point(50.0, 0.0), PI/8)

    val xVals = Array(path.toPathList().get(0).size) { i -> path.toPathList().get(0).getX(i) }
    val yVals = Array(path.toPathList().get(0).size) { i -> path.toPathList().get(0).getY(i) }

    val plot = Plotly.plot {
        scatter {
            x.set(xVals)
            y.set(yVals)
            mode = ScatterMode.markers
            marker = Marker{
                size = 0.8
            }
            name = "Part1"
        }
        layout {
            title = "Model"
            width = 800
            height = 800
            xaxis = Axis {
                range = -50.0..150.0
                zeroline = false
            }
            yaxis = Axis {
                range = -50.0..150.0
                zeroline = false
            }
        }
    }

    plot.makeFile()
}

private var lx = 0.0
private var ly = 0.0

public fun graphVector(vectorPath: VectorPath) {
    vectorPath.visitCmds(
        { x, y -> println("moveTo($x,$y)"); lx = x; ly = y },
        { x, y -> println("lineTo($x,$y)"); lx = x; ly = y },
        { x1, y1, x2, y2 -> println("quadTo($x1,$y1, $x2, $y2)"); lx = x2; ly = y2 },
        { x1, y1, x2, y2, x3, y3 -> graphCubic(x1, y1, x2, y2, x3, y3); lx = x3; ly = y3},
        { println("close()") })
}

public fun graphLine(){

}

public fun graphCubic(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double){
    for(t in 0 until 100){
        val out: Point = Point()
        Bezier.cubicCalc(lx, ly, x1,y1, x2, y2, x3, y3, t/100.0, out)
        points.add(out)
    }
}
