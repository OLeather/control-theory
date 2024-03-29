package com.github.oleather.models.profiles

import com.github.oleather.models.NProfileModule
import com.github.oleather.models.PolynomialSegment
import com.github.oleather.models.State
import com.github.oleather.models.combineModules
import space.kscience.kmath.functions.Polynomial
import kotlin.math.*

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
        var profile = calculateProfile(n, initialStates, finalStates, maxStates, minStates)
        val finalState = profile.getValue(profile.getEndX(), true)!!
        val i = n - 1
        if (n > 1) {
            if ((finalState > finalStates[i] && initialStates[i] < finalStates[i])) {
                profile = correctProfile(profile, n, initialStates, finalStates, maxStates, minStates)
            }
            if ((finalState < finalStates[i] && initialStates[i] > finalStates[i])) {
                profile = correctProfile(profile, n, initialStates, finalStates, maxStates, minStates)
            }
        }

        profileModule = profile
    }

    private fun correctProfile(
        profile: NProfileModule,
        n: Int,
        initialStates: Array<Double>,
        finalStates: Array<Double>,
        maxStates: Array<Double>,
        minStates: Array<Double>
    ): NProfileModule {
        val finalState = profile.getValue(profile.getEndX(), true)!!
        val i = n - 1
        if ((finalState > finalStates[i] && initialStates[i] < finalStates[i])) {
            val x = solveForX({ x ->
                val newMaxStates = maxStates.copyOf();
                newMaxStates[i] = x

                val newProfile = calculateProfile(n, initialStates, finalStates, newMaxStates, minStates)
                return@solveForX newProfile.getValue(newProfile.getEndX(), true)!!
            }, { x ->
                val dx = 0.01

                val newMaxStates = maxStates.copyOf()
                newMaxStates[i] = x - (dx / 2)
                val newProfile = calculateProfile(n, initialStates, finalStates, newMaxStates, minStates)

                val newMaxStates1 = maxStates.copyOf();
                newMaxStates1[i] = x + (dx / 2)
                val newProfile1 = calculateProfile(n, initialStates, finalStates, newMaxStates1, minStates)

                val dy = newProfile1.getValue(newProfile1.getEndX(), true)!! - newProfile.getValue(
                    newProfile.getEndX(),
                    true
                )!!
                if ((dy / dx).isNaN() || (dy / dx) == 0.0) {
                    return@solveForX 0.001
                }

                return@solveForX dy / dx
            }, finalStates[i], maxStates[i], toleranmce = .01)


            val newMaxStates = maxStates.copyOf();
            newMaxStates[i] = x

            return calculateProfile(n, initialStates, finalStates, newMaxStates, minStates)
        } else if ((finalState < finalStates[i] && initialStates[i] > finalStates[i])) {
            val x = solveForX({ x ->
                val newMinStates = minStates.copyOf();
                newMinStates[i] = x

                val newProfile = calculateProfile(n, initialStates, finalStates, maxStates, newMinStates)
                return@solveForX newProfile.getValue(newProfile.getEndX(), true)!!
            }, { x ->
                val dx = 0.01

                val newMinStates = minStates.copyOf()
                newMinStates[i] = x - (dx / 2)
                val newProfile = calculateProfile(n, initialStates, finalStates, maxStates, newMinStates)

                val newMinStates1 = minStates.copyOf();
                newMinStates1[i] = x + (dx / 2)
                val newProfile1 = calculateProfile(n, initialStates, finalStates, maxStates, newMinStates1)

                val dy = newProfile1.getValue(newProfile1.getEndX(), true)!! - newProfile.getValue(
                    newProfile.getEndX(),
                    true
                )!!
                if ((dy / dx).isNaN() || (dy / dx) == 0.0) {
                    return@solveForX -0.001
                }

                return@solveForX dy / dx
            }, finalStates[i], minStates[i], toleranmce = .01)


            val newMinStates = minStates.copyOf();
            newMinStates[i] = x

            return calculateProfile(n, initialStates, finalStates, maxStates, newMinStates)
            return profile
        } else {
            return profile
        }
    }

    private fun calculateAccDecModule(
        cruiseVelocity: Double,
        i: Int,
        initialStates: Array<Double>,
        finalStates: Array<Double>,
        maxStates: Array<Double>,
        minStates: Array<Double>
    ): Pair<NProfileModule, NProfileModule> {
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
        val accModule = accProfile.profileModule

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
        val decModule = decProfile.profileModule

        return Pair(accModule, decModule)
    }

    private fun calculateProfile(
        n: Int,
        initialStates: Array<Double>,
        finalStates: Array<Double>,
        maxStates: Array<Double>,
        minStates: Array<Double>
    ): NProfileModule {
        var cruiseModule: NProfileModule
        var accModule = NProfileModule()
        var decModule = NProfileModule()
        var dAccModule = NProfileModule()
        var dDecModule = NProfileModule()
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
            val pair = calculateAccDecModule(cruiseVelocity, i, initialStates, finalStates, maxStates, minStates)

            //Populate the deceleration module with all modules of the deceleration profile
            dAccModule = pair.first
            dDecModule = pair.second
            //Integrate the acceleration and deceleration modules
            accModule = dAccModule.copyOf().integrate()
            decModule = dDecModule.copyOf().integrate()

            //Shift the Y of the acceleration profile by the initial state
            accModule.shiftY(initialStates[i])
        }


        //Make the cruise module intersect the last point on the acceleration module
        cruiseModule.shiftX(accModule.getEndX(), true, true)
        cruiseModule.shiftY(accModule.getValue(accModule.getEndX()) ?: 0.0)


        val reversed = finalState > initialStates[i]

        //Set the cruise module end x to the intersection point of the cruise line and the remaining distance (y) of the
        //profile given the total distance, acceleration module distance, and deceleration module distance. If the
        //velocity cannot reach the max velocity given acceleration constraints, use the acceleration module's end y
        //value instead to maintain a continuous profile, but overshoot position. This overshoot will be dealt with later.
        val cruiseEndVal = if (reversed) max(
            finalState.minus(decModule.getValue(decModule.getEndX()) ?: 0.0),
            (accModule.getValue(accModule.getEndX()) ?: 0.0)
        ) else min(
            finalState.minus(decModule.getValue(decModule.getEndX()) ?: 0.0),
            (accModule.getValue(accModule.getEndX()) ?: 0.0)
        )

        cruiseModule.functions[0].endX =
            cruiseModule.solveForX(cruiseEndVal)

        //Line up the deceleration module with the end of the cruise module
        decModule.shiftX(cruiseModule.getEndX(), true, true)
        decModule.shiftY(cruiseModule.getValue(cruiseModule.getEndX()) ?: 0.0)

        return combineModules(accModule, cruiseModule, decModule)
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

    return solveForX(
        { x: Double -> module.getValue(x, true)!! },
        { x: Double -> module.getDerivative(x, true)!! },
        y,
        (module.getStartX() + module.getEndX()) / 2
    )
}

public fun solveForX(
    getValue: (x: Double) -> (Double),
    getDerivative: (x: Double) -> (Double),
    y: Double,
    initialX: Double,
    breakCondition: (x: Double) -> (Boolean) = { false },
    toleranmce: Double = 1.0e-10
): Double {
    var x = initialX
    var value = getValue(x).minus(y)
    var t = value / getDerivative(x)
    var i = 0
    val ITERATIONS = 50
    while (t.absoluteValue >= toleranmce) {
        if (breakCondition(x)) {
            return x
        }
        value = getValue(x) - y
        t = value / getDerivative(x)
        x -= t
        i++
        if (i > ITERATIONS) return x
    }
    return x
}


