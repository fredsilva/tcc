package br.com.uft.scicumulus.utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.IOException;

/**
 *
 * @author Frederico da Silva Santos Classe que envia comandos SSH entre a
 * máquina local e o servidor Para o seu funcionamento a chave RSA da máquina
 * cliente deve ser adicioanada ao arquivo authorized_keys no servidor
 */
public class SSH {

    private String server;
    private String user;

    public SSH() {
        this.server = "162.243.77.191";
        this.user = "admin";
    }

    public boolean sendFiles(String pathLocal, String pathServer) throws Exception {
        Process process = null;
        if (System.getProperty("os.name").equals("Linux")) {
            try {
                process = Runtime.getRuntime().exec("scp -r " + pathLocal + " " + user + "@" + server + ":" + pathServer);
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

//    public void createDirectory(String name) throws IOException {
//        try {
//            JSch jsch = new JSch();
//            Session session = jsch.getSession("admin", "162.243.77.191", 22);
//            User user = new User();
//            user.getPassword();
//            session.setUserInfo(user);
//            session.connect();
//
//            ChannelExec channel = (ChannelExec) session.openChannel("exec");
//            channel.setCommand("mkdir /deploy/"+name);            
//            channel.connect();
//            channel.disconnect();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public class User implements UserInfo {

        @Override
        public String getPassword() {
            return "fred1026";
        }

        @Override
        public boolean promptYesNo(String str) {
            return true;
        }

        @Override
        public void showMessage(String message) {

        }

        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public boolean promptPassphrase(String paramString) {
            return true;
        }

        @Override
        public boolean promptPassword(String message) {
            return true;
        }
    }
}
