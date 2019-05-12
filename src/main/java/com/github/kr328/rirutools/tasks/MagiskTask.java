package com.github.kr328.rirutools.tasks;

import com.github.kr328.rirutools.extensions.MagiskExtension;
import com.github.kr328.rirutools.utils.PathUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleScriptException;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MagiskTask extends DefaultTask {
    @TaskAction
    void onAction() throws IOException {
        MagiskExtension extension = getProject().getExtensions().getByType(MagiskExtension.class);
        File outputFile = new File(getProject().file(
                PathUtils.normalize(extension.getOutput()
                        .replace("$buildDir", getProject().getBuildDir().getAbsolutePath()))).getAbsolutePath());

        if (extension.getZip().getZipMap().isEmpty()) {
            setDidWork(false);
            return;
        }

        //noinspection ResultOfMethodCallIgnored
        outputFile.getParentFile().mkdirs();

        ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(outputFile));

        for (Map.Entry<File, String> entry : extension.getZip().getZipMap().entrySet()) {
            archiveEntry(entry.getKey(), entry.getValue(), outputStream);
        }

        outputStream.close();
    }

    private void archiveEntry(File source, String target, ZipOutputStream stream) throws IOException {
        if (source.isDirectory()) {
            for (File f : Objects.requireNonNull(source.listFiles()))
                archiveEntry(f, target + "/" + f.getName(), stream);
        } else if (source.isFile()) {
            ZipEntry entry = new ZipEntry(PathUtils.zipEntry(target));
            stream.putNextEntry(entry);
            readFileToStream(source, stream);
        } else
            throw new GradleScriptException("Magisk file not found.", new FileNotFoundException(source.getAbsolutePath()));
    }

    private void readFileToStream(File file, OutputStream stream) throws IOException {
        byte[] buffer = new byte[4096];
        FileInputStream inputStream = new FileInputStream(file);
        int read_length;

        while ((read_length = inputStream.read(buffer)) > 0)
            stream.write(buffer, 0, read_length);

        inputStream.close();
    }
}
