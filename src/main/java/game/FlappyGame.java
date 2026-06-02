package game;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import ui.GameView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FlappyGame {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameView view;

    private Bird bird;
    private List<Pipe> pipes;
    private GameState gameState;
    private int score;
    private AnimationTimer gameLoop;
    private int highScore = 0;

    private Image backgroundImage;
    private Image birdImage;
    private Image topPipeImage;
    private Image bottomPipeImage;
    private Image readyImage;
    private Image gameOverImage;

    private final double pipeSpeed = 3.0;
    private final int pipeSpawnInterval = 200;
    private int spawnCounter = 0;
    private final Random random = new Random();
    private final double pipeGap = 195.0;

    public FlappyGame(Canvas canvas, GameView view) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.view = view;

        this.gameState = GameState.MENU;
        this.score = 0;

        loadImages();
        initGame();
        createGameLoop();
    }

    private void loadImages() {
        try {
            backgroundImage = new Image(getClass().
                    getResourceAsStream("/images/flappybirdbg.png"));
            birdImage = new Image(getClass().
                    getResourceAsStream("/images/flappybird.png"));
            topPipeImage = new Image(getClass().
                    getResourceAsStream("/images/toppipe.png"));
            bottomPipeImage = new Image(getClass().
                    getResourceAsStream("/images/bottompipe.png"));
            readyImage = new Image(getClass().
                    getResourceAsStream("/images/ready.png"));
            gameOverImage = new Image(getClass().
                    getResourceAsStream("/images/game_over.png"));

        } catch (Exception e) {
            System.out.println("Images could not be loaded - Error: " + e.getMessage());
        }
    }

    public void initGame() {
        this.bird = new Bird(100, canvas.getHeight() / 2);
        this.pipes = new ArrayList<>();
        this.score = 0;
        this.spawnCounter = 0;
    }

    public void createGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
    }

    public void start() {
        gameLoop.start();
    }

    public void stop() {
        gameLoop.stop();
    }

    public void update() {
        bird.update(gameState);

        if (gameState == GameState.PLAYING) {
            spawnCounter++;

            if (spawnCounter >= pipeSpawnInterval) {
                spawnPipes();
                spawnCounter = 0;
            }

            Iterator<Pipe> iterator = pipes.iterator();
            while (iterator.hasNext()) {
                Pipe pipe = iterator.next();
                pipe.move(pipeSpeed);

                if (pipe.getX() + pipe.getWidth() < 0) {
                    iterator.remove();
                    continue;
                }

                if (pipe.isUpper() && !pipe.isPassed() && bird.getX() > pipe.getX() + pipe.getWidth()) {
                    pipe.setPassed(true);
                    score++;
                    view.onScoreChanged(score);
                }

                if (checkCollision(bird, pipe)) {
                    gameOver();
                }
            }

                if (bird.getY() >= canvas.getHeight() - bird.getSize() || bird.getY() <= 0) {
                    gameOver();
                }
            }
        }

        private void spawnPipes() {

        double minPipeHeight = 50;
        double maxPipeHeight = canvas.getHeight() - pipeGap - minPipeHeight;
        double topPipeHeight = minPipeHeight + (maxPipeHeight - minPipeHeight) * random.nextDouble();
        double bottomPipeHeight = canvas.getHeight() - topPipeHeight - pipeGap;

        double pipeWidth = 60;
        pipes.add(new Pipe(canvas.getWidth(), 0, pipeWidth, topPipeHeight, true, false));
        pipes.add(new Pipe(canvas.getWidth(), topPipeHeight + pipeGap, pipeWidth, bottomPipeHeight, false, false));
    }

    private boolean checkCollision(Bird b, Pipe p) {
        return b.getX() < p.getX() + p.getWidth() &&
                b.getX() + b.getSize() > p.getX() &&
                b.getY() < p.getY() + p.getHeight() &&
                b.getY() + b.getSize() > p.getY();
    }

    private void gameOver() {
        gameState = GameState.GAME_OVER;
        if (score > highScore) {
            highScore = score;
        }
        view.onGameOver(score);
    }




    private void render() {

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }

        for (Pipe pipe : pipes) {
            if (pipe.isUpper() && topPipeImage != null) {
                gc.drawImage(topPipeImage, pipe.getX(), pipe.getY(), pipe.getWidth(), pipe.getHeight());
            } else if (!pipe.isUpper() && bottomPipeImage != null) {
                gc.drawImage(bottomPipeImage, pipe.getX(), pipe.getY(), pipe.getWidth(), pipe.getHeight());
            }
        }

        if (birdImage != null) {
            gc.drawImage(birdImage, bird.getX(), bird.getY(), bird.getSize(), bird.getSize());
        } else {
            gc.setFill(Color.YELLOW);
            gc.fillRect(bird.getX(), bird.getY(), bird.getSize(), bird.getSize());
        }

        gc.setFill(Color.WHITE);
        gc.setFont(new javafx.scene.text.Font(24));

        if (gameState == GameState.PLAYING) {
            gc.fillText("Score: " + score, 20, 40);
        } else if (gameState == GameState.GAME_OVER) {
            if (gameOverImage != null) {
                double x = (canvas.getWidth() - gameOverImage.getWidth()) / 2;
                double y = (canvas.getHeight() - gameOverImage.getHeight()) / 2 - 50;
                gc.drawImage(gameOverImage, x, y);
            }

            gc.setFill(Color.WHITE);
            gc.setFont(new javafx.scene.text.Font(28));
            gc.fillText("Final Score: " + score,
                    canvas.getWidth() / 2 - 80, canvas.getHeight() / 2 + 80);
        }

        if (gameState == GameState.MENU) {

            if (readyImage != null) {
                double x = (canvas.getWidth() - readyImage.getWidth()) / 2;
                double y = (canvas.getHeight() - readyImage.getHeight()) / 2 - 50;
                gc.drawImage(readyImage, x, y);
            }

            gc.setFill(Color.BLACK);
            gc.fillText("Click to START", canvas.getWidth() / 4, canvas.getHeight() / 2);

        } else if (gameState == GameState.GAME_OVER) {
            if (gameOverImage != null) {
                double x = (canvas.getWidth() - gameOverImage.getWidth()) / 2;
                double y = (canvas.getHeight() - gameOverImage.getHeight()) / 2 - 50;
                gc.drawImage(gameOverImage, x, y);
            }
            gc.setFill(Color.RED);
            gc.setFont(new javafx.scene.text.Font(20));
            gc.fillText("Press to New Game Button \n to RESTART.", canvas.getWidth() / 5, canvas.getHeight() / 2);
        }

    }

    public void handleKey(KeyCode key) {

        if (key == KeyCode.SPACE || key == KeyCode.UP) {
            if (gameState == GameState.MENU) {
                gameState = GameState.PLAYING;
                bird.jump(gameState);
            } else if (gameState == GameState.PLAYING) {
                bird.jump(gameState);
            }
        }

    }


    public int getScore() { return score; }


}