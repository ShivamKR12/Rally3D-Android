package com.shivamkr12.rally

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Vector3

class RallyGame : ApplicationAdapter() {
    private lateinit var modelBatch: ModelBatch
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var font: BitmapFont
    
    private lateinit var environment: Environment
    private lateinit var camera: PerspectiveCamera

    private lateinit var assets: AssetManager
    private var car: Car? = null
    private var trackInstance: ModelInstance? = null
    
    private lateinit var inputManager: InputManager

    override fun create() {
        modelBatch = ModelBatch()
        spriteBatch = SpriteBatch()
        font = BitmapFont()
        font.data.setScale(3f)

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.near = 1f
        camera.far = 1000f

        inputManager = InputManager()
        Gdx.input.inputProcessor = inputManager

        assets = AssetManager()
        
        assets.load("sports-car.obj", Model::class.java)
        assets.load("sports-red.png", Texture::class.java)
        assets.load("forest_track.obj", Model::class.java)
        assets.load("forest_track.png", Texture::class.java)
    }

    private fun setupGame() {
        if (car == null && assets.isLoaded("sports-car.obj") && assets.isLoaded("sports-red.png")) {
            val carModel = assets.get("sports-car.obj", Model::class.java)
            val instance = ModelInstance(carModel)
            val carTexture = assets.get("sports-red.png", Texture::class.java)
            instance.materials.forEach { mat ->
                mat.set(TextureAttribute.createDiffuse(carTexture))
            }
            car = Car(instance)
        }

        if (trackInstance == null && assets.isLoaded("forest_track.obj") && assets.isLoaded("forest_track.png")) {
            val trackModel = assets.get("forest_track.obj", Model::class.java)
            trackInstance = ModelInstance(trackModel)
            val trackTexture = assets.get("forest_track.png", Texture::class.java)
            trackInstance?.materials?.forEach { mat ->
                mat.set(TextureAttribute.createDiffuse(trackTexture))
            }
        }
    }

    override fun render() {
        if (assets.update()) {
            setupGame()
        }

        inputManager.update()

        car?.let {
            it.update(
                Gdx.graphics.deltaTime,
                inputManager.isGasPressed,
                inputManager.isBrakePressed,
                inputManager.isLeftPressed,
                inputManager.isRightPressed
            )

            // Adjust camera to look more like the original game
            val camOffset = Vector3(0f, 10f, -20f)
            camOffset.rotate(Vector3.Y, it.rotationY)
            
            camera.position.set(it.position).add(camOffset)
            
            // Look slightly ahead of the car
            val lookTarget = Vector3(it.position)
            camera.lookAt(lookTarget)
        }
        
        camera.update()

        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0.5f, 0.7f, 1.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch.begin(camera)
        trackInstance?.let { modelBatch.render(it, environment) }
        car?.let { modelBatch.render(it.modelInstance, environment) }
        modelBatch.end()
        
        // Draw some simple UI
        spriteBatch.begin()
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()
        
        // Let's use simple text buttons for now
        font.draw(spriteBatch, "LEFT", 50f, h - 50f)
        font.draw(spriteBatch, "RIGHT", w/2 + 50f, h - 50f)
        font.draw(spriteBatch, "BRAKE", 50f, 150f)
        font.draw(spriteBatch, "GAS", w/2 + 50f, 150f)
        
        if (car != null) {
            font.draw(spriteBatch, "Speed: ${car?.speed?.toInt()}", w/2 - 100f, h/2)
        } else {
            font.draw(spriteBatch, "Loading Assets...", w/2 - 100f, h/2)
        }
        spriteBatch.end()
    }

    override fun dispose() {
        modelBatch.dispose()
        spriteBatch.dispose()
        font.dispose()
        assets.dispose()
    }
}