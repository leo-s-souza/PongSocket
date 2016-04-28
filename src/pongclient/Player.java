package pongclient;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaPlay.GameEngine;
import javaPlay.GameStateController;
import javaPlay.Keyboard;
import javaPlay.Sprite;
import pongcomunication.BarPosition;
import pongcomunication.GameState;

class Player implements GameStateController {

    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    GameState gameState;
    BarPosition barPosition;

    //Inicialização de variaveis de controle
    private int posBarraA;
    private int posBarraB;
    private int altura;
    private int posBolaY;
    private int posBolaX;
    private int pontuacaoA = 0;
    private int pontuacaoB = 0;

    // Váriaveis Correção Jeferson
    private int movimentoBarra = 5;
    private int playerNumber;

    //tela
    private int largura;

    //Variáveis utilizadas para os Sprites
    private Sprite figuraBola;
    private Sprite barra1;
    private Sprite barra2;
    private Sprite figuraBackground;

    //Inicialização das Classes do Game
    Background background = new Background();
    Bola bola1 = new Bola();
    Barra barraA = new Barra();
    Barra barraB = new Barra();

    private long nextSend;
    private String hostName;
    private int port = 4444;

    public Player(String hostName) throws IOException {
        //inicializando o game com a tamanho padrão do GameEngine
        altura = GameEngine.getInstance().getGameCanvas().getHeight();
        largura = GameEngine.getInstance().getGameCanvas().getWidth();

        //Iniciando a bola no meio da tela
        posBarraA = altura / 2;
        posBarraB = altura / 2;

        this.hostName = hostName;

        try {
            //Carregamento dos sprites do game
            figuraBackground = new Sprite("pongsocket.images/background.png", 1, 800, 600);
            figuraBola = new Sprite("pongsocket.images/bola.png", 3, 34, 34);
            barra1 = new Sprite("pongsocket.images/Pong_pad01.png", 3, 25, 100);
//            barra1 = new Sprite(getClass().getResource("images/Pong_pad01.png"), 3, 25, 100);
            barra2 = new Sprite("pongsocket.images/Pong_pad02.png", 3, 25, 100);
//            barra2 = new Sprite(getClass().getResource("images/Pong_pad02.png"), 3, 25, 100);
        } catch (Exception erro) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, erro);
            System.exit(-1);
        }
        background.setSprite(figuraBackground);
        bola1.setSprite(figuraBola);
        barraA.setSprite(barra1);
        barraB.setSprite(barra2);
    }

    @Override
    public void step(long l) {
        try {
            sleep(6);
        } catch (InterruptedException erro) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, erro);
        }

        if (playerNumber == 1) {
            atualizaBarraA();
        } else if (playerNumber == 2) {
            atualizaBarraB();
        }

        atualizaGameState();
    }

    public void atualizaBarraA() {
        Keyboard teclado = GameEngine.getInstance().getKeyboard();
        if ((teclado.keyDown(Keyboard.UP_KEY) == true) && (posBarraA > 10)) {
            posBarraA -= movimentoBarra;
            if (posBarraA < 10) {
                posBarraA = 10;
            }
        }
        if ((teclado.keyDown(Keyboard.DOWN_KEY) == true) && (posBarraA < (altura - 149))) {
            posBarraA += movimentoBarra;
            if (posBarraA > altura - 149) {
                posBarraA = altura - 149;
            }
        }
    }

    public void atualizaBarraB() {
        //Configração das teclas de controle do Game
        Keyboard teclado = GameEngine.getInstance().getKeyboard();
        if ((teclado.keyDown(Keyboard.UP_KEY) == true) && (posBarraB > 10)) {
            posBarraB -= movimentoBarra;
            if (posBarraB < 10) {
                posBarraB = 10;
            }
        }
        if ((teclado.keyDown(Keyboard.DOWN_KEY) == true) && (posBarraB < (altura - 149))) {
            posBarraB += movimentoBarra;
            if (posBarraB > altura - 149) {
                posBarraB = altura - 149;
            }
        }
    }

    public void readGameState() {
        try {
            gameState = (GameState) in.readObject();

            posBolaX = gameState.getPosBolaX();
            posBolaY = gameState.getPosBolaY();
            pontuacaoA = gameState.getPontuacaoA();
            pontuacaoB = gameState.getPontuacaoB();

            if (playerNumber == 1) {
                posBarraB = gameState.getPosBarraB();
            } else if (playerNumber == 2) {
                posBarraA = gameState.getPosBarraA();
            }

            nextSend = System.currentTimeMillis() + 33;
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void atualizaGameState() {
        if (System.currentTimeMillis() >= nextSend) {
            try {
                if (playerNumber == 1) {
                    barPosition = new BarPosition(posBarraA);
                } else if (playerNumber == 2) {
                    barPosition = new BarPosition(posBarraB);
                }
                out.writeObject(barPosition);

                readGameState();

            } catch (IOException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Metodo draw. Executado a cada ciclo de clock para redesenhar a tela do
     * Game
     *
     * @param graphic
     */
    @Override
    public void draw(Graphics graphic) {

        //Inicializando a tela de fundo do game
        background.x = -1;
        background.y = 0;
        background.draw(graphic);

        //Escrevendo os nomes dos players na tela
        graphic.setColor(Color.green);
        Font font = new Font("arial", Font.BOLD, 18);
        graphic.setFont(font);
        graphic.drawString("Player A", largura / 2 - 90, 55);
        graphic.drawString("Player B", largura / 2 + 10, 55);
        graphic.drawString(String.valueOf(pontuacaoB), largura / 2 - 60, 75);
        graphic.drawString(String.valueOf(pontuacaoA), largura / 2 + 40, 75);

        //Desenhando a Bola
        bola1.x = posBolaX;
        bola1.y = posBolaY;
        bola1.draw(graphic);

        //Desenhando a Barra A
        barraA.x = 13;
        barraA.y = posBarraA;
        barraA.draw(graphic);

        //Desenhando a Barra B
        barraB.x = largura - 55;
        barraB.y = posBarraB;
        barraB.draw(graphic);
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }

    @Override
    public void start() {
        try {
            socket = new Socket(hostName, port);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Não foi possivel conectar ao host: " + hostName + ".");
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, e);
        }
        readGameState();
        playerNumber = gameState.getPlayerNumber();
    }

    @Override
    public void stop() {

    }

}
