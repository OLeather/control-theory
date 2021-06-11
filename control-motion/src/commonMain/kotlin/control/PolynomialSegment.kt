package com.github.oleather.models

import com.github.oleather.models.profiles.calcDerivative
import com.github.oleather.models.profiles.calcIntegral
import com.github.oleather.models.profiles.solveForX
import space.kscience.kmath.functions.value
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.functions.Polynomial

public class PolynomialSegment(
    public var polynomial: Polynomial<Double>,
    public var startX: Double,
    public var endX: Double
) {

    public var xShift: Double = 0.0

    public fun getValue(x: Double): Double = polynomial.value(RealField, x - xShift)

    public fun solveForX(y: Double): Double = solveForX(this, y)

    public fun getDerivative(x: Double, n: Int = 1): Double{
        var derivative = Polynomial(polynomial.coefficients)
        repeat(n){
            derivative = calcDerivative(derivative)
        }
        return derivative.value(RealField, x - xShift)
    }


    public fun getIntegral(x: Double): Double =
        calcIntegral(polynomial).value(RealField, x - xShift)

    public fun shiftX(x: Double, moveEndpoints: Boolean = false, compound: Boolean = false) {
        xShift = if(compound) x+xShift else x
        if(moveEndpoints){
            if(compound){
                val startEndDist = endX-startX
                startX = xShift
                endX = startX + startEndDist
            }
            else{
                startX += xShift
                endX += xShift
            }
        }
    }

    public fun shiftY(y: Double) {
        val coeff = polynomial.coefficients.toMutableList()
        coeff[0] = coeff[0] + y
        polynomial = Polynomial(coeff)
    }

    public fun setYShift(y: Double){
        val coeff = polynomial.coefficients.toMutableList()
        coeff[0] = y
        polynomial = Polynomial(coeff)
    }
}