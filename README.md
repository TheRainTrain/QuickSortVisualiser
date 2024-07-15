# QuickSort Visualiser
Visualise quicksort. In Java.  
Disclaimer: This is not an accurate representation of the speed of quicksort due to the
usage of a monitor ensuring we render the sorting through each iteration as it happens.
Even with 0ms delay, we still wait for our """draw-calls""" (not actual draw-calls to
the GPU, we're using Swing here) to finish.

## Build
Get Maven and run `mvn install`.

## Running
After you build, just go into the target directory and run the jar file, either with
`java -jar QuickSortVisualiser-1.0.0-SNAPSHOT.jar` or by double-clicking it if you've
set up your operating system to open .jar files that way.

## Conclusion
This was fun. You can edit the parameters to the
[Graphics#drawLine(int, int, int, int)](https://docs.oracle.com/javase/8/docs/api/java/awt/Graphics.html#drawLine-int-int-int-int-)
call near the end of the single .java file in this project to visualise the sorting in
different, perhaps eccentric, ways.
