package com.fiorano.openesb.tools;

import java.io.*;

/**
 * Created by Janardhan on 4/11/2016.
 */
public class StreamRedirector extends Thread {
    // Input Stream
    private InputStream inputStream;
    private PrintStream outputStream;

    /**
     * Constructs a stream redirector
     *
     * @param in
     * @param out
     */
    public StreamRedirector(InputStream in, PrintStream out) {
        inputStream = in;
        outputStream = out;
        if (in != null)
            start();
    }


    /**
     * Main processing method for the StreamRedirector object
     */
    public void run() {
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = null;
        String str = "";
        //  manually redirect the process's input to the console
        try {
            reader = new BufferedReader(streamReader);
            while ((str = reader.readLine()) != null) {
                log(str);
            }
        } catch (Throwable thr) {
            log("<Error> Redirector quitting abruptly .... {0}");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Log the parameter string
     *
     * @param logStr
     */
    private void log(String logStr) {
        if (outputStream == null)
            return;
        outputStream.println(logStr);
    }

}

