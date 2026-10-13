
package KiemTraTK1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Dashboard {

    public static void main(String[] args) {
        String serverUrl = "http://localhost:5001/dashboard";

        System.out.println("==================================");
        System.out.println("       CHAT TCP DASHBOARD");
        System.out.println("==================================");

        try {
            URL url = new URL(serverUrl);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int status = connection.getResponseCode();

            if (status == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()
                        )
                );

                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                reader.close();
            } else {
                System.out.println(
                        "Dashboard chua san sang. HTTP: " + status
                );
            }

            connection.disconnect();

        } catch (Exception e) {
            System.out.println("Khong the ket noi Dashboard.");
            System.out.println(
                    "Hay kiem tra Server co ho tro HTTP dashboard khong."
            );
        }
    }
}
