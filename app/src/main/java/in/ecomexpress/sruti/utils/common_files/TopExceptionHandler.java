package in.ecomexpress.sruti.utils.common_files;

import java.util.*;
import java.io.*;

/*class Main {

    public static int LongestIncreasingSequence(int[] arr) {
        // code goes here
        int number = arr.length;
        int lis[] = new int[number];
        int i, j, max = 0;
        for(i=0;i< number;i++)
            lis[i]=1;
        for (i = 1; i < number; i++)
            for (j = 0; j < i; j++)
                if (arr[i] > arr[j] && lis[i] < lis[j] + 1)
                    lis[i] = lis[j] + 1;

        for (i = 0; i < number; i++)
            if (max < lis[i])
                max = lis[i];
        return max;
    }

    public static void main (String[] args) {
        // keep this function call here
        Scanner s = new Scanner(System.in);
        System.out.print(LongestIncreasingSequence(s.nextLine()));
    }

}*/


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

import static in.ecomexpress.sruti.utils.common_files.Logger.errorLogging;

public class TopExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;
    private Activity app = null;
    private String line;

    public TopExceptionHandler(Activity app) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
    }

    public void uncaughtException(Thread t, Throwable e) {
        StackTraceElement[] arr = e.getStackTrace();
        String report = e.toString() + "\n\n";
        report += "--------- Stack trace ---------\n\n";
        for (int i = 0; i < arr.length; i++) {
            report += "    " + arr[i].toString() + "\n";
        }
        report += "-------------------------------\n\n";

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause

        report += "--------- Cause ---------\n\n";

        Throwable cause = e.getCause();
        if (cause != null) {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (int i = 0; i < arr.length; i++) {
                report += "    " + arr[i].toString() + "\n";
            }
        }
        report += "-------------------------------\n\n";

        try {
            FileOutputStream trace = app.openFileOutput("stack.trace", Context.MODE_PRIVATE);
            trace.write(report.getBytes());
            trace.close();

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"dipaksikka@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "crash report azar");
            String body = "Mail this to dipaksikka@gmail.com: " + "\n" + trace + "\n";
            i.putExtra(Intent.EXTRA_TEXT, body);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                 Toast.makeText(app, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException ioe) {
            // ...
        }

        defaultUEH.uncaughtException(t, e);
    }


    private void startActivity(Intent chooser) {
    }
}