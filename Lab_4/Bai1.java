package Lab_4;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class Bai1 {

    public static void main(String[] args) {

        // Kiểm tra số lượng tham số
        // args[0] = hostname
        // args[1] = URI
        if (args.length != 2) {
            System.out.println(
                "Usage: java Lab_4.Bai1 <hostname> <URI>"
            );
            return;
        }

        String hostname = args[0];
        String uriText = args[1];

        // =========================
        // PHẦN 1: KIỂM TRA HOSTNAME
        // =========================

        try {

            InetAddress[] addresses =
                    InetAddress.getAllByName(hostname);

            System.out.println("===== HOST =====");
            System.out.println("Hostname: " + hostname);

            // In tất cả địa chỉ IP
            for (InetAddress address : addresses) {

                System.out.println(
                    "- IP: " + address.getHostAddress()
                );

                // Xác định IPv4 hoặc IPv6
                if (address instanceof Inet4Address) {

                    System.out.println("  Type: IPv4");

                } else if (address instanceof Inet6Address) {

                    System.out.println("  Type: IPv6");
                }

                // Kiểm tra Loopback
                System.out.println(
                    "  Loopback: "
                    + address.isLoopbackAddress()
                );

                // Kiểm tra Site local
                System.out.println(
                    "  Site local: "
                    + address.isSiteLocalAddress()
                );
            }

        } catch (UnknownHostException e) {

            System.err.println(
                "Không phân giải được hostname: "
                + hostname
            );

            return;
        }

        // =========================
        // PHẦN 2: PHÂN TÍCH URI
        // =========================

        try {

            URI uri = new URI(uriText);

            System.out.println();
            System.out.println("===== URI =====");

            System.out.println("URI: " + uri);

            System.out.println(
                "Scheme: " + uri.getScheme()
            );

            System.out.println(
                "Host: " + uri.getHost()
            );

            System.out.println(
                "Port: " + uri.getPort()
            );

            System.out.println(
                "Path: " + uri.getPath()
            );

            System.out.println(
                "Query: " + uri.getQuery()
            );

            System.out.println(
                "Fragment: " + uri.getFragment()
            );

        } catch (URISyntaxException e) {

            System.err.println(
                "URI không hợp lệ: " + uriText
            );
        }
    }
}