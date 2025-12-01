import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Writer implements Runnable {

    private final BlockingQueue<String> inputQueue;
    private final String fileName;
    private final int numberOfWorkers;

    public Writer(BlockingQueue<String> inputQueue, String fileName, int numberOfWorkers) {
        this.inputQueue = inputQueue;
        this.fileName = fileName;
        this.numberOfWorkers = numberOfWorkers;
    }

    @Override
    public void run() {
        System.out.println("Writer: Started listening for results...");

        // We use BufferedWriter for better performance when writing to disk
        // The try-with-resources block ensures the file is automatically closed and saved
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {

            int stopSignalsReceived = 0;

            while (true) {
                // 1. Retrieve the processed result from Queue 2
                String line = inputQueue.take();

                // 2. Logic to handle the "Poison Pill" (EOF)
                if (line.equals("EOF")) {
                    stopSignalsReceived++;

                    // Critical Check:
                    // Since we have multiple workers running in parallel, we will receive multiple "EOF" strings.
                    // We must NOT close the file until we have received exactly one "EOF" from EACH worker.
                    if (stopSignalsReceived == numberOfWorkers) {
                        System.out.println("Writer: Received all " + numberOfWorkers + " stop signals. Closing file.");
                        break; // All data has been written. Break the loop to close the file.
                    }

                    // If we haven't received all signals yet, just continue the loop
                    // and wait for the other workers to finish.
                    continue;
                }

                // 3. Write the actual data to the file
                bw.write(line);
                bw.newLine(); // Add a line break so the output is formatted correctly
            }

        } catch (IOException e) {
            System.err.println("Writer Error: Could not write to " + fileName);
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Writer Error: Interrupted.");
        }
    }
}