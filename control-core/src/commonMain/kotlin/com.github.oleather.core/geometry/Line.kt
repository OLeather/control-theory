package com.github.oleather.core.geometry

import com.soywiz.korma.geom.Point

//Line represented in standard form Ax+By=C
public class Line {
    public val a: Double
    public val b: Double
    public val c: Double

    public constructor(point0: Point, point1: Point) : this(point0.x, point0.y, point1.x, point1.y)
    public constructor(x0: Double, y0: Double, x1: Double, y1: Double) : this(
        y1 - y0,
        x0 - x1,
        (y1 - y0) * x0 + (x0 - x1) * y0
    )

    public constructor(a: Double, b: Double, c: Double) {
        this.a = a
        this.b = b
        this.c = c
    }

    public fun intersection(other: Line): Point? {
        val d = a * other.b - other.a * b
        if (d != 0.0) {
            val x = (other.b * c - b * other.c) / d
            val y = (a * other.c - other.a * c) / d

            return Point(x, y)
        }
        return null
    }

    public fun getPoint(variable: Double, xAxis: Boolean = true): Point {
        if (xAxis && b != 0.0) {
            return Point(variable, (c - a * variable) / b)
        } else {
            return Point((c - b * variable) / a, variable)
        }
    }
}