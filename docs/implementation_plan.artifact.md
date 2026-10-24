# Goal Description
We will port the core physics and driving mechanics from the Python Ursina `Car` class into Kotlin using LibGDX. We will also implement a chase camera, a basic ground plane to drive on, and on-screen touch controls so you can drive the car on your Android device.

## Proposed Changes

### Assets Integration
Instead of rendering a red box, we will prepare to load actual 3D models.
- **Action**: In the implementation phase, we will copy the car model (e.g., `sports-car.obj` and `sports-red.png`) from the original Python source into the Android `assets/` folder. (LibGDX can load `.obj` files directly using `ObjLoader`).

### Car Logic Port (`Car.kt`)
We will create a `Car` class that mimics `car.py`.
#### [NEW] [Car.kt](file:///C:/Users/dhruv/AndroidStudioProjects/Rally/app/src/main/java/com/shivamkr12/rally/Car.kt)
- **State Properties**: Position (x, y, z), rotation, speed, velocity, topspeed, acceleration, friction, turning_speed.
- **`update(delta: Float, isAccelerating: Boolean, isBraking: Boolean, turnDirection: Float)`**:
  - Apply acceleration or friction based on input.
  - Apply steering (rotation) scaled by current speed.
  - Compute forward movement using sine/cosine of the rotation angle.
  - Update the 3D `ModelInstance` transform.

### On-Screen Controls (`InputManager.kt`)
Android needs touch inputs since there is no keyboard.
#### [NEW] [InputManager.kt](file:///C:/Users/dhruv/AndroidStudioProjects/Rally/app/src/main/java/com/shivamkr12/rally/InputManager.kt)
- Create a 2D UI overlay using LibGDX `Stage`.
- Add on-screen touch areas or buttons for:
  - Left / Right steering.
  - Gas (Accelerate).
  - Brake / Reverse.

### Game Loop Update (`RallyGame.kt`)
Tie everything together.
#### [MODIFY] [RallyGame.kt](file:///C:/Users/dhruv/AndroidStudioProjects/Rally/app/src/main/java/com/shivamkr12/rally/RallyGame.kt)
- Load the car `.obj` model (or fallback to the box if the model isn't available right away).
- Create a large textured box or grid to act as the ground/track.
- Initialize `InputManager` to read screen touches.
- In `render()`:
  - Pass inputs to `Car.update()`.
  - Update the `PerspectiveCamera` to follow the car from behind and slightly above (Chase Cam).
  - Draw the ground and the car using `ModelBatch`.
  - Draw the UI using `Stage`.

## Verification Plan
1. Copy necessary `.obj` and texture files to `app/src/main/assets`.
2. Build and run the app.
3. Verify that the on-screen controls appear.
4. Verify that pressing the gas moves the car forward, left/right turns the car, and the camera follows the car across a ground plane.