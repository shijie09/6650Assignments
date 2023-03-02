import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

import java.util.concurrent.TimeoutException;

public class MultiThreads {
  private static final int numOfThread = 5;
  private static Connection connection;
  public static void main(String[] args) throws InterruptedException, IOException, TimeoutException {


    System.out.println("*********************************************************");
    System.out.println("Processing Begins");
    System.out.println("*********************************************************");

    ConnectionFactory factory = new ConnectionFactory();

    factory.setHost("34.219.55.238");

    connection = factory.newConnection();
    ProcessLikesAndDislikes processLikesAndDislikes = new ProcessLikesAndDislikes();
    for (int i = 0; i < numOfThread; i++) {
      Consumer1 consumer1 = new Consumer1(connection, processLikesAndDislikes
      );
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