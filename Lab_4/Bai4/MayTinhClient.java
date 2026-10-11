
package Lab_4.Bai4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MayTinhClient {

    public static void main(String[] args) {

        String host = "localhost";
        int port = 5000;

        try (
            Socket socket = new Socket(host, port);

            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream(), "UTF-8"));

            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);

            Scanner sc = new Scanner(System.in)
        ) {

            System.out.println("Da ket noi den Server!");
            System.out.println("Nhap: CALC toan_tu so1 so2");
            System.out.println("Vi du: CALC + 100 200");
            System.out.println("Nhap QUIT de thoat.");

            while (true) {

                System.out.print("Client > ");

                String request = sc.nextLine();

                // Gui yeu cau den Server
                out.println(request);

                if (request.equalsIgnoreCase("QUIT")) {
                    break;
                }

                // Nhan phan hoi tu Server
                String response = in.readLine();

                if (response == null) {
                    System.out.println("Server da dong ket noi!");
                    break;
                }

                System.out.println("Server > " + response);
            }

        } catch (Exception e) {
            System.out.println("Loi Client: " + e.getMessage());
        }
    }
}
