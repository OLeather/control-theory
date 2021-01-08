package com.github.oleather.control.profiles

import com.github.oleather.control.NProfileModule
import com.github.oleather.control.PolynomialSegment
import com.github.oleather.control.State
import com.github.oleather.control.combineModules
import kscience.kmath.functions.Polynomial
import kotlin.math.absoluteValue

/**
 * Generates a Motion Profile to the Nth degree
 */
public class NMotionProfile(
    n: Int,
    initialStates: Array<Double>,
    finalStates: Array<Double>,
    maxStates: Array<Double>,
    minStates: Array<Double>
) : MotionProfile() {
    public var profileModule: NProfileModule = NProfileModule()

    init {
        var cruiseModule: NProfileModule = NProfileModule()
        var accModule: NProfileModule = NProfileModule()
        var decModule: NProfileModule = NProfileModule()
        //Get index from the degree - 1
        val i = n - 1
        val finalState = finalStates[i]
        //Adapt cruise velocity based on whether the profile is negative or not
        val cruiseVelocity = if (initialStates[i] < finalState) maxStates[i] else -minStates[i]
        //Create cruise module with indefinite end
        cruiseModule = NProfileModule(
            mutableListOf(
                PolynomialSegment(
                    Polynomial(listOf(if (i == 0) initialStates[i] else 0.0, cruiseVelocity)),
                    0.0,
                    1e10
                )
            )
        )
        //If we are not on the last stage, we want to create an acceleration and deceleration profile
        if (i != 0) {
            //Prepare states arrays for acceleration profile
            val accInitialStates = initialStates.copyOf()
            val accFinalStates = finalStates.copyOf()
            val accMaxStates = maxStates.copyOf()
            val accMinStates = minStates.copyOf()
            //Remove all final states for acceleration profiles so that final states only affect the last states of the profile
            for (i in accFinalStates.indices) {
                accFinalStates[i] = 0.0
            }

            //Set the final state of the acceleration profile to the cruise velocity. Makes the acceleration profile reach the cruise velocity.
            accFinalStates[i - 1] = cruiseVelocity

            //Create acceleration profile to the degree of i, which is n-1
            val accProfile = NMotionProfile(i, accInitialStates, accFinalStates, accMaxStates, accMinStates)

            //Populate the acceleration module with all modules of the acceleration profile
            accModule = accProfile.profileModule

            //Prepare states arrays for deceleration profile
            val decInitialStates = initialStates.copyOf()
            val decFinalStates = finalStates.copyOf()
            val decMaxStates = maxStates.copyOf()
            val decMinStates = minStates.copyOf()

            //Remove all initial states for the deceleration profiles so that initial states only affect the first states of the profile
            for (i in decInitialStates.indices) {
                decInitialStates[i] = 0.0
            }

            //Set the initial state of the deceleration profile to the cruise velocity. Makes the deceleration profile go from the cruise velocity to the final state.
            decInitialStates[i - 1] = cruiseVelocity

            //Create the deceleration profile with the degree of i, which is n-1
            val decProfile = NMotionProfile(i, decInitialStates, decFinalStates, decMaxStates, decMinStates)

            //Populate the deceleration module with all modules of the deceleration profile
            decModule = decProfile.profileModule

            //Integrate the acceleration and deceleration modules
            accModule.integrate()
            decModule.integrate()

            //Shift the Y of the acceleration profile by the initial state
            accModule.shiftY(initialStates[i])
        }

        //Make the cruise module intersect the last point on the acceleration module
        cruiseModule.shiftX(accModule.getEndX(), true, true)
        cruiseModule.shiftY(accModule.getValue(accModule.getEndX()) ?: 0.0)

        //Set the cruise module end x to the intersection point of the cruise line and the remaining distance (y) of the profile given the total distance, acceleration module distance, and deceleration module distance.
        cruiseModule.functions[0].endX =
            cruiseModule.solveForX(finalState.minus(decModule.getValue(decModule.getEndX()) ?: 0.0))

        //Line up the deceleration module with the end of the cruise module
        decModule.shiftX(cruiseModule.getEndX(), true, true)
        decModule.shiftY(cruiseModule.getValue(cruiseModule.getEndX()) ?: 0.0)

        profileModule = combineModules(accModule, cruiseModule, decModule)
    }

    /**
     * Returns the state at a given time
     */
    override fun getStateAtTime(t: Double): State {
        return State(profileModule.getValue(t) ?: 0.0, profileModule.getDerivative(t) ?: 0.0, 0.0)
    }

    public fun totalTime(): Double = profileModule.getEndX()
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