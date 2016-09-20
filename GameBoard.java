package BattleShip;

import java.util.ArrayList;

public class GameBoard {

    int rowCount = 10;
    int colCount = 10;
    final String LINE_END = System.getProperty("line.separator");
    ArrayList< ArrayList< Cell>> cells;
    ArrayList< Ship> myShips = new ArrayList<Ship>();

    public GameBoard(int rowCount, int colCount) {
        this.rowCount = rowCount;
        this.colCount = colCount;

        //create the 2D array of cells
        this.cells = new ArrayList<ArrayList<Cell>>();
        for (int y = 0; y < rowCount; y++) {
            this.cells.add(new ArrayList<Cell>());
            for (int x = 0; x < colCount; x++) {
                this.cells.get(y).add(new Cell());
            }
        }
        //System.out.println("y: "+this.cells.size()+"  x: "+this.cells.get(5).size());
    }

    public String draw() {

        StringBuilder board = new StringBuilder();
        for (int i = 0; i < this.rowCount + 2; i++) {
            if (i == 0 || i == this.rowCount + 1) {
                board.append("+");
            } else if (this.rowCount <= 10 && this.colCount <= 10) {
                board.append(i - 1);
            } else {
                board.append("|");
            }
            for (int j = 0; j < this.colCount; j++) {
                if (i == 0 || i == this.colCount + 1) {
                    if (this.rowCount <= 10 && this.colCount <= 10) {
                        board.append(j);
                    } else {
                        board.append("-");
                    }
                } else {
                    //System.out.println("i: "+i+"  j: "+j);
                    board.append(this.cells.get(i - 1).get(j).draw());
                }

            }

            if (i == 0 || i == this.colCount + 1) {
                board.append("+" + this.LINE_END);
            } else {
                board.append("|" + this.LINE_END);
            }
        }

        return board.toString();
        //draw the entire board... I'd use a StringBuilder object to improve speed
        //remember - you must draw one entire row at a time, and don't forget the
        //pretty border...
    }

    //this will print only what has been fired on - will be used to print the opponent's board
    public String drawBare() {

        StringBuilder board = new StringBuilder();
        for (int i = 0; i < this.rowCount + 2; i++) {
            if (i == 0 || i == this.rowCount + 1) {
                board.append("+");
            } else if (this.rowCount <= 10 && this.colCount <= 10) {
                board.append(i - 1);
            } else {
                board.append("|");
            }
            for (int j = 0; j < this.colCount; j++) {
                if (i == 0 || i == this.colCount + 1) {
                    if (this.rowCount <= 10 && this.colCount <= 10) {
                        board.append(j);
                    } else {
                        board.append("-");
                    }
                } else //System.out.println("i: "+i+"  j: "+j);
                if (this.cells.get(i - 1).get(j).hasBeenStruckByMissile()) {
                    if (this.cells.get(i - 1).get(j).getShip() != null) {
                        board.append("s");
                    } else {
                        board.append("x");
                    }
                } else {
                    board.append(" ");
                }

            }

            if (i == 0 || i == this.colCount + 1) {
                board.append("+" + this.LINE_END);
            } else {
                board.append("|" + this.LINE_END);
            }
        }

        return board.toString();
        //draw the entire board... I'd use a StringBuilder object to improve speed
        //remember - you must draw one entire row at a time, and don't forget the
        //pretty border...
    }

//add in a ship if it fully 1) fits on the board and 2) doesn't collide w/
//an existing ship.
//Returns true on successful addition; false, otherwise
    public boolean addShip(Ship s, Position sternLocation, HEADING bowDirection) {
        s.position = new ArrayList<Cell>();
        //I don't know if the ship's position has already been added... but just in case it hasn't I am going to make that happen now.
        //while I also initialize variables to check the bounds
        ArrayList<Position> listOfPositions = new ArrayList<Position>();

        int eastestX = sternLocation.x;
        int westestX = sternLocation.x;
        int northestY = sternLocation.y;
        int southestY = sternLocation.y;
        switch (bowDirection) {
            case NORTH:
                northestY = sternLocation.y - s.getLength() + 1;
                for (int i = 0; i < s.getLength(); i++) {
                    listOfPositions.add(new Position(sternLocation.x, sternLocation.y - i));
                    System.out.println("Adding position going N");
                }
                break;
            case SOUTH:
                southestY = sternLocation.y + s.getLength() - 1;
                for (int i = 0; i < s.getLength(); i++) {
                    listOfPositions.add(new Position(sternLocation.x, sternLocation.y + i));
                    System.out.println("Adding position going S");
                }
                break;
            case EAST:
                eastestX = sternLocation.x + s.getLength() - 1;
                for (int i = 0; i < s.getLength(); i++) {
                    listOfPositions.add(new Position(sternLocation.x + i, sternLocation.y));
                    System.out.println("Adding position going E");
                }
                break;
            case WEST:
                westestX = sternLocation.x - s.getLength() + 1;
                for (int i = 0; i < s.getLength(); i++) {
                    listOfPositions.add(new Position(sternLocation.x - i, sternLocation.y));
                    System.out.println("Adding position going W");
                }
                break;
        }
        boolean isCollision = false;
        //now that I have the list of the ship's positions, check for collisions
        for (Position p : listOfPositions) {
            System.out.println(p);
            try {
                //for each position, I need to find the cell, and check whether each cell already has a ship assigned - if it does, print error and return false
                if (this.cells.get(p.y).get(p.x).ship != null) {
                    System.out.println("Error: collision while trying to add ship.");
                    return false;
                } else {//I might as well add cells to ship now since I'm not going to end up using the ship anyway if it doesn't end up working
                    //but I won't add the ship to the cells until later, since that would have lasting repercussions
                    s.position.add(this.cells.get(p.y).get(p.x));
                }
            } catch (Exception e) {
                //If the ship isn't on th board, it'll hit here.
                return false;
            }
        }

        if (eastestX >= 0 && westestX <= (this.rowCount - 1) && northestY >= 0 && southestY <= (this.colCount - 1)) {//we know no collisions,
            //so now we are checking that it is actually on the board
            //if it is, then let's add the ship to the board.  do this by adding the ship to the list, and editing the cells to reflect the ship
            this.myShips.add(s);
            for (Position p : listOfPositions) {
                this.cells.get(p.y).get(p.x).setShip(s);
            }
        } else {
            return false;
        }
        return true;

    }

    //Returns A reference to a ship, if that ship was struck by a missle.
    //The returned ship can then be used to print the name of the ship which
    //was hit to the player who hit it.
    //Ensure you handle missiles that may fly off the grid
    public Ship fireMissle(Position coordinate) {
        if (coordinate.x >= 0 && coordinate.x < this.colCount && coordinate.y >= 0 && coordinate.y < this.rowCount) {
            Cell c = this.cells.get(coordinate.y).get(coordinate.x);
            c.hasBeenStruckByMissile(true);
            return c.ship;
        } else {
            return null;
        }

    }

    //Here's a simple driver that should work without touching any of the code below this point
    public static void main(String[] args) {
        System.out.println("Hello World");
        GameBoard b = new GameBoard(10, 10);
        System.out.println(b.draw());

        Ship s = new Cruiser("Cruiser");
        if (b.addShip(s, new Position(3, 6), HEADING.WEST)) {
            System.out.println("Added " + s.getName() + "Location is ");
        } else {
            System.out.println("Failed to add " + s.getName());
        }

        s = new Destroyer("Vader");
        if (b.addShip(s, new Position(3, 5), HEADING.NORTH)) {
            System.out.println("Added " + s.getName() + "Location is ");
        } else {
            System.out.println("Failed to add " + s.getName());
        }

        System.out.println(b.draw());

        b.fireMissle(new Position(3, 5));
        System.out.println(b.draw());
        b.fireMissle(new Position(3, 4));
        System.out.println(b.draw());
        b.fireMissle(new Position(3, 3));
        System.out.println(b.draw());

        b.fireMissle(new Position(0, 6));
        b.fireMissle(new Position(1, 6));
        b.fireMissle(new Position(2, 6));
        b.fireMissle(new Position(3, 6));
        System.out.println(b.draw());

        b.fireMissle(new Position(6, 6));
        System.out.println(b.draw());
    }
}
