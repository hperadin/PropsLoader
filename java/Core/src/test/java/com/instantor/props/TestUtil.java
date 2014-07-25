package com.instantor.props;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;

import org.junit.rules.TemporaryFolder;

public class TestUtil {
    static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (final IOException e) {
            throw new RuntimeException("Error in tests!", e);
        }
    }

    static File writeFile(final File file, final String content) {
        try {
            if (file.exists()) {
                file.delete();
            }

            final File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }

            final FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.close();
            return file;
        } catch (final IOException e) {
            throw new RuntimeException("Error in tests!", e);
        }
    }

    static String readFile(final File file) {
        try {
            final byte[] body = Files.readAllBytes(file.toPath());
            return new String(body, "ISO-8859-1");
        } catch (final IOException e) {
            throw new RuntimeException("Error in tests!", e);
        }
    }

    static File newFolderInTemp(final TemporaryFolder temp, final String name) {
        try {
            return temp.newFolder(name);
        } catch (final IOException e) {
            throw new RuntimeException("Error in tests!", e);
        }
    }
}
