package KiemTraTK1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        try (
            Socket socket = new Socket(HOST, PORT);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true
            );
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("=== CHAT CLIENT ===");

            System.out.print("Nhap ten cua ban: ");
            String username = scanner.nextLine().trim();

            if (username.isEmpty() || username.contains("|")) {
                System.out.println("Ten khong hop le.");
                return;
            }

            out.println(username);

            // Nhan danh sach phong tu Server
            String response = in.readLine();

            if (response == null || !response.startsWith("ROOMS|")) {
                System.out.println("Khong nhan duoc danh sach phong.");
                return;
            }

            System.out.println("Da ket noi Server.");
            System.out.println(
                "Danh sach phong: " + response.substring(6)
            );

            // Luong rieng de nhan tin nhan lien tuc
            Thread receiver = new Thread(() -> {
                try {
                    String message;

                    while ((message = in.readLine()) != null) {

                        if (message.startsWith("CHAT|")) {
                            String[] parts = message.split("\\|", 3);

                            if (parts.length == 3) {
                                System.out.println(
                                    "\n[" + parts[1] + "]: " + parts[2]
                                );
                                System.out.print("> ");
                            }
                        }

                        else if (message.startsWith("INFO|")) {
                            System.out.println(
                                "\n" + message.substring(5)
                            );
                            System.out.print("> ");
                        }

                        else if (message.startsWith("ROOMS|")) {
                            System.out.println(
                                "\nDanh sach phong: "
                                + message.substring(6)
                            );
                            System.out.print("> ");
                        }

                        else if (message.startsWith("ERROR|")) {
                            System.out.println(
                                "\nLoi: " + message.substring(6)
                            );
                            System.out.print("> ");
                        }
                    }

                } catch (IOException e) {
                    System.out.println("\nDa dong ket noi den Server.");
                }
            });

            receiver.setDaemon(true);
            receiver.start();

            // Vong lap chon phong va chat
            while (true) {

                System.out.println("\n===== DANH SACH PHONG =====");
                System.out.println("1. Study");
                System.out.println("2. Game");
                System.out.println("3. TraiNghiem");
                System.out.println("Nhap ten phong hoac /exit.");

                System.out.print("Chon phong: ");
                String room = scanner.nextLine().trim();

                if (room.equalsIgnoreCase("/exit")) {
                    out.println("/exit");
                    break;
                }

                if (room.equals("1")) {
                    room = "Study";
                } else if (room.equals("2")) {
                    room = "Game";
                } else if (room.equals("3")) {
                    room = "TraiNghiem";
                }

                out.println("JOIN|" + room);

                System.out.println(
                    "Nhap tin nhan. /quit de roi phong, /exit de thoat."
                );

                while (true) {
                    System.out.print("> ");
                    String text = scanner.nextLine();

                    if (text.equals("/quit")) {
                        out.println("/quit");
                        break;
                    }

                    if (text.equals("/exit")) {
                        out.println("/exit");
                        return;
                    }

                    out.println("MSG|" + text);
                }
            }

        } catch (IOException e) {
            System.out.println(
                "Khong the ket noi Server: " + e.getMessage()
            );
        }
    }
}
