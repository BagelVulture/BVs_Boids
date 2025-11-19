class Bird {
    Vector position;
    Vector velocity;
    boolean isImportant;

    public Bird(Vector position, Vector velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public void updateVelocity(Bird[] birds,
                               double cohesionCoefficient,
                               int alignmentCoefficient,
                               double separationCoefficient) {
        Vector v = new Vector(velocity.data[0], velocity.data[1]);

        if (cohesionCoefficient > 0 && birds.length > 0) {
            v = v.plus(cohesion(birds, cohesionCoefficient));
        }

        if (alignmentCoefficient > 0 && birds.length > 0) {
            v = v.plus(alignment(birds, alignmentCoefficient));
        }

        if (separationCoefficient > 0 && birds.length > 0) {
            v = v.plus(separation(birds, separationCoefficient));
        }

        velocity = v;
        limitVelocity();
    }

    public void updatePosition(int xMax, int yMax, double speedMultiplier) {
        position = position.plus(velocity.times(speedMultiplier));

        // Wrap around horizontally and vertically
        position.data[0] = (position.data[0] + xMax) % xMax;
        position.data[1] = (position.data[1] + yMax) % yMax;
    }


    public Vector cohesion(Bird[] birds, double cohesionCoefficient) {
        if (birds.length == 0 || cohesionCoefficient == 0) return new Vector(0,0);
        Vector center = new Vector(0,0);
        for (Bird b : birds) center = center.plus(b.position);
        center = center.div(birds.length);
        return center.minus(position).div(cohesionCoefficient);
    }

    public Vector alignment(Bird[] birds, int alignmentCoefficient) {
        if (birds.length == 0 || alignmentCoefficient == 0) return new Vector(0,0);
        Vector avgVelocity = new Vector(0,0);
        for (Bird b : birds) avgVelocity = avgVelocity.plus(b.velocity);
        avgVelocity = avgVelocity.div(birds.length);
        return avgVelocity.minus(velocity).div(alignmentCoefficient);
    }

    public Vector separation(Bird[] birds, double separationCoefficient) {
        Vector c = new Vector(0,0);
        for (Bird bird : birds)
            if ((bird.position.minus(position).magnitude()) < separationCoefficient)
                c = c.minus(bird.position.minus(position));
        return c;
    }


    public void limitVelocity() {
        int vlim = 100;
        if (this.velocity.magnitude() > vlim) {
            this.velocity = this.velocity.div(this.velocity.magnitude());
            this.velocity = this.velocity.times(vlim);
        }
    }

    public String toString() {
        return "Position: " + this.position + " Velocity: " + this.velocity;
    }
}