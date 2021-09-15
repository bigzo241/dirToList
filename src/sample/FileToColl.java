package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileToColl implements Comparator{

    private Path file;
    private Stack stack = new Stack();
    private SortedSet root = new TreeSet(this);
    private List<String> directoryListName = new ArrayList<>();

    public FileToColl(Path file) {
        this.setFile(file);
        this.stack.push(root);
    }

    // getter et setter
    public Path getFile() {
        return this.file;
    }

    protected void setFile(Path file) {
        if (Files.isRegularFile(file)) {
            this.file = file;
        } else {
            System.out.println(this.file.toString() + " n'est pas un fichier");
        }
    }

    // getter directoryListName
    public List<String> getDirectoryListName() {
        return directoryListName;
    }

    /**
     * methode qui permet de stocker les elements d'une liste de fichier dans une collection de collection
     * @return SortedSet une collection qui permet de stocker les elements par dossier
     * @throws IOException
     */
    public SortedSet toColl() throws IOException {
        int t = 0;
        SortedSet temp;
        BufferedReader reader = Files.newBufferedReader(this.file, Charset.defaultCharset());
        String line;

        while ((line = reader.readLine()) != null) {
            if (t == 0) {
                t++;
                directoryListName.add(line.substring(7));
            } else {
                if (line.startsWith("Visite/")) {
                    SortedSet dir = new TreeSet(this);
                    temp = (TreeSet) stack.peek();
                    temp.add(dir);
                    this.stack.push(dir);
                    directoryListName.add(line.substring(7));
                } else if (line.equalsIgnoreCase("fin")) {
                    this.stack.pop();
                } else {
                    temp = (SortedSet) stack.peek();
                    temp.add(line);
                }
            }
        }

        return this.root;
    }

    /*
        Methode qui explique comment trier les elements de la collection TreeSet utilisée dans cette classe
     */
    @Override
    public int compare(Object o1, Object o2) {
        if(o1.getClass().getSimpleName().equalsIgnoreCase("String") && o2.getClass().getSimpleName().equalsIgnoreCase("Treeset")){
            return -1;
        } else if(o1.getClass().getSimpleName().equalsIgnoreCase("Treeset") && o2.getClass().getSimpleName().equalsIgnoreCase("String")){
            return 1;
        } else{
            return 1;
        }
    }
}
