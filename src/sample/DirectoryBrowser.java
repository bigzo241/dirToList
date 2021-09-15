package sample;


import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

public class DirectoryBrowser extends SimpleFileVisitor<Path> {

    private int t = 0;
    private int n = 0;
    private String option;
    private final AffichageOption affOpt = AffichageOption.getInstance();

    public DirectoryBrowser(String option) {
        this.option = option;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        t++;

        if (t == 1) {
            System.out.println("Visite/" + dir.getFileName());
            n = dir.getNameCount() - 1;
            return CONTINUE;
        }

        System.out.println("Visite/" + dir.subpath(n, dir.getNameCount()) );

        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        affOpt.setP(file);
        try {
            affOpt.affich(this.option);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        System.out.println("Fin");
        if(exc != null){
            exc.printStackTrace();
        }
        return CONTINUE;
    }

}
