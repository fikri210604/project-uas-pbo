/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package projectakhir;
//LIBRARY
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class MenuController implements Initializable { //DEKLARASI KELAS
//DEKLARASI VARIABEL
    @FXML
    private ImageView pane;
    @FXML
    private Button newGameButton;
    @FXML
    private Button loadGameButton;
    @FXML
    private Button aboutButton;
    @FXML
    private Button exitButton;
    private Stage stage;
    private Scene scene;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    


    @FXML
    private void handleNewGameAction(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("signUp.fxml"));
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    private void handleLoadGameAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("logInController.fxml"));
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleAboutAction(ActionEvent event) {
        Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
        aboutAlert.setTitle("About the Game");
        aboutAlert.setHeaderText("Game Description");
        aboutAlert.setContentText("Ini adalah game tentang roket yang menjelajah angkasa dengan Fitur sebagai Berikut\n"
                + "1. Roket dapat bergeser kanan dan ke kiri, serta dapat menembak dan memberikan skor 5.\n"
                + "2. Untuk masuk ke game ini, Anda dapat membuat akun, lalu masuk ke halaman login untuk memulai permainan.\n"
                + "3. Game ini memiliki tingkatan yang cukup sulit, jadi bersiaplah untuk menghadapi serangan roket.");
    // Tampilkan dialog
    aboutAlert.showAndWait();
    }


    @FXML
    private void handleExitAction(ActionEvent event) {
        System.exit(0);
    }
    
}
