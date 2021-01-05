package com.github.oleather.control

import com.github.oleather.control.profiles.NMotionProfile
import kscience.kmath.functions.OrderedPiecewisePolynomial
import kscience.kmath.functions.value
import kscience.kmath.operations.RealField
import com.github.oleather.control.profiles.calcDerivative
import com.github.oleather.control.profiles.calcIntegral
import kscience.kmath.functions.Polynomial
import com.github.oleather.control.profiles.solveForX

public class NProfileModule(public val functions: MutableList<PolynomialSegment>) {
    public constructor() : this(arrayListOf())

    public fun addFunction(function: PolynomialSegment) {
        functions.add(function)
        functions.sortBy { it.startX }
    }

    public fun getValue(x: Double, extendRange: Boolean = false): Double? = getSegment(x, extendRange)?.getValue(x)

    public fun solveForX(y: Double): Double = solveForX(this, y)

    public fun getDerivative(x: Double, extendRange: Boolean = false): Double? =
        getSegment(x, extendRange)?.getDerivative(x)

    public fun getIntegral(x: Double, extendRange: Boolean = false): Double? =
        getSegment(x, extendRange)?.getIntegral(x)

    public fun getSegment(x: Double, extendRange: Boolean = false): PolynomialSegment? {
        if (functions.isEmpty()) {
            return null
        }
        for (func in functions) {
            if (func.startX <= x && func.endX >= x) {
                return func
            }
        }
        if (functions.first().startX > x && extendRange) {
            return functions.first()
        } else if (extendRange) {
            return functions.last()
        }
        return null
    }

    public fun integrate() {
        for (i in functions.indices) {
            val newCoefficients = functions[i].polynomial.coefficients.toMutableList()
//            newCoefficients[0] = 0.0
            val xShift = functions[i].xShift
            functions[i].polynomial = Polynomial(newCoefficients)
            functions[i] =
                PolynomialSegment(calcIntegral(functions[i].polynomial), functions[i].startX, functions[i].endX)
            functions[i].shiftX(xShift)
            if (i > 0) {
                functions[i].shiftY(functions[i - 1].getValue(functions[i - 1].endX))
            }
        }
    }

    public fun getStartX(): Double = if (functions.size != 0) functions.first().startX else 0.0
    public fun getEndX(): Double = if (functions.size != 0) functions.last().endX else 0.0

    public fun shiftX(x: Double, moveEndpoints: Boolean = false, compound: Boolean = false) {
        for (func in functions) {
            func.shiftX(x, moveEndpoints, compound)
        }
    }

    public fun shiftY(y: Double) {
        for (func in functions) {
            func.shiftY(y)
        }
    }

    public fun setYShift(y: Double) {
        for (func in functions) {
            func.setYShift(y)
        }
    }
}

public fun combineModules(vararg modules: NProfileModule): NProfileModule {
    val newModule = NProfileModule()
    for (module in modules) {
        for (func in module.functions) {
            newModule.addFunction(func)
        }
    }
    return newModule
}