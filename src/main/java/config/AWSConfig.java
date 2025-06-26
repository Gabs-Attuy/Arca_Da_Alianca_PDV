/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author gabs
 */
public class AWSConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = AWSConfig.class.getClassLoader().getResourceAsStream("aws-credentials.properties")) {
            if (input == null) {
                System.out.println("Arquivo aws-credentials.properties não encontrado!");
            } else {
                props.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getAwsAccessKey() {
        return props.getProperty("AWS_ACCESS_KEY_ID");
    }

    public static String getAwsSecretKey() {
        return props.getProperty("AWS_SECRET_ACCESS_KEY");
    }

    public static String getAwsTopicArn() {
        return props.getProperty("AWS_TOPIC_ARN");
    }
}
