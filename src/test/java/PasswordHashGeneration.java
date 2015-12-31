import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Yuriy Tumakha
 */
public class PasswordHashGeneration {

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("TestPassword"));
    }

}