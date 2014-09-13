/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
            text = new String(text+"/");
        }
        return text;
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
}
