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
        if (System.getProperty("os.name").equals("Linux")){
            process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
        }
        
        output = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader br = output;
        String linhaDoBuffer = null;
        String processor, cache, cores;
        processor = cache = cores = new String();        
        try {
            while ((linhaDoBuffer = br.readLine()) != null) {
                try {
                    if (linhaDoBuffer.contains("model name")) {
                        processor = linhaDoBuffer.substring(12).trim();                                                                        
                    }
                    if (linhaDoBuffer.contains("cache size")){
                        cache = linhaDoBuffer.substring(12).trim();                             
                    }
                    if(linhaDoBuffer.contains("cpu cores")){
                        cores = linhaDoBuffer.substring(12).trim();
                    }
                } catch (IndexOutOfBoundsException e) {
                    return "Hardware unidentified";
                }                
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return processor+", Cores("+cores+") "+", Cache: "+cache;
    }
    
    public void createDirectory(String name) throws IOException{        
        Process process = null;        
        if (System.getProperty("os.name").equals("Linux")){
            process = Runtime.getRuntime().exec("mkdir "+name.replace(" ", "").toLowerCase());
        }
    }
}
