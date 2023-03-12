    import java.awt.datatransfer.StringSelection;
    import java.util.ArrayList;
    import java.io.*;
    import java.util.HashMap;
    import java.util.HashSet;

    import java.util.Map;

    public class Level{

       public String id;
       public ArrayList<Exit> exit;
       public Tile[][] map;
       public int size;
       public Player player;
       static HashMap<String,Level> LinkedLevel = new HashMap<String,Level>();
       // Linked Level est utilisé pour sauvegarder tout niveau present dans les boites monde a la création

        public ArrayList<Target> tagerts;

       /**
        *  Convention des fichier niveau
        *
        * @ : Player
        * . : emptyspace
        * # : Wall
        * & : box
        * Alphabet : world box
        * *:target
        * Les fichiers niveaux sont séparés tel que ( name size map sublevel.name sublevel.size)
        * si il y a une world box sa lettre
        * */

       //fichier niveau de test
       public String testlevel = "L 5 #####" +
                                 "#...#" +
                                 "#@&.#" +
                                 "#.*A#" +
                                 "#####" +
               " A 3 #########";
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

       public Level(String lvl){
            this.tagerts = new ArrayList<Target>();
           String tab[] = lvl.split(" ");
           this.id=tab[0];
           this.size= Integer.parseInt(tab[1]);
           this.map= new Tile[this.size][this.size];
           Level.LinkedLevel.put(tab[0],this );
            // j en premier puis i dans les boucles pour avoir i pour les X et j pour les Y
           for (int j = 0; j < this.size; j++) {
               for (int i = 0; i < this.size; i++) {
                   char c = tab[2].charAt(i + j * this.size);
                   if (c == '#') this.map[i][j] = new Wall();
                   else if (c == '@') this.map[i][j] = new EmptySpace(player = new Player(i,j));
                   else if (c == '.') this.map[i][j] = new EmptySpace();
                   else if (c == '&') this.map[i][j] = new EmptySpace(new Box());
                   else if (c == '*') {
                       Target t = new Target();
                       this.map[i][j] = t;
                       this.tagerts.add(t);
                   }
                   if (Character.isLetter(c)) {
                       if (this.id.charAt(0) == c)
                           this.map[i][j] = new EmptySpace(new WorldBox(this));
                       else {
                           int t = 3;
                           while (t < tab.length) {
                               if (tab[t].charAt(0) == c)
                                   break;
                               t += 3;
                           }
                           if(Level.LinkedLevel.keySet().contains(tab[t]))
                               this.map[i][j] = new EmptySpace(new WorldBox(Level.LinkedLevel.get((tab[t]))));

                           else
                           this.map[i][j] = new EmptySpace(new WorldBox(new Level(tab[t] + " " + tab[t + 1] + " " + tab[t + 2] + " " + lvl)));
                       }
                   }
               }
           }
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
                FileReader fr = new FileReader(System.getProperty("user.dir") + "\\src\\" + path);
                // Créer l'objet BufferedReader
                BufferedReader br = new BufferedReader(fr);
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    // ajoute la ligne au buffer

                    sb.append(line);
                    if (line.length() < 2) sb.append("\n");
                }
                fr.close();
                System.out.println("Contenu du fichier: ");
                System.out.println(sb.toString());
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }

        //ya wldi clean code ta3 sah XD
       boolean hasWin() {
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

        public int isMovableHere(int x, int y, int dx, int dy, String dir) {
            if (this.exit != null && (this.exit.isExit(x, y, dir))) {
                if (ExitTheWorldBox(x,y,dx,dy,dir)){
                    return 2;
                }
                return 0;
            }
            if (!(this.map[x + dx][y + dy] instanceof Wall)) {
                if (((EmptySpace) this.map[x + dx][y + dy]).Content == null) {
                    ((EmptySpace) this.map[x + dx][y + dy]).Content = ((EmptySpace) this.map[x][y]).Content;
                    ((EmptySpace) this.map[x][y]).Content = null;
                    return 1;
                }
                else if ((((EmptySpace)this.map[x + dx][y + dy]).Content instanceof WorldBox)) {
                    int size = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).inside.size;
                    if(dir.equals("up")){
                        int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryDown();
                        return EntryInWorldBox(x,y,dx,dy,dir,i,size-1);
                    }
                    if(dir.equals("down")){
                        int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryUp();
                        return EntryInWorldBox(x,y,dx,dy,dir,i,0);
                    }
                    if(dir.equals("left")){
                        int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryRight();
                        return EntryInWorldBox(x,y,dx,dy,dir,size-1,i);
                    }
                    if(dir.equals("right")){
                        int i = ((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryLeft();
                        return EntryInWorldBox(x,y,dx,dy,dir,0,i);
                    }
                }
                else if (isMovableHere(x + dx, y + dy, dx, dy, dir) == 1) {
                    ((EmptySpace)this.map[x + dx][y + dy]).Content = ((EmptySpace)this.map[x][y]).Content;
                    ((EmptySpace)this.map[x][y]).Content = null;
                    return 1;
                }
            }
            return 0;
        }

        public boolean ExitTheWorldBox(int x, int y, int dx, int dy, String dir) {
                if (this.exit.isMovableHere(this.exit. +dx, y + dy, dx, dy, dir)) {
                    Element temp = ((EmptySpace) this.map[x][y]).Content;
                    ((EmptySpace) this.map[x][y]).Content = null;
                    int xx = this.exit.x;
                    int yy = this.exit.y;
                    this = this.exit.Lvl;
                    ((EmptySpace) this.map[xx][yy]).Content = temp;
                    this.player = new Player(xx, yy);
                    return true;
                }
                return false;
        }

        public boolean EntryInWorldBox(int x, int y, int dx, int dy, String dir,int posX,int posY) {
            if(((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).entryCheck(dir)){
                ((EmptySpace)((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).inside.map[posX][posY]).Content = ((EmptySpace)this.map[x][y]).Content;
                ((EmptySpace)this.map[x][y]).Content = null;
                //this.displayTerminal(((WorldBox) ((EmptySpace)this.map[x + dx][y + dy]).Content).inside);
                ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside.player = this.player;
                ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).exitGen(this,x+dx,y+dy);
                this= ((WorldBox) ((EmptySpace) this.map[x + dx][y + dy]).Content).inside;
                this.player.posX = posX;
                this.player.posY = posY;
                return true;
            }
            return false;
        }
    }


