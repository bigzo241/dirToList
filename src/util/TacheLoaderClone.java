package util;

import javafx.concurrent.Task;
import sample.CollToFIle;
import sample.DirToFile;
import sample.FileToColl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.SortedSet;

public class TacheLoaderClone extends Task<Void> {

    private Path dir;
    private String listName;
    private Boolean size;
    private Boolean date;

    // constructeurs
    public TacheLoaderClone() {

    }

    public TacheLoaderClone(Path dir, String listName, Boolean size, Boolean date) {
        this.dir = dir;
        this.listName = listName;
        this.size = size;
        this.date = date;
    }

    // setters
    public void setDir(Path dir) {
        this.dir = dir;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void setSize(Boolean size) {
        this.size = size;
    }

    public void setDate(Boolean date) { this.date = date; }


    // methodes redefinies
    @Override
    protected Void call() throws Exception {
        System.out.println("Debut de la tache. Thread : " + Thread.currentThread().getName());
        Instant debut = Instant.now();

        DirToFile dirToFile = new DirToFile(dir, size, date);
        Path listFile = dirToFile.toFile();

        FileToColl fileToColl = new FileToColl(listFile);
        SortedSet itemsColl = fileToColl.toColl();

        CollToFIle collToFIle = new CollToFIle(listName + ".txt", itemsColl, fileToColl.getDirectoryListName());
        collToFIle.toFile();

        System.out.println("Suppression du fichier oldList.txt");
        Files.deleteIfExists(Paths.get("oldList.txt"));

        Instant fin = Instant.now();
        Duration duree = Duration.between(debut, fin);
        System.out.println(" Le traitement a duré : " + duree.getSeconds() + " seconde(s)");
        System.out.println("Fin de la tache. Thread : " + Thread.currentThread().getName());

        return null;
    }

    @Override
    protected void failed() {
        super.failed();
        System.out.println(Thread.currentThread().getName());
        System.out.println("Tache echouée");
        try {
            Files.deleteIfExists(Paths.get("oldList.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void running() {
        super.running();
    }


    @Override
    protected void cancelled() {
        super.cancelled();

        System.setOut(System.out);

        System.out.println("Tache annulée");

        try {
            Files.deleteIfExists(Paths.get("oldList.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
