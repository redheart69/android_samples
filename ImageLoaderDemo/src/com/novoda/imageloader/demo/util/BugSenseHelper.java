package com.novoda.imageloader.demo.util;

import android.content.Context;
//import com.bugsense.trace.BugSenseHandler;
import java.io.IOException;
import java.io.InputStream;

public class BugSenseHelper {

    private Context context;
    private boolean isRunning;

    public BugSenseHelper(Context context) {
        this.context = context;
    }

    public void initBugSense() throws BugsenseApiKeyFailedException {
        try {
            try {
                initBugSenseIfKeyIsAvailable();
                isRunning = true;
            } catch (IOException e) {
                throw new BugsenseApiKeyFailedException(e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            throw new BugsenseApiKeyFailedException(e.getMessage());
        }
    }

    private void initBugSenseIfKeyIsAvailable() throws IOException {
            String key = getApiKeyFromAssets();
//            BugSenseHandler.initAndStartSession(context, key);
    }

    private String getApiKeyFromAssets() throws IOException {
        InputStream inputStream = context.getAssets().open("bugsense_api_key.txt");
        String key = readInputStream(inputStream);
        return key.trim();
    }

    private String readInputStream(InputStream in) throws IOException {
        StringBuffer stream = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            stream.append(new String(b, 0, n));
        }

        return stream.toString();
    }

    public void closeBugsense() {
        if (isRunning) {
//            BugSenseHandler.closeSession(context);
        }
    }


}
