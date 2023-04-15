public class WorldBox extends Box{
    public Level inside;
    public boolean isZoomed;

    public WorldBox(Level l){
        sprite="assets/worldcrate.png";
        inside=l;

    }

    //On test pour chaque direction la bordure pour trouver une entrée
    public int entryLeft(){
        if(inside.map[0][inside.size/2] instanceof EmptySpace) return inside.size/2;
        return -1;
    }
    public int entryUp(){
        if(inside.map[inside.size/2][0] instanceof EmptySpace) return inside.size/2;
        return -1;
    }
    public int entryRight(){
        if(inside.map[inside.size-1][inside.size/2] instanceof EmptySpace) return inside.size/2;
        return -1;
    }
    public int entryDown(){
        if(inside.map[inside.size/2][inside.size-1] instanceof EmptySpace) return inside.size/2;
        return -1;
    }

    //Génération des sorties d'un Lvl
    public void exitGen(Level l,int x,int y){

        while(!inside.exit.isEmpty()) inside.exit.remove(0);

        for(int i=0;i< inside.size;i++){
            if(inside.map[i][inside.size-1]instanceof EmptySpace) this.inside.exit.add(new Exit(l,i, inside.size-1,"down",x,y));
            if(inside.map[i][0]instanceof EmptySpace) this.inside.exit.add(new Exit(l,i, 0,"up",x,y));
            if(inside.map[inside.size-1][i]instanceof EmptySpace) this.inside.exit.add(new Exit(l,inside.size-1,i ,"right",x,y));
            if(inside.map[0][i]instanceof EmptySpace) this.inside.exit.add(new Exit(l,0,i ,"left",x,y));

        }

    }

    //On vérifie si une entrée existe dans la direction indiquée
    public Boolean entryCheck(String direction){
        if((direction.equals("up")) && (this.entryDown() >= 0)){
            return true;
        }
        if((direction.equals("down")) && (this.entryUp() >= 0)){
            return true;
        }
        if((direction.equals("left")) && (this.entryRight() >= 0)){
            return true;
        }
        if((direction.equals("right")) && (this.entryLeft() >= 0)){
            return true;
        }
        return false;
    }

}