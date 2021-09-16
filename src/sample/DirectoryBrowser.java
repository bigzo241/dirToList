package sample;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

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
        if (Files.isExecutable(dir) && Files.isReadable(dir)) {
            t++;

            if (t == 1) {
                System.out.println("Visite/" + dir.getFileName());
                n = dir.getNameCount() - 1;
                return CONTINUE;
            }

            System.out.println("Visite/" + dir.subpath(n, dir.getNameCount()) );

            return CONTINUE;
        } else {
            return SKIP_SUBTREE;
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS) && !Files.isHidden(file)) {
            affOpt.setP(file);
            affOpt.affich(this.option);
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

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return CONTINUE;
    }
}
