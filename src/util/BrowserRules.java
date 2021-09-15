package util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;

public class BrowserRules extends SimpleFileVisitor<Path> {

    private short t = 0;
    private List<Path> listAvailable = new ArrayList<Path>();

    public List<Path> getListAvailable() {
        return listAvailable;
    }


    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (t == 0) {
            t++;
            return FileVisitResult.CONTINUE;
        } else {
            System.out.println("Visite du repertoire : " + dir.toString());
            return FileVisitResult.SKIP_SUBTREE;
        }
    }


    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
        if (matcher.matches(file.getFileName())) {
            this.listAvailable.add(file);
        }
        return CONTINUE;
    }


    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//        System.out.println("Fin");
        return CONTINUE;
    }


    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        System.out.println("Fin");
        return CONTINUE;
    }
}