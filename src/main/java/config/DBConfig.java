package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author jeffe
 */
public class DBConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = DBConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Arquivo config.properties não encontrado!");
            } else {
                props.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getUrl() {
        return props.getProperty("db.url");
    }

    public static String getUser() {
        return props.getProperty("db.user");
    }

    public static String getPassword() {
        return props.getProperty("db.password");
    }
}
