
package Lab_4.Bai5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    public static void main(String[] args) {

        String host = "localhost";
        int port = 5001;

        try (
            Socket socket = new Socket(host, port);

            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream(), "UTF-8"));

            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);

            Scanner sc = new Scanner(System.in)
        ) {

            // Chon nickname
            System.out.print("Nhap nickname: ");
            String nickname = sc.nextLine().trim();

            out.println("NICK " + nickname);

            String response = in.readLine();

            if (response == null || !response.startsWith("OK NICK ")) {
                System.out.println("Khong the tham gia: " + response);
                return;
            }

            System.out.println("Da vao phong chat voi ten: " + nickname);
            System.out.println("Lenh: USERS, MSG noi_dung, QUIT");

            // Thread rieng de nhan tin nhan lien tuc
            Thread receiver = new Thread(() -> {
                try {
                    String message;

                    while ((message = in.readLine()) != null) {
                        System.out.println();
                        System.out.println(message);
                        System.out.print("> ");

                        if (message.equals("BYE")) {
                            break;
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Da ngat ket noi den Server.");
                }
            });

            receiver.setDaemon(true);
            receiver.start();

            // Thread chinh dung de gui lenh
            while (true) {
                System.out.print("> ");
                String command = sc.nextLine().trim();

                if (command.equalsIgnoreCase("QUIT")) {
                    out.println("QUIT");
                    break;
                }

                out.println(command);
            }

        } catch (Exception e) {
            System.out.println("Loi Client: " + e.getMessage());
        }
    }
}
