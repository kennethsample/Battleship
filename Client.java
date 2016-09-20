package BattleShip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Client {

    final String NEWL = System.getProperty("line.separator");
    private String name = "Player";
    PrintWriter out = null;
    BufferedReader in = null;
    GameManager man = null;
    GameBoard board = new GameBoard(10, 10);
    GameBoard targets = new GameBoard(10, 10);

    Client(BufferedReader in, PrintWriter out, GameManager manager) {
        this.in = in;
        this.out = out;
        this.man = manager;
    }

    public void playGame() throws IOException {
        this.out.println(NEWL + NEWL + "   Missiles Away! Game has begun");
        this.out.println("   To Launch a missle at your enemy:");
        this.out.println("F 2 4");
        this.out.println("   Fires a missile at coordinate x=2, y=4."+this.NEWL);
        this.out.println("   To chat with the enemy:");
        this.out.println("C This is my message."+this.NEWL);
        this.out.println("   To draw the current boards:");
        this.out.println("D"+this.NEWL);
        boolean gameOver = false;
        out.println("Target Board:" + this.NEWL + this.targets.drawBare());
        out.println("Your Ships: " + this.NEWL + this.board.draw());
        while (!gameOver) {// put Code Here to process in game commands, after each command, print the target board and game board w/ updated state
            out.println("------------------------");
            out.println("   Waiting for Next Command...\n\n");
            out.flush();
            processCommand();

            //Perform test here to see if we have run or lost
            if (allMyShipsAreDestroyed()) {
                out.println("All your ships are destroyed!  You lose!");
                gameOver = true;
            }
            if (allEnemyShipsAreDestroyed()) {
                out.println("All your enemy's ships are destroyed!  You win!");
                gameOver = true;
            }
        }
    }

    //Returns a bool, true iff all of this client's ships are destroyed
    boolean allMyShipsAreDestroyed() {
        for (Ship s : this.board.myShips) {
            if (s.isAlive()) {
                return false;
            }
        }
        return true;
    }

    //Returns a bool, true iff all of the opponent's ships are destroyed
    boolean allEnemyShipsAreDestroyed() {
        for (Ship s : this.targets.myShips) {
            if (s.isAlive()) {
                return false;
            }
        }
        return true;
    }

    //"F 2 4" = Fire command
    //"C Hello world, i am a chat message"
    //"D" - Redraw the latest game and target boards
    boolean processCommand() throws IOException {
        String input = in.readLine().trim();
        System.out.println("String: " + input);
        String[] separated = input.split(" ");
        if (!separated[0].equals("F") && !separated[0].equals("C") && !separated[0].equals("D")) {
            out.println("Incorrect input - must start with F C or D.  Please try again.");
            return false;
        } else if (separated[0].equals("F")) {
            if (separated.length == 3) {
                //since processFireCmd just gets the coordinates, make a new array for those
                String[] coords = new String[2];
                coords[0] = separated[1];
                coords[1] = separated[2];
                return processFireCmd(coords);
            } else {
                out.println("Incorrect input - a fire command must have 3 inputs, \'F\' plus the two coordinates");
                return false;
            }
        } else if (separated[0].equals("C")) {
            if (separated.length > 1) {
                return processChatCmd(input.substring(1).trim());
            } else {
                out.println("If you want to chat with your opponent, you have to actually say something.");
            }
        } else if (separated[0].equals("D")) {
            out.println("My board:" + this.NEWL);
            out.println(this.board.draw() + this.NEWL + this.NEWL);
            out.println(this.man.getOpponent(this).getName() + "'s board:" + this.NEWL);
            out.println(this.targets.drawBare() + this.NEWL);
        }
        return false;
    }

    //When a fire command is typed, this method parses the coordinates and launches a missle at the enemy
    boolean processFireCmd(String[] s) {
        try {
            int x = Integer.parseInt(s[0]);
            int y = Integer.parseInt(s[1]);
            Ship ship = this.targets.fireMissle(new Position(x, y));
            if (ship == null) {
                out.println("That's a miss.");
            } else {
                boolean isSunk = !ship.isAlive();
                if (ship.isAlive()) {
                    out.println("That's a hit on " + ship.getName() + "!");
                    return true;
                } else {
                    out.println("That's a hit on " + ship.getName() + "!  It is sunk!");
                    return true;
                }
            }
        } catch (Exception e) {
            out.println("Incorrect input formatting - please try again.1");
        }
        return false;
    }

    //Send a message to the opponent
    boolean processChatCmd(String s) {
        this.man.getOpponent(this).out.println(this.name + ": " + s);
        return true;
    }

    GameBoard getGameBoard() {
        return this.board;
    }

    public void initPlayer() throws IOException {
        //1.Get player name
        //2.Print out instructions
        out.println("Please enter your name:\n");
        //out.flush();
        this.name = in.readLine();
//Here's some nice instructions to show a client		
        out.println("   You will now place 2 ships. You may choose between either a Cruiser (C) ");
        out.println("   and Destroyer (D)...");
        out.println("   Enter Ship info. An example input looks like:");
        out.println("\nD 2 4 S USS MyBoat\n");
        out.println("   The above line creates a Destroyer with the stern located at x=2 (col),");
        out.println("   y=4 (row) and the front of the ship will point to the SOUTH (valid");
        out.println("   headings are N, E, S, and W.  The name of the ship will be \"USS MyBoat\"");
        out.println("Enter Ship 1 information:");
        //out.flush();

        //Get ship locations from the player for all 2 ships (or more than 2 if you're using more ships)
        while (this.board.myShips.size() < 2) {
            int currentSize = this.board.myShips.size();
            //I do try catch because it is easier to try to parse the String and see if I get errors than to do ifs for everything
            try {
                String input = in.readLine();
                String[] separated = input.split(" ");
                for (String s : separated) {
                    System.out.println(s + " ");

                }//
                System.out.println("Length: " + separated.length);
                //first let's make sure some basic items are correct
                if (!separated[0].equals("D") && !separated[0].equals("C")
                        || (!separated[3].equals("N") && !separated[3].equals("S") && !separated[3].equals("E") && !separated[3].equals("W"))
                        || separated.length < 5) {
                    out.println("Incorrect format - please try again using the correct format.");
                } else {
                    int x = Integer.parseInt(separated[1]);
                    int y = Integer.parseInt(separated[2]);
                    String name = "";
                    for (int i = 4; i < separated.length; i++) {
                        name += separated[i];
                    }
                    Ship s;
                    switch (separated[0]) {
                        case "D":
                            s = new Destroyer(name);
                            break;
                        case "C":
                            s = new Cruiser(name);
                            break;
                        default:
                            throw new Error("Something went wrong, this error should never be hit.");
                    }

                    HEADING heading;
                    switch (separated[3]) {
                        case "N":
                            heading = HEADING.NORTH;
                            break;
                        case "S":
                            heading = HEADING.SOUTH;
                            break;
                        case "E":
                            heading = HEADING.EAST;
                            break;
                        case "W":
                            heading = HEADING.WEST;
                            break;
                        default:
                            throw new Error("Something went wrong, this error should never be hit.");
                    }
                    if (this.board.addShip(s, new Position(x, y), heading)) {
                    } else {
                        out.println("Ship was not added due to collision with another ship, or ship not being completely on the board");
                    }
                }
            } catch (Error e) {
                out.println("Incorrect format - please try again using the correct format.3");
            }
            if (this.board.myShips.size() > currentSize) {

                if (this.board.myShips.size() < 2) {
                    out.println("Ship added!  Here is your current board with the ship added:");
                    out.println(this.board.draw());
                    out.println("Please enter Ship 2 information:");
                } else {
                    out.println("Ship added!  Once your opponent adds his/her/its ships, the game will begin.");
                }

            }
        }

        //After all game state is input, draw the game board to the client
        out.println("Here is your board:");
        out.println(this.board.draw());
        //need to associate targets with the opps board
        this.targets = this.man.getOpponent(this).board;
    }

    String getName() {
        return this.name;
    }

    public static void main(String[] args) {
    }
}
