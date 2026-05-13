import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CheckHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        System.out.println("Admin@123: " + encoder.matches("Admin@123", hash));
        System.out.println("Staff@123: " + encoder.matches("Staff@123", hash));
        System.out.println("Customer@123: " + encoder.matches("Customer@123", hash));
        System.out.println("password: " + encoder.matches("password", hash));
        System.out.println("123456: " + encoder.matches("123456", hash));
    }
}
