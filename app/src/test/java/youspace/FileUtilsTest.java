package youspace;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import youspace.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    private static Path tempDir;

    @BeforeAll
    static void setup() throws IOException {
        tempDir = Files.createTempDirectory("youspace_test");
    }

    @AfterAll
    static void cleanup() throws IOException {
        // Hapus temp files
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
        
        // Bersihkan folder uploads buatan lokal jika ada di unit test
        File uploadsDir = new File("uploads");
        if (uploadsDir.exists()) {
            File[] files = uploadsDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".txt")) {
                        f.delete();
                    }
                }
            }
        }
    }

    @Test
    void testNullOrEmptyPath() {
        assertNull(FileUtils.saveUploadedImage(null));
        assertNull(FileUtils.saveUploadedImage(""));
        assertNull(FileUtils.saveUploadedImage("   "));
    }

    @Test
    void testNonExistentFile() {
        String nonExistent = "this_file_does_not_exist.jpg";
        assertEquals(nonExistent, FileUtils.saveUploadedImage(nonExistent));
    }

    @Test
    void testAlreadyRelativeUploadsPath() {
        String relativePath = "uploads/some_uuid.png";
        assertEquals(relativePath, FileUtils.saveUploadedImage(relativePath));

        String relativePathWindows = "uploads\\some_uuid.png";
        assertEquals(relativePathWindows, FileUtils.saveUploadedImage(relativePathWindows));
    }

    @Test
    void testValidFileUploaded() throws IOException {
        // Buat file temporary sebagai tiruan file yang diunggah admin
        Path dummyFile = Files.createTempFile(tempDir, "test_venue", ".txt");
        Files.writeString(dummyFile, "dummy image content");

        String absolutePath = dummyFile.toAbsolutePath().toString();
        String resultPath = FileUtils.saveUploadedImage(absolutePath);

        assertNotNull(resultPath);
        assertTrue(resultPath.startsWith("uploads/"));
        assertTrue(resultPath.endsWith(".txt"));

        // Pastikan file baru hasil salinan ada di folder uploads lokal
        File copiedFile = new File(resultPath);
        assertTrue(copiedFile.exists());
        assertEquals("dummy image content", Files.readString(copiedFile.toPath()));

        // Bersihkan file salinan tersebut
        copiedFile.delete();
    }
}
