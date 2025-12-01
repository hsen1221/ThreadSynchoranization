import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        // 1. Create the Blocking Queues
        // Queue 1: Buffer between Producer (Reader) and Workers.
        BlockingQueue<String> queue1 = new LinkedBlockingQueue<>();

        // Queue 2: Buffer between Workers and the final Writer.
        BlockingQueue<String> queue2 = new LinkedBlockingQueue<>();

        // 2. Configuration
        int numberOfWorkers = 4;
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        System.out.println(">>> Starting the Pipeline Architecture...");

        // 3. Start the Final Consumer (Writer)
        // We start the Writer first so it is ready to receive data immediately.
        // We pass 'numberOfWorkers' so the Writer knows how many "stop signals" to wait for.
        Thread writerThread = new Thread(new Writer(queue2, outputFile, numberOfWorkers));
        writerThread.setName("Writer-Thread");
        writerThread.start();

        // 4. Start the Multiple Consumers (Workers)
        // These threads will perform the actual text processing (Uppercasing) in parallel.
        for (int i = 0; i < numberOfWorkers; i++) {
            Thread workerThread = new Thread(new Worker(queue1, queue2));
            workerThread.setName("Worker-" + (i + 1)); // Naming threads helps with debugging
            workerThread.start();
        }

        // 5. Start the Producer
        // This begins the chain by reading the file and filling Queue 1.
        Thread producerThread = new Thread(new Producer(queue1, inputFile, numberOfWorkers));
        producerThread.setName("Producer-Thread");
        producerThread.start();

        System.out.println(">>> All threads launched. Processing...");
    }
}