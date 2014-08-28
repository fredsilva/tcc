/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Frederico da Silva Santos
 */
public class SystemInfo {

    public String getHardware() throws IOException {
        BufferedReader output = null;
        Process process = null;
        String hardware = new String();
        if (System.getProperty("os.name").equals("Linux")){
            process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
        }
        
        output = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader br = output;
        String linhaDoBuffer = null;
        try {
            while ((linhaDoBuffer = br.readLine()) != null) {
                try {
                    if (linhaDoBuffer.contains("model name")) {
                        hardware = linhaDoBuffer.substring(12);                                                
                        break;
                    }
                } catch (IndexOutOfBoundsException e) {

                }                
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return hardware;
    }
}
