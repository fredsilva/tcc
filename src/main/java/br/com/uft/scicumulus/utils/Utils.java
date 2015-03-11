/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.utils;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.stage.Screen;

/**
 *
 * @author Frederico da Silva Santos
 */
public abstract class Utils {

    public static Object loadFXML(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource(path));
            loader.load();
            Object controlador = loader.getController();
            return controlador;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }

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
//    public static void copyFiles(List<File> oriFile, String dest) throws FileNotFoundException, IOException {
//        for (File file : oriFile) {
//            File destFile = new File(dest + "/" + file.getName());
//            FileChannel oriChannel = new FileInputStream(file).getChannel();
//            FileChannel destChannel = new FileOutputStream(destFile).getChannel();
//            destChannel.transferFrom(oriChannel, 0, oriChannel.size());
//            oriChannel.close();
//            destChannel.close();
//        }
//    }
//
//    public static void copyFile(File[] oriFile, String dest) throws FileNotFoundException, IOException {
//        //Copia todos os arquivos de um diretório
//        for (int i = 0; i < oriFile.length; i++) {
//            if (oriFile[i].isFile()) {
//                File destFile = new File(dest + "/" + oriFile[i].getName());
//                FileChannel oriChannel = new FileInputStream(oriFile[i]).getChannel();
//                FileChannel destChannel = new FileOutputStream(destFile).getChannel();
//                destChannel.transferFrom(oriChannel, 0, oriChannel.size());
//                oriChannel.close();
//                destChannel.close();
//            }
//            if (oriFile[i].isDirectory()) {
//                oriFile[i].mkdirs();
//            }
//        }
//    }
    
    public static void copyFile(File src, String dest) throws IOException{
        File fileDest = new File(dest);
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);
    
        byte[] buf = new byte[1024];
        int len;
        while((len = in.read(buf)) > 0){
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    public static void copyFiles(File srcFolder, String destFolder) throws FileNotFoundException, IOException {
        //Chamada do método para copiar os diretórios
        try {
            File fileDest = new File(destFolder);
            if (!srcFolder.exists()) {
                System.exit(0);
            } else {
                try {
                    copyFolder(srcFolder, fileDest);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }

        } catch (Exception ex) {
        }
    }

    public static void copyFolder(File ori, File dest) throws IOException {
        //Copia um diretório inteiro com seus subdiretórios
        if (ori.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String files[] = ori.list();
            for (String file : files) {
                File srcFile = new File(ori, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(ori);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }
//    public static void copyFile(File source, File destination) throws IOException {
//        if (destination.exists()) {
//            destination.delete();
//        }
//
//        FileChannel sourceChannel = null;
//        FileChannel destinationChannel = null;
//
//        try {
//            sourceChannel = new FileInputStream(source).getChannel();
//            destinationChannel = new FileOutputStream(destination).getChannel();
//            sourceChannel.transferTo(0, sourceChannel.size(),
//                    destinationChannel);
//        } finally {
//            if (sourceChannel != null && sourceChannel.isOpen()) {
//                sourceChannel.close();
//            }
//            if (destinationChannel != null && destinationChannel.isOpen()) {
//                destinationChannel.close();
//            }
//        }
//    }
//    public void copyDirectory(File srcDir, File dstDir) throws IOException {
//        if (srcDir.isDirectory()) {
//            if (!dstDir.exists()) {
//                dstDir.mkdir();
//            }
//            String[] children = srcDir.list();
//            for (int i=0; i<children.length; i++) {
//                copyDirectory(new File(srcDir, children[i]),
//                                     new File(dstDir, children[i]));
//            }
//        } else {
//            // Este método está implementado na dica – Copiando um arquivo utilizando o Java
//            copyFile(srcDir, dstDir);
//        }
//    }

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

    public static void saveFile(String fileName, Object object) throws FileNotFoundException, IOException {
        try {
            FileOutputStream fileStream = new FileOutputStream(fileName);
            ObjectOutputStream os = new ObjectOutputStream(fileStream);
            os.writeObject(object);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveFileJson(String fileName, Object object) {
        Gson gson = new Gson();        
        String json = gson.toJson(object);

        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
