package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

    public Main() { System.out.println("Constructeur : " + Thread.currentThread().getName()); }

    public void init(){
        System.out.println("Methode init : " + Thread.currentThread().getName());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Methode start : " + Thread.currentThread().getName());

        HBox root = FXMLLoader.load(getClass().getResource("../fxml/sample.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("../fxml/root.css").toExternalForm());

        primaryStage.setTitle("Listez le contenu d'un dossier dans un fichier texte");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) { launch(args); }

    public void stop(){
        System.out.println("Methode stop : " + Thread.currentThread().getName());
        try {
            Files.deleteIfExists(Paths.get("oldList.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
