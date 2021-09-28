package sample;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.SortedSet;

public class DIrToFileRunnable implements Runnable{

    private Path dir;
    private String listName;
    private Boolean size;
    private Boolean date;
    private StackPane stackPane;


    public DIrToFileRunnable(Path dir, String listName, Boolean size, Boolean date, StackPane stackPane) {
        this.dir = dir;
        this.listName = listName;
        this.size = size;
        this.date = date;
        this.stackPane = stackPane;
    }

    @Override
    public void run() {
        System.out.println("Debut de la tache. Thread : " + Thread.currentThread().getName());
        Instant debut = Instant.now();

        DirToFile dirToFile = new DirToFile(dir, size, date);
        Path listFile = null;
        try {
            listFile = dirToFile.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileToColl fileToColl = new FileToColl(listFile);
        SortedSet itemsColl = null;
        try {
            itemsColl = fileToColl.toColl();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CollToFIle collToFIle = new CollToFIle(listName + ".txt", itemsColl, fileToColl.getDirectoryListName());
        collToFIle.toFile();

        System.out.println("Suppression du fichier oldList.txt");
        try {
            Files.deleteIfExists(Paths.get("oldList.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Instant fin = Instant.now();
        Duration duree = Duration.between(debut, fin);
        System.out.println(" Le traitement a duré : " + duree.getSeconds() + " seconde(s)");
        System.out.println("Fin de la tache. Thread : " + Thread.currentThread().getName());

        Platform.runLater(() -> {this.stackPane.getChildren().remove(1);});
        System.out.println("Loader fin");

//        this.makeListView();
    }
}
