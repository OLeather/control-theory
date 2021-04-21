package com.github.oleather.models

import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.VectorPath
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

public class Part(vectorPath: VectorPath, public val joints: ArrayList<Joint> = arrayListOf()) {

    private val transformedVectorPath: VectorPath = vectorPath.clone()

    public fun rotateAround(origin: Point, rads: Double){
        val tempVectorPath = transformedVectorPath.clone()
        transformedVectorPath.clear()
        for(point in tempVectorPath.getPoints()){
            val deltaPoint = Point(point.x-origin.x, point.y-origin.y)
            val atan = atan2(deltaPoint.y, deltaPoint.x)
            val angle =  if (atan < 0) atan + PI*2 else atan + rads
            val rotatedPoint = Point(cos(angle)*deltaPoint.length, sin(angle)*deltaPoint.length)
            transformedVectorPath.moveTo(rotatedPoint.x + origin.x, rotatedPoint.y + origin.y)
        }
        transformedVectorPath.close()
    }

    public fun movePointTo(localPoint: Point, newPoint: Point){
        val deltaPoint = newPoint-localPoint
        moveBy(deltaPoint)
    }

    public fun moveBy(deltaPoint: Point){
        val tempVectorPath = transformedVectorPath.clone()
        transformedVectorPath.clear()
        for(point in tempVectorPath.getPoints()){
            transformedVectorPath.moveTo(point.x + deltaPoint.x, point.y + deltaPoint.y)
        }
        transformedVectorPath.close()
    }

    public fun addJoint(joint: Joint){
        joints.add(joint)
    }

    public fun getPoints(): List<IPoint> {
        return transformedVectorPath.getPoints()
    }
}