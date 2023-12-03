package com.mobilesuit.clientplugin.util;

import java.io.File;
import java.nio.file.Paths;

public class PathNormalizer {
    public static String normalize(String path) {
        return Paths.get(path)
                .normalize()
                .toString()
                .replace(File.separator, "/");
    }
}
