package game;

public class Bird {

    private double x;
    private double y;
    private double velocity = 0;
    private final double gravity = 0.3;
    private final double jumpStrength = -6;
    private final double size = 40;



    public Bird(double startX, double startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update(GameState state) {
        double canvasHeight = GameConfig.CANVAS_HEIGHT;

        if (state == GameState.PLAYING || state == GameState.GAME_OVER) {
            velocity += gravity;
            y += velocity;

            if (y > canvasHeight - size) {
                y = canvasHeight - size;
                velocity = 0;
            }

            if (y < 0) {
                y = 0;
                velocity = 0;
            }

        } else if (state == GameState.MENU) {
            velocity = 0;
        }
    }

    public void jump(GameState state) {
        if (state == GameState.PLAYING) {
            velocity = jumpStrength;
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return size; }
}