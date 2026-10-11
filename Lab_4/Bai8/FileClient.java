
package Lab_4.Bai8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Scanner;

public class FileClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8000;

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in)) {

            System.out.print("Nhap duong dan file can gui: ");
            String input = sc.nextLine().trim();

            Path file = Path.of(input).toAbsolutePath().normalize();

            if (!Files.isRegularFile(file)) {
                System.out.println("File khong ton tai!");
                return;
            }

            String fileName = file.getFileName().toString();
            long fileSize = Files.size(file);
            String hash = sha256(file);

            System.out.println("Ten file: " + fileName);
            System.out.println("Kich thuoc: " + fileSize + " byte");
            System.out.println("SHA-256: " + hash);

            try (
                Socket socket = new Socket(HOST, PORT);
                DataOutputStream out = new DataOutputStream(
                        socket.getOutputStream());
                DataInputStream in = new DataInputStream(
                        socket.getInputStream());
                var fileIn = Files.newInputStream(file)
            ) {

                // 1. Gui metadata truoc
                out.writeUTF(fileName);
                out.writeLong(fileSize);
                out.writeUTF(hash);
                out.flush();

                // 2. Gui noi dung file
                byte[] buffer = new byte[8192];
                int count;

                while ((count = fileIn.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }

                out.flush();

                // 3. Nhan ket qua tu Server
                String response = in.readUTF();

                System.out.println("Phan hoi Server: " + response);

                if (response.equals("OK")) {
                    System.out.println("Truyen file thanh cong!");
                } else {
                    System.out.println("Truyen file that bai!");
                }
            }

        } catch (Exception e) {
            System.out.println("Loi Client: " + e.getMessage());
        }
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
