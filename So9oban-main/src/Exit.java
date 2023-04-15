public class Exit {
    public Level Lvl;
    public int x;
    public int y;

    public int boxx;

    public int boxy;


    public String Direction;
    public Exit(Level lvl, int x,int y,String direction,int bx,int by){
        this.Lvl = lvl;
        //posexit
        this.x = x;
        this.y = y;
        this.Direction = direction;
        //box
        this.boxx=bx;
        this.boxy=by;

    }

    //On vérifie si la case est une sortie
    public Boolean isExit(int x, int y, String direction){

        if( (this.x ==x) && (this.y == y) && (direction.equals(this.Direction))){
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Exit)obj).x==this.x && ((Exit)obj).y==this.y && ((Exit)obj).Direction.equals(this.Direction);
    }
}