package mk.ukim.finki.PIDP;_

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Exercise
{
    //Function ti find the biggest prime number in an array (parallel)
    private static int findBiggestPrimeParallel(int[] array, int numThreads)
    {
        // Create a ThreadPoolExecutor
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        // Divide the array into chunks and submit tasks
        int chunkSize = (int) Math.ceil(array.length / (double) numThreads);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++)
        {
            int start = i * chunkSize;
            int end = Math.min(array.length, start + chunkSize);

            // Submit a task for each chunk
            futures.add(executorService.submit(() ->
            {
                int largestPrimeInChunk = 0;
                for (int j = start; j < end; j++)
                {
                    if (isPrime(array[j]) && array[j] > largestPrimeInChunk)
                    {
                        largestPrimeInChunk = array[j];
                    }
                }
                return largestPrimeInChunk; // Return the largest prime in the chunk
            }));
        }

        // Collect results from all chunks
        int overallLargestPrime = 0;
        for (Future<Integer> future : futures)
        {
            try
            {
                int largestPrimeInChunk = future.get();
                if (largestPrimeInChunk > overallLargestPrime)
                {
                    overallLargestPrime = largestPrimeInChunk;
                }
            } catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        }

        // Shutdown the executor
        executorService.shutdown();

        return overallLargestPrime;

    }

    //Function ti find the biggest prime number in an array (sequential)
    private static int findBiggestPrimeSequential(int[] array)
    {
        int largestPrime = -1;
        for (int number : array)
        {
            if (isPrime(number) && number > largestPrime)
            {
                largestPrime = number;
            }
        }
        return largestPrime;
    }

    public static void main(String[] args)
    {
        // Generate a large test case
        int arraySize = 1_000_000_000;
        int[] array = generateRandomArray(arraySize);

        //Number of threads
        int numThreads = 12;

        long startTime = System.currentTimeMillis();
        int largestPrimeSequential = findBiggestPrimeSequential(array);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;


        System.out.println("Largest Prime Sequential: " + largestPrimeSequential);
        System.out.println("Sequential Execution Time: " + elapsedTime + " ms");


        startTime = System.currentTimeMillis();
        int largestPrimeParallel = findBiggestPrimeParallel(array, numThreads);
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;

        // Print the largest prime
        System.out.println("Largest Prime Parallel: " + largestPrimeParallel);
        System.out.println("Parallel Execution Time: " + elapsedTime + " ms");
    }

    //Function to check if a number is prime
    public static boolean isPrime(int num)
    {
        if (num <= 1) return false;
        for (int i = 2; i <= Math.sqrt(num); i++)
        {
            if (num % i == 0) return false;
        }
        return true;
    }

    // Function to generate a random array of integers
    public static int[] generateRandomArray(int size)
    {
        Random rand = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++)
        {
            array[i] = rand.nextInt(100000); // Random number between 0 and 100,000
        }
        return array;
    }
}
