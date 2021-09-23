package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import util.BrowserRules;
import util.ComponentCustomized;
import util.ServiceLoader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Path dirPath;
    private int t = 0;
    private ServiceLoader serviceLoader;

    @FXML
    private RadioButton trieNom;
    @FXML
    private RadioButton trieDate;
    @FXML
    private RadioButton trieTaille;
    @FXML
    private Label dirPathLabel;
    @FXML
    private Tooltip dirPathTooltip;
    @FXML
    private TextField fileName;
    @FXML
    private ListView<Hyperlink> listAvailable;
    @FXML
    private CheckBox cbSize;
    @FXML
    private CheckBox cbDate;

    private Button cancel = new Button("Annuler");

    @FXML
    private StackPane stackPane;
    @FXML
    private Button btnstart;


    // getters et setters
    public Path getDirPath() {
        return this.dirPath;
    }

    public void setDirPath(Path p) {
        if (Files.isDirectory(p)) {
            this.dirPath = p;
        } else {
            System.out.println(p.toString() + " n'est pas un dossier");
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.cancel.setId("btnCancel");
        this.cancel.setStyle("-fx-opacity : 1; -fx-background : black;");
        this.cancel.setOnAction(e -> serviceLoader.cancel());

        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(trieDate, trieNom, trieTaille);
        this.makeListView();
    }


    @FXML
    public void openFileChooser(ActionEvent e){
        DirectoryChooser dirChooser = new DirectoryChooser();

        dirChooser.setTitle("Selectionner un dossier");
        dirChooser.setInitialDirectory(new File("C:\\"));

        File dir = dirChooser.showDialog(null);
        if(dir != null) {
            System.out.println("le dossier selectionné est : " + dir.toString());
            this.dirPathLabel.setText(dir.toString());
            this.dirPathTooltip.setText(dir.toString());
            this.setDirPath(dir.toPath());
        } else {
            System.out.println("Aucun dossier selectionné");
        }
    }


    @FXML
    public void start() throws IOException {
        System.out.println("Start");
        if(this.getDirPath() == null || this.fileName.getText().isEmpty()){
            System.out.println("Aucun chemin specifié ou aucun nom de fichier");
            Alert alert = ComponentCustomized.getDialogBox(Alert.AlertType.WARNING, "Choisissez un dossier ou entrez un nom pour la liste");
            alert.show();
        } else {
            this.showLoader();

            if (t == 0) {
                ServiceLoader.setDir(this.dirPath);
                ServiceLoader.setListName(this.fileName.getText());
                ServiceLoader.setSize(cbSize.isSelected());
                ServiceLoader.setDate(cbDate.isSelected());
                serviceLoader = ServiceLoader.getService();

                serviceLoader.start();
            } else {
                ServiceLoader.setDir(this.dirPath);
                ServiceLoader.setListName(this.fileName.getText());
                ServiceLoader.setSize(cbSize.isSelected());
                ServiceLoader.setDate(cbDate.isSelected());

                serviceLoader.restart();
            }
            t++;
//            Instant debut = Instant.now();
//
//            DirToFile dirToFile = new DirToFile(this.dirPath, cbSize.isSelected(), cbDate.isSelected());
//            Path listFile = dirToFile.toFile();
//
//            FileToColl fileToColl = new FileToColl(listFile);
//            SortedSet itemsColl = fileToColl.toColl();
//
//            CollToFIle collToFIle = new CollToFIle(this.fileName.getText() + ".txt", itemsColl, fileToColl.getDirectoryListName());
//            collToFIle.toFile();
//
//            System.out.println("Suppression du fichier oldList.txt");
//            Files.deleteIfExists(Paths.get("oldList.txt"));
//
//            Instant fin = Instant.now();
//            Duration duree = Duration.between(debut, fin);
//            System.out.println(" Le traitement a duré : " + duree.getSeconds() + " seconde(s)");


            this.stackPane.getChildren().remove(1);
            System.out.println("Loader fin");

            this.makeListView();
        }
    }


    public List<Path> findAvailableList() {
        System.out.println("user.dir : " + System.getProperty("user.dir"));
        Path userDirPath = Paths.get(System.getProperty("user.dir"));
        BrowserRules browserRules = new BrowserRules();

        try {
            Files.walkFileTree(userDirPath, browserRules);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (browserRules.getSortedList().isEmpty()) {
            return null;
        } else {
//            System.out.println(browserRules.getSortedList());
            return browserRules.getSortedList();
        }
    }


    public void makeListView() {
        if (this.findAvailableList() == null) {
            Label placeHolder = new Label("Aucune liste disponible");
            listAvailable.setPlaceholder(placeHolder);
        } else {
            if (listAvailable.getItems().size() != 0)
                listAvailable.getItems().remove(0, listAvailable.getItems().size());

            for (int i = this.findAvailableList().size() - 1 ; i >= 0 ; i--) {
                Hyperlink link = new Hyperlink(this.findAvailableList().get(i).getFileName().toString());
                MenuItem item1 = new MenuItem("Ouvrir le dossier");
                MenuItem item2 = new MenuItem("Supprimer");

                int finalI = i;
                item1.setOnAction(event -> {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(this.findAvailableList().get(finalI).getParent().toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                item2.setOnAction(event -> {
                    try {
                        Files.deleteIfExists(this.findAvailableList().get(finalI));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    this.makeListView();
                });

                link.setContextMenu(new ContextMenu(item1, item2));

                link.setOnAction(e -> {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(this.findAvailableList().get(finalI).toFile());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });

                link.getStyleClass().add("basicStyle");
                link.setStyle("-fx-font-size : 15px");
                listAvailable.getItems().add(link);
            }
        }
    }

    public void showLoader() {
        System.out.println("Loader");

        ProgressIndicator progressInd = new ProgressIndicator();

        VBox vBox = new VBox(50);
        vBox.setId("forwardContener");
        vBox.setAlignment(Pos.CENTER);

        vBox.getChildren().addAll(progressInd, this.cancel);

        this.stackPane.getChildren().add(vBox);
    }
}
