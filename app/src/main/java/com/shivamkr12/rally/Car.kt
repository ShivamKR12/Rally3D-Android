package com.shivamkr12.rally

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3

class Car(val modelInstance: ModelInstance) {
    // Ursina is Left-handed, LibGDX is Right-handed. We may need to invert Z.
    // Let's start the car near the origin to see the track better.
    val position = Vector3(0f, 5f, 0f)
    var rotationY = 90f // Degrees

    var speed = 0f
    var topspeed = 34f
    var acceleration = 23f
    var brakingStrength = 30f
    var friction = 5f
    var turningSpeed = 60f

    init {
        updateTransform()
    }

    fun update(delta: Float, gas: Boolean, brake: Boolean, turnLeft: Boolean, turnRight: Boolean) {
        if (gas) {
            speed += acceleration * delta
        } else if (brake) {
            speed -= brakingStrength * delta
        } else {
            if (speed > 0) {
                speed -= friction * delta
                if (speed < 0) speed = 0f
            } else if (speed < 0) {
                speed += friction * delta
                if (speed > 0) speed = 0f
            }
        }

        if (speed > topspeed) speed = topspeed
        if (speed < -15f) speed = -15f

        if (speed != 0f) {
            val turnAmount = turningSpeed * delta * Math.signum(speed)
            if (turnLeft) {
                rotationY += turnAmount
            }
            if (turnRight) {
                rotationY -= turnAmount
            }
        }

        val radians = rotationY * MathUtils.degreesToRadians
        // Adjust for typical 3D math: sin/cos mapping to X/Z
        val vx = MathUtils.sin(radians) * speed * delta
        val vz = MathUtils.cos(radians) * speed * delta

        position.x += vx
        position.z += vz

        updateTransform()
    }

    private fun updateTransform() {
        modelInstance.transform.setToTranslation(position)
        modelInstance.transform.rotate(Vector3.Y, rotationY)
        modelInstance.transform.scale(0.5f, 0.5f, 0.5f)
    }
}