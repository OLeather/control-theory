package com.github.oleather.control.controllers

public class PIDFController {
    public var p: Double = 0.0
    public var i: Double = 0.0
    public var d: Double = 0.0
    public var f: Double = 0.0

    private var integral = 0.0
    private var lastError = 0.0

    public fun calculate(goal: Double, measurement: Double, feedforward: Double, dt: Double) = calculate(goal, measurement, dt) + f*feedforward

    public fun calculate(goal: Double, measurement: Double, dt: Double): Double {
        val error = goal-measurement
        integral += error
        val derivative = (error-lastError)/dt
        lastError = error
        return p*error + i * integral + d*derivative
    }
}