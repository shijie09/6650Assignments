import Model.EventGenerator;
import Model.Event;
import Model.Processor;

import Model.Record;
import Model.RecordsAnalysis;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreads {
  private static String urlBase;
  private static AtomicInteger successReq;
  private static AtomicInteger failReq;
  private static BlockingQueue<Event> events;
  private static final int numOfThread = 500;
  private static final int totalReq = 500000;
  private static final int processorNumberEach = 1000;
  private static String pathName;
  public static void main(String[] args) throws InterruptedException {
//    urlBase = "http://localhost:8080/server2_war_exploded";
    urlBase = "http://my-alb-1984034483.us-west-2.elb.amazonaws.com/server2_war";
    urlBase = "http://lb-sevlet-1252734141.us-west-2.elb.amazonaws.com/hw1_war";
//    urlBase = "http://34.217.81.52:8080/server2_war";
    successReq = new AtomicInteger(0);
    failReq = new AtomicInteger(0);
    events = new LinkedBlockingQueue<>();
//    pathName = "/Users/xieshijie/Desktop/new6650/Assignment1/client2/src/main/java/output/MultThreadData.csv";
    List<Record> records = Collections.synchronizedList(new ArrayList<>());
    System.out.println("*********************************************************");
    System.out.println("Processing Begins");
    System.out.println("*********************************************************");

    CountDownLatch latch = new CountDownLatch(numOfThread);
    long start = System.currentTimeMillis();
    EventGenerator eventGenerator = new EventGenerator(events, totalReq);
    Thread generatorThread = new Thread(eventGenerator);
    generatorThread.start();

    for (int i = 0; i < numOfThread; i++) {
      Processor processor = new Processor(urlBase,successReq, failReq, processorNumberEach, events, latch, records);
      Thread thread = new Thread(processor);
      thread.start();
    }
    latch.await();
    long end = System.currentTimeMillis();
    long wallTime = end - start;
    RecordsAnalysis recordsAnalysis = new RecordsAnalysis(records);
    long maxLatency = recordsAnalysis.getMaxLatency();
    long minLatency = recordsAnalysis.getMinLatency();
    long percentitle99Latency = recordsAnalysis.get99PercentileLatency();
    double medianTime = recordsAnalysis.getMedianLatency();
    double meanLatency = recordsAnalysis.getMeanLatency();

//    try(BufferedWriter writer = new BufferedWriter(new FileWriter(pathName))) {
//      writer.write("Start Time, Request Type, Latency, Response Code"+ System.lineSeparator());
//      for(Record rec : records) {
//        writer.write(rec.toString() + System.lineSeparator());
//
//      }
//    } catch(FileNotFoundException fnfe) {
//      System.out.println("*** OOPS! A file was not found : " + fnfe.getMessage());
//      fnfe.printStackTrace();
//    } catch(IOException ioe) {
//      System.out.println("Something went wrong! : " + ioe.getMessage());
//      ioe.printStackTrace();
//    }

    System.out.println("*********************************************************");
    System.out.println("Processing Ends");
    System.out.println("*********************************************************");
    System.out.println("Number of successful requests :" + successReq.get());
    System.out.println("Number of failed requests :" + failReq.get());
    System.out.println("Mean Response Time :" + meanLatency);
    System.out.println("Median Response Time :" + medianTime);
    System.out.println("Min Response Time :" + minLatency);
    System.out.println("Max Response Time :" + maxLatency);
    System.out.println("Total wall time: " + wallTime);
    System.out.println("p99 (99th percentile) response time: " + percentitle99Latency);
    System.out.println( "Throughput: " + (int)((successReq.get() + failReq.get()) / (double)(wallTime / 1000)) + " requests/second");
    System.out.println("Number of threads: " + numOfThread);
  }
}
