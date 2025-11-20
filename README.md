# Boid Simulation

A simple Java-based boid simulation built using Swing.  
Visualizes flocking behavior based on Craig Reynolds’ classic Boids model, with interactive sliders to control simulation parameters in real-time.

---

## Features

### Simulation
- Boids follow three basic rules:
  - **Cohesion** – every boid tries to move towards thecenter of mass of all surrounding boids
  - **Alignment** – every boid tries to match the speed and direction of all surrounding boids
  - **Separation** – every boid tries to steer away from all surrounding boids
- Adjustable eyesight range for each boid
- Focused boid highlighting with eyesight range visualization

### GUI Controls
- Adjustable sliders for:
  - Cohesion, Alignment, Separation
  - Boid FOV (degrees)
  - Boid movement speed
  - Boid icon size
  - Initial random velocity
  - Number of boids
  - Neighbor view distance
- Buttons for:
  - Starting and stoping the simulation
  - Reseting the boids
  - Reseting the sliders
  - Focusing on random boid
  - Launching the tutorial
 
### How to Run
- Download a the source code or a release. Releases can be found at https://github.com/BagelVulture/BVs_Boids/releases/tag/1.0.0, current version is 1.0.0
- Ensure you have java 8 or higher. Java can be downloaded from https://www.java.com/en/download/manual.jsp
- If you are using a release decompress the .zip
- Run the .jar in the root folder by double clicking, right-clicking and selecting "open" (or "open with" and choosing JavaLauncher), or running ```java -jar BVs_Boids.jar``` in the terminal.

  Note that if you move the .jar out of the folder it **will not work**. I am trying to fix this problem, but currently it is stumping me.
- A new window with the simulation should appear.
