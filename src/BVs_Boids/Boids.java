package BVs_Boids;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Boids {
    Bird[] birds;
    int N;       // number of boids
    int xRes;    // width of field
    int yRes;    // height of field

    int cols, rows;                     // number of grid cells in each direction
    List<Bird>[][] grid;                // spatial grid
    double cellWidth, cellHeight;       // computed cell dimensions

    /**
     * Initialize boids randomly within the field.
     */
    @SuppressWarnings("unchecked")
    public Boids(int amount, int width, int height, double newBoidsVelocity) {
        N = amount;
        xRes = width;
        yRes = height;
        birds = new Bird[N];
        Random rand = new Random();

        for (int i = 0; i < N; i++) {
            birds[i] = new Bird(
                new Vector(rand.nextInt(xRes), rand.nextInt(yRes)),
                new Vector((Math.random() * newBoidsVelocity) - (newBoidsVelocity/2), (Math.random() * newBoidsVelocity) - (newBoidsVelocity/2))
            );
        }

        // placeholder grid initialization
        cols = rows = 1;
        grid = new ArrayList[1][1];
        grid[0][0] = new ArrayList<>();
        cellWidth = width;
        cellHeight = height;
    }

    /**
     * Updates boids using an automatically determined neighbor search area.
     *
     * @param gridCellsAcross        Number of cells across the field (e.g. 5 → 5×5 grid)
     * @param distanceThreshold      Max distance (in pixels) for neighbor detection
     * @param cohesionCoefficient    Movement toward center of nearby boids
     * @param alignmentCoefficient   Velocity matching factor
     * @param separationCoefficient  Repulsion from nearby boids
     */
    @SuppressWarnings("unchecked")
    public void move(int gridCellsAcross,
                     double distanceThreshold,
                     double cohesionCoefficient,
                     int alignmentCoefficient,
                     double separationCoefficient,
                     double speedMultiplier, int fovDegrees) {

        // rebuild grid structure
        cols = Math.max(1, gridCellsAcross);
        rows = Math.max(1, gridCellsAcross);
        cellWidth = (double) xRes / cols;
        cellHeight = (double) yRes / rows;

        grid = new ArrayList[cols][rows];
        for (int i = 0; i < cols; i++)
            for (int j = 0; j < rows; j++)
                grid[i][j] = new ArrayList<>();

        // populate grid
        for (Bird b : birds) {
            int cx = (int) (b.position.data[0] / cellWidth);
            int cy = (int) (b.position.data[1] / cellHeight);
            cx = clamp(cx, 0, cols - 1);
            cy = clamp(cy, 0, rows - 1);
            grid[cx][cy].add(b);
        }

        // update boids
        for (Bird b : birds) {
            Bird[] neighbours = getNearbyBirds(b, distanceThreshold, fovDegrees);
            b.updateVelocity(neighbours,
                    cohesionCoefficient, alignmentCoefficient, separationCoefficient);
            b.updatePosition(xRes, yRes, speedMultiplier);

        }
    }

    /**
     * Draws all boids.
     */
    public void draw(Graphics g, double boidSize, int distance, int fov) {
        for (Bird b : birds) {
            if (b.isImportant) {
                drawCircle(g, distance, fov, b);
                drawImportant(g, boidSize, b);
            } else {
                drawNormal(g, boidSize, b);
            }
        }
    }

    private Bird[] getNearbyBirds(Bird b, double distanceThreshold, double fovDegrees) {
        List<Bird> nearby = new ArrayList<>();

        int cx = (int) (b.position.data[0] / cellWidth);
        int cy = (int) (b.position.data[1] / cellHeight);

        int cellRadiusX = (int) Math.ceil(distanceThreshold / cellWidth);
        int cellRadiusY = (int) Math.ceil(distanceThreshold / cellHeight);

        // Bird's facing direction (in radians)
        double facingAngle = Math.atan2(b.velocity.data[1], b.velocity.data[0]);
        // Half of the field of view, converted to radians
        double halfFov = Math.toRadians(fovDegrees / 2.0);

        for (int dx = -cellRadiusX; dx <= cellRadiusX; dx++) {
            for (int dy = -cellRadiusY; dy <= cellRadiusY; dy++) {
                int nx = cx + dx;
                int ny = cy + dy;
                if (nx < 0 || nx >= cols || ny < 0 || ny >= rows)
                    continue;

                for (Bird other : grid[nx][ny]) {
                    if (other == b) continue;

                    // Determine if this boid is near an edge, within its view distance
                    boolean nearEdgeX = (b.position.data[0] < distanceThreshold) || (b.position.data[0] > xRes - distanceThreshold);
                    boolean nearEdgeY = (b.position.data[1] < distanceThreshold) || (b.position.data[1] > yRes - distanceThreshold);

// Compute deltas from this bird to the other
                    double deltaX = other.position.data[0] - b.position.data[0];
                    double deltaY = other.position.data[1] - b.position.data[1];

// If near an edge, wrap deltas across boundaries
                    if (nearEdgeX || nearEdgeY) {
                        if (deltaX >  xRes / 2.0) deltaX -= xRes;
                        if (deltaX < -xRes / 2.0) deltaX += xRes;
                        if (deltaY >  yRes / 2.0) deltaY -= yRes;
                        if (deltaY < -yRes / 2.0) deltaY += yRes;
                    }

// Compute wrapped (or normal) distance
                    double distWrapped = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                    if (distWrapped >= distanceThreshold)
                        continue;

// Compute angle toward the wrapped (or normal) position
                    double angleToOther = Math.atan2(deltaY, deltaX);

                    // Compute the smallest angular difference
                    double angleDiff = angleDifference(facingAngle, angleToOther);

                    // Check if within FOV
                    if (Math.abs(angleDiff) <= halfFov) {
                        nearby.add(other);
                    }
                }
            }
        }

        Bird[] result = new Bird[nearby.size()];
        return nearby.toArray(result);
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    double angleDifference(double a, double b) {
        double diff = a - b;
        while (diff < -Math.PI) diff += 2 * Math.PI;
        while (diff > Math.PI) diff -= 2 * Math.PI;
        return diff;
    }

    public void setImportantBoid() {

        int tracker = 0;
        int random = (int) (Math.random() * birds.length);
        for (Bird b : birds) {
            if (b.isImportant) {
                b.isImportant = false;
            }
        }
        for (Bird b : birds) {
            tracker++;
            if (random == tracker) {
                b.isImportant = true;
            }
        }
    }

    private void drawNormal(Graphics g, double boidSize, Bird b){
        int x = (int) b.position.data[0] - 10;
        int y = (int) b.position.data[1] - 10;

        Image image = new ImageIcon("src/BVs_Boids/Boid.png").getImage();

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        double angle = Math.atan2(b.velocity.data[1], b.velocity.data[0]) + Math.toRadians(90);
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double centerX = x + (imageWidth / 2.0);
        double centerY = y + (imageHeight / 2.0);

        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(angle);
        transform.scale(boidSize, boidSize);
        transform.translate(-imageWidth / 2.0, -imageHeight / 2.0);

        g2d.drawImage(image, transform, null);
        g2d.setTransform(originalTransform);
    }
    private void drawImportant(Graphics g, double boidSize, Bird b){
        int x = (int) b.position.data[0] - 10;
        int y = (int) b.position.data[1] - 10;

        Image image = new ImageIcon("src/BVs_Boids/FocusedBoid.png").getImage();

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        double angle = Math.atan2(b.velocity.data[1], b.velocity.data[0]) + Math.toRadians(90);
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double centerX = x + (imageWidth / 2.0);
        double centerY = y + (imageHeight / 2.0);

        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(angle);
        transform.scale(boidSize * 1.5, boidSize * 1.5);
        transform.translate(-imageWidth / 2.0, -imageHeight / 2.0);

        g2d.drawImage(image, transform, null);
        g2d.setTransform(originalTransform);
    }
    private void drawCircle(Graphics g, int distance, int fov, Bird b){
        int x = (int) b.position.data[0] - 20;
        int y = (int) b.position.data[1] - 20;

        int circleNumber = (int) (fov/ 22.5) - 1;

        Image circle = new ImageIcon("src/BVs_Boids/Circle_" + circleNumber + ".png").getImage();

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        double angle = Math.atan2(b.velocity.data[1], b.velocity.data[0]) + Math.toRadians(90);
        int imageWidth = circle.getWidth(null);
        int imageHeight = circle.getHeight(null);
        double centerX = x + (imageWidth / 2.0);
        double centerY = y + (imageHeight / 2.0);

        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(angle);
        transform.scale((double) distance / 17, (double) distance / 17);
        transform.translate(-imageWidth / 2.0, -imageHeight / 2.0);

        g2d.drawImage(circle, transform, null);
        g2d.setTransform(originalTransform);
    }

    public Bird[] getBirds() {
        return birds;
    }
}