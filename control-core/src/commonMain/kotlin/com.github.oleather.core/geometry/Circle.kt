package com.github.oleather.core.geometry

import com.soywiz.korma.geom.Point
import kotlin.math.abs
import kotlin.math.sqrt

public class Circle public constructor(public val radius: Double, public val center: Point) {

    //https://cp-algorithms.com/geometry/circle-line-intersection.html
    public fun intersection(line: Line): Pair<Point, Point>? {
        val centeredLine = Line(line.getPoint(-1.0) - center, line.getPoint(1.0) - center)

        val a = centeredLine.a
        val b = centeredLine.b
        val c = -centeredLine.c

        val r = radius
        val x = -a * c / (a * a + b * b)
        val y = -b * c / (a * a + b * b)

        if (c * c > r * r * (a * a + b * b) + 2e-9) {
            return null
        }

        val d = r * r - c * c / (a * a + b * b)
        val mult = sqrt(d / (a * a + b * b))
        val x0 = x + b * mult
        val y0 = y - a * mult
        val x1 = x - b * mult
        val y1 = y + a * mult

        return Pair(Point(x0, y0), Point(x1, y1))
    }

    //http://paulbourke.net/geometry/circlesphere/
    public fun intersection(circle: Circle): Pair<Point, Point>? {
        val r0 = radius
        val r1 = circle.radius
        val p0 = center
        val p1 = circle.center
        val d = center.distanceTo(circle.center)
        if (d > r0 + r1 || d < abs(r0 - r1)) {
            return null
        }
        if (d == 0.0 && r0 == r1) {
            return Pair(
                Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
                Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
            )
        }

        val a = (r0 * r0 - r1 * r1 + d * d) / (2 * d)
        val h = sqrt(r0 * r0 - a * a)
        val p2 = p0 + ((p1 - p0) * a) / d

        val b = ((p1 - p0) * h) / d

        val intersect1 = Point(p2.x + b.y, p2.y - b.x)
        val intersect2 = Point(p2.x - b.y, p2.y + b.x)

        println(" $p2 $intersect1 $intersect2")

        return Pair(intersect1, intersect2)
    }
}

