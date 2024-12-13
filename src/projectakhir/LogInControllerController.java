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

/**
 * FXML Controller class
 *
 * @author 
 */
public class LogInControllerController implements Initializable {

    private static final String urlLink = "jdbc:mysql://localhost:3306/game_project";
    private static final String user = "root";
    private static final String password = "Asd12345*";
    private static final String query = "SELECT * FROM account WHERE username_player = ? AND password_player = ?";

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button logIn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic if needed
    }

    @FXML
    private void loginClick(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password.", Alert.AlertType.ERROR);
            return;
        }

        if (authenticateUser(username, password)) {
            gameStart();
        } else {
            showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(urlLink, user, LogInControllerController.password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();

            // If a match is found
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        return false;
    }

    private void gameStart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SpaceExplorerGame.fxml"));
            Parent menuRoot = loader.load();
            // Get current stage and set new scene
            Stage currentStage = (Stage) logIn.getScene().getWindow();
            currentStage.setScene(new Scene(menuRoot));
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

    private void signUpClick(ActionEvent event) {
        try {
            // Load the signup FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SpaceExplorerGame.fxml"));
            Parent signUpRoot = loader.load();

            // Get current stage and set new scene
            Stage currentStage = (Stage) logIn.getScene().getWindow();
            currentStage.setScene(new Scene(signUpRoot));
            currentStage.setTitle("Sign Up");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load to game. Please try again later.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void homeBack(MouseEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
