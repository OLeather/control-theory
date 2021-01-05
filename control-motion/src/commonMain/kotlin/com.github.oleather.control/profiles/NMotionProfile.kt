package com.github.oleather.control.profiles

import com.github.oleather.control.NProfileModule
import com.github.oleather.control.PolynomialSegment
import com.github.oleather.control.State
import com.github.oleather.control.combineModules
import kscience.kmath.functions.Polynomial
import kotlin.math.absoluteValue
import kotlin.math.max

public class NMotionProfile(
    private val n: Int,
    private val initialStates: Array<Double>,
    private val finalStates: Array<Double>,
    private val maxStates: Array<Double>,
    private val minStates: Array<Double>
) : MotionProfile() {
    public var accModule: NProfileModule = NProfileModule()
    public var cruiseModule: NProfileModule = NProfileModule()
    public var decModule: NProfileModule = NProfileModule()

    init {
        reverseMethod()
    }

    private fun reverseMethod() {
        val i = n - 1
        //Adapt final state based on if we are in final stage or not
        val finalState = if (i == n - 1) finalStates[i] else maxStates[i + 1]
        //Adapt cruise acceleration based on whether the profile is negative or not
        val cruiseAccel = if (initialStates[i] < finalState) maxStates[i] else -minStates[i]
        //Create cruise module with indefinite end
        cruiseModule = NProfileModule(
            mutableListOf(
                PolynomialSegment(
                    Polynomial(listOf(if (i == 0) initialStates[i] else 0.0, cruiseAccel)),
                    0.0,
                    1e10
                )
            )
        )

        if (i != 0) {
            //Prepare states arrays for acceleration profile
            val accInitialStates = initialStates.copyOf()
            val accFinalStates = finalStates.copyOf()
            val accMaxStates = maxStates.copyOf()
            val accMinStates = minStates.copyOf()
            accFinalStates[i - 1] = cruiseAccel
            val accProfile = NMotionProfile(i, accInitialStates, accFinalStates, accMaxStates, accMinStates)
            accModule = combineModules(
                accProfile.accModule,
                accProfile.cruiseModule,
                accProfile.decModule
            )

            //Prepare states arrays for acceleration profile
            val decInitialStates = initialStates.copyOf()
            val decFinalStates = finalStates.copyOf()
            val decMaxStates = maxStates.copyOf()
            val decMinStates = minStates.copyOf()
            decInitialStates[i - 1] = cruiseAccel

            val decProfile = NMotionProfile(i, decInitialStates, decFinalStates, decMaxStates, decMinStates)
            decModule = combineModules(
                decProfile.accModule,
                decProfile.cruiseModule,
                decProfile.decModule
            )

            accModule.integrate()
            accModule.shiftY(initialStates[i])
            decModule.integrate()

        }

        cruiseModule.shiftX(accModule.getEndX(), true, true)
        cruiseModule.shiftY(accModule.getValue(accModule.getEndX()) ?: 0.0)

        cruiseModule.functions[0].endX =
            cruiseModule.solveForX(finalState.minus(decModule.getValue(decModule.getEndX()) ?: 0.0))

        decModule.shiftX(cruiseModule.getEndX(), true, true)
        decModule.shiftY(cruiseModule.getValue(cruiseModule.getEndX()) ?: 0.0)
    }

    override fun getStateAtTime(t: Double): State {
        return State(0.0, 0.0, 0.0)
    }

    public fun totalTime(): Double = 20.0

}

public fun calcIntegral(polynomial: Polynomial<Double>): Polynomial<Double> {
    val newCoefficients = mutableListOf<Double>()
    newCoefficients.add(0, 0.0)
    for (i in polynomial.coefficients.indices) {
        newCoefficients.add(i + 1, polynomial.coefficients[i] / (i + 1))
    }

    return Polynomial(newCoefficients)
}

public fun calcDerivative(polynomial: Polynomial<Double>): Polynomial<Double> {
    val newCoefficients = mutableListOf<Double>()
    for (i in polynomial.coefficients.indices) {
        if (i > 0) {
            newCoefficients.add(i - 1, polynomial.coefficients[i] * i)
        }
    }
    return Polynomial(newCoefficients)
}

public fun solveForX(polynomial: Polynomial<Double>, y: Double): Double {
    return solveForX(PolynomialSegment(polynomial, -1e10, 1e10), y)
}

public fun solveForX(piecewise: PolynomialSegment, y: Double): Double =
    solveForX(NProfileModule(arrayListOf(piecewise)), y)

public fun solveForX(module: NProfileModule, y: Double): Double {
    var x = 0.0
    var value = module.getValue(x, true)?.minus(y)
    var t = value?.div(module.getDerivative(x, true)!!)
    var i = 0
    val ITERATIONS = 50
    while (t!!.absoluteValue >= 1.0e-10) {
        value = module.getValue(x, true)!!.minus(y)
        t = value / module.getDerivative(x, true)!!
        x -= t
        i++
        if (i > ITERATIONS) return 0.0
    }
    return x
}