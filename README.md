We chose the option1 to solve from the homework.

We will use LinkedBlockingQueue which is a blocking queue because for example when queue 1 is empty, the consumers will wait until the preoducer thread puts data in the queue1 and It handles the synchronization safely, so multiple threads can pull from it simultaneously without error.

We created an intellij project because we don’t need the complexities of the maven and gradle.

We will perform uppercasing the letters from input.txt file and print the uppercase letters in an output.txt file with each thread did what.
