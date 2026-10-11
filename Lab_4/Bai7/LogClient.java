
package Lab_4.Bai7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LogClient {

    private static final String HOST = "localhost";
    private static final int PORT = 7000;

    public static void main(String[] args) {

        try (
            Socket socket = new Socket(HOST, PORT);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(),
                            StandardCharsets.UTF_8));

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);

            Scanner sc = new Scanner(System.in)
        ) {

            // Gui HELLO truoc khi gui noi dung
            System.out.print("Nhap clientId: ");
            String clientId = sc.nextLine().trim();

            out.println("HELLO " + clientId);

            String response = in.readLine();
            System.out.println("Server: " + response);

            if (response == null || !response.startsWith("OK HELLO ")) {
                System.out.println("Khong the dang nhap.");
                return;
            }

            System.out.println("Nhap noi dung tin nhan.");
            System.out.println("Nhap QUIT de ket thuc.");

            while (true) {

                System.out.print("Tin nhan > ");
                String message = sc.nextLine();

                out.println(message);

                if (message.equals("QUIT")) {
                    System.out.println("Server: " + in.readLine());
                    break;
                }

                System.out.println("Server: " + in.readLine());
            }

        } catch (Exception e) {
            System.out.println("Loi Client: " + e.getMessage());
        }
    }
}
