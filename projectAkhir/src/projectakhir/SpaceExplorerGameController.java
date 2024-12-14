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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setUpGame();
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
        gameCanvas.requestFocus();
    }

    private void setUpGame() {
        // Sembunyikan elemen yang terkait dengan Game Over
        gameOverLabel.setVisible(false);
        finalScoreLabel.setVisible(false);
        restartButton.setVisible(false);
        exitButton.setVisible(false);
        mainMenu.setVisible(false);
        gameCanvas.requestFocus();

        // Buat pemain (player)
        player = new Rocket(WIDTH / 2, HEIGHT - 80, PLAYER_IMG);
        player.getView().setId("player"); // Set ID untuk elemen runtime
        gameCanvas.getChildren().add(player.getView());

        // Buat musuh awal
        opponents = new ArrayList<>();
        for (int i = 0; i < 10; i++) { // Buat 15 musuh awal
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
                score += 5; // Tambahkan skor Kalau berhasil Melewati
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
                    scoreLabel.setText("Score: " + score); // Perbarui label skor
                }
            }
        }
        // Mengganti Lawan jika point sudah mencapat 500 
        if (score >= 500 && !(opponents.get(3) instanceof Opponent2)) {
            for (Opponent opponent : opponents) {
                gameCanvas.getChildren().remove(opponent.getView());
            }
            opponents.clear();
            for (int i = 0; i < 15; i++) {
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

    // Kelas Rocket
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

    public class Rocket extends Character {

        private List<Bullet> bullets;
        private long lastShotTime;
        private static final long SHOOT_COOLDOWN = 300; // milliseconds

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
                lastShotTime = currentTime; // Update last shot time
                // Tambahkan poin setiap kali peluru ditembakkan
                SpaceExplorerGameController.this.score += 10;
                SpaceExplorerGameController.this.scoreLabel.setText("Score: " + SpaceExplorerGameController.this.score);
            }
        }

        public void updateBullets() {
            List<Bullet> bulletsToRemove = new ArrayList<>();
            for (Bullet bullet : bullets) {
                bullet.moveUp(); // Move the bullet upwards
                if (bullet.getBulletView().getLayoutY() < 0) {
                    bulletsToRemove.add(bullet); // Mark bullet for removal if it goes off-screen
                }
            }
            for (Bullet bullet : bulletsToRemove) {
                gameCanvas.getChildren().remove(bullet.getBulletView()); // Remove from canvas
            }
            bullets.removeAll(bulletsToRemove); // Remove from list
        }
    }

    // Bullet class to represent the bullet fired by the rocket
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

    public class Opponent1 extends Opponent {

        private int speed;

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

    public class Opponent2 extends Opponent {

        private int speed;

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

    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (gameOver) {
            return;
        }

        double moveAmount = 10; // Amount to move the player
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
        player.setPosition(newX, newY); // Update player position
        System.out.println("Key Pressed: " + event.getCode());
        // Pastikan pemain tetap dalam batas layar
        newX = Math.max(0, Math.min(WIDTH - player.getView().getFitWidth(), newX));
        newY = Math.max(0, Math.min(HEIGHT - player.getView().getFitHeight(), newY));
        player.setPosition(newX, newY); // Update posisi pemain

    }

    @FXML
    private void restartGame(ActionEvent event) {
        score = 0;
        gameOver = false;
        // Reset elemen FXML
        player.getBullets().clear();
        gameOverLabel.setVisible(false);
        finalScoreLabel.setVisible(false);
        restartButton.setVisible(false);
        exitButton.setVisible(false);
        mainMenu.setVisible(false);
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
            e.printStackTrace(); // Handling error when FXML fails to load
        }
    }

    public void saveScore(String playerName, int score) {
        // Konfigurasi koneksi database
        String url = "jdbc:mysql://localhost:3306/game_project"; // Ganti dengan nama database Anda
        String user = "root"; // Ganti dengan username database Anda
        String password = "Asd12345*"; // Ganti dengan password database Anda

        String query = "INSERT INTO scores (player_name, score) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameter query
            stmt.setString(1, playerName);
            stmt.setInt(2, score);

            // Eksekusi query
            stmt.executeUpdate();
            System.out.println("Skor berhasil disimpan ke database!");
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan skor ke database: " + e.getMessage());
        }
    }

}
