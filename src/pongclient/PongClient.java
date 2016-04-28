package pongclient;

import java.io.IOException;
import java.util.Objects;
import javaPlay.GameEngine;
import javax.swing.JOptionPane;

public class PongClient {

    public static void main(String[] args) throws IOException {
        String ip = JOptionPane.showInputDialog(null, "Digite o ip do servidor, ou deixe o campo em branco para rodar no host local.", "Entrada", JOptionPane.PLAIN_MESSAGE);

        if (Objects.isNull(ip) || ip.isEmpty()) {
            ip = "127.0.0.1";
        }

        GameEngine.getInstance().addGameStateController(0, new Player(ip));
        GameEngine.getInstance().setStartingGameStateController(0);
        GameEngine.getInstance().run();
    }

}
