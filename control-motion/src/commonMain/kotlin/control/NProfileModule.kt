package com.github.oleather.models

import com.github.oleather.models.profiles.calcDerivative
import com.github.oleather.models.profiles.calcIntegral
import space.kscience.kmath.functions.Polynomial
import com.github.oleather.models.profiles.solveForX

public class NProfileModule(public val functions: MutableList<PolynomialSegment>) {
    public constructor() : this(arrayListOf())

    public fun addFunction(function: PolynomialSegment) {
        functions.add(function)
        functions.sortBy { it.startX }
    }

    public fun getValue(x: Double, extendRange: Boolean = false): Double? = getSegment(x, extendRange)?.getValue(x)

    public fun solveForX(y: Double): Double = solveForX(this, y)

    public fun getDerivative(x: Double, extendRange: Boolean = false, n: Int = 1): Double? =
        getSegment(x, extendRange)?.getDerivative(x, n)


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

    public fun integrate(): NProfileModule {
        for (i in functions.indices) {
            val newCoefficients = functions[i].polynomial.coefficients.toMutableList()
            val xShift = functions[i].xShift
            functions[i].polynomial = Polynomial(newCoefficients)
            functions[i] =
                PolynomialSegment(calcIntegral(functions[i].polynomial), functions[i].startX, functions[i].endX)
            functions[i].shiftX(xShift)
            if (i > 0) {
                functions[i].shiftY(functions[i - 1].getValue(functions[i - 1].endX))
            }
        }
        return this
    }

    public fun differentiate(): NProfileModule {
        for (i in functions.indices) {
            val newCoefficients = functions[i].polynomial.coefficients.toMutableList()
            val xShift = functions[i].xShift
            functions[i].polynomial = Polynomial(newCoefficients)
            functions[i] =
                PolynomialSegment(calcDerivative(functions[i].polynomial), functions[i].startX, functions[i].endX)
            functions[i].shiftX(xShift)
        }
        return this
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

    public fun copyOf(): NProfileModule {
        return NProfileModule(functions.toMutableList())
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