package me.rainenjoyer.quicksortvisualiser;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class QuickSortVisualiser implements Runnable {
    private static final int UPPER_ELEMENT_BOUND = 1500;

    private int pivot;
    private int low = 0;
    private int high = 1;

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(new QuickSortVisualiser());
        } catch(Exception e) {
            handleException(e);
        }
    }

    @Override
    public void run() {
        int limit = inputInt("Limit:", 3000);
        int delay = inputInt("Delay (milliseconds):", 0);
        int[] array = getArray(limit);

        System.out.println(Arrays.toString(array));

        JFrame frame = new JFrame("QuickSort Visualiser");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        AtomicReference<BufferedImage> buffer = new AtomicReference<>();
        buffer.set(new BufferedImage(limit, UPPER_ELEMENT_BOUND, BufferedImage.TYPE_INT_ARGB));

        new Thread(() -> {
            Object sync = new Object();

            new Thread(() -> quicksort(sync, array, 0, array.length - 1)).start();

            long lastCall = System.currentTimeMillis();

            boolean done = false;
            while(true) {
                if(!done && low >= high || low < 0) {
                    done = true;

                    this.low = 0;
                    this.high = array.length;

                    this.pivot = -1;

                    System.out.println(Arrays.toString(array));
                }

                synchronized(sync) {
                    sync.notify();
                }

                BufferedImage flip = new BufferedImage(limit, UPPER_ELEMENT_BOUND, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = flip.getGraphics();
                displayArray(array, pivot, graphics, Color.LIGHT_GRAY, Color.WHITE, Color.GREEN);

                buffer.set(flip);

                if(done)
                    break;

                try {
                    Thread.sleep(Math.max(0, delay - (System.currentTimeMillis() - lastCall)));
                    lastCall = System.currentTimeMillis();
                } catch(Exception e) {
                    handleException(e);
                }
            }
        }).start();


        new Thread(() -> {
            while(true) {
                frame.getGraphics().drawImage(buffer.get(), 0, 0, frame.getWidth(), frame.getHeight(), frame);

                try {
                    Thread.sleep(33);
                } catch(InterruptedException e) {
                    handleException(e);
                }
            }
        }).start();
    }

    private static void handleException(Exception e) {
        e.printStackTrace();

        JOptionPane.showMessageDialog(null, e.getMessage(), e.getClass().getSimpleName(),
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    private void quicksort(Object sync, int[] array, int low, int high) {
        this.low = low;
        this.high = high;


        if(low >= high || low < 0)
            return;

        synchronized(sync) {
            int pivot = partition(array, low, high);
            this.pivot = pivot;

            try {
                sync.wait();
            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }

            quicksort(sync, array, low, pivot - 1);
            quicksort(sync, array, pivot + 1, high);
        }
    }

    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low;

        for(int j = low; j < high; j++) {
            if(array[j] <= pivot) {
                int oldI = array[i];

                array[i] = array[j];
                array[j] = oldI;

                i++;
            }
        }

        int oldI = array[i];

        array[i] = array[high];
        array[high] = oldI;

        return i;
    }

    private int inputInt(Object message, Object initialInput) {
        try {
            String input = JOptionPane.showInputDialog(message, initialInput);
            if(input == null)
                System.exit(0);

            return Integer.parseInt(input);
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid integer!");
            return inputInt(message, initialInput);
        }
    }

    private int[] getArray(int limit) {
        Random random = new Random();

        int[] array = null;
        try {
            array = new int[limit];
        } catch(NegativeArraySizeException e) {
            handleException(e);
        }

        for(int i = 0; i < limit; i++)
            array[i] = random.nextInt(UPPER_ELEMENT_BOUND);

        return array;
    }

    private void displayArray(int[] array, int pivot, Graphics graphics, Color unfocusedColor, Color regularColor,
                              Color pivotColor) {
        graphics.clearRect(0, 0, array.length, UPPER_ELEMENT_BOUND);

        for(int i = 0; i < array.length; i++) {
            int elemHeight = (int)(array[i] * .965f);

            int x = i;
            int y = UPPER_ELEMENT_BOUND - elemHeight;

            if(i == pivot)
                graphics.setColor(pivotColor);
            else if(i < low || i > high)
                graphics.setColor(unfocusedColor);
            else
                graphics.setColor(regularColor);

            graphics.drawLine(x, y, x, y + elemHeight);
        }
    }
}