package com.example.letsdoit;


import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.util.HashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileReader;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * ProfanityDetector detects the bad words and filters the inappropriate words
 */
public class ProfanityDetector {

    static int largestWordLength = 0;

    /**
     * Filters the text in a case where the entry contains inappropriate words
     * @param s the entry
     * @return the text that is filtered
     */
    public static String getFilteredText(String s) {

        ArrayList<String> words = new ArrayList<String>();

        words.add( "aptal");
        words.add( "salak");
        words.add( "mal");
        words.add( "gerizekal˝");
        words.add( "geri zekal˝");
        words.add( "retard");
        words.add( "asshole");
        words.add( "bitch");
        words.add( "idiot");
        for( String word : words)
        {
            Pattern rx = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
            s = rx.matcher(s).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
        }
        return s;
    }

}



