public class Product {

    private final String code;
    private final String name;
    private final double unitPrice;
    private final int quantity;

    public Product(String code, String name,
                   double unitPrice, int quantity) {

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Mã sản phẩm không được rỗng"
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Tên sản phẩm không được rỗng"
            );
        }

        if (unitPrice <= 0 || quantity < 0) {
            throw new IllegalArgumentException(
                    "Giá hoặc số lượng không hợp lệ"
            );
        }

        this.code = code;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public double inventoryValue() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return "%s - %s: %,.0f VND".formatted(
                code,
                name,
                inventoryValue()
        );
    }

    // Dùng để Debug riêng Product
    public static void main(String[] args) {

        System.setOut(new java.io.PrintStream(
                new java.io.FileOutputStream(
                        java.io.FileDescriptor.out
                ),
                true,
                java.nio.charset.StandardCharsets.UTF_8
        ));

        Product product = new Product(
                "SP01",
                "Bút bi",
                5000,
                10
        );

        System.out.println("Thông tin sản phẩm:");
        System.out.println(product);

        System.out.println(
                "Giá trị tồn kho: "
                + product.inventoryValue()
                + " VND"
        );
    }
}