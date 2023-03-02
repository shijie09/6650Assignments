import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

import java.util.concurrent.TimeoutException;

public class MultiThreads {
  private static final int numOfThread = 5;
  private static Connection connection;
  private static String userId = "123";
  public static void main(String[] args) throws InterruptedException, IOException, TimeoutException {


    System.out.println("*********************************************************");
    System.out.println("Processing Begins");
    System.out.println("*********************************************************");

    ConnectionFactory factory = new ConnectionFactory();

    factory.setHost("34.219.55.238");
    connection = factory.newConnection();
    ProcessSwipees processSwipees = new ProcessSwipees();
    for (int i = 0; i < numOfThread; i++) {
      Consumer2 consumer2 = new Consumer2(connection,
          processSwipees);
      Thread thread = new Thread(consumer2);
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