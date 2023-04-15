import java.util.ArrayList;
import java.util.HashMap;

import java.util.Stack;

public class History {
    public class Move{
        public Player p;
        public int x,y;
        public ArrayList<Target> t = new ArrayList<>();
        public ArrayList<Exit> e ;

        public ArrayList<Infinibox> in =new ArrayList<>();

        Tile[][] ma ;

        public Move(){};
    }

    private final int MAX_LENGTH = 99;
    Stack<HashMap<String,Move>> moves ;

    public History() {
        this.moves = new Stack<>();
    }

    public void addMove () {
        HashMap<String,Move> M = new  HashMap<>();
        ArrayList<Infinibox> inft = new ArrayList<>();
        for (Infinibox y:Level.inf ){
            try {
                inft.add((Infinibox) y.clone());
            }catch (CloneNotSupportedException e){
                System.out.println("error");
            }
        }
        for (Level l : Level.LinkedLevel.values()) {
            Move tmp = new Move();
            tmp.e = new ArrayList<>(l.exit);
            tmp.p = l.player;
            if(l.player!=null) {
                tmp.x = l.player.posX;
                tmp.y = l.player.posY;
            }

            Tile[][] t = new Tile[l.size][l.size];
            for (int j = 0; j < l.size ; j++) {
                for (int i = 0; i < l.size; i++) {
                    if(l.map[i][j] instanceof Wall) t[i][j]=l.map[i][j];
                    else if (l.map[i][j] instanceof Target) {
                        Target tar = new Target(((Target) l.map[i][j]).Content);
                        t[i][j]= tar;


                    } else if(l.map[i][j] instanceof EmptySpace) t[i][j]= new EmptySpace(((EmptySpace) l.map[i][j]).Content);


                }
            }
            tmp.ma = t;
            tmp.in=inft;

        M.put(l.id,tmp);
        }
        moves.push(M);
    }

    public void returnMove (Display d) {
        if(!moves.isEmpty()){
            HashMap<String,Move> M = moves.pop();
            for (Level l : Level.LinkedLevel.values()) {
                l.map = M.get(l.id).ma;
                l.player = M.get(l.id).p;
                if(M.get(l.id).p!= null){
                    d.setLvl(l);
                    l.player.posX = M.get(l.id).x;
                    l.player.posY = M.get(l.id).y;
                }

                //Level.inf.clear();
                Level.inf = M.get(l.id).in;
                l.exit = M.get(l.id).e;
                l.tagerts = M.get(l.id).t;
            }


        }
    }
}
