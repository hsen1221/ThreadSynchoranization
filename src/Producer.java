import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {

    // We use 'final' for best practices since these references won't change
    private final BlockingQueue<String> outputQueue;
    private final String fileName;
    private final int numberOfWorkers;

    public Producer(BlockingQueue<String> outputQueue, String fileName, int numberOfWorkers) {
        this.outputQueue = outputQueue;
        this.fileName = fileName;
        this.numberOfWorkers = numberOfWorkers;
    }

    @Override
    public void run() {
        System.out.println("Producer: Started reading " + fileName);

        // Using "try-with-resources" guarantees the file reader closes even if errors occur
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineCount = 0;

            // 1. Read the file line by line
            while ((line = br.readLine()) != null) {
                // put() is a blocking method. If the queue is full, it waits here.
                // This prevents the producer from reading the file faster than workers can process it.
                outputQueue.put(line);
                lineCount++;
            }

            System.out.println("Producer: Finished reading. Total lines sent: " + lineCount);

            // 2. Send Termination Signals (Poison Pill)
            // Crucial: We must add one "EOF" for EVERY worker thread.
            // If we have 4 workers, we must queue 4 "EOF" strings.
            // When a worker picks up an "EOF", it shuts itself down.
            for (int i = 0; i < numberOfWorkers; i++) {
                outputQueue.put("EOF");
            }
            System.out.println("Producer: Sent " + numberOfWorkers + " termination signals.");

        } catch (IOException e) {
            System.err.println("Producer Error: Could not read file " + fileName);
            e.printStackTrace();
        } catch (InterruptedException e) {
            // Restore interrupted state
            Thread.currentThread().interrupt();
            System.err.println("Producer Error: Interrupted while waiting to put data in queue.");
        }
    }
}