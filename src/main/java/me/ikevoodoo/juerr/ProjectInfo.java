package me.ikevoodoo.juerr;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class ProjectInfo {

    private ProjectInfo() {

    }

    private static final HashMap<Class<?>, List<Class<?>>> CLASSES = new HashMap<>();

    public static void load(Class<?> clazz) throws URISyntaxException, IOException {
        if (isLoaded(clazz)) return;

        Package pack = clazz.getPackage();
        if (pack == null) {
            List<Class<?>> classes = new ArrayList<>();
            classes.add(clazz);
            CLASSES.put(clazz, classes);
            return;
        }

        String packageName = pack.getName();

        String packagePath = packageName.replace('.', '/');
        URL resource = clazz.getClassLoader().getResource(packagePath);
        if (resource == null) return;
        URI pkg = resource.toURI();
        List<Class<?>> classes = new ArrayList<>();

        Path root;
        if(pkg.getScheme().equals("jar")) {
            try {
                root = FileSystems.getFileSystem(pkg).getPath(packagePath);
            } catch (final FileSystemNotFoundException e) {
                root = FileSystems.newFileSystem(pkg, Collections.emptyMap()).getPath(packagePath);
            }
        } else {
            root = Paths.get(pkg);
        }

        try(Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                String path = file.toString().replace("/", ".");
                String name = path.substring(path.indexOf(packageName), path.length() - 6);
                try {
                    classes.add(Class.forName(name));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        CLASSES.put(clazz, classes);
    }

    public static List<Class<?>> getClasses(Class<?> clazz) {
        return CLASSES.get(clazz);
    }

    public static boolean isLoaded(Class<?> clazz) {
        return !CLASSES.isEmpty() && CLASSES.containsKey(clazz);
    }

}
