import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class ConsoleReaderDemo {

    public static void main(String[] args) {

        // Ép output của Java sử dụng UTF-8
        System.setOut(new java.io.PrintStream(
                new java.io.FileOutputStream(java.io.FileDescriptor.out),
                true,
                StandardCharsets.UTF_8
        ));

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));

        int count = 0;

        System.out.println("Nhập văn bản; nhập q để kết thúc:");

        try {
            while (true) {

                String line = reader.readLine();

                if (line == null || line.equalsIgnoreCase("q")) {
                    break;
                }

                count++;

                System.out.printf("Dòng %d: %s%n", count, line);
            }

        } catch (IOException e) {
            System.err.println("Không thể đọc dữ liệu: " + e.getMessage());
        }

        System.out.println("Tổng số dòng đã nhập: " + count);
    }
}