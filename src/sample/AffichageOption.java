package sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AffichageOption {

    private static Path p = null;

    private static AffichageOption instance = null;

    private AffichageOption() {}

    public static AffichageOption getInstance() {
        if (AffichageOption.instance == null)
            synchronized (AffichageOption.class) {
                instance = new AffichageOption();
            }

        return instance;
    }

    public void setP(Path p) {
        AffichageOption.p = p;
    }


    public void affichTaille() throws IOException {
        System.out.print("\t\t\t" + this.getFileSize(p));
    }

    public void affichDate() {
        System.out.print("\t\t\t" + this.getDate(p));
    }

    public void affich(String options) throws IOException {
        System.out.print("\t" + p.getFileName());

        if (options.charAt(0) == '1') {
            this.affichTaille();
        }

        if (options.charAt(1) == '1') {
            this.affichDate();
        }

        System.out.println();
    }

    /**
     * calcule la taille d'un fichier
     * @param file chemin pointant vers le fichier
     * @return retourne la taille du fichier soit en octet, kilo octet, Mega octet, Giga octet et Tera octet
     * @throws IOException
     */
    public String getFileSize(Path file) throws IOException {
        float taille = Files.size(file);
        int t = 0;

        while (taille >= 1024) {
            taille /= 1024;
            t++;
        }

        switch (t) {
            case 1:
                return taille + " ko";
            case 2:
                return taille + " Mo";
            case 3:
                return taille + " Go";
            case 4:
                return taille + " To";
            default:
                return taille + " o";
        }

    }

    /**
     * methode qui permet d'obtenir la derniere date de modification d'un fichier
     * @param file fichier dont la date doit etre determinée
     * @return String date
     */
    public String getDate(Path file) {
        LocalDateTime date = null;
        try {
            FileTime fileTime = Files.getLastModifiedTime(file);
            date =  LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
