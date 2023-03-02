import Model.EventGenerator;
import Model.Event;
import Model.Processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreads {
  private static String urlBase;
  private static AtomicInteger successReq;
  private static AtomicInteger failReq;
  private static BlockingQueue<Event> events;
  private static final int numOfThread = 100;
  private static final int totalReq = 500000;
  private static final int processorNumberEach = 5000;

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

    CountDownLatch latch = new CountDownLatch(numOfThread);
    long start = System.currentTimeMillis();
    EventGenerator eventGenerator = new EventGenerator(events, totalReq);
    Thread generatorThread = new Thread(eventGenerator);
    generatorThread.start();

    for (int i = 0; i < numOfThread; i++) {
      Processor processor = new Processor(urlBase,successReq, failReq, processorNumberEach, events, latch);
      Thread thread = new Thread(processor);
      thread.start();
    }
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
    System.out.println("Total threads: " + numOfThread);
    System.out.println( "Throughput: " + (int)((successReq.get() + failReq.get()) / (double)(wallTime / 1000)) + " requests/second");
  }
}
