package controller;

import javafx.collections.ObservableList;
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
import util.RunnableDirToFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements Initializable {

    private Path dirPath;
    private RunnableDirToFile tache;

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

    private final Button cancel = new Button("Annuler");

    @FXML
    private StackPane stackPane;


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
        listAvailable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.cancel.setId("btnCancel");
        this.cancel.setStyle("-fx-opacity : 1; -fx-background : black;");
        this.cancel.setOnAction(e -> {
            tache.cancel();
            this.stackPane.getChildren().remove(1);
        });

        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(trieDate, trieNom, trieTaille);
        this.makeListView();
    }


    @FXML
    public void openFileChooser() {
        DirectoryChooser dirChooser = new DirectoryChooser();

        dirChooser.setTitle("Selectionner un dossier");
        dirChooser.setInitialDirectory(new File("C:\\"));

        File dir = dirChooser.showDialog(null);
        if(dir != null) {
            System.out.println("le dossier selectionn?? est : " + dir.toString());
            this.dirPathLabel.setText(dir.toString());
            this.dirPathTooltip.setText(dir.toString());
            this.setDirPath(dir.toPath());
        } else {
            System.out.println("Aucun dossier selectionn??");
        }
    }


    @FXML
    public void start() {
        System.out.println("Start");
        if(this.getDirPath() == null || this.fileName.getText().isEmpty()){
            System.out.println("Aucun chemin specifi?? ou aucun nom de fichier");
            Alert alert = ComponentCustomized.getDialogBox(Alert.AlertType.WARNING, "Choisissez un dossier ou entrez un nom pour la liste");
            alert.show();
        } else {

            this.showLoader();

            tache = new RunnableDirToFile();
            tache.setDir(this.dirPath);
            tache.setListName(this.fileName.getText());
            tache.setSize(cbSize.isSelected());
            tache.setDate(cbDate.isSelected());
            tache.setOnSucceeded(e -> {
                System.out.println(Thread.currentThread());
                System.out.println("Tache accomplie avec success");
                this.stackPane.getChildren().remove(1);
                System.out.println("Loader fin");
                this.makeListView();
            });

            ExecutorService exeService = Executors.newSingleThreadExecutor();
            exeService.submit(tache);
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

            MenuItem item1 = new MenuItem("Ouvrir le dossier");
            MenuItem item2 = new MenuItem("Supprimer");

            for (int i = this.findAvailableList().size() - 1 ; i >= 0 ; i--) {
                Hyperlink link = new Hyperlink(this.findAvailableList().get(i).getFileName().toString());

                int finalI = i;

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


            listAvailable.setOnMouseClicked(mouseEvent -> {
                ObservableList<Hyperlink> selectedItems = this.listAvailable.getSelectionModel().getSelectedItems();

                if (!selectedItems.isEmpty()) {
                    this.listAvailable.setContextMenu(new ContextMenu(item1, item2));
                    item1.setOnAction(e -> {
                        Desktop desktop = Desktop.getDesktop();
                        Path p = Paths.get(selectedItems.get(0).getText());
                        try {
                            desktop.open(p.toAbsolutePath().getParent().toFile());
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    });

                    item2.setOnAction(e -> {
                        for (Hyperlink item : selectedItems) {
                            try {
                                Files.deleteIfExists(Paths.get(item.getText()));
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                        this.makeListView();
                    });
                }
            });
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