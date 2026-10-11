
package Lab_4.Bai8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

public class FileServer {

    private static final int PORT = 8000;
    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024;

    private static final Path UPLOAD_DIR = Path.of("upload");

    public static void main(String[] args) {

        try {
            Files.createDirectories(UPLOAD_DIR);

            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("File Server dang chay!");
                System.out.println("Port: " + PORT);
                System.out.println("Thu muc luu: "
                        + UPLOAD_DIR.toAbsolutePath());

                while (true) {
                    Socket socket = server.accept();
                    handleClient(socket);
                }
            }

        } catch (Exception e) {
            System.out.println("Loi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {

        Path tempFile = null;

        try (
            Socket client = socket;
            DataInputStream in = new DataInputStream(
                    client.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    client.getOutputStream())
        ) {

            // 1. Nhan metadata truoc phan du lieu
            String fileName = in.readUTF();
            long fileSize = in.readLong();
            String expectedHash = in.readUTF();

            // 2. Kiem tra ten file, kich thuoc va SHA-256
            if (!isSafeFileName(fileName)) {
                out.writeUTF("ERR INVALID_FILENAME");
                out.flush();
                return;
            }

            if (fileSize < 0 || fileSize > MAX_FILE_SIZE) {
                out.writeUTF("ERR INVALID_SIZE");
                out.flush();
                return;
            }

            if (!expectedHash.matches("[0-9a-fA-F]{64}")) {
                out.writeUTF("ERR INVALID_HASH");
                out.flush();
                return;
            }

            // Server tu tao ten va duong dan luu
            String savedName = UUID.randomUUID() + "_" + fileName;
            Path destination = UPLOAD_DIR.resolve(savedName)
                    .normalize();

            if (!destination.getParent().equals(
                    UPLOAD_DIR.toAbsolutePath().normalize())
                    && !destination.getParent().equals(
                    UPLOAD_DIR.normalize())) {
                out.writeUTF("ERR INVALID_PATH");
                out.flush();
                return;
            }

            Files.createDirectories(UPLOAD_DIR);
            tempFile = Files.createTempFile(
                    UPLOAD_DIR, "receiving_", ".tmp");

            // 3. Doc dung so byte da khai bao, khong dung EOF
            try (var fileOut = Files.newOutputStream(
                    tempFile, StandardOpenOption.WRITE)) {

                byte[] buffer = new byte[8192];
                long remaining = fileSize;

                while (remaining > 0) {
                    int length = (int) Math.min(
                            buffer.length, remaining);

                    int count = in.read(buffer, 0, length);

                    if (count == -1) {
                        throw new IOException(
                                "File bi ngat truoc khi nhan du byte");
                    }

                    fileOut.write(buffer, 0, count);
                    remaining -= count;
                }
            }

            // 4. Tinh lai SHA-256
            String actualHash = sha256(tempFile);

            if (!actualHash.equalsIgnoreCase(expectedHash)) {
                out.writeUTF("ERR HASH_MISMATCH");
                out.flush();
                return;
            }

            // 5. Chi dua file hop le vao ten luu cuoi cung
            Files.move(tempFile, destination);
            tempFile = null;

            out.writeUTF("OK");
            out.flush();

            System.out.println("Nhan file: " + fileName);
            System.out.println("Kich thuoc: " + fileSize + " byte");
            System.out.println("SHA-256: " + actualHash);
            System.out.println("Da luu: " + destination.toAbsolutePath());

        } catch (Exception e) {
            System.out.println("Loi nhan file: " + e.getMessage());

            try {
                // Co the gui loi neu ket noi van con hoat dong
                // Server se dong ket noi sau khi xu ly loi.
            } catch (Exception ignored) {
            }

        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static boolean isSafeFileName(String name) {

        if (name == null || name.isBlank()
                || name.equals(".") || name.equals("..")) {
            return false;
        }

        // Khong chap nhan duong dan do Client chi dinh
        if (name.contains("/") || name.contains("\\")
                || name.contains("..")) {
            return false;
        }

        return !name.contains(":");
    }

    private static String sha256(Path file) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (var input = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int count;

            while ((count = input.read(buffer)) != -1) {
                digest.update(buffer, 0, count);
            }
        }

        return HexFormat.of().formatHex(digest.digest());
    }
}
