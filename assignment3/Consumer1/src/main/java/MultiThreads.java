
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.util.concurrent.TimeoutException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;


public class MultiThreads {
  private static final int numOfThread = 100;
  private static Connection connection;
  public static void main(String[] args) throws InterruptedException, IOException, TimeoutException {


    System.out.println("*********************************************************");
    System.out.println("Processing Begins");
    System.out.println("*********************************************************");

    ConnectionFactory factory = new ConnectionFactory();

    factory.setHost("34.222.139.42");
    factory.setHost("localhost");
    connection = factory.newConnection();

    AwsCredentialsProvider credentialsProvider = SystemPropertyCredentialsProvider.create();
    System.setProperty("aws.accessKeyId", "ASIA32MRRSJ3XNJQXLDH");
    System.setProperty("aws.secretAccessKey", "+/pqMWJuKMuxNkKmMYU93SR0+Q7PPtCSRmCp6gAa");
    System.setProperty("aws.sessionToken", "FwoGZXIvYXdzEIP//////////wEaDL0jExt9rX2QcC3ENCLJAWYUME4xjCRzmFl0rwuZ2XEphPExUPYPifWA3tfehzI9QKtAV4YGBj4bvyxDzUYWRWeT5KOaSVRcKgLun/w/++iAASiaNJyzER952DZJMDpIOEcpF5i9d60WBub/KeaFdKjT2IdgEas1ZDr6hcx02M6UdKuC2tgGKH0gbQp+iUSrB0wc4c3V0PIqINeX6QT5ahhk/Y0zMqNozZXC3mLuiWWwuawRnZsOIY9a9j0Iaufo3FcrVvMQpfA+hvi2SBHOLcN63eSx3dKuqCivyoyhBjItO5uYZehA196zBqTkQI/BIIJcxl5LS1gLW9rtCXaUDYd1mJFpiglSNSLIf/5v");

    DynamoDbClient client1 = DynamoDbClient.builder()
        .credentialsProvider(credentialsProvider)
        .region(Region.US_WEST_2)
        .build();
    DynamoDbClient client2 = DynamoDbClient.builder()
        .credentialsProvider(credentialsProvider)
        .region(Region.US_WEST_2)
        .build();

    for (int i = 0; i < numOfThread; i++) {
      Consumer1 consumer1 = new Consumer1(connection, client1, client2);
      Thread thread = new Thread(consumer1);
      thread.start();
      System.out.println("start  " + i);
    }
    System.out.println("finished thread looping");
    System.out.println("finished thread await");



    System.out.println("*********************************************************");
    System.out.println("Processing Ends");
    System.out.println("*********************************************************");




  }
}