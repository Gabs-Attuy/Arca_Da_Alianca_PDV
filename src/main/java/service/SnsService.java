/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import config.AWSConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

/**
 *
 * @author gabs
 */
public class SnsService {

    private final SnsClient snsClient;
    private final String topicArn = AWSConfig.getAwsTopicArn();

    public SnsService() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                AWSConfig.getAwsAccessKey(),
                AWSConfig.getAwsSecretKey()
        );

        this.snsClient = SnsClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public void enviarMensagem(String mensagem) {
        PublishRequest request = PublishRequest.builder()
                .message(mensagem)
                .topicArn(topicArn)
                .build();

        PublishResponse response = snsClient.publish(request);
        System.out.println("Mensagem enviada! ID: " + response.messageId());
    }
}