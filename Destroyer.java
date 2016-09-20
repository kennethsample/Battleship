package BattleShip;

class Destroyer extends Ship{

    public Destroyer(String name){
        super(name);
    }
    @Override
    public char drawShipStatusAtCell(boolean isDamaged) {
        if(isDamaged){
            return 'd';
        }else{
            return 'D';
        }
    }

    @Override
    public int getLength() {
        return 2;
    }
    
}