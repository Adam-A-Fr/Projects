import java.util.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.text.AbstractDocument.Content;

public class Level{

    public ArrayList<Exit> exit ;

    public Boolean ExitError;
   public String id;
   public int cycle;
   public Tile[][] map;
   public int size;
   public Player player;
   public ArrayList<TransitionMaker> tM;
   static HashMap<String,Level> LinkedLevel = new HashMap<String,Level>();
   // Linked Level est utilisé pour sauvegarder tout niveau present dans les boites monde a la création
   public ArrayList<Target> tagerts;
   static ArrayList<Infinibox> inf = new ArrayList<>();



/***
 *   Constructeur de la classe {@code Level} permettant la création d'un niveau a partir d'une String
 *
 * @author Hakiki Mohammed Iheb
 *
 * @param lvl : une chaine de caractere suivant les convention de niveau
 *
 *
 * @requires lvl!=null
 * @requires lvl.split(" ").length%3 == 0
 *
 *
 *
 */
    public Level(String lvl) {

        this.tM = new ArrayList<>();
        this.tM.add(new TransitionMaker(this));

        this.tagerts = new ArrayList<Target>();
        String tab[] = lvl.split("\n\n");
        String Cur[] = tab[0].split("\n");
        String tmp[] = Cur[0].split(" ");
        this.id = tmp[0];
        this.cycle = 0;
        this.size = Integer.parseInt(tmp[1]);
        this.map = new Tile[this.size][this.size];
        this.exit = new ArrayList<Exit>();
        this.ExitError= false;
        Level.LinkedLevel.put(tmp[0], this);
        String map = "";
        for (int i = 1; i <= this.size; i++)
            map += Cur[i];
        int decalage =0;
        WorldBox wb = null;
        int x = 0,y = 0;
        int cmpinfini = 0;
        for (int j = 0; j < this.size; j++) {
            for (int i = 0; i < this.size; i++) {
                char c = map.charAt(i + j * this.size +decalage);
                if (c == '#') this.map[i][j] = new Wall();
                else if (c == 'A') {
                    if (Level.LinkedLevel.keySet().contains("A")){
                        this.map[i][j] = new EmptySpace(player=new Player(i, j, Level.LinkedLevel.get("A")));
                        TransitionMaker leclone = (TransitionMaker) tM.get(0).clone();
                            tM.add(leclone);
                    }else {
                        int t = 1;
                        while (t< tab.length && c != tab[t].charAt(0)) t++;
                        if (t==tab.length) this.map[i][j] = new EmptySpace(player=new Player(i, j, null));
                        else this.map[i][j] = new EmptySpace(player=new Player(i,j,new Level(tab[t]+"\n\n"+lvl)));

                    }
                }
                else if (c == ' ') this.map[i][j] = new EmptySpace();
                else if (c == 'B') this.map[i][j] = new EmptySpace(new Box());
                else if (c == 'a') {
                    Target tm;
                    if (Level.LinkedLevel.keySet().contains("A")){
                        tm = new Target(player= new Player(i, j, Level.LinkedLevel.get("A")));
                        TransitionMaker leclone = (TransitionMaker) tM.get(0).clone();
                            tM.add(leclone);
                    }else {
                        int t = 1;
                        while (t< tab.length && 'A' != tab[t].charAt(0)) t++;
                        if (t==tab.length) tm = new Target(player=new Player(i, j, null));
                        else tm = new Target(player=new Player(i,j,new Level(tab[t]+"\n\n"+lvl)));

                    }

                    this.map[i][j] = tm;
                    this.tagerts.add(tm);
                }
                else if (c == 'b') {
                    Target t = new Target(new Box());
                    this.map[i][j] = t;
                    this.tagerts.add(t);
                }
                else if (c == '@') {
                    Target t = new Target();
                    this.map[i][j] = t;
                    this.tagerts.add(t);
                } else if (c =='!') {
                    cmpinfini++;
                    decalage++;
                    i--;
                } else if (c=='?') {
                    cmpinfini=1;
                    decalage++;
                    i--;
                }


                else if (Character.isLetter(c)) {
                    if(cmpinfini!=0){
                        Infinibox ifb;
                        this.map[i][j]=new EmptySpace(ifb = new Infinibox(Character.toString(c),this.id,cmpinfini,i,j));
                        cmpinfini=0;
                        inf.add(ifb);
                    }
                    else {
                        if (this.id.charAt(0) == c){
                            this.map[i][j] = new EmptySpace(wb =new WorldBox(this));
                            x=i;
                            y=j;
                            TransitionMaker leclone = (TransitionMaker) tM.get(0).clone();
                            tM.add(leclone);

                        } else {

                            if (Level.LinkedLevel.keySet().contains(Character.toString(c))){
                                this.map[i][j] = new EmptySpace(new WorldBox(Level.LinkedLevel.get(Character.toString(c))));
                                ((WorldBox) ((EmptySpace) this.map[i][j]).Content).exitGen(this, i, j);
                            }

                            else{
                                int t=1;
                                while(c!=tab[t].charAt(0)) t++;
                                this.map[i][j] = new EmptySpace(new WorldBox(new Level(tab[t]+"\n\n"+lvl)));
                                ((WorldBox) ((EmptySpace) this.map[i][j]).Content).exitGen(this, i, j);

                            }

                        }

                    }

                }
            }


        }
        if(player!=null && player.world!=null) player.exitGen(this, player.posX, player.posY);
        if(wb!=null) wb.exitGen(this,x,y);
    }





    /**
     * this method checks the win condition of the level if the tagets are all
     * filled in the level and it's sublevels
     * the idea is simple in every level and it's sublevel i'm gonna create a method that checks if the all the
     * tagerts in the level are filled and in this function aka "hasWin" im gonna call them and they all must return
     * true to make this method returns true .
     * @return
     */
    public static String filetolvl(String path) {
        try {

            // Créer l'objet File Reader
            FileReader fr = new FileReader(System.getProperty("user.dir") + "\\src\\assets\\outlevels\\" + path);
            // Créer l'objet BufferedReader
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                // ajoute la ligne au buffer

                sb.append(line);
                sb.append("\n");
            }
            fr.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    //ya wldi clean code ta3 sah XD
   static boolean hasWin() {
    for (Map.Entry<String, Level> entry : LinkedLevel.entrySet()) {
        if (!entry.getValue().levelTargetsAreFilled()) {
            return false;
        }
    }
    return true;
   }

    /**
     * this one is to ckeck the level tagerts
     * @return
     */
   public boolean levelTargetsAreFilled () {
        for (Target t : tagerts) {
            if (!t.isFilled()) {
                return false;
            }
        }
       return true;
   }

     public void wallGestion(){
         for (int j = 0; j < this.size; j++) {
             for (int i = 0; i < this.size; i++) {
                 if(this.map[i][j] instanceof Wall) {
                     if(i==0||this.map[i-1][j]instanceof Wall){
                         //Mur Gauche
                         if (j==0||this.map[i][j-1]instanceof Wall){
                             //Mur Haut
                             if(i==size-1||this.map[i+1][j]instanceof Wall){
                                 //Mur Droit
                                 if (j==size-1||this.map[i][j+1]instanceof Wall)
                                     //Mur bas
                                     map[i][j].sprite="assets/Walls/innerWall.png";
                                 else map[i][j].sprite="assets/Walls/Squarewall 3connectionB.png";
                             }
                             else{
                                 if (j==size-1||this.map[i][j+1]instanceof Wall)
                                     //Mur bas
                                     map[i][j].sprite="assets/Walls/Squarewall 3connectionR.png";
                                 else map[i][j].sprite="assets/Walls/CornerBR.png";
                             }
                         }
                         else{
                             if(i==size-1||this.map[i+1][j]instanceof Wall){
                                 //Mur Droit
                                 if (j==size-1||this.map[i][j+1]instanceof Wall)
                                     //Mur bas
                                     map[i][j].sprite="assets/Walls/Squarewall 3connectionU.png";
                                 else map[i][j].sprite="assets/Walls/corridorwallH.png";
                             }
                             else{
                                 if (j==size-1||this.map[i][j+1]instanceof Wall)
                                     //Mur bas
                                     map[i][j].sprite="assets/Walls/CornerUR.png";
                                 else map[i][j].sprite="assets/Walls/Squarewall1connectionR.png";
                             }

                         }
                     }else{
                         if (j==0||this.map[i][j-1]instanceof Wall){
                             //Mur Haut
                             if(i==size-1||this.map[i+1][j]instanceof Wall){
                                 //Mur Droit
                                 if (j==size-1||this.map[i][j+1]instanceof Wall)
                                     //Mur bas
                                     map[i][j].sprite="assets/Walls/Squarewall 3connectionL.png";
                                 else map[i][j].sprite="assets/Walls/CornerBL.png";
                             }
                             else{
                                 if (j==size-1||this.map[i][j+1]instanceof Wall)
                                     //Mur bas
                                     map[i][j].sprite="assets/Walls/corridorwall.png";
                                 else map[i][j].sprite="assets/Walls/Squarewall1connectionB.png";
                             }
                         }
                         else{
                             if(i==size-1||this.map[i+1][j]instanceof Wall){
                                 //Mur Droit
                                 if (j==size-1||this.map[i][j+1]instanceof Wall)
                                     //Mur bas
                                     map[i][j].sprite="assets/Walls/CornerUL.png";
                                 else map[i][j].sprite="assets/Walls/Squarewall1connectionL.png";
                             }
                             else{
                                 if (j==size-1||this.map[i][j+1]instanceof Wall)
                                     //Mur bas
                                     map[i][j].sprite="assets/Walls/Squarewall1connectionU.png";
                                 else map[i][j].sprite="assets/Walls/Squarewall.png";
                             }

                         }
                     }
                 }
             }
         }
   }

    public String ShortestPath(int x, int y) {
        int[] dx = {1, 0, -1, 0};
        int[] dy = {0, 1, 0, -1};
        String[] dir = {"R", "D", "L", "U"};

        int sx = player.posX;
        int sy = player.posY;
        int[][] t = new int[this.size][this.size];
        for (int j = 0; j < this.size; j++)
            for (int i = 0; i < this.size; i++) {
                if (this.map[i][j] instanceof EmptySpace && ((EmptySpace) this.map[i][j]).isEmpty()) t[i][j] = 1;
                else t[i][j] = 0;
            }
        boolean[][] visited = new boolean[size][size];
        int[][] dist = new int[size][size];
        String[][] path = new String[size][size];

        for (int i = 0; i < size; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
            Arrays.fill(path[i], "");
        }

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{sx, sy});
        visited[sx][sy] = true;
        dist[sx][sy] = 0;
        while(!queue.isEmpty()){
            int[] curr =queue.poll();

            int cx = curr[0];
            int cy = curr[1];

            if(cx==x && cy==y){
                return path[x][y];
            }
            for (int i = 0; i < 4; i++) {
                int nx = cx +dx[i];
                int ny = cy +dy[i];

                if(nx>=0 && nx<size && ny>=0 && ny < size && t[nx][ny]==1 && !visited[nx][ny]){
                    visited[nx][ny] = true ;
                    dist[nx][ny] = dist[cx][cy] + 1;
                    path[nx][ny] = path[cx][cy]+dir[i];
                    queue.offer(new int[]{nx,ny});
                }

            }

        }
        return "";







    }
    //Méthode Principal de Gestion d'un mouvement (dx;dy) d'un objet en position (x;y) dans une direction "dir"
    public int isMovableHere(int x, int y, int dx, int dy, String dir) {
       //exception
       if((this.exit.isEmpty()) && ((x+dx >=size) || (x+dx < 0) || (y+dy >=size) || (y+dy < 0))){
           return 0;
       }
       //exception
       if((this.cycle==0) && CycleCheck(x,y,dx,dy,dir)) {
           return 0;
       }
        //Gen Exit du player Im A World
        if (((EmptySpace) this.map[x][y]).Content instanceof Player) {
            if(this.player!=null && this.player.world!=null)
                ((Player) ((EmptySpace) this.map[x][y]).Content).exitGen(this,x, y );
        }

        //Vérification si l'objet essaye de sortir
        if (! this.exit.isEmpty() ){
            for(int i=0;i<this.exit.size();i++){
                if (this.exit.get(i).isExit(x, y, dir)){
                    if(((EmptySpace) this.map[x][y]).Content instanceof Player) {
                        switch (ExitTheWorldBox(x, y, dx, dy, dir, i)){
                            case 1 :
                                return 3;
                            case 2:
                                return 4;
                            default:
                                break;
                        }

                    }


                    if (( (this.exit.get(i).boxx+dx >=this.exit.get(i).Lvl.size) || (this.exit.get(i).boxx+dx < 0) || (this.exit.get(i).boxy+dy >=this.exit.get(i).Lvl.size) || (this.exit.get(i).boxy+dy < 0))){
                        ArrayList<Infinibox> tmp =new ArrayList<>();
                        for (Infinibox c: Level.inf) {
                            if(c.levelbound.equals(exit.get(i).Lvl.id) ) {tmp.add(c);
                                ;}
                        }
                        int cmp =1, r=0;
                        boolean found = false;
                        while(!found && cmp<=tmp.size()){
                            if(tmp.get(r).grade==cmp){
                                Level L = Level.LinkedLevel.get(tmp.get(r).actualevel);
                                if(tmp.get(r).x+dx<0 || tmp.get(r).x+dx>=L.size || tmp.get(r).y+dy <0 || tmp.get(r).y+dy>=L.size){
                                    cmp++; r =0;
                                     continue;
                                }

                                if ( (!((this.map[tmp.get(r).x + dx][tmp.get(r).y + dy]) instanceof Wall) && ((  (((EmptySpace) this.map[tmp.get(r).x + dx][tmp.get(r).y + dy])).Content==null)
                                || (L.isMovableHere(tmp.get(r).x + dx, tmp.get(r).y + dy, dx, dy, dir) == 1)))){
                                    ((EmptySpace) this.map[tmp.get(r).x + dx][tmp.get(r).y + dy]).Content = ( ((EmptySpace) this.map[x][y]).Content);

                                    ((EmptySpace) this.map[x][y]).Content = null;

                                    return 1;
                                }

                            }else r++;
                        }
                        return 0;
                    }
                    if(!(this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy] instanceof Wall))
                    if(((((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx+dx][this.exit.get(i).boxy + dy]).Content instanceof WorldBox))){
                        if(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.size==this.size){

                                //Transfert
                                if(!((((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[x-dx*(size-1)][y-dy*(size-1)]) instanceof Wall )){
                                    if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[x - dx * (size - 1)][y - dy * (size - 1)]).Content == null)
                                            || (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.isMovableHere(x - dx * (size - 1),y - dy * (size - 1) ,dx,dy,dir ) == 1)){
                                        ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[x - dx * (size - 1)][y - dy * (size - 1)]).Content = ((EmptySpace) this.map[x][y]).Content ;
                                        if(((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[x - dx * (size - 1)][y - dy * (size - 1)]).Content instanceof WorldBox) ((WorldBox) ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[x - dx * (size - 1)][y - dy * (size - 1)]).Content).exitGen(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside,x - dx * (size - 1),y - dy * (size - 1));
                                        ((EmptySpace) this.map[x][y]).Content = null ;
                                        return 1;
                                    }
                                }

                            }else{
                                // Entrée standard aprés sortie (all -> centre)
                                if(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).entryCheck(dir)){
                                    int s = ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.size;

                                    if(dir.equals("up")){
                                        int g = ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).entryDown();
                                        if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][s-1]).Content == null)
                                                || (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.isMovableHere(g,s-1,dx,dy,dir ) == 1)){
                                            ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][s-1]).Content = ((EmptySpace) this.map[x][y]).Content ;
                                            if(((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][s-1]).Content instanceof WorldBox) ((WorldBox) ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][s-1]).Content).exitGen(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside,g,s-1);
                                            ((EmptySpace) this.map[x][y]).Content = null ;
                                            return 1;


                                        }
                                    }
                                    else if(dir.equals("down")){
                                        int g = ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).entryUp();
                                        if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][0]).Content == null)
                                                || (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.isMovableHere(g,0,dx,dy,dir ) == 1)){
                                            ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][0]).Content = ((EmptySpace) this.map[x][y]).Content ;
                                            if(((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][0]).Content instanceof WorldBox) ((WorldBox) ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][s-1]).Content).exitGen(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside,g,0);
                                            ((EmptySpace) this.map[x][y]).Content = null ;
                                            return 1;


                                        }
                                    }
                                    else if(dir.equals("left")){
                                        int g = ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).entryRight();
                                        if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[s-1][g]).Content == null)
                                                || (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.isMovableHere(s-1,g,dx,dy,dir ) == 1)){
                                            ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[s-1][g]).Content = ((EmptySpace) this.map[x][y]).Content ;
                                            if(((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[s-1][g]).Content instanceof WorldBox) ((WorldBox) ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[s-1][g]).Content).exitGen(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside,s-1,g);
                                            ((EmptySpace) this.map[x][y]).Content = null ;
                                            return 1;


                                        }
                                    }
                                    else if(dir.equals("right")){
                                        int g = ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).entryLeft();
                                        if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[0][g]).Content == null)
                                                || (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.isMovableHere(0,g,dx,dy,dir ) == 1)){
                                            ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[0][g]).Content = ((EmptySpace) this.map[x][y]).Content ;
                                            if(((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[0][g]).Content instanceof WorldBox) ((WorldBox) ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[0][g]).Content).exitGen(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside,0,g);
                                            ((EmptySpace) this.map[x][y]).Content = null ;
                                            return 1;


                                        }

                                    }
                                }
                            }

                        }
                    //Gestion des exit d'une box
                    int xx = this.exit.get(i).boxx;
                    int yy = this.exit.get(i).boxy;
                    if (( (this.exit.get(i).boxx+dx <this.exit.get(i).Lvl.size) && (this.exit.get(i).boxx+dx > 0)
                            && (this.exit.get(i).boxy+dy <this.exit.get(i).Lvl.size) && (this.exit.get(i).boxy+dy > 0))
                            && !(this.exit.get(i).Lvl.map[xx+dx][yy + dy] instanceof Wall)
                            &&( (((EmptySpace) this.exit.get(i).Lvl.map[xx+dx][yy + dy]).Content == null)
                            || (this.exit.get(i).Lvl.isMovableHere(xx+dx,yy+dy, dx, dy, dir)==1) ) ) {

                        ((EmptySpace) this.exit.get(i).Lvl.map[xx+dx][yy+dy]).Content = ((EmptySpace) this.map[x][y]).Content;

                        ((EmptySpace) this.map[x][y]).Content = null;
                        if(((EmptySpace) this.exit.get(i).Lvl.map[xx+dx][yy+dy]).Content instanceof WorldBox)
                            ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[xx+dx][yy+dy]).Content).exitGen(this.exit.get(i).Lvl,xx+dx,yy+dy);
                        return 1;
                    }
                    return 0;
                }
            }

        }

        // On test différent cas sur la case ou on essaye de se rendre (Wall -> EmptyCase -> WorldBox -> MovableContent -> EdibleContent)
        if (!(this.map[x + dx][y + dy] instanceof Wall)) {


            if (((EmptySpace) this.map[x + dx][y + dy]).Content == null) {
                if(((EmptySpace) this.map[x][y]).Content instanceof Infinibox){
                    ((Infinibox) ((EmptySpace) this.map[x][y]).Content).x = ((Infinibox) ((EmptySpace) this.map[x][y]).Content).x + dx;
                    ((Infinibox) ((EmptySpace) this.map[x][y]).Content).y = ((Infinibox) ((EmptySpace) this.map[x][y]).Content).y + dy;
                }


                if (((EmptySpace) this.map[x][y]).Content instanceof WorldBox){
                    ((WorldBox)((EmptySpace) this.map[x][y]).Content).exitGen(this, x+dx, y+dy);
                }
                 

                ((EmptySpace) this.map[x + dx][y + dy]).Content = ((EmptySpace) this.map[x][y]).Content;
                ((EmptySpace) this.map[x][y]).Content = null;
                if (((EmptySpace) this.map[x+dx][y+dy]).Content instanceof Player) {
                    if(this.player.world!=null)
                        ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).exitGen(this, x+dx , y+dy );
                }
                if(this.player !=null){
                    if(((EmptySpace) this.map[x+dx][y+dy]).Content instanceof WorldBox){
                        WorldBox wb = (WorldBox)((EmptySpace) this.map[x+dx][y+dy]).Content ;
                        if (wb.inside == this&&this.tM.size()==2) this.tM.get(1).isMoving=true;
                    }
                    TransitionMaker.setDirection(dx,dy);
                    TransitionMaker.incrementer();    
                }
                return 1;
            }
            else if (((EmptySpace)this.map[x + dx][y + dy]).Content instanceof WorldBox){




                if (! this.exit.isEmpty() && ( (this.exit.get(0).boxx+dx >=this.exit.get(0).Lvl.size) || (this.exit.get(0).boxx+dx < 0) || (this.exit.get(0).boxy+dy >=this.exit.get(0).Lvl.size) || (this.exit.get(0).boxy+dy < 0))){
                    ArrayList<Infinibox> tmp =new ArrayList<>();

                    for (Infinibox c: Level.inf) {
                        if(c.levelbound.equals(exit.get(0).Lvl.id) ) {tmp.add(c);
                            ;}
                    }
                    int cmp =1, r=0;
                    boolean found = false;
                    while(!found && cmp<=tmp.size()){
                        if(tmp.get(r).grade==cmp){
                            Level L = Level.LinkedLevel.get(tmp.get(r).actualevel);
                            if(tmp.get(r).x+dx<0 || tmp.get(r).x+dx>=L.size || tmp.get(r).y+dy <0 || tmp.get(r).y+dy>=L.size){
                                cmp++; r =0; continue;
                            }

                            if ( (!((this.map[tmp.get(r).x + dx][tmp.get(r).y + dy]) instanceof Wall) && ((  (((EmptySpace) this.map[tmp.get(r).x + dx][tmp.get(r).y + dy])).Content==null)
                                    || (L.isMovableHere(tmp.get(r).x + dx, tmp.get(r).y + dy, dx, dy, dir) == 1)))){
                                ((EmptySpace) this.map[tmp.get(r).x + dx][tmp.get(r).y + dy]).Content = ( ((EmptySpace) this.map[x+dx][y+dy]).Content);
                                ((EmptySpace) this.map[x+dx][y+dy]).Content = ((EmptySpace) this.map[x][y]).Content;
                                 ((EmptySpace) this.map[x][y]).Content = null;

                                ((WorldBox)((EmptySpace) this.map[tmp.get(r).x + dx][tmp.get(r).y + dy]).Content).exitGen(this,tmp.get(r).x + dx,tmp.get(r).y + dy);
                                return 1;

                            }


                        }else r++;
                    }
                    return 0;
                }


                boolean CantExit = false;
                for(int i=0;i<this.exit.size();i++) {
                    if (this.exit.get(i).isExit(x+dx, y+dy, dir)) {
                        int xx = this.exit.get(i).boxx;
                        int yy = this.exit.get(i).boxy;
                        if (!(this.exit.get(i).Lvl.map[xx+dx][yy + dy] instanceof Wall)
                                &&( (((EmptySpace) this.exit.get(i).Lvl.map[xx+dx][yy + dy]).Content == null)
                                || (this.exit.get(i).Lvl.isMovableHere(xx+dx,yy+dy, dx, dy, dir)==1) ) ) {

                            ((EmptySpace) this.exit.get(i).Lvl.map[xx+dx][yy+dy]).Content = ((EmptySpace) this.map[x+dx][y+dy]).Content;
                            ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[xx+dx][yy+dy]).Content).exitGen(this.exit.get(i).Lvl,xx+dx,yy+dy);
                            ((EmptySpace) this.map[x+dx][y+dy]).Content = null;
                            ((EmptySpace)this.map[x + dx][y + dy]).Content = ((EmptySpace)this.map[x][y]).Content;
                            ((EmptySpace)this.map[x][y]).Content = null;
                            return 1;
                        }
                        CantExit = true;
                    }
                }

                if( (!CantExit) && ((isMovableHere(x + dx, y + dy, dx, dy, dir) == 1))){
                    ((WorldBox) ((EmptySpace) this.map[x + 2 * dx][y + 2 * dy]).Content).exitGen(this, x + 2 * dx, y + 2 * dy);
                    ((EmptySpace) this.map[x + dx][y + dy]).Content = ((EmptySpace) this.map[x][y]).Content;
                    ((EmptySpace) this.map[x][y]).Content = null;
                    if (this.player != null) {
                        TransitionMaker.incrementer();
                        if (((EmptySpace) this.map[x + dx][y + dy]).Content instanceof WorldBox) {
                            WorldBox wb = (WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content;
                            if (wb.inside == this) this.tM.get(1).isMoving = true;
                        }
                    }
                    return 1;
                }
                if( ( (x+2*dx<size) && (x+2*dx >= 0) && (y+2*dy<size) && (y+2*dy>= 0))
                        &&!(this.map[x+2*dx][y+2*dy] instanceof Wall)){
                    if(!this.exit.isEmpty() && CanIPush(x,y,dx,dy,dir)){
                        return 0;
                    }
                    if(CheckWorldBox(x+2*dx, y+2*dy, -dx, -dy, ReverseDir(dir)) == 1){
                        ((EmptySpace)this.map[x + 2*dx][y + 2*dy]).Content = ((EmptySpace)this.map[x+dx][y+dy]).Content;
                        ((EmptySpace)this.map[x + dx][y + dy]).Content = ((EmptySpace)this.map[x][y]).Content;
                        ((EmptySpace)this.map[x][y]).Content = null;
                       if (((EmptySpace) this.map[x+dx][y+dy]).Content instanceof Player) {
                            if(this.player.world!=null)
                                ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).exitGen(this, x+dx , y+dy );
                        }
                        ((WorldBox) ((EmptySpace) this.map[x + 2*dx][y + 2*dy]).Content).exitGen(this,x+2*dx,y+2*dy);
                        return 1;
                    }
                    if (CanIPush(x, y, dx, dy, dir)) {
                        return 0;
                    }
                }

                if (((EmptySpace) this.map[x][y]).Content instanceof Player) {
                    int check = PlayerCheckWorldBox(x, y, dx, dy, dir);
                    if(check==2) return 2;
                    if((this.player.world!=null) && (!(this.map[x+dx][y+dy] instanceof Wall))){
                        if((CheckPlayerWorld(x+dx, y+dy, -dx, -dy, ReverseDir(dir)) == 1)){
                            ((EmptySpace)this.map[x + dx][y + dy]).Content = ((EmptySpace)this.map[x][y]).Content;
                            ((EmptySpace)this.map[x][y]).Content = null;
                            if (((EmptySpace) this.map[x+dx][y+dy]).Content instanceof Player) {
                                if(this.player.world!=null)
                                    ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).exitGen(this, x+dx , y+dy );
                            }
                            return 1;
                        }
                    }
                } else {
                    return CheckWorldBox(x, y, dx, dy, dir);
                }
            }
            if (isMovableHere(x + dx, y + dy, dx, dy, dir) == 1) {
                if(((EmptySpace) this.map[x][y]).Content instanceof Infinibox){
                    ((Infinibox) ((EmptySpace) this.map[x][y]).Content).x = ((Infinibox) ((EmptySpace) this.map[x][y]).Content).x + dx;
                    ((Infinibox) ((EmptySpace) this.map[x][y]).Content).y = ((Infinibox) ((EmptySpace) this.map[x][y]).Content).y + dy;
                }
                ((EmptySpace)this.map[x + dx][y + dy]).Content = ((EmptySpace)this.map[x][y]).Content;
                ((EmptySpace)this.map[x][y]).Content = null;

                if (((EmptySpace) this.map[x+dx][y+dy]).Content instanceof Player) {
                    if(this.player.world!=null)
                        ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).exitGen(this, x + dx, y + dy);
                }
                if(this.player!=null){
                    if(((EmptySpace) this.map[x+dx][y+dy]).Content instanceof WorldBox){
                        WorldBox wb = (WorldBox)((EmptySpace) this.map[x+dx][y+dy]).Content ;
                        if (wb.inside == this) this.tM.get(1).isMoving=true;
                    }
                    TransitionMaker.incrementer();
                }
                return 1;
            }
            if((((EmptySpace)this.map[x][y]).Content instanceof Player) && (this.player.world!=null) && (!(this.map[x+dx][y+dy] instanceof Wall))){
                if((CheckPlayerWorld(x+dx, y+dy, -dx, -dy, ReverseDir(dir)) == 1)){
                    ((EmptySpace)this.map[x + dx][y + dy]).Content = ((EmptySpace)this.map[x][y]).Content;
                    ((EmptySpace)this.map[x][y]).Content = null;

                    return 1;
                }
            }

        }
        return 0;
    }

    //Renvoie la Direction inversé
    public String ReverseDir(String dir){
        if(dir.equals("up")){
            return "down";
        }
        else if(dir.equals("down")){
            return "up";
        }
        else if(dir.equals("left")){
            return "right";
        }
        else if(dir.equals("right")){
            return "left";
        }
        return null;
    }

    //Test si une ligne ou colonne est "occupé"
    public boolean IsFill(int x, int y, int dx, int dy, int size,Level lvl) {
        for(int i=0;i<size;i++){
            if((lvl.map[x + i*dx][y + i*dy] instanceof Wall) || (((EmptySpace) lvl.map[x + i*dx][y + i*dy]).Content == null) ){
                return false;
            }
        }
        return true;
    }

    //Renvoie la coordonnée d'une WorldBox du meme Lvl sur la ligne ou colonne ou 0
    public int IsFull(int x, int y, int dx, int dy, int size,Level lvl) {
       int i;
        for(i=0;i<size;i++){
            if((!(lvl.map[x + i*dx][y + i*dy] instanceof Wall)) && ((EmptySpace)lvl.map[x + i*dx][y + i*dy]).Content instanceof WorldBox && ((WorldBox)((EmptySpace)lvl.map[x + i*dx][y + i*dy]).Content).inside.equals(this)){
                return i;
            }
        }
        return 0;
    }

    //Test si il y a une exit sur la position (x;y)
    public boolean IsAnExit(int x, int y,String dir) {
        for (int i = 0; i < this.exit.size(); i++) {
            if (this.exit.get(i).isExit(x, y, dir)) {
                return true;
            }
        }
        return false;
    }

    //Brouillon de la gestion d'un "Cycle", on arrive à detecter la présence d'un cycle dans un monde
    // mais pas à le gérer correctement, on le bloque alors pour éviter les erreurs
    public boolean CycleCheck(int x, int y, int dx, int dy, String dir) {
        int size = this.size;
        int i = 0;
        if(dir.equals("up")){
            if((i=IsFull(x, y, 0, -1, y, this)) != 0 ){
                System.out.print(i);
                if(IsAnExit(x-dx,y-dy,ReverseDir(dir))){
                    this.cycle ++;
                    /*
                    Element temp = ((EmptySpace) this.map[x][i-1]).Content;
                    ((EmptySpace) this.map[x][i-1]).Content = null;
                    isMovableHere(x,y,dx,dy,dir);
                    ((EmptySpace) this.map[x][y]).Content= temp;
                    */
                    this.cycle=0;
                    return true;
                }
            }
            if(IsFill(x, y, 0, -1, y, this) && (!IsAnExit(x,y,ReverseDir(dir))) &&(!(this.map[x -dx][y + -dy] instanceof Wall)) && ((EmptySpace)this.map[x - dx][y - dy]).Content instanceof WorldBox && ((WorldBox)((EmptySpace)this.map[x - dx][y - dy]).Content).inside.equals(this)
            && IsAnExit(x,0,dir))  {
                this.cycle ++;
                /*Element temp = ((EmptySpace) this.map[x][0]).Content;
                ((EmptySpace) this.map[x][0]).Content = null;
                isMovableHere(x,y,dx,dy,dir);
                ((EmptySpace) this.map[x][y]).Content= temp;*/
                this.cycle=0;
                return true;
            }

        }
        else if(dir.equals("down")){
            Boolean check = false;
            if((i=IsFull(x, y, 0, 1, size-y, this)) != 0){
                if(IsAnExit(x-dx,y-dy,ReverseDir(dir))){
                    this.cycle ++;/*
                    Element temp = ((EmptySpace) this.map[x][i+1]).Content;
                    ((EmptySpace) this.map[x][i+1]).Content = null;
                    isMovableHere(x,y,dx,dy,dir);
                    ((EmptySpace) this.map[x][y]).Content= temp;
                    */this.cycle=0;
                    return true;
                }
            }
            if(IsFill(x, y, 0, 1, size-y, this) && (!IsAnExit(x,y,ReverseDir(dir))) &&(!(this.map[x -dx][y + -dy] instanceof Wall)) && ((EmptySpace)this.map[x - dx][y - dy]).Content instanceof WorldBox && ((WorldBox)((EmptySpace)this.map[x - dx][y - dy]).Content).inside.equals(this)
            && IsAnExit(x,size-1,dir))  {
                this.cycle ++;
                /*Element temp = ((EmptySpace) this.map[x][size-1]).Content;
                ((EmptySpace) this.map[x][size-1]).Content = null;
                isMovableHere(x,y,dx,dy,dir);
                ((EmptySpace) this.map[x][y]).Content= temp;*/
                this.cycle=0;
                return true;
            }
        }

        else if(dir.equals("left")){
            if((i=IsFull(x, y, -1, 0, x, this))!= 0){
                if(IsAnExit(x-dx,y-dy,ReverseDir(dir))) {
                    this.cycle ++;
                    /*Element temp = ((EmptySpace) this.map[i+1][y]).Content;
                    ((EmptySpace) this.map[i+1][y]).Content = null;
                    isMovableHere(x,y,dx,dy,dir);
                    ((EmptySpace) this.map[x][y]).Content= temp;*/
                    this.cycle=0;
                    return true;
                }
           }
            if(IsFill(x, y, -1, 0, x, this) && (!IsAnExit(x,y,dir)) &&(!IsAnExit(x,y,ReverseDir(dir))) &&(!(this.map[x -dx][y + -dy] instanceof Wall))&&((EmptySpace)this.map[x - dx][y - dy]).Content instanceof WorldBox && ((WorldBox)((EmptySpace)this.map[x - dx][y - dy]).Content).inside.equals(this)
                    && IsAnExit(0,y,dir))  {
                this.cycle ++;
                /*Element temp = ((EmptySpace) this.map[0][y]).Content;
                ((EmptySpace) this.map[0][y]).Content = null;
                isMovableHere(x,y,dx,dy,dir);
                ((EmptySpace) this.map[x][y]).Content= temp;*/
                this.cycle=0;
                return true;
            }
        }
        else if(dir.equals("right")){
            if((i=IsFull(x, y, 1, 0, size-x, this)) != 0){
                System.out.print(i);
                if(IsAnExit(x,y,ReverseDir(dir))){
                    this.cycle ++;
                   /* Element temp = ((EmptySpace) this.map[i-1][y]).Content;
                    ((EmptySpace) this.map[i-1][y]).Content = null;
                    isMovableHere(x,y,dx,dy,dir);
                    ((EmptySpace) this.map[x][y]).Content= temp;*/
                    this.cycle=0;
                    return true;
                }
            }
            if(IsFill(x, y, 1, 0, size-x, this) && (!this.exit.isEmpty())&&(!IsAnExit(x,y,ReverseDir(dir)))
            && (!(this.map[x -dx][y + -dy] instanceof Wall))&&((EmptySpace)this.map[x - dx][y - dy]).Content instanceof WorldBox
            && ((WorldBox)((EmptySpace)this.map[x - dx][y - dy]).Content).inside.equals(this) && IsAnExit(size-1,y,dir))  {
                this.cycle ++;
               /*Element temp = ((EmptySpace) this.map[size-1][y]).Content;
                ((EmptySpace) this.map[size-1][y]).Content = null;
                isMovableHere(x,y,dx,dy,dir);
                ((EmptySpace) this.map[x][y]).Content= temp;*/
                this.cycle =0;
                return true;
            }
        }
        return  false;
    }

    //Gestion de la mécanique de "pousette" entre une worlbox et un tierce objet
    public boolean CanIPush(int x, int y, int dx, int dy, String dir) {
        System.out.print("Fonction");
        int size = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).inside.size;
        if(dir.equals("up")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryDown();
            if(((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryCheck(dir)) {
                if (((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).entryCheck(ReverseDir(dir))) {
                    if (IsFill(i, size - 1, 0, -1, size, ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside)) {
                        Element temp = ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][size - 1]).Content;
                        ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][size - 1]).Content = null;
                        CheckWorldBox(x + 2 * dx, y + 2 * dy, -dx, -dy, ReverseDir(dir));
                        ((EmptySpace) this.map[x + 2 * dx][y + 2 * dy]).Content = ((EmptySpace) this.map[x + dx][y + dy]).Content;
                        ((EmptySpace) this.map[x + dx][y + dy]).Content = temp;
                        return true;
                    }
                }
            }
        }

        else if(dir.equals("down")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryUp();
            if(((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryCheck(dir)) {
                if (((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).entryCheck(ReverseDir(dir))) {
                    if (IsFill(i, 0, dx, dy, size, ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside)) {
                        Element temp = ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][0]).Content;
                        ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][0]).Content = null;
                        CheckWorldBox(x + 2 * dx, y + 2 * dy, -dx, -dy, ReverseDir(dir));
                        ((EmptySpace) this.map[x + 2 * dx][y + 2 * dy]).Content = ((EmptySpace) this.map[x + dx][y + dy]).Content;
                        ((EmptySpace) this.map[x + dx][y + dy]).Content = temp;
                        return true;
                    }
                }
            }
        }
        else if(dir.equals("left")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryRight();
            if(((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryCheck(dir)) {
                if (((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).entryCheck(ReverseDir(dir))) {
                    if (IsFill(size - 1, i, -1, 0, size, ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside)) {
                        Element temp = ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[size - 1][i]).Content;
                        ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[size - 1][i]).Content = null;
                        CheckWorldBox(x + 2 * dx, y + 2 * dy, -dx, -dy, ReverseDir(dir));
                        ((EmptySpace) this.map[x + 2 * dx][y + 2 * dy]).Content = ((EmptySpace) this.map[x + dx][y + dy]).Content;
                        ((EmptySpace) this.map[x + dx][y + dy]).Content = temp;
                        return true;
                    }
                }
            }
        }
        else if(dir.equals("right")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryLeft();
            if(((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryCheck(dir)) {
                if (((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).entryCheck(ReverseDir(dir))) {
                    if (IsFill(0, i, dx, dy, size, ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside)) {
                        Element temp = ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[0][i]).Content;
                        ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[0][i]).Content = null;
                        CheckWorldBox(x + 2 * dx, y + 2 * dy, -dx, -dy, ReverseDir(dir));
                        ((EmptySpace) this.map[x + 2 * dx][y + 2 * dy]).Content = ((EmptySpace) this.map[x + dx][y + dy]).Content;
                        ((EmptySpace) this.map[x + dx][y + dy]).Content = temp;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Vérification de la possible entrée dans un monde joueur
    public int CheckPlayerWorld(int x, int y, int dx, int dy, String dir) {
        int size =((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.size;
        if(dir.equals("up")){
            int i = ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).entryDown();
            if(((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).entryCheck(dir)) {
                if ((((EmptySpace) ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.map[i][size - 1]).Content == null)
                        || (((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.isMovableHere(i, size - 1, dx, dy, dir) == 1)) {
                    ((EmptySpace) ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.map[i][size - 1]).Content = ((EmptySpace) this.map[x][y]).Content;
                    if(((EmptySpace) ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world.map[i][size - 1]).Content instanceof WorldBox ) ((WorldBox)((EmptySpace) ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world.map[i][size - 1]).Content).exitGen(((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world, i, size -1);
                    ((EmptySpace) this.map[x][y]).Content = null;
                    return 1;
                }
                else {
                    return CheckWorldBox(x+2*dx, y+2*dy, -2*dx, -2*dy, ReverseDir(dir));
                }
            }
        }
        else if(dir.equals("down")){
            int i = ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).entryUp();
            if(((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).entryCheck(dir)) {
                if ((((EmptySpace) ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.map[i][0]).Content == null)
                        || (((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.isMovableHere(i, 0, dx, dy, dir) == 1)) {
                    ((EmptySpace) ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.map[i][0]).Content = ((EmptySpace) this.map[x][y]).Content;
                    ((EmptySpace) this.map[x][y]).Content = null;
                    if(((EmptySpace) ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world.map[i][0]).Content instanceof WorldBox ) ((WorldBox)((EmptySpace) ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world.map[i][0]).Content).exitGen(((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world, i, 0);
                    return 1;
                }
                else {
                    return CheckWorldBox(x+2*dx, y+2*dy, -2*dx, -2*dy, ReverseDir(dir));
                }
            }
        }
        else if(dir.equals("left")){
            int i = ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).entryRight();
            if(((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).entryCheck(dir)) {
                if ((((EmptySpace) ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.map[size - 1][i]).Content == null)
                        || (((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.isMovableHere(size - 1, i, dx, dy, dir) == 1)) {
                    ((EmptySpace) ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.map[size - 1][i]).Content = ((EmptySpace) this.map[x][y]).Content;
                    ((EmptySpace) this.map[x][y]).Content = null;
                    if(((EmptySpace) ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world.map[size -1][i]).Content instanceof WorldBox ) ((WorldBox)((EmptySpace) ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world.map[size -1][i]).Content).exitGen(((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world, size-1, i);
                    return 1;
                }
                else {
                    return CheckWorldBox(x+2*dx, y+2*dy, -2*dx, -2*dy, ReverseDir(dir));
                }
            }
        }
        else if(dir.equals("right")){
            int i = ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).entryLeft();
            if(((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).entryCheck(dir)) {
                if ((((EmptySpace) ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.map[0][i]).Content == null)
                        || (((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.isMovableHere(0, i, dx, dy, dir) == 1)) {
                    ((EmptySpace) ((Player)((EmptySpace)this.map[x+dx][y+dy]).Content).world.map[0][i]).Content = ((EmptySpace) this.map[x][y]).Content;
                    ((EmptySpace) this.map[x][y]).Content = null;
                    if(((EmptySpace) ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world.map[0][i]).Content instanceof WorldBox ) ((WorldBox)((EmptySpace) ((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world.map[0][i]).Content).exitGen(((Player) ((EmptySpace) this.map[x + dx][y + dy]).Content).world, 0, i);

                    return 1;
                }
                else {
                    return CheckWorldBox(x+2*dx, y+2*dy, -2*dx, -2*dy, ReverseDir(dir));
                }
            }
        }
        return 0;
    }

    //Vérification de la possible entrée d'un joueur dans une WorldBox
    public int PlayerCheckWorldBox(int x, int y, int dx, int dy, String dir) {
        int size = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).inside.size;
        if(dir.equals("up")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryDown();
            System.out.println(size);
            return EntryInWorldBox(x,y,dx,dy,dir,i,size-1) ?2:0;
        }
        else if(dir.equals("down")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryUp();
            return EntryInWorldBox(x,y,dx,dy,dir,i,0) ?2:0;
        }
        else if(dir.equals("left")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryRight();
            return EntryInWorldBox(x,y,dx,dy,dir,size-1,i) ?2:0;
        }
        else if(dir.equals("right")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryLeft();
            return EntryInWorldBox(x,y,dx,dy,dir,0,i) ?2:0;
        }
        return 0;
    }

    //Vérification de la possible entrée d'une box dans une WorldBox
    public int CheckWorldBox(int x, int y, int dx, int dy, String dir) {
        int size = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).inside.size;
        if(dir.equals("up")) {
            int i = ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).entryDown();
            if (((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).entryCheck(dir)) {
                if (!IsFill(i, size - 1, 0, -1, size, ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside)) {
                    if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][size - 1]).Content == null)
                            || (((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.isMovableHere(i, size - 1, dx, dy, dir) == 1)) {
                        ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][size - 1]).Content = ((EmptySpace) this.map[x][y]).Content;
                        ((EmptySpace) this.map[x][y]).Content = null;
                        if(((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][size - 1]).Content instanceof WorldBox ) ((WorldBox)((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][size - 1]).Content).exitGen(((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside, i, size -1);
                        return 1;
                    }
                }
            }
        }
        else if(dir.equals("down")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryUp();
            if(((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryCheck(dir)) {
                if (!IsFill(i,0, dx, dy, size, ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside)) {
                    if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][0]).Content == null)
                            || (((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.isMovableHere(i, 0, dx, dy, dir) == 1)) {
                        ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][0]).Content = ((EmptySpace) this.map[x][y]).Content;
                        ((EmptySpace) this.map[x][y]).Content = null;
                        if(((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][0]).Content instanceof WorldBox ) ((WorldBox)((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[i][0]).Content).exitGen(((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside, i, 0);

                        return 1;
                    }
                }
            }
        }
        else if(dir.equals("left")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryRight();
            if(((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryCheck(dir)) {
                if (!IsFill(size-1, i, -1, 0, size, ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside)) {
                    if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[size - 1][i]).Content == null)
                            || (((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.isMovableHere(size - 1, i, dx, dy, dir) == 1)) {
                        ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[size - 1][i]).Content = ((EmptySpace) this.map[x][y]).Content;
                        ((EmptySpace) this.map[x][y]).Content = null;
                        if(((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[size -1][i]).Content instanceof WorldBox ) ((WorldBox)((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[size -1][i]).Content).exitGen(((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside, size-1, i);
                        return 1;
                    }
                }
            }
        }
        else if(dir.equals("right")){
            int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryLeft();
            if(((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryCheck(dir)) {
                if (!IsFill(0,i, dx, dy, size, ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside)) {
                    if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[0][i]).Content == null)
                            || (((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.isMovableHere(0, i, dx, dy, dir) == 1)) {
                        ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[0][i]).Content = ((EmptySpace) this.map[x][y]).Content;
                        ((EmptySpace) this.map[x][y]).Content = null;
                        if(((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[0][i]).Content instanceof WorldBox ) ((WorldBox)((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.map[0][i]).Content).exitGen(((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside, 0, i);

                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    //Vérification de la possible sortie d'un joueur d'une WorldBox
    public int ExitTheWorldBox(int x, int y, int dx, int dy, String dir,int i) {
        if (((this.exit.get(i).boxx + dx >= this.exit.get(i).Lvl.size) || (this.exit.get(i).boxx + dx < 0) || (this.exit.get(i).boxy + dy >= this.exit.get(i).Lvl.size) || (this.exit.get(i).boxy + dy < 0))) {
            System.out.println(Level.inf.size());
            ArrayList<Infinibox> tmp = new ArrayList<>();
            for (Infinibox c : Level.inf) {
                System.out.println(c.levelbound + "  " + c.grade);
                if (c.levelbound.equals(exit.get(i).Lvl.id)) {
                    tmp.add(c);
                    System.out.println("teta");
                }
            }
            int cmp = 1, r = 0;

            while ( cmp <= tmp.size()) {
                if (tmp.get(r).grade == cmp) {
                    Level L = Level.LinkedLevel.get(tmp.get(r).actualevel);
                    if (tmp.get(r).x + dx < 0 || tmp.get(r).x + dx >= this.size || tmp.get(r).y + dy < 0 || tmp.get(r).y + dy >= this.size) {
                        cmp++;
                        r = 0;
                        continue;
                    }
                    if  (!((this.map[tmp.get(r).x + dx][tmp.get(r).y + dy]) instanceof Wall))
                        if(((  (((EmptySpace) this.map[tmp.get(r).x + dx][tmp.get(r).y + dy])).Content==null)
                            || (this.isMovableHere(tmp.get(r).x + dx, tmp.get(r).y + dy, dx, dy, dir) != 0))){
                        ((EmptySpace) this.map[tmp.get(r).x + dx][tmp.get(r).y + dy]).Content = this.player;
                        ((EmptySpace) this.map[this.player.posX][this.player.posY]).Content = null;
                        this.player.posX = tmp.get(r).x + dx;
                        this.player.posY = tmp.get(r).y + dy;
                        return 1;}


                    } else r++;
                }
                return 0;

        }

            if (((this.exit.get(i).boxx + dx < this.exit.get(i).Lvl.size) && (this.exit.get(i).boxx + dx >= 0) && (this.exit.get(i).boxy + dy < this.exit.get(i).Lvl.size) && (this.exit.get(i).boxy + dy >= 0))) {
                if(!(this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy] instanceof Wall))
                if (((((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content instanceof WorldBox))) {
                    if (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.size == this.size) {
                        //Transfert
                        if (!((((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[this.player.posX - dx * (size - 1)][this.player.posY - dy * (size - 1)]) instanceof Wall)) {
                            if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[this.player.posX - dx * (size - 1)][this.player.posY - dy * (size - 1)]).Content == null)
                                    || (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.isMovableHere(this.player.posX - dx * (size - 1), this.player.posY - dy * (size - 1), dx, dy, dir) == 1)) {
                                ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[this.player.posX - dx * (size - 1)][this.player.posY - dy * (size - 1)]).Content = this.player;
                                ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.player = this.player;
                                this.player.posY = this.player.posY - dy * (size - 1);
                                this.player.posX = this.player.posX - dx * (size - 1);
                                if (this.player.world != null)
                                    this.player.exitGen(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside, this.player.posX, this.player.posY);
                                this.player = null;
                                ((EmptySpace) this.map[x][y]).Content = null;
                                return 2;
                            }
                        }

                    } else {
                        // Entrée standard aprés sortie (all -> centre)
                        if (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).entryCheck(dir)) {
                            int s = ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.size;

                            if (dir.equals("up")) { //add B and P
                                int g = ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).entryDown();
                                if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][s - 1]).Content == null)
                                        || (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.isMovableHere(g, s - 1, dx, dy, dir) == 1)) {
                                    ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][s - 1]).Content = this.player;
                                    ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.player = this.player;
                                    this.player.posY = s - 1;
                                    this.player.posX = g;
                                    if (this.player.world != null)
                                        this.player.exitGen(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside, this.player.posX, this.player.posY);
                                    this.player = null;
                                    ((EmptySpace) this.map[x][y]).Content = null;
                                    return 2;


                                }
                            } else if (dir.equals("down")) { //add B and P
                                int g = ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).entryUp();
                                if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][0]).Content == null)
                                        || (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.isMovableHere(g, 0, dx, dy, dir) == 1)) {
                                    ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[g][0]).Content = this.player;
                                    ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.player = this.player;
                                    this.player.posY = 0;
                                    this.player.posX = g;
                                    if (this.player.world != null)
                                        this.player.exitGen(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside, this.player.posX, this.player.posY);
                                    this.player = null;
                                    ((EmptySpace) this.map[x][y]).Content = null;
                                    return 2;


                                }
                            } else if (dir.equals("left")) { //add B and P
                                int g = ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).entryRight();
                                if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[s - 1][g]).Content == null)
                                        || (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.isMovableHere(s - 1, g, dx, dy, dir) == 1)) {
                                    ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[s - 1][g]).Content = this.player;
                                    ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.player = this.player;
                                    this.player.posY = g;
                                    this.player.posX = s - 1;
                                    if (this.player.world != null)
                                        this.player.exitGen(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside, this.player.posX, this.player.posY);
                                    this.player = null;
                                    ((EmptySpace) this.map[x][y]).Content = null;
                                    return 2;


                                }
                            } else if (dir.equals("right")) { //add B and P
                                int g = ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).entryLeft();
                                if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[0][g]).Content == null)
                                        || (((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.isMovableHere(0, g, dx, dy, dir) == 1)) {
                                    ((EmptySpace) ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.map[0][g]).Content = this.player;
                                    ((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside.player = this.player;
                                    this.player.posY = g;
                                    this.player.posX = 0;
                                    if (this.player.world != null)
                                        this.player.exitGen(((WorldBox) ((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content).inside, this.player.posX, this.player.posY);
                                    this.player = null;
                                    ((EmptySpace) this.map[x][y]).Content = null;
                                    return 2;


                                }

                            }
                        }


                    }

                }
            }

            if (((this.exit.get(i).boxx + dx < this.exit.get(i).Lvl.size) && (this.exit.get(i).boxx + dx >= 0) && (this.exit.get(i).boxy + dy < this.exit.get(i).Lvl.size) && (this.exit.get(i).boxy + dy >= 0))
                    && !(this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy] instanceof Wall)
                    && ((((EmptySpace) this.exit.get(i).Lvl.map[this.exit.get(i).boxx + dx][this.exit.get(i).boxy + dy]).Content == null)
                    || (this.exit.get(i).Lvl.isMovableHere(this.exit.get(i).boxx + dx, this.exit.get(i).boxy + dy, dx, dy, dir) == 1))) {
                Element temp = ((EmptySpace) this.map[x][y]).Content;
                ((EmptySpace) this.map[x][y]).Content = null;
                int xx = this.exit.get(i).boxx;
                int yy = this.exit.get(i).boxy;
                ((EmptySpace) this.exit.get(i).Lvl.map[xx + dx][yy + dy]).Content = temp;
                    if (!this.equals(this.exit.get(i).Lvl)) {
                    this.exit.get(i).Lvl.player = this.player;
                    this.player = null;
                }

                this.exit.get(i).Lvl.player.posX = xx + dx;
                this.exit.get(i).Lvl.player.posY = yy + dy;
                if (this.exit.get(i).Lvl.player.world != null)
                    this.exit.get(i).Lvl.player.exitGen(this.exit.get(i).Lvl, xx + dx, xx + dx);
                return 1;
            }

            return 0;
        }


    public boolean EntryInWorldBox(int x, int y, int dx, int dy, String dir,int posX,int posY) {
        if(((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryCheck(dir)){
            if ((((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x+ dx][y+ dy]).Content).inside.map[posX][posY]).Content == null)
                    || ((WorldBox) ((EmptySpace) this.map[x+ dx][y+ dy]).Content).inside.isMovableHere(posX,posY,dx,dy,dir)==1) {
                ((EmptySpace) ((WorldBox) ((EmptySpace) this.map[x+ dx][y+ dy]).Content).inside.map[posX][posY]).Content = ((EmptySpace) this.map[x][y]).Content;
                ((EmptySpace) this.map[x][y]).Content = null;
                if(!this.equals(((WorldBox) ((EmptySpace) this.map[x+ dx][y+ dy]).Content).inside)) {
                    ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.player = this.player;
                    this.player = null;
                }
                ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).exitGen(this,x+dx,y+dy);
                ((WorldBox) ((EmptySpace) this.map[x+ dx][y+ dy]).Content).inside.player.posX = posX;
                ((WorldBox) ((EmptySpace) this.map[x+ dx][y+ dy]).Content).inside.player.posY = posY;
                if(((WorldBox) ((EmptySpace) this.map[x+ dx][y+ dy]).Content).inside.player.world!=null)
                    ((WorldBox) ((EmptySpace) this.map[x+ dx][y+ dy]).Content).inside.player.exitGen(((WorldBox) ((EmptySpace) this.map[x+ dx][y+ dy]).Content).inside, posX,posY);
                return true;
            }
        }
        return false;
    }



}