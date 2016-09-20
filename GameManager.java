package BattleShip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class GameManager {

    private final int PORT_NUMBER = 10001;
    private ArrayList<Client> clients = new ArrayList<Client>();
    private ServerSocket listener = null;

    public GameManager() {
    }

    //Returns a client reference to the opponent. This way, we can inspect attributes
    //and send messages between clients... Each client has a reference to the GameManager
    //so a client is able to use this method to get a reference to his opponent
    public Client getOpponent(Client me) {
        for (Client c : this.clients) {
            if (!c.equals(me)) {
                return c;
            }
        }
        throw new Error("Something has gone wrong, I can't find my opponent!");
    }

    //In a asychronous nature, begin playing the game. This should only occur after 
    //the players have been fully initialized.
    public void playGame() throws IOException {
//        boolean gameIsOver = false;
//        while (!gameIsOver) {
//            for (Client c : this.clients) {
//                c.playGame();
//                if (c.allMyShipsAreDestroyed() || c.allMyShipsAreDestroyed()) {
//                    gameIsOver = true;
//                    System.out.println("The game is over.");
//                }
//
//            }
//
//        }
        //Each player may begin firing missiles at the other player. First player to lose all ships is the loser.
        //Asynchronously process missile fire commands from each player	

        this.clients.parallelStream().forEach(client -> 
        {
            try {
                client.playGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    );
    }

    //Create a server listener socket and wait for two clients to connect.
    //Use the new client socket to create a PrintWriter and BufferedReader
    //so you can pass these two streams into the constructor of a new client.
    //Don't forget about try/finally blocks, if needed
    //basic socket construction came from: https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
    boolean waitFor2PlayersToConnect() throws IOException {

        try {
            ServerSocket serverSocket=new ServerSocket(PORT_NUMBER);
            //connect with client 1
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            //out.println("Test 1");
            //out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println("You are connected to the server. Once your opponent joins game setup will begin.");
            //connect with client 2
            Socket clientSocket2 = serverSocket.accept();
            PrintWriter out2 = new PrintWriter(clientSocket2.getOutputStream(), true);
            //out2.println("Test 2");
            BufferedReader in2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
            //initialize the client objects
            Client p1 = new Client(in, out, this);
            Client p2 = new Client(in2, out2, this);

            //add them to our list of clients
            this.clients.add(p1);
            this.clients.add(p2);
            return true;
        } catch (Exception e) {
            System.out.println("Error adding players." + e.toString());
            e.printStackTrace();
        }
        return false;
        
    }
//let players initialize their name, and gameboard here. This should be done asynchronously 
    void initPlayers() throws IOException {
        this.clients.parallelStream().forEach(client -> 
        {
            try {
                client.initPlayer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    );

    }

    //Main driver for the program... Hit Crtl-F11 in eclipse to launch the server...
    //Of course, it has to compile first...
    public static void main(String[] args) throws IOException {
        GameManager m = new GameManager();

        System.out.println("<<<---BattleShip--->>>");
        System.out.println("Waiting for two players to connect to TCP:10001");
        m.waitFor2PlayersToConnect();
        System.out.println("Clients have joined!!!");
        m.initPlayers();
        System.out.println(m.clients.get(0).getName() + " vs " + m.clients.get(1).getName() + " Let's Rumble...");
        m.playGame();
        System.out.println("Shutting down server now... Disconnecting Clients...");
    }
}
