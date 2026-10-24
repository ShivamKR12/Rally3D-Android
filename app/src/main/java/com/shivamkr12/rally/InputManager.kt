package com.shivamkr12.rally

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter

class InputManager : InputAdapter() {
    var isGasPressed = false
    var isBrakePressed = false
    var isLeftPressed = false
    var isRightPressed = false

    fun update() {
        isGasPressed = false
        isBrakePressed = false
        isLeftPressed = false
        isRightPressed = false

        val width = Gdx.graphics.width
        val height = Gdx.graphics.height

        for (i in 0..19) {
            if (Gdx.input.isTouched(i)) {
                val screenX = Gdx.input.getX(i)
                val screenY = Gdx.input.getY(i)

                if (screenX < width / 2) {
                    // Top left = Left
                    if (screenY < height / 2) {
                        isLeftPressed = true
                    } 
                    // Bottom left = Brake
                    else {
                        isBrakePressed = true
                    }
                } else {
                    // Top right = Right
                    if (screenY < height / 2) {
                        isRightPressed = true
                    } 
                    // Bottom right = Gas
                    else {
                        isGasPressed = true
                    }
                }
            }
        }
    }
}