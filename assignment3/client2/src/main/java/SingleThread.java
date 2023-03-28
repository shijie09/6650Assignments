import Model.EventGenerator;
import Model.Event;
import Model.Processor;

import Model.Record;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
  private static List<Record> records;
  private static String pathName;

  public static <CSVWriter> void main(String[] args) throws InterruptedException {
//    urlBase = "https://virtserver.swaggerhub.com/IGORTON/Twinder/1.0.0";
//    urlBase = "http://localhost:8080/hw1_war_exploded/";
    urlBase = "http://54.191.26.40:8080/hw1_war";
    successReq = new AtomicInteger(0);
    failReq = new AtomicInteger(0);
    events = new LinkedBlockingQueue<>();
    pathName = "/Users/xieshijie/Desktop/new6650/client2/src/main/java/output/SingleThreadData.csv";
    records = new ArrayList<>();
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
    Processor processor = new Processor(urlBase, successReq, failReq, totalReq, events, latch, records);
    Thread thread = new Thread(processor);
    thread.start();

    latch.await();
    double sumNum = 0;
    long end = System.currentTimeMillis();
    long wallTime = end - start;

    try(BufferedWriter writer = new BufferedWriter(new FileWriter(pathName))) {
      writer.write("Start Time, Request Type, Latency, Response Code"+ System.lineSeparator());
      for(Record rec : records) {
        writer.write(rec.toString() + System.lineSeparator());
        sumNum += rec.getLatency();

      }
    } catch(FileNotFoundException fnfe) {
      System.out.println("*** OOPS! A file was not found : " + fnfe.getMessage());
      fnfe.printStackTrace();
    } catch(IOException ioe) {
      System.out.println("Something went wrong! : " + ioe.getMessage());
      ioe.printStackTrace();
    }
    double meanTime = sumNum / (double)(records.size());
    System.out.println("*********************************************************");
    System.out.println("Processing Ends");
    System.out.println("*********************************************************");
    System.out.println("Number of successful requests :" + successReq.get());
    System.out.println("Number of failed requests :" + failReq.get());
    System.out.println("Mean Time :" + meanTime);
    System.out.println("Total wall time: " + wallTime);
    System.out.println( "Throughput: " + (int)((successReq.get() + failReq.get()) / (double)(wallTime / 1000)) + " requests/second");
  }
}

