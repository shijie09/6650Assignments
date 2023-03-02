import Model.EventGenerator;
import Model.Event;
import Model.Processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleThread {

  private static String urlBase;
  private static AtomicInteger successReq;
  private static AtomicInteger failReq;
  private static BlockingQueue<Event> events;
  private static int totalReq = 10000;

  public static void main(String[] args) throws InterruptedException {
//    urlBase = "https://virtserver.swaggerhub.com/IGORTON/Twinder/1.0.0";
//    urlBase = "http://localhost:8080/hw1_war_exploded/";
    urlBase = "http://54.191.26.40:8080/hw1_war";
    successReq = new AtomicInteger(0);
    failReq = new AtomicInteger(0);
    events = new LinkedBlockingQueue<>();

    System.out.println("*********************************************************");
    System.out.println("Processing Begins");
    System.out.println("*********************************************************");
    long start = System.currentTimeMillis();
    EventGenerator eventGenerator = new EventGenerator(events, totalReq);
    eventGenerator.run(); //mark run the generator so that the events is not empty
    Thread generatorThread = new Thread(eventGenerator);
    generatorThread.start();
// new a processor to process the events_queue.
    CountDownLatch latch = new CountDownLatch(1);
    Processor processor = new Processor(urlBase, successReq, failReq, totalReq, events, latch);
    Thread thread = new Thread(processor);
    thread.start();
    latch.await();

    long end = System.currentTimeMillis();
    long wallTime = end - start;

    System.out.println("*********************************************************");
    System.out.println("Processing Ends");
    System.out.println("*********************************************************");
    System.out.println("Number of total requests :" + totalReq);
    System.out.println("Number of successful requests :" + successReq.get());
    System.out.println("Number of failed requests :" + failReq.get());
    System.out.println("Total wall time: " + wallTime);
    System.out.println( "Throughput: " + (int)((successReq.get() + failReq.get()) / (double)(wallTime / 1000)) + " requests/second");
    System.out.println( "Average Response time: " + (double)(wallTime / 1000)/ (successReq.get() + failReq.get()) + " requests/second");
  }
}

