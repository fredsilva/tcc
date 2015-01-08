/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.stage.Screen;

/**
 *
 * @author Frederico da Silva Santos
 */
public abstract class Utils {

    public static void setFullScreen(Region region) {
        //Coloca objeto do tamanho da tela        
        Rectangle2D primaryScreen = Screen.getPrimary().getVisualBounds();
        region.setPrefWidth(primaryScreen.getWidth());
        region.setPrefHeight(primaryScreen.getHeight());
    }

    public static String slashInString(String text) {
        //Remove uma / no ínicio da string ou adiciona uma / no fim da string        
        if (text.charAt(0) == '/') {
            text = text.substring(1, text.length());
        }
        if (text.charAt(text.length() - 1) != '/') {
            text = new String(text + "/");
        }
        return text;
    }

    /* Copia arquivos entre diretórios */
    public static void copyFiles(List<File> oriFile, String dest) throws FileNotFoundException, IOException {
        for (File file : oriFile) {
            File destFile = new File(dest+"/"+file.getName());
            FileChannel oriChannel = new FileInputStream(file).getChannel();
            FileChannel destChannel = new FileOutputStream(destFile).getChannel();
            destChannel.transferFrom(oriChannel, 0, oriChannel.size());
            oriChannel.close();
            destChannel.close();
        }
    }

    public static void copyFile(File source, File destination) throws IOException {
        if (destination.exists()) {
            destination.delete();
        }

        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;

        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destinationChannel = new FileOutputStream(destination).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(),
                    destinationChannel);
        } finally {
            if (sourceChannel != null && sourceChannel.isOpen()) {
                sourceChannel.close();
            }
            if (destinationChannel != null && destinationChannel.isOpen()) {
                destinationChannel.close();
            }
        }
    }

    public static void createFile(String name, String text) throws IOException {
        try {
            File file = new File(name);
            file.createNewFile();
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(text);
            bw.close();
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        FileOutputStream fos = new FileOutputStream(path + name);
//        OutputFormat format = OutputFormat.createPrettyPrint();
//        XMLWriter writer = new XMLWriter(fos, format);
//        writer.write(doc);
//        writer.flush();        
    }
}
