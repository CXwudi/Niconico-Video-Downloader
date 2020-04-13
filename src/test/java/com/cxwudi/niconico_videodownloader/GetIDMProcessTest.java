package com.cxwudi.niconico_videodownloader;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

class GetIDMProcessTest {

    @Test
    void getProcess() {
        try {
            String line;
            Process p = Runtime.getRuntime().exec
                    (System.getenv("windir") +"\\system32\\"+"tasklist.exe /v /FO:CSV /FI \"imagename eq IDMan.exe\" /nh");
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line); //<-- Parse data here.
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }


    }

}