package org.jenkinsci.deprecatedusage;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WarReader implements Closeable {
    private final ZipFile zipFile;
    private final Enumeration<? extends ZipEntry> entries;
    private ZipEntry entry;
    private JarReader jarReader;

    public WarReader(File warFile) throws IOException {
        super();
        this.zipFile = new ZipFile(warFile);
        this.entries = zipFile.entries();
    }

    public String nextClass() throws IOException {
        if (jarReader != null) {
            final String fileName = jarReader.nextClass();
            if (fileName != null) {
                return fileName;
            } else {
                jarReader.close();
                jarReader = null;
            }
        }
        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            final String fileName = entry.getName();
            if (fileName.startsWith("WEB-INF/lib/") && fileName.endsWith(".jar")) {
                jarReader = new JarReader(zipFile.getInputStream(entry));
                return this.nextClass();
            } else if (fileName.startsWith("WEB-INF/classes/") && fileName.endsWith(".class")) {
                return fileName;
            }
        }
        return null;
    }

    public InputStream getInputStream() throws IOException {
        if (jarReader != null) {
            return jarReader.getInputStream();
        }
        return zipFile.getInputStream(entry);
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
