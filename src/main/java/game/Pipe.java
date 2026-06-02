package game;

public class Pipe {
    private double x;
    private double y;
    private double width;
    private double height;
    private boolean isUpper;  // true up , false bottom
    private boolean passed;

    public Pipe ( double x, double y , double width , double height , boolean isUpper , boolean passed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isUpper = isUpper;
        this.passed = false;
    }

    public void move (double speed) {
        this.x -= speed;
    }

    public boolean isoffScreen() {
        return this.x + this.width < 0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isUpper() { return isUpper; }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed (boolean passed) {
        this.passed = passed;
    }
}

