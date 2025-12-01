import java.util.concurrent.BlockingQueue;

public class Worker implements Runnable {

    private final BlockingQueue<String> inputQueue;
    private final BlockingQueue<String> outputQueue;

    public Worker(BlockingQueue<String> inputQueue, BlockingQueue<String> outputQueue) {
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();

        try {
            while (true) {
                // 1. Retrieve data (Blocking)
                // .take() will wait efficiently if the queue is empty.
                String line = inputQueue.take();

                // 2. Check for Termination Signal (Poison Pill)
                if (line.equals("EOF")) {
                    // IMPORTANT: Pass the signal to the next stage (The Writer).
                    // The Writer needs to know that *this specific* worker has finished.
                    outputQueue.put("EOF");

                    System.out.println(threadName + ": Received stop signal. Terminating.");
                    break; // Exit the loop to stop the thread
                }

                // 3. Process the Data (The Actual Job)
                // Convert text to Uppercase.
                // We add the thread name to the string so we can see which thread processed it in the output file.
                String processedLine = line.toUpperCase() + " [" + threadName + "]";

                // 4. Send result to the next stage
                outputQueue.put(processedLine);
            }
        } catch (InterruptedException e) {
            // Good practice: restore the interrupted status
            Thread.currentThread().interrupt();
            System.err.println(threadName + ": Interrupted.");
        }
    }
}