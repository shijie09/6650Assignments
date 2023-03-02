package Model;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.*;

public class ChannelPoolGenerator {
  private Connection connection;
  private BlockingQueue<Channel> pool;
  private final static int capacity = 100;
  private final static String QUEUE_NAME = "helloPool";

  public ChannelPoolGenerator() throws IOException, TimeoutException {
    System.out.println("test1");
    ConnectionFactory factory = new ConnectionFactory();
    System.out.println("test2");
    factory.setHost("localhost");
//    factory.setHost("50.35.82.93");
//    factory.setUsername("guest");
//    factory.setPassword("guest");

    try {
      this.connection = factory.newConnection();
    } catch (IOException | TimeoutException e) {
      System.err.println("Something Went Wrong in Connection");
      e.printStackTrace();
    }

    this.pool = new LinkedBlockingQueue<>();

    for (int i = 0; i < this.capacity; i++) {
      try {
        Channel channel = this.connection.createChannel();
//        channel.queueDeclare(this.QUEUE_NAME, false, false, false, null);
        this.pool.add(channel);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public Channel takeChannel() throws InterruptedException {
    return this.pool.take();
  }

  public void add(Channel channel) {
    this.pool.offer(channel);
  }
}