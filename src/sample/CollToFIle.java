package sample;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class CollToFIle {

    private SortedSet root;
    private String newListName;
    private List<String> directoryListName;

    public CollToFIle(String newListName, SortedSet root, List<String> directoryListName) {
        this.root = root;
        this.setNewListName(newListName);
        this.directoryListName = directoryListName;
    }

    // setters
    public void setNewListName(String newListName) {
        if (!newListName.equalsIgnoreCase("")) {
            this.newListName = newListName;
        } else {
            System.out.println("Saississez un nom correct pour la liste");
        }
    }


    /**
     * Methode qui permet de representer de facon lineaire la structure d'un dossier. Cette representation
     * est sauvegardée dans un fichier texte dont le nom est donné par l'argument listName
     *
     * @return
     */
    public Path toFile() {
        Path pathToNewListe = Paths.get(this.newListName);
        if (root.isEmpty()){
            System.out.println("Impossible de creer la nouvelle liste triée. Dossier vide");
//            System.exit(0);
        }else {
            try (BufferedWriter writter = Files.newBufferedWriter(Paths.get(this.newListName), Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                writter.write(directoryListName.get(0), 0, directoryListName.get(0).length());
                writter.newLine();
                directoryListName.remove(0);
                parcoursListe(this.root, writter, directoryListName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pathToNewListe;
    }

    /**
     * Methode permettant de parcourir recursivement une collection de type SortedSet
     *
     * @param list collection decrivant la structure du dossier
     * @param writter flux d'entré vers un fichier
     * @param directoryName collection contenant les noms des dossiers
     */
    private void parcoursListe(SortedSet list, BufferedWriter writter, List<String> directoryName) throws IOException {
        for (Object o : list){
            if(o.getClass().getSimpleName().equalsIgnoreCase("TreeSet")){
                writter.write(directoryName.get(0), 0, directoryName.get(0).length());
                writter.newLine();
                directoryName.remove(0);
                parcoursListe((TreeSet)o, writter, directoryName);
            }
            else{
                writter.write("\t" + o.toString(), 0, "\t".length() + o.toString().length());
                writter.newLine();
            }
        }
    }

}
