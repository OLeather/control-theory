package com.github.oleather.models

import com.soywiz.korma.geom.Point

public class SliderJoint(
    part1: Part,
    part1Point: Point,
    part2: Part,
    part2Point: Point,
    grounded: Boolean = false
) : Joint(JointType.Linear, part1, part1Point, part2, part2Point, grounded) {
}