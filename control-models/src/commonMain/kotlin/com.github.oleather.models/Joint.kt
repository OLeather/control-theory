package com.github.oleather.models

import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.VectorPath

public open class Joint(
    public val type: JointType,
    public val part1: Part,
    public val part1Point: Point,
    public val part2: Part,
    public val part2Point: Point,
    public var grounded: Boolean = false
) {
    public var locked: Boolean = false

}