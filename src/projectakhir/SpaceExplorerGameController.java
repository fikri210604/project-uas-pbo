/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package projectakhir;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SpaceExplorerGameController implements Initializable {

    private static final Random RAND = new Random();
    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;

    private static final Image PLAYER_IMG = new Image(SpaceExplorerGameController.class.getResource("/projectakhir/image/images/player.png").toExternalForm());

    private Rocket player;
    private List<Opponent> opponents;
    private GraphicsContext gc;
    private Timeline gameLoop;
    private int score = 0;
    private boolean gameOver = false;
    private boolean gameStarted = false;

    @FXML
    private ImageView rocketPlayer;
    @FXML
    private Label gameOverLabel;
    @FXML
    private Label finalScoreLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button restartButton;
    @FXML
    private Button exitButton;
    @FXML
    private AnchorPane gameCanvas;
    @FXML
    private Button mainMenu;
    @FXML
    private Label startGameLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setUpGame();
        startGameLabel.setVisible(true);
        gameCanvas.setFocusTraversable(true);
        gameCanvas.requestFocus();
        gameCanvas.setOnKeyPressed(this::onKeyPressed);
        gameOverLabel.setVisible(false);
        finalScoreLabel.setVisible(false);
        exitButton.setVisible(false);
        restartButton.setVisible(false);
        gameCanvas.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::onKeyPressed);
            }
        });
        // Game tidak berjalan apabila belum dipencet tobol
        gameLoop = new Timeline(new KeyFrame(Duration.millis(300), e -> runGameLoop()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameCanvas.requestFocus();
    }

    private void setUpGame() {
        gameOverLabel.setVisible(false);
        finalScoreLabel.setVisible(false);
        restartButton.setVisible(false);
        exitButton.setVisible(false);
        mainMenu.setVisible(false);
        gameCanvas.requestFocus();
        // Buat pemain (player)
        player = new Rocket(WIDTH / 2, HEIGHT - 80, PLAYER_IMG);
        player.getView().setId("player"); 
        gameCanvas.getChildren().add(player.getView());

        // Buat musuh awal
        opponents = new ArrayList<>();
        for (int i = 0; i < 10; i++) { // Buat 10 musuh awal
            Opponent1 opponent = new Opponent1(RAND.nextDouble() * (WIDTH - 40), -RAND.nextDouble() * 200);
            opponent.getView().setId("opponent_" + i); // Set ID unik untuk musuh
            opponents.add(opponent);
            gameCanvas.getChildren().add(opponent.getView());
        }

        // Inisialisasi loop permainan
        gameLoop = new Timeline(new KeyFrame(Duration.millis(100), e -> runGameLoop()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    private void runGameLoop() {
        if (gameOver) {
            gameOverLabel.setVisible(true);
            finalScoreLabel.setText("Final Score: " + score);
            finalScoreLabel.setVisible(true);
            restartButton.setVisible(true);
            exitButton.setVisible(true);
            mainMenu.setVisible(true);
            rocketPlayer.setVisible(false);
            player.getView().setVisible(false);
            for (Opponent opponent : opponents) {
                opponent.getView().setVisible(false); // Menyembunyikan setiap musuh
            }

            for (Bullet bullet : player.getBullets()) {
                bullet.getBulletView().setVisible(false); // Menyembunyikan gambar peluru
            }
            scoreLabel.setText("Score: " + score);
            gameLoop.stop();
            return;
        }
        rocketPlayer.setLayoutX(player.getX());
        rocketPlayer.setLayoutY(player.getY());

        // Gerakan Musuh Muncul Secara Random
        List<Opponent> opponentsToRemove = new ArrayList<>();
        for (Opponent opponent : opponents) {
            opponent.moveDown();
            if (opponent.getY() > HEIGHT) {
                scoreLabel.setText("Score: " + score);
                opponent.setPosition(RAND.nextInt(WIDTH - 40), -40);
                score += 5; // Tambahkan skor Kalau berhasil Melewati Musuh
            }
            if (opponent.getView().getBoundsInParent().intersects(player.getView().getBoundsInParent())) {
                gameOver = true;
            }
        }
        player.updateBullets();

        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : player.getBullets()) {
            for (Opponent opponent : opponents) {
                if (bullet.getBulletView().getBoundsInParent().intersects(opponent.getView().getBoundsInParent())) {
                    bulletsToRemove.add(bullet);
                    opponentsToRemove.add(opponent);
                    score += 10; // Tambahkan skor saat musuh tertembak
                    scoreLabel.setText("Score: " + score); 
                }
            }
        }
        // Mengganti Lawan jika point sudah mencapat 500 
        if (score >= 500 && !(opponents.get(3) instanceof Opponent2)) {
            for (Opponent opponent : opponents) {
                gameCanvas.getChildren().remove(opponent.getView());
            }
            opponents.clear();
            for (int i = 0; i < 15; i++) { // Musuh menjadi 15 ketika sudah sampai 500 poinnya
                Opponent2 newOpponent = new Opponent2(RAND.nextInt(WIDTH - 40), -RAND.nextInt(200));
                opponents.add(newOpponent);
                gameCanvas.getChildren().add(newOpponent.getView());
            }
        }
        // Menghilangkan peluru yang sudah ditembak
        player.getBullets().removeAll(bulletsToRemove);
        opponents.removeAll(opponentsToRemove);
        for (Bullet bullet : bulletsToRemove) {
            gameCanvas.getChildren().remove(bullet.getBulletView());
        }
        for (Opponent opponent : opponentsToRemove) {
            gameCanvas.getChildren().remove(opponent.getView());
        }
    }

    // Kelas Character sebagai kelas awal
    public abstract class Character {

        protected double x, y;
        protected ImageView view;

        public Character(double x, double y, Image image) {
            this.x = x;
            this.y = y;
            this.view = new ImageView(image);
            view.setLayoutX(x);
            view.setLayoutY(y);
        }

        public ImageView getView() {
            return view;
        }

        public void setPosition(double x, double y) {
            this.x = x;
            this.y = y;
            view.setLayoutX(x);
            view.setLayoutY(y);
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public void move(double dx, double dy) {
            this.x += dx;
            this.y += dy;
            view.setLayoutX(x);
            view.setLayoutY(y);
        }
    }
    // Kelas Rocket mewarisi kelas Character
    public class Rocket extends Character {

        private List<Bullet> bullets;
        private long lastShotTime;
        private static final long SHOOT_COOLDOWN = 100; 

        Rocket(double x, double y, Image image) {
            super(x, y, image);
            this.view.setFitWidth(50);
            this.view.setFitHeight(50);
            bullets = new ArrayList<>();
            lastShotTime = System.currentTimeMillis();
        }

        public List<Bullet> getBullets() {
            return bullets;
        }

        public void shoot(AnchorPane gameCanvas) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime >= SHOOT_COOLDOWN) {
                Bullet bullet = new Bullet(x + 20, y);
                bullets.add(bullet);
                gameCanvas.getChildren().add(bullet.getBulletView());
                lastShotTime = currentTime; 
                SpaceExplorerGameController.this.score += 10;
                SpaceExplorerGameController.this.scoreLabel.setText("Score: " + SpaceExplorerGameController.this.score);
            }
        }

        public void updateBullets() {
            List<Bullet> bulletsToRemove = new ArrayList<>();
            for (Bullet bullet : bullets) {
                bullet.moveUp(); 
                if (bullet.getBulletView().getLayoutY() < 0) {
                    bulletsToRemove.add(bullet);
                }
            }
            for (Bullet bullet : bulletsToRemove) {
                gameCanvas.getChildren().remove(bullet.getBulletView()); 
            }
            bullets.removeAll(bulletsToRemove); 
        }
    }

    // Kelas Bullet yang direpresentasikan oleh class Rocket
    public class Bullet {

        private ImageView bulletView;

        Bullet(double x, double y) {
            this.bulletView = new ImageView(new Image("/projectakhir/image/images/explosion.png"));
            bulletView.setFitWidth(10);
            bulletView.setFitHeight(20);
            bulletView.setLayoutX(x);
            bulletView.setLayoutY(y);
        }

        public ImageView getBulletView() {
            return bulletView;
        }

        public void moveUp() {
            bulletView.setLayoutY(bulletView.getLayoutY() - 10);
        }
    }
    
    // Kelas Opponent yang mewarisi kelas Character
    public class Opponent extends Character {

        private int speed;
        private ImageView Image;

        Opponent(double x, double y, Image image) {
            super(x, y, image);
            this.view.setFitWidth(40);
            this.view.setFitHeight(40);
            this.speed = 10;
        }

        public void moveDown() {
        }

        public void increaseSpeed(double increaseSpeed) {
        }
    }
    
    // Kelas Opponent1 yang mewarisi kelas Opponent dan Melakukan Polymorphism terhadap kelas Opponent
    public class Opponent1 extends Opponent {

        private int speed;

        //Mengganti gambar
        Opponent1(double x, double y) {
            super(x, y, new Image(SpaceExplorerGameController.class.getResource(
                    "/projectakhir/image/images/1.png").toExternalForm()));
            this.speed = 10;
        }

        @Override
        public void moveDown() {
            move(0, speed);
        }

        @Override
        public void increaseSpeed(double increaseSpeed) {
            this.speed += increaseSpeed;
        }
    }
    // Kelas Opponent2 yang mewarisi kelas Opponent dan Melakukan Polymorphism terhadap kelas Opponent
    public class Opponent2 extends Opponent {

        private int speed;
        
        //Mengganti gambar 
        Opponent2(double x, double y) {
            super(x, y, new Image(SpaceExplorerGameController.class.getResource(
                    "/projectakhir/image/images/2.png").toExternalForm()));
            this.speed = 20;
        }

        @Override
        public void moveDown() {
            move(0, speed);
        }

        @Override
        public void increaseSpeed(double increaseSpeed) {
            this.speed += increaseSpeed;
        }
    }
    
    // Handler tombol
    @FXML
    private void onKeyPressed(KeyEvent event) {
         if (gameStarted) {
    } else {
             // Cek apakah tombol yang ditekan adalah tombol mulai
             switch (event.getCode()) {
                 case W:
                 case A:
                 case S:
                 case D:
                 case UP:
                 case DOWN:
                 case LEFT:
                 case RIGHT:
                     startGameLabel.setVisible(false);
                     gameStarted = true;
                     gameLoop.play();
                     break;
                 default: 
                     return;
             }
        }
        if (gameOver) {
            return;
        }

        double moveAmount = 30; 
        double newX = rocketPlayer.getLayoutX();
        double newY = rocketPlayer.getLayoutY();

        switch (event.getCode()) {
            case RIGHT:
            case D:
                newX += moveAmount;
                break;
            case LEFT:
            case A:
                newX -= moveAmount;
                break;
            case UP:
            case W:
                newY -= moveAmount;
                break;
            case DOWN:
            case S:
                newY += moveAmount;
                break;
            case P:
                player.shoot(gameCanvas);
                break;
            default:
                break;

        }
        player.setPosition(newX, newY); 
        System.out.println("Key Pressed: " + event.getCode());
        newX = Math.max(0, Math.min(WIDTH - player.getView().getFitWidth(), newX));
        newY = Math.max(0, Math.min(HEIGHT - player.getView().getFitHeight(), newY));
        player.setPosition(newX, newY); 

    }

    @FXML
    private void restartGame(ActionEvent event) {
        score = 0;
        gameOver = false;
        player.getBullets().clear();
        gameOverLabel.setVisible(false);
        finalScoreLabel.setVisible(false);
        restartButton.setVisible(false);
        exitButton.setVisible(false);
        mainMenu.setVisible(false);
        startGameLabel.setVisible(true);
        setUpGame();
    }

    @FXML
    private void exitGame(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void mainMenuAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Parent mainMenuRoot = loader.load();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(mainMenuRoot));
            currentStage.setTitle("Main Menu");
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
}
