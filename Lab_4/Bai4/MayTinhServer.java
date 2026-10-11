
package Lab_4.Bai4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MayTinhServer {

    public static String xuLy(String request) {

        String[] parts = request.trim().split("\\s+");

        // Kiem tra dinh dang
        if (parts.length != 4 || !parts[0].equals("CALC")) {
            return "ERR INVALID_FORMAT";
        }

        String operator = parts[1];

        // Kiem tra toan tu
        if (!operator.equals("+")
                && !operator.equals("-")
                && !operator.equals("*")
                && !operator.equals("/")) {
            return "ERR UNSUPPORTED_OPERATOR";
        }

        double a;
        double b;

        // Kiem tra du lieu so
        try {
            a = Double.parseDouble(parts[2]);
            b = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            return "ERR INVALID_NUMBER";
        }

        double result;

        // Thuc hien phep tinh
        switch (operator) {
            case "+":
                result = a + b;
                break;

            case "-":
                result = a - b;
                break;

            case "*":
                result = a * b;
                break;

            case "/":
                if (b == 0) {
                    return "ERR DIVIDE_BY_ZERO";
                }
                result = a / b;
                break;

            default:
                return "ERR UNSUPPORTED_OPERATOR";
        }

        // Tra ket qua
        if (Double.isFinite(result)
                && result == Math.floor(result)
                && Math.abs(result) < 1e15) {
            return "OK " + (long) result;
        }

        return "OK " + result;
    }

    public static void main(String[] args) {

        int port = 5000;

        try (ServerSocket server = new ServerSocket(port)) {

            System.out.println("May tinh Server dang chay!");
            System.out.println("Port: " + port);
            System.out.println("Dang cho Client ket noi...");

            while (true) {

                Socket socket = server.accept();

                System.out.println("Client da ket noi!");

                try (
                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                            socket.getInputStream(), "UTF-8"));

                    PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
                ) {

                    String request;

                    while ((request = in.readLine()) != null) {

                        if (request.equalsIgnoreCase("QUIT")) {
                            break;
                        }

                        System.out.println("Nhan: " + request);

                        String response = xuLy(request);

                        out.println(response);

                        System.out.println("Tra: " + response);
                    }

                } finally {
                    socket.close();
                    System.out.println("Client da ngat ket noi.");
                }
            }

        } catch (Exception e) {
            System.out.println("Loi Server: " + e.getMessage());
        }
    }
}
