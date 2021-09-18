package util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.FileVisitResult.CONTINUE;

public class BrowserRules extends SimpleFileVisitor<Path> implements Comparator<Path> {

    private final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
    private short t = 0;
    private List<Path> sortedList = new ArrayList<>();


    public List<Path> getSortedList() {
        this.sortedList.sort(this);
        return this.sortedList;
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
        if (matcher.matches(file.getFileName())) {
            sortedList.add(file);
        }
        return CONTINUE;
    }


    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return CONTINUE;
    }


    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        System.out.println("Fin");
        return CONTINUE;
    }

    @Override
    public int compare(Path o1, Path o2) {
        int n = 4;

        try {
            n = Files.getLastModifiedTime(o1).compareTo(Files.getLastModifiedTime(o2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return n;
    }
}