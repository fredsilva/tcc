package br.com.uft.scicumulus.hydra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;

/**
 * Classe HydraUtils
 *
 * @author Eduardo
 * @since 2010-12-25
 */
public class HydraUtils {
    protected static boolean verbose = false;
    public static int SLEEP_INTERVAL = 100;
    public static String SEPARATOR = "/";
    private static Locale local = new Locale("en");
    private static DecimalFormatSymbols simbols = new DecimalFormatSymbols(local);
    private static DecimalFormat formatDec[] = {
        new DecimalFormat("###0", simbols),
        new DecimalFormat("###0.0", simbols),
        new DecimalFormat("###0.00", simbols),
        new DecimalFormat("###0.000", simbols),
        new DecimalFormat("###0.0000", simbols),
        new DecimalFormat("###0.00000", simbols),
        new DecimalFormat("###0.000000", simbols),
        new DecimalFormat("###0.0000000", simbols),
        new DecimalFormat("###0.00000000", simbols),
        new DecimalFormat("###0.000000000", simbols)
    };

    /**
     * Obtêm um valor do tipo FLOAT em uma String, respeitando o número de casas
     * decimais informadas pelo parâmetro decimal
     *
     * @param value
     * @param decimal
     * @return
     */
    public static String formatFloat(Double value, int decimal) {
        if (decimal != -1) {
            return formatDec[decimal].format(value);
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * Método que faz com que uma thread durma por 100 ms
     */
    public static void sleep() {
        try {
            Thread.sleep(SLEEP_INTERVAL);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Método que adiciona uma barra, caso o diretório informado não termine com
     * uma barra
     *
     * @param value
     * @return
     */
    public static String checkDir(String value) {
        if (value != null && value.charAt(value.length() - 1) != '/') {
            value += "/";
        }
        return value;
    }

    /**
     * Cria um diretório atravês do caminha passado como parâmetro
     *
     * @param directory
     * @return
     */
    public static boolean createDirectory(String directory) {
        boolean result = true;
        File f = new File(directory);
        try {
            f.mkdir();
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        f = null;
        return result;
    }

    /**
     * Cria um arquivo com o nome informado como parâmetro
     *
     * @param fileName
     */
    public static void CreateFile(String fileName) {
        File f = new File(fileName);
        try {
            f.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        f = null;
    }

    /**
     * Cria um arquivo com o nome e o conteúdo passados como parâmetros
     *
     * @param fileName
     * @param Data
     */
    public static void WriteFile(String fileName, String Data) {
        try {
            CreateFile(fileName);
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            out.write(Data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lê um arquivo
     *
     * @param fileDirectory
     * @return
     * @throws IOException
     */
    public static String ReadFile(String fileDirectory) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileDirectory));
        StringBuilder contents = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            contents.append(line);
            contents.append(System.getProperty("line.separator"));
        }
        in.close();
        return contents.toString();
    }

    /**
     * Obtêm o caminho de um arquivo na forma canônica
     *
     * @param file
     * @return
     */
    public static String getCanonicalPath(File file) {
        String result = "";
        try {
            result = file.getCanonicalPath();
            result = result.replaceAll("\\\\", "/");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Obtêm o nome do arquivo
     *
     * @param file
     * @return
     */
    public static String getFileName(String fullname) {
        File file = new File(fullname);
        String result = getFileName(file);
        file = null;
        return result;
    }

    public static String getFileName(File file) {
        String result = "";
        result = file.getName();
        result = result.replaceAll("\\\\", "/");
        return result;
    }

    /**
     * Avalia se o sistema operacional de execução é Windows
     *
     * @return
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("win") >= 0);
    }

    /**
     * Avalia se o sistema operacional de execução é MAC
     *
     * @return
     */
    public static boolean isMacOS() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0);
    }

    /**
     * Move um arquivo
     *
     * @param destination
     * @param origin
     * @throws IOException
     * @throws InterruptedException
     */
    public static void moveFile(String origin, String destination) throws IOException, InterruptedException {
        String cmd = "";
        createDirectory(destination);
        if (HydraUtils.isWindows()) {
            cmd = "move " + origin + " " + destination;
            cmd = cmd.replace("/", "\\");
        } else {
            cmd = "mv " + origin + " " + destination;
        }
        runCommand(cmd, null);
    }

    /**
     * Realiza a cópia de um arquivo
     *
     * @param destination
     * @param origin
     * @throws IOException
     * @throws InterruptedException
     */
    public static void copyFile(String origin, String destination) throws IOException, InterruptedException {
        String cmd = "";
        if (HydraUtils.isWindows()) {
            cmd = "xcopy " + origin + " " + destination;
            cmd = cmd.replace("/", "\\");
            cmd += " /q /c /y";
        } else {
            HydraUtils.createDirectory(destination);
            cmd = "cp " + origin + " " + destination;
        }
        runCommand(cmd, null);
    }

    /**
     * Mêtodo que copia os arquivos do template
     *
     * @param origin
     * @throws IOException
     * @throws InterruptedException
     */
    public static void copyTemplateFiles(String origin, String destination) throws IOException, InterruptedException {
        String cmd = "";
        if (HydraUtils.isWindows()) {
            cmd = "xcopy " + origin + "*.* " + destination;
            cmd = cmd.replace("/", "\\");
            cmd += " /s /q /c /y ";
        } else if (HydraUtils.isMacOS()) {
            cmd = "cp " + origin + "* " + destination;
        } else {
            cmd = "cp " + origin + "* " + destination + " -r -f";
        }
        runCommand(cmd, null);
    }

    /**
     * Deleta um arquivo
     *
     * @param fileName
     * @param fileDir
     * @throws IOException
     * @throws InterruptedException
     */
    public static void deleteFile(String fileName, String fileDir) throws IOException, InterruptedException {
        //Código que deleta um arquivo após a execução da tarefa.
        String cmd = "";
        if (HydraUtils.isWindows()) {
            cmd = "del " + fileDir + fileName;
            cmd = cmd.replace("/", "\\");
            cmd += " /s /q ";
        } else {
            cmd = "rm " + fileDir + fileName;
        }
        runCommand(cmd, null);
    }

    public static boolean isFile(String fileDir) {
        File file = new File(fileDir);
        boolean result = file.exists();
        file = null;
        return result;
    }

    /**
     * Roda um comando genêrico, tendo em vista o sistema operacional
     * @param cmd
     * @param run
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static int runCommand(String cmd, String dir) throws IOException, InterruptedException {
        Runtime run = Runtime.getRuntime();
        int result = 0;
        String command[] = null;
        if (HydraUtils.isWindows()) {
            String cmdWin[] = {"cmd.exe", "/c", cmd};
            command = cmdWin;
        } else {
            String cmdLinux = cmd;
            if (cmd.contains(">")) {
                cmdLinux = cmd.replace(">", ">>");
            }
            String cmdLin[] = {"/bin/bash", "-c", cmdLinux};
            command = cmdLin;
        }
        if (verbose)
            System.out.println(command[command.length-1]);
        Process pr = null;
        if (dir == null) {
            pr = run.exec(command);
        } else {
            pr = run.exec(command, null, new File(dir));
        }
        InputStream err = pr.getErrorStream();
        InputStream in = pr.getInputStream();
        InputStream out = pr.getInputStream();
        pr.waitFor();
        close(err);
        close(in);
        close(out);
        result = pr.exitValue();
        pr.destroy();
        return result;
    }

    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                // ignored
            }
        }
    }

    /**
     * Método que adiciona quebra de linha
     *
     * @param stream
     * @return
     */
    private static String getStreamAsString(InputStream stream) {
        String out = "";
        Scanner s = new Scanner(stream);
        while (s.hasNextLine()) {
            out.concat(s.nextLine() + System.getProperty("line.separator"));
        }
        return out;
    }
}
