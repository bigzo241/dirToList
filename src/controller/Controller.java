package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sample.CollToFIle;
import sample.DirToFile;
import sample.FileToColl;
import util.BrowserRules;
import util.ComponentCustomized;
import util.StageFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedSet;

public class Controller implements Initializable {

    private Path dirPath;

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
        } else {
            ProgressIndicator progress = ComponentCustomized.getProgressControl('c', false);
            Stage transparentStage = StageFactory.getStage('t', 300, 300);
            AnchorPane root = (AnchorPane) transparentStage.getScene().getRoot();
            root.getChildren().add(progress);
            transparentStage.show();

            Instant debut = Instant.now();

            DirToFile dirToFile = new DirToFile(this.dirPath, cbSize.isSelected(), cbDate.isSelected());
            Path listFile = dirToFile.toFile();

            FileToColl fileToColl = new FileToColl(listFile);
            SortedSet itemsColl = fileToColl.toColl();

            CollToFIle collToFIle = new CollToFIle(this.fileName.getText() + ".txt", itemsColl, fileToColl.getDirectoryListName());
            collToFIle.toFile();

            System.out.println("Suppression du fichier oldList.txt");
            Files.deleteIfExists(Paths.get("oldList.txt"));

            Instant fin = Instant.now();
            Duration duree = Duration.between(debut, fin);
            System.out.println(" Le traitement a duré : " + duree.getSeconds() + " seconde(s)");

            transparentStage.close();

            this.makeListView();
        }
    }

    public List findAvailableList() {
        System.out.println("user.dir : " + System.getProperty("user.dir"));
        Path userDirPath = Paths.get(System.getProperty("user.dir"));
        BrowserRules browserRules = new BrowserRules();

        try {
            Files.walkFileTree(userDirPath, browserRules);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (browserRules.getListAvailable().isEmpty()) {
            return null;
        } else {
            return browserRules.getListAvailable();
        }
    }

    public void makeListView() {
        if (this.findAvailableList() == null) {
            Label placeHolder = new Label("Aucune liste disponible");
            listAvailable.setPlaceholder(placeHolder);
        } else {
            if (listAvailable.getItems().size() != 0)
                listAvailable.getItems().remove(0, listAvailable.getItems().size()-1);

            for (Object p : this.findAvailableList()) {
                Path list = (Path) p;
                Hyperlink link = new Hyperlink(list.getFileName().toString());

                MenuItem item1 = new MenuItem("Ouvrir le dossier");
                MenuItem item2 = new MenuItem("Supprimer");

                item1.setOnAction(event -> {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(list.getParent().toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                item2.setOnAction(event -> {
                    try {
                        Files.deleteIfExists(list);
                        this.makeListView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                link.setContextMenu(new ContextMenu(item1, item2));

                link.setOnAction(e -> {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(list.toFile());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });

                listAvailable.getItems().add(link);
            }
        }
    }
}
