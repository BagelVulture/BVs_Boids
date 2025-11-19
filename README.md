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
Releases can be found at https://github.com/BagelVulture/BVs_Boids/releases/tag/1.0.0 or at the root as BVs_Boids.jar, current version is 1.0.0
