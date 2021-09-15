package sample;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DirToFile {

    private Path dir;
    private String option = "";

    public DirToFile(Path dir, Boolean sizeIsCheck, Boolean dateIsCheck) {
        this.setDir(dir);

        if (sizeIsCheck) {
            this.option += "1";
        } else {
            this.option += "0";
        }

        if (dateIsCheck) {
            this.option += "1";
        } else {
            this.option += "0";
        }
    }

    // getter et setter
    public Path getDir() {
        return this.dir;
    }

    protected void setDir(Path dir) {
        if (Files.isDirectory(dir)) {
            this.dir = dir;
        } else {
            System.out.println(dir.toString() + " n'est pas un dossier");
        }
    }

    /**
     * methode qui permet de lister le contenu d'un dossier dans un fichier texte
     * @return le fichier texte contenant les elements du dossier
     * @throws IOException gestion des erreurs
     */
    public Path toFile() throws IOException {
        System.out.println("\n Parcours d'un repertoire ");
        Path liste = Paths.get("oldList.txt");

        OutputStream outstream = Files.newOutputStream(liste, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(outstream));
        Files.walkFileTree(this.getDir(), new DirectoryBrowser(option));
        System.setOut(oldOut);
        System.out.println("Sortie standard");

        return liste;
    }
}
