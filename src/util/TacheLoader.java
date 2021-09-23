package util;

import javafx.concurrent.Task;
import sample.CollToFIle;
import sample.DirToFile;
import sample.FileToColl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.SortedSet;

public class TacheLoader extends Task<Void> {

    private static Path dir;
    private static String listName;
    private static Boolean size;
    private static Boolean date;

    private static TacheLoader instance;

    private TacheLoader(Path dir, String listName, Boolean size, Boolean date) {
        TacheLoader.dir = dir;
        TacheLoader.listName = listName;
        TacheLoader.size = size;
        TacheLoader.date = date;
    }

    public static TacheLoader getTask() {
        if (instance == null){
            synchronized (TacheLoader.class) {
                instance = new TacheLoader(TacheLoader.dir, TacheLoader.listName, TacheLoader.size, TacheLoader.date);
            }
        }

        return instance;
    }


    public static void setDir(Path dir) {
        TacheLoader.dir = dir;
    }

    public static void setListName(String listName) {
        TacheLoader.listName = listName;
    }

    public static void setSize(Boolean size) {
        TacheLoader.size = size;
    }

    public static void setDate(Boolean date) {
        TacheLoader.date = date;
    }

    @Override
    protected Void call() throws Exception {

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

        return null;
    }
}
