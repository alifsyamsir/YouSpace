package youspace.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUtils {

    private static final String UPLOADS_DIR = "uploads";

    /**
     * Menyalin file gambar dari path asal ke folder relative 'uploads'
     * dan mengembalikan path relatif yang baru untuk disimpan di database.
     *
     * @param sourcePath path asal file (bisa absolut atau relatif)
     * @return path relatif baru (misal: 'uploads/uuid.jpg') atau path asal jika gagal/sudah relatif
     */
    public static String saveUploadedImage(String sourcePath) {
        if (sourcePath == null || sourcePath.trim().isEmpty()) {
            return null;
        }

        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            return sourcePath;
        }

        // Cek apakah file sudah berada di dalam folder uploads
        String normalizedPath = sourcePath.replace('\\', '/');
        if (normalizedPath.startsWith(UPLOADS_DIR + "/")) {
            return sourcePath;
        }

        try {
            // Memastikan folder uploads ada
            File uploadsDir = new File(UPLOADS_DIR);
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs();
            }

            // Mendapatkan ekstensi file
            String extension = "";
            String fileName = sourceFile.getName();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = fileName.substring(dotIndex);
            }

            // Generate nama unik menggunakan UUID
            String uniqueName = UUID.randomUUID().toString() + extension;
            File destFile = new File(uploadsDir, uniqueName);

            // Salin file
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Kembalikan relative path dengan format standard forward slash '/'
            return UPLOADS_DIR + "/" + uniqueName;

        } catch (IOException e) {
            System.err.println("Gagal menyalin file gambar: " + e.getMessage());
            e.printStackTrace();
            return sourcePath;
        }
    }
}
