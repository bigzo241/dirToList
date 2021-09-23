package util;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.nio.file.Path;

public class ServiceLoader extends Service<Void> {

    private static Path dir;
    private static String listName;
    private static Boolean size;
    private static Boolean date;

    private static ServiceLoader instance;

    private ServiceLoader(Path dir, String listName, Boolean size, Boolean date) {
        ServiceLoader.dir = dir;
        ServiceLoader.listName = listName;
        ServiceLoader.size = size;
        ServiceLoader.date = date;
    }

    @Override
    protected Task<Void> createTask() {
//        return new TacheLoader(dir, listName, size, date);
        TacheLoader.setDir(ServiceLoader.dir);
        TacheLoader.setDate(ServiceLoader.date);
        TacheLoader.setSize(ServiceLoader.size);
        TacheLoader.setListName(ServiceLoader.listName);
        return TacheLoader.getTask();
    }


    public static ServiceLoader getService() {
        if (instance == null) {
            synchronized (ServiceLoader.class) {
                instance = new ServiceLoader(ServiceLoader.dir, ServiceLoader.listName, ServiceLoader.size, ServiceLoader.date);
            }
        }

        return instance;
    }

    public static void setDir(Path dir) {
        ServiceLoader.dir = dir;
    }

    public static void setListName(String listName) {
        ServiceLoader.listName = listName;
    }

    public static void setSize(Boolean size) {
        ServiceLoader.size = size;
    }

    public static void setDate(Boolean date) {
        ServiceLoader.date = date;
    }
}
