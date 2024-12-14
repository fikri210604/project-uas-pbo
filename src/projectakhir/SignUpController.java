package projectakhir;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SignUpController implements Initializable {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/game_project";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "Asd12345*";
    private static final String SELECT_USER_QUERY = "SELECT username_player FROM account WHERE username_player = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO account (username_player, password_player) VALUES (?, ?)";

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    private Button signUp;  

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inisialisasi jika perlu
    }

    @FXML
    private void homeBack(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void signUpClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in both username and password.", Alert.AlertType.ERROR);
            return;
        }
        if (!registerUser(username, password)) {
            showAlert("Error", "Username already exists or database error occurred.", Alert.AlertType.ERROR);
        } else {
            showAlert("Success", "Sign up successful! Redirecting to the menu...", Alert.AlertType.INFORMATION);
            redirectToMenu();
        }
    }
    private boolean registerUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement checkStmt = conn.prepareStatement(SELECT_USER_QUERY);
             PreparedStatement insertStmt = conn.prepareStatement(INSERT_USER_QUERY)) {
            checkStmt.setString(1, username);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                return false; 
            }
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();
            return true; // Pendaftaran berhasil

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        return false;
    }

    private void redirectToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Parent menuRoot = loader.load();
            Stage currentStage = (Stage) signUp.getScene().getWindow();
            currentStage.setScene(new Scene(menuRoot));
            currentStage.setTitle("Main Menu");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load menu. Please try again later.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
