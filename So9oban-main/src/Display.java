import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * La classe {@code Display} sert Ã  afficher une instance de{@code Level} dans une interface graphique (une fenetre)
 * de taille fixe et vous pouvez la fermer en cliquant sur la bouton X .Cette interface affiche les Objets de jeu
 * (Ã©lements et le monde de jeu )en affichant des images.Elle a comme atributs des paramÃ¨tres d'affichage
 *
 * Dans cette version on suppose que la map est carrÃ©e
 * Scale c'est pour zommer ,il faut respecter toujours la taille maximale de l'Ã©cran
 * les sizes sont donnÃ©s en pixel
 * on imagine que cette window est une matrice dont:
 * maxcol est le nombre de colonnes dans cette fenetre
 * maxRow est le nombre de lignes dans cette fenetre
 * @author Boutrik Mohamed
 * @version 1 (1/12/2022)
 *
 */

public class Display extends JPanel implements Runnable {

    /*attributes */
     Level lvl;
    public static final int originalTileSize = 48;
    private int scaledTileSize;

    private final int DEFAULT_SCALE = 2;
    public final int windowSize;
    private int FPS = 60;
    private Thread gameThread;
    static ArrayList<BufferedImage> links;
    int maxDepth = 4;
    History history = new History();
    KeyHandler keyH= new KeyHandler();
    MouseSPath ms = new MouseSPath();

    boolean finish =false ;

    /*constructor */
    public Display(Level lvl){
        super();
        this.scaledTileSize = originalTileSize * DEFAULT_SCALE;
        this.windowSize = lvl.size * scaledTileSize;
        this.lvl = lvl;
        links = loadingImages();
        //TransitionMaker.links = links;
        history.addMove();

        JFrame frame = new JFrame();
        frame.setTitle("So9oban");
        frame.setSize(windowSize,windowSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setPreferredSize(new Dimension(windowSize, windowSize));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.addMouseListener(ms);
        this.setFocusable(true);



        //frame.setVisible(true);
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        this.requestFocusInWindow();
        frame.setVisible(true);
    }
    /*
     * Modification du Display "Système" afin d'afficher les Elements présent sur une Target
     * */

    public void setLvl(Level lvl) {
        this.lvl = lvl;
    }

    public void displayTerminal(Level lvl) {
        for (int i = 0; i < lvl.size; i++) {
            for (int j = 0; j < lvl.size; j++) {
                if (lvl.map[j][i] instanceof Wall) {
                    System.out.print('#');
                } else if (lvl.map[j][i] instanceof Target) {
                    Target tar = (Target) lvl.map[j][i];
                    if (tar.Content==null) {
                        System.out.print('@');
                    } else {
                        if (tar.Content instanceof Player) {
                            System.out.print('a');
                        } else
                        if (tar.Content instanceof WorldBox) {
                            WorldBox wb = (WorldBox) tar.Content;
                            System.out.print(wb.inside.id);
                        } else
                        if (tar.Content instanceof Box) {
                            System.out.print('b');
                        }
                    }
                } else if (lvl.map[j][i] instanceof EmptySpace ) {
                    EmptySpace obj = (EmptySpace) lvl.map[j][i];
                    if (obj.isEmpty()) {
                        System.out.print(' ');
                    } else {
                        if (obj.Content instanceof Player) {
                            System.out.print('A');
                        } else
                        if (obj.Content instanceof WorldBox) {
                            WorldBox wb = (WorldBox) obj.Content;
                            System.out.print(wb.inside.id);
                        } else
                        if (obj.Content instanceof Box) {
                            System.out.print('B');
                        }
                    }
                }
            }
            System.out.println();
        }
    }


    /**
     * retourne le buffer sur l'image de {@code Tile} spÃ©cifiÃ© si elle rÃ©ussit pas elle retourne null
     * @requires t!=null;
     * @param t est le tile Ã  afficher et qu'on cherche son buffer
     * @return un buffer sur l'image de tile ou null si elle Ã©choue
     * @throws NullPointerException si t est null
     */


    /**
     * retourne le buffer sur l'image de {@code Element} spÃ©cifiÃ© si elle rÃ©ussit pas elle retourne null
     * @requires t!=null;
     * @param e est le tile Ã  afficher et qu'on cherche son buffer
     * @return un buffer sur l'image d'element ou null si elle Ã©choue
     * @throws NullPointerException si t est null
     */
    // chargement des images dans une liste de buffers dont l'ordre est important
    public ArrayList<BufferedImage> loadingImages() {
        ArrayList<BufferedImage> result = new ArrayList<>();
        BufferedImage buf = null;
        // wall = index 0
        try {
            buf = ImageIO.read(getClass().getResourceAsStream("assets/wall.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error occured");
        }
        result.add(buf);
        // box index =1
        try {
            buf = ImageIO.read(getClass().getResourceAsStream("assets/crate.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error occured");
        }
        result.add(buf);
        // ground index =2
        try {
            buf = ImageIO.read(getClass().getResourceAsStream("assets/ground.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error occured");
        }
        result.add(buf);
        // target index =3
        try {
            buf = ImageIO.read(getClass().getResourceAsStream("assets/target.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error occured");
        }
        result.add(buf);
        // player index =4
        try {
            buf = ImageIO.read(getClass().getResourceAsStream("assets/player.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error occured");
        }
        result.add(buf);

        // infini index =5
        try {
            buf = ImageIO.read(getClass().getResourceAsStream("assets/infini.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error occured");
        }
        result.add(buf);
        return result;
    }
    /**
     * elle dessine l'image dint  x,int y u{@code Tile} spÃ©cifiÃ© dans la position (x,y)passÃ©es en argument
     * @requires x>=0 && y>=0;
     * @requires t!=null;
     * @param g2 instance de {@code Graphics2D} pour utiliser les mÃ©thodes de dessin
     * @param t le tile Ã  afficher
     * @param x abcisse de l'image Ã  afficher
     * @param y ordonnÃ©e de l'image Ã  afficher
     * @throws NullPointerException si t est null
     * @throws IllegalArgumentException si x<0 ou y<0
     */



    public void drawTile(Graphics2D g2, Tile t, int posx, int posy) {
        if (t == null) {
            throw new NullPointerException("Tile est null");
        }
        /*
        if (posx < 0 || posy < 0) {
            throw new IllegalArgumentException("une des coordonnÃ©e est nÃ©gative");
        }*/
        if (t instanceof Target) {
            g2.drawImage(links.get(3), posx, posy, originalTileSize, originalTileSize, null);
            return;
        }
        if (t instanceof Wall)
            g2.drawImage(links.get(0), posx, posy, originalTileSize, originalTileSize, null);
        if (t instanceof EmptySpace)
            g2.drawImage(links.get(2), posx, posy, originalTileSize, originalTileSize, null);
    }


    /**
     * elle dessine l'image du{@code Element} spÃ©cifiÃ© dans la position (x,y)passÃ©es en argument
     * @requires x>=0 && y>=0;
     * @requires e!=null;
     * @param g2 instance de {@code Graphics2D} pour utiliser les mÃ©thodes de dessin
     * @param e l'Ã©lement  Ã  afficher
     * @param x abcisse de l'image Ã  afficher
     * @param y ordonnÃ©e de l'image Ã  afficher
     * @throws NullPointerException si t est null
     * @throws IllegalArgumentException si x<0 ou y<0
     */

    public void drawElement(Graphics2D g2,Element e,int posx,int posy){
        if (e==null){
            throw new NullPointerException("Tile est null");
        }/*
        if (posx<0||posy<0){
            throw new IllegalArgumentException("une des coordonnÃ©e est nÃ©gative");
        }*/
        if (e instanceof Player)
            g2.drawImage(links.get(4), posx, posy, originalTileSize, originalTileSize, null);
        if (e instanceof Box)
            g2.drawImage(links.get(1), posx, posy, originalTileSize, originalTileSize, null);
    }

    public void update() {
        updatePlayer(this.lvl.player.posX, this.lvl.player.posY);
    }

    //Mise à jour des données du Player
    public void updatePlayer(int x, int y) {
        int dx = 0;
        int dy = 0;
        int check = 0;
        int test = 0;


        if (keyH.upPressed) {
            dy = -1;
            check = lvl.isMovableHere(x, y, dx, dy, "up");
            test=MooveCheck(check,x, y, dx, dy);
            if (test==1){
                this.lvl.player.posY--;
                this.displayTerminal(this.lvl);
            }
            keyH.upPressed = false;
            history.addMove();
        } else if (keyH.downPressed) {
            dy = 1;
            check = lvl.isMovableHere(x, y, dx, dy, "down");
            test=MooveCheck(check,x, y, dx, dy);
            if (test==1) {
                this.lvl.player.posY++;
                this.displayTerminal(this.lvl);
            }
            keyH.downPressed = false;
            history.addMove();
        } else if (keyH.leftPressed) {
            dx = -1;
            check = lvl.isMovableHere(x, y, dx, dy, "left");
            test=MooveCheck(check,x, y, dx, dy);
            if (test==1) {
                this.lvl.player.posX--;
                this.displayTerminal(this.lvl);
            }
            keyH.leftPressed = false;
            history.addMove();
        } else if (keyH.rightPressed) {
            dx = 1;
            check = lvl.isMovableHere(x, y, dx, dy, "right");
            test=MooveCheck(check,x, y, dx, dy);
            if (test==1){
                this.lvl.player.posX++;
                this.displayTerminal(this.lvl);
            }
            keyH.rightPressed = false;
            history.addMove();
        } else if (keyH.MKeyispressed) {
            history.returnMove(this);
            keyH.MKeyispressed =false;

        }


    }

    // Gestion des différent cas qui s'effectue lors d'un déplacement et changement de Lvl si nécéssaire
    public int MooveCheck(int i,int x, int y,int dx,int dy){
        if(i!=0){
            switch (i){
                case 4:
                    this.lvl =((WorldBox) ((EmptySpace) lvl.exit.get(0).Lvl.map[lvl.exit.get(0).boxx + dx][lvl.exit.get(0).boxy + dy]).Content).inside;
                    return 0;
                case 3:
                    this.lvl = this.lvl.exit.get(0).Lvl;
                    return 0;
                case 2 :
                    this.lvl = ((WorldBox) ((EmptySpace) this.lvl.map[x+dx][y+dy]).Content).inside;
                    return 0;
                default :
                    return 1;
            }
        }
        return 0;
    }


    /* elle permet d'actualiser le jeu */
    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double oneDrawDuration = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / oneDrawDuration;
            lastTime = currentTime;
            if (delta >= 1) {
                update();

                repaint();
                if(Level.hasWin()&&!this.finish){
                    showMessageDialog(null, "GG you won");
                    this.finish = true;
                };
                delta--;
            }
        }
    }

 
    /**
     * elle affiche le niveau sur une fenetre
     */



    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (this.lvl.exit.isEmpty()) {
            paintScreen(g2);
        } else {
            if(this.lvl.tM.size()==2){
                if(this.lvl.exit.get(0).Lvl!=this.lvl){
                    paintScreen(g2);
                }else{
                    paintZoom(g2);
            }
        }else{
            if(this.lvl.exit.get(0).Lvl!=this.lvl)
                paintZoom(g2);
                else paintScreen(g2);
        }
    }     
        g2.dispose();
    }
    public void paintScreen(Graphics2D g2){
        if(ms.pressed){
            Point p=ms.getTilePosition(this, DEFAULT_SCALE, 0);
            if(this.lvl.ShortestPath(p.x,p.y).length()>0){
                System.out.println(this.lvl.ShortestPath(p.x,p.y));
                ((EmptySpace) this.lvl.map[p.x][p.y]).Content=this.lvl.player;
                ((EmptySpace) this.lvl.map[this.lvl.player.posX][this.lvl.player.posY]).Content = null;
                this.lvl.player.posX=p.x;
                this.lvl.player.posY=p.y;
            }
            ms.pressed = false;
        }
        double sc = (double)windowSize / (originalTileSize * this.lvl.size);
        g2.scale(sc, sc);
        lvl.tM.get(0).setScale(sc);
        lvl.tM.get(0).setTransformationVector(0, 0);
        ArrayList<String> s = new ArrayList<>();
        s.add(this.lvl.id);
        paintLevel(g2, this.lvl, 0, 0, 1, maxDepth,s,true);
    }
    public void paintZoom(Graphics2D g2){
        double oldscale = windowSize / (originalTileSize * this.lvl.exit.get(0).Lvl.size);
            double scale;
            scale = (oldscale * this.lvl.exit.get(0).Lvl.size - 1) - oldscale;
            double marge = (windowSize - originalTileSize * (scale + oldscale)) / scale;
            g2.scale(scale, scale);
            lvl.tM.get(0).setScale(scale);
            int dx,dy;
            if (this.lvl.id.equals("A")){
                dx = -(int) ( this.lvl.player.posX* originalTileSize - marge);
                dy = -(int) ( this.lvl.player.posY* originalTileSize - marge);
            }else{
                dx = -(int) (this.lvl.exit.get(0).boxx * originalTileSize - marge);
                dy = -(int) (this.lvl.exit.get(0).boxy * originalTileSize - marge);
            }
            lvl.tM.get(0).setTransformationVector(dx, dy);
            g2.translate(dx, dy);
            ArrayList<String> s = new ArrayList<>();
            paintLevel(g2, this.lvl.exit.get(0).Lvl, 0, 0, 1, maxDepth,s,true);
        if(ms.pressed){
            Point p=ms.getTilePosition(this, (double)scale/this.lvl.size, marge*scale);
            if(this.lvl.ShortestPath(p.x,p.y).length()>0){
                System.out.println(this.lvl.ShortestPath(p.x,p.y));
                ((EmptySpace) this.lvl.map[p.x][p.y]).Content=this.lvl.player;
                ((EmptySpace) this.lvl.map[this.lvl.player.posX][this.lvl.player.posY]).Content = null;
                this.lvl.player.posX=p.x;
                this.lvl.player.posY=p.y;
            }
            ms.pressed = false;
        }

    }
    public void paintLevel(Graphics2D g2,Level l,int dx,int dy,double sc,int md,ArrayList<String> s,boolean transitioinfos){

      int numiter = 1;
      if (md==0){
          g2.scale(sc,sc);
         return;
      }
      g2.scale(sc,sc);
      for (int i = 0;i<l.size;i++){
        for (int j = 0;j<l.size;j++){
            if(l.map[i][j] instanceof EmptySpace){
             EmptySpace es =(EmptySpace)l.map[i][j];
                  //target,ground
                if (es.isEmpty()){
                    drawTile(g2,es,i*originalTileSize+Double.valueOf(dx/sc).intValue(),j*
                    originalTileSize+Double.valueOf(dy/sc).intValue());
                }else
                //player,box or worldbox
                {
                    if (es.Content instanceof WorldBox){
                    Level lev = ((WorldBox)es.Content).inside;
                    if(lev==l || s.contains(lev.id)){

                        int dx2 =i*originalTileSize;
                        int dy2 = j*originalTileSize;
                        double newscale=(double)1/lev.size;

                        if(l==this.lvl && lev == this.lvl && md ==maxDepth&&this.lvl.tM.size()==2){
                            if(transitioinfos){
                            if((this.lvl.tM.size()==2)?this.lvl.tM.get(1).isMoving:false)
                                if(!this.lvl.exit.isEmpty())numiter = 1;
                                else numiter = 1;
                                else numiter=2;
                            lvl.tM.get(1).setDelta(dx2/newscale,dy2/newscale);
                            lvl.tM.get(1).setScale(lvl.tM.get(0).scale*newscale);
                            lvl.tM.get(1).setTransformationVector(lvl.tM.get(0).transformationVector.x/newscale,lvl.tM.get(0).transformationVector.y/newscale);
                        }
                        }
                        paintLevel(g2,lev,i*originalTileSize+Double.valueOf(dx/sc).intValue(),j*
                        originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size,--md,s,true);

                        md++;

                    }else {
                        s.add(lev.id);

                        paintLevel(g2,lev,i*originalTileSize+Double.valueOf(dx/sc).intValue(),j*
                        originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size,md,s,true);
                        s.remove(s.size()-1);
                    }
                        g2.scale(lev.size,lev.size);
                    }else if (es.Content instanceof Player){
                        Level lev = ((Player)es.Content).world;
                        if (lev!=null){
                            if (lev==l || s.contains(lev.id)){
                            paintLevel(g2,lev,i*originalTileSize+Double.valueOf(dx/sc).intValue(),j*
                            originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size,--md, s,false);

                            }else{
                                s.add(lev.id);
                                paintLevel(g2,lev,i*originalTileSize+Double.valueOf(dx/sc).intValue(),j*
                                originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size,md,s,false);
                                s.remove(s.size()-1);
                            }
                            g2.scale(lev.size,lev.size);
                            float alpha =(float) 0.3;
                            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha);
                            g2.setComposite(ac);
                            g2.drawImage(links.get(4),Double.valueOf(i*originalTileSize+dx/sc).intValue()
                        ,Double.valueOf(j*originalTileSize+dy/sc).intValue(),48,48,null);
                         ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1);
                        g2.setComposite(ac);
                        }else drawElement(g2,es.Content,Double.valueOf(i*originalTileSize+dx/sc).intValue()
                        ,Double.valueOf(j*originalTileSize+dy/sc).intValue());
                    }else if (es.Content instanceof Infinibox){
                        Level lev= Level.LinkedLevel.get(((Infinibox)es.Content).levelbound);
                        paintLevel(g2,lev,i*originalTileSize+Double.valueOf(dx/sc).intValue(),j*
                        originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size,--md,s,false);
                        md++;
                        g2.scale(lev.size,lev.size);
                        paintinfini(g2,(i)*originalTileSize+Double.valueOf(dx/sc).intValue(),(j)*originalTileSize+Double.valueOf(dy/sc).intValue(),sc,(Infinibox)es.Content);
                    }else
                        drawElement(g2,es.Content,Double.valueOf(i*originalTileSize+dx/sc).intValue()
                        ,Double.valueOf(j*originalTileSize+dy/sc).intValue());
                }
            }else
            //wall
            {
                drawTile(g2,l.map[i][j],i*originalTileSize+Double.valueOf(dx/sc).intValue(),
                j*originalTileSize+Double.valueOf(dy/sc).intValue());
            }
            }
            }

        if ((transitioinfos)&(this.lvl==l)&(md==maxDepth)){
            if (!this.lvl.exit.isEmpty()){
            if (this.lvl.exit.get(0).boxy==0){
                String dir = "up";
                Infinibox ib = getExtremLevel(Level.inf, dir,0);
                if (ib!=null)  {
                    Level ll = Level.LinkedLevel.get(ib.actualevel);
                    int dxx = (this.lvl.exit.get(0).boxx-1)*originalTileSize;
                    paintextrem(g2,ll,dir,dxx,0,new Point(ib.x,ib.y), md,sc, s);
                }

            }
            if (this.lvl.exit.get(0).boxy==this.lvl.size-1){
                String dir = "down";
                Infinibox ib = getExtremLevel(Level.inf, dir,0);
                if (ib!=null)  {
                    Level ll = Level.LinkedLevel.get(ib.actualevel);
                    int dxx = (this.lvl.exit.get(0).boxx-1)*originalTileSize;
                    paintextrem(g2,ll,dir,dxx,0,new Point(ib.x,ib.y), md,sc, s);
                }

            }
            if (this.lvl.exit.get(0).boxx==0){
                String dir = "left";
                Infinibox ib = getExtremLevel(Level.inf, dir,0);
                if (ib!=null)  {
                    Level ll = Level.LinkedLevel.get(ib.actualevel);
                    int dyy = (this.lvl.exit.get(0).boxy-1)*originalTileSize;
                    paintextrem(g2,ll,dir,0,dyy,new Point(ib.x,ib.y), md,sc, s);
                }

            }
            if (this.lvl.exit.get(0).boxx==this.lvl.size-1){
                String dir = "right";
                Infinibox ib = getExtremLevel(Level.inf, dir,0);
                if (ib!=null)  {
                    Level ll = Level.LinkedLevel.get(ib.actualevel);
                    int dyy = (this.lvl.exit.get(0).boxy-1)*originalTileSize;
                    paintextrem(g2,ll,dir,0,dyy,new Point(ib.x,ib.y), md,sc, s);
                }

            }}
            if (TransitionMaker.numOfTrans>0&&(this.lvl.player.world==null?true:this.lvl.player.world.tM.size()!=2)){
                lvl.tM.get(0).setDelta((double)dx/sc,(double) dy/sc);
                lvl.tM.get(0).setScale(lvl.tM.get(0).scale*sc);
                lvl.tM.get(0).setTransformationVector(lvl.tM.get(0).transformationVector.x/sc,lvl.tM.get(0).transformationVector.y/sc);
                transitions((Graphics2D)getGraphics(),numiter);
            }


        }
      }
      public void paintinfini(Graphics2D g2,int dx,int dy,double sc,Infinibox ib){
        int numofinf = ib.grade;
        g2.scale((double)1/numofinf,(double)1/numofinf);
        int pas = 48/numofinf;
        if(numofinf>1)dx+=48/4;
        for (int i =0;i<numofinf;i++){
            g2.drawImage(links.get(5),dx*numofinf,dy*numofinf, 48,48,null);
            dy+=pas;
        }
        g2.scale(numofinf,numofinf);
      }
      public Infinibox getExtremLevel(ArrayList<Infinibox> ib,String dir,int index ){
        if (index == ib.size())return null;
        if(dir.equals("up")||dir.equals("down"))
            if(inextrem(ib.get(index)).y==-1)return ib.get(index);
        if(dir.equals("right")||dir.equals("left"))
            if(inextrem(ib.get(index)).x==-1)return ib.get(index);
         return getExtremLevel(ib, dir, ++index);
      }
      public void paintextrem(Graphics2D g2,Level l ,String direction ,int dx,int dy,Point p,int md,Double sc,ArrayList<String>s){
        int j;
        int x,y;
        if (direction.equals("up")){
            y =p.y-1;
            x = p.x;
        }else
        if (direction.equals("down")){
            y =p.y+1;
            x = p.x;
        }else
        if (direction.equals("left")){
            x= p.x-1;
            y = p.y;
        } else{
            x= p.x+1;
            y = p.y;
        }
        //horizontally
        if(direction.equals("up")||direction.equals("down")){
            if(direction.equals("up"))j=-1;
            else j=1;
        for(int i =x-1;i<=x+1;i++){
            if(l.map[i][y] instanceof EmptySpace){
                EmptySpace es =(EmptySpace)l.map[i][y];
                     //target,ground
                   if (es.isEmpty()){
                       drawTile(g2,es,(i-x+1)*originalTileSize+Double.valueOf(dx/sc).intValue(),(this.lvl.exit.get(0).boxy+j)*
                       originalTileSize+Double.valueOf(dy/sc).intValue());
                   }else
                   //player,box or worldbox
                   {
                       if (es.Content instanceof WorldBox){
                       Level lev = ((WorldBox)es.Content).inside;
                       if(lev==l || s.contains(lev.id)){
                           paintLevel(g2,lev,(i-x+1)*originalTileSize+Double.valueOf(dx/sc).intValue(),(this.lvl.exit.get(0).boxy+j)*
                           originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size,--md,s,true);
                           md++;

                       }else {
                           s.add(lev.id);
                           paintLevel(g2,lev,(i-x+1)*originalTileSize+Double.valueOf(dx/sc).intValue(),(this.lvl.exit.get(0).boxy+j)*
                           originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size,md,s,true);
                           s.remove(s.size()-1);
                       }
                           g2.scale(lev.size,lev.size);
                       }else if (es.Content instanceof Player){
                           Level lev = ((Player)es.Content).world;
                           if (lev!=null){
                               paintLevel(g2,lev,(i-x+1)*originalTileSize+Double.valueOf(dx/sc).intValue(),(this.lvl.exit.get(0).boxy+j)*
                               originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size, md, s,true);
                               g2.scale(lev.size,lev.size);
                               float alpha =(float) 0.3;
                               AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha);
                               g2.setComposite(ac);
                               g2.drawImage(links.get(4),Double.valueOf(i*originalTileSize+dx/sc).intValue()
                               ,(this.lvl.exit.get(0).boxy+j)*originalTileSize+Double.valueOf(dy/sc).intValue()
                               ,48,48,null);
                            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1);
                           g2.setComposite(ac);
                           }else drawElement(g2,es.Content,(i-x+1)*originalTileSize+Double.valueOf(dx/sc).intValue()
                           ,(this.lvl.exit.get(0).boxy+j)*originalTileSize+Double.valueOf(dy/sc).intValue());
                       }else if (es.Content instanceof Infinibox){
                           Level lev= Level.LinkedLevel.get(((Infinibox)es.Content).levelbound);
                           paintLevel(g2,lev,(i-x+1)*originalTileSize+Double.valueOf(dx/sc).intValue(),(this.lvl.exit.get(0).boxy+j)*originalTileSize+Double.valueOf(dy/sc).intValue()
                           ,(double)1/lev.size,--md,s,true);
                           md++;
                           g2.scale(lev.size,lev.size);

                        }else
                           drawElement(g2,es.Content,Double.valueOf((i-x+1)*originalTileSize+dx/sc).intValue()
                           ,(this.lvl.exit.get(0).boxy+j)*originalTileSize+Double.valueOf(dy/sc).intValue());
                   }
               }else
               //wall
               {
                   drawTile(g2,l.map[i][y],(i-x+1)*originalTileSize+Double.valueOf(dx/sc).intValue(),
                   (this.lvl.exit.get(0).boxy+j)*originalTileSize+Double.valueOf(dy/sc).intValue());
               }
            }
        }else{
            if(direction.equals("left"))j=-1;
            else j=1;
            for(int i = p.y-1;i<=p.y+1;i++){
                if(l.map[x][i] instanceof EmptySpace){
                    EmptySpace es =(EmptySpace)l.map[x][i];
                         //target,ground
                       if (es.isEmpty()){
                           drawTile(g2,es,(this.lvl.exit.get(0).boxx+j)*originalTileSize+Double.valueOf(dx/sc).intValue(),(i-y+1)*
                           originalTileSize+Double.valueOf(dy/sc).intValue());
                       }else
                       //player,box or worldbox
                       {
                           if (es.Content instanceof WorldBox){
                           Level lev = ((WorldBox)es.Content).inside;
                           if(lev==l || s.contains(lev.id)){
                               paintLevel(g2,lev,(this.lvl.exit.get(0).boxx+j)*originalTileSize+Double.valueOf(dx/sc).intValue(),(i-y+1)*
                               originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size,--md,s,true);
                               md++;

                           }else {
                               s.add(lev.id);
                               paintLevel(g2,lev,(this.lvl.exit.get(0).boxx+j)*originalTileSize+Double.valueOf(dx/sc).intValue(),(i-y+1)*
                               originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size,md,s,true);
                               s.remove(s.size()-1);
                           }
                               g2.scale(lev.size,lev.size);
                           }else if (es.Content instanceof Player){
                               Level lev = ((Player)es.Content).world;
                               if (lev!=null){
                                   paintLevel(g2,lev,(this.lvl.exit.get(0).boxx+j)*originalTileSize+Double.valueOf(dx/sc).intValue(),(i-y+1)*
                                   originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size, md, s,true);
                                   g2.scale(lev.size,lev.size);
                                   float alpha =(float) 0.3;
                                   AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha);
                                   g2.setComposite(ac);
                                   drawElement(g2,es.Content,(this.lvl.exit.get(0).boxx+j)*originalTileSize+Double.valueOf(dx/sc).intValue()
                               ,Double.valueOf((i-y+1)*originalTileSize+dy/sc).intValue());
                                ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1);
                               g2.setComposite(ac);
                               }else drawElement(g2,es.Content,(this.lvl.exit.get(0).boxx+j)*originalTileSize+Double.valueOf(dx/sc).intValue()
                               ,Double.valueOf((i-y+1)*originalTileSize+dy/sc).intValue());
                           }else if (es.Content instanceof Infinibox){
                               Level lev= Level.LinkedLevel.get(((Infinibox)es.Content).levelbound);
                               paintLevel(g2,lev,(this.lvl.exit.get(0).boxx+j)*originalTileSize+Double.valueOf(dx/sc).intValue(),(i-y+1)*
                               originalTileSize+Double.valueOf(dy/sc).intValue(),(double)1/lev.size,--md,s,true);
                               md++;
                               g2.scale(lev.size,lev.size);
                           }else
                               drawElement(g2,es.Content,(this.lvl.exit.get(0).boxx+j)*originalTileSize+Double.valueOf(dx/sc).intValue()
                               ,Double.valueOf((i-y+1)*originalTileSize+dy/sc).intValue());
                       }
                   }else
                   //wall
                   {
                       drawTile(g2,l.map[x][i],(this.lvl.exit.get(0).boxx+j)*originalTileSize+Double.valueOf(dx/sc).intValue(),
                       (i-y+1)*originalTileSize+Double.valueOf(dy/sc).intValue());
                   }
                }


        }
    }

      public Point inextrem(Box b){
        int posx,posy;
        if(b instanceof WorldBox){
            posx=this.lvl.exit.get(0).boxx;
            posy=this.lvl.exit.get(0).boxy;
        }else{
            Infinibox ib = (Infinibox)b;
            posx= ib.x;
            posy=ib.y;
        }
        if(posx!=this.lvl.size-1&&posx!=0)posx=-1;
        if(posy!=this.lvl.size-1&&posy!=0)posy=-1;
        return new Point(posx, posy);
      }
//transitions
public void transitions(Graphics2D g2,int numiter){
    int dx = TransitionMaker.dx;
    int dy =TransitionMaker.dy;
    boolean test;
    int naptime = 2;
    int transx =(this.lvl.player.posX-dx)*originalTileSize ;
    int transy = (this.lvl.player.posY-dy)*originalTileSize ;
     if(this.lvl.tM.size()==2){
        if(this.lvl.tM.get(1).isMoving&&!this.lvl.exit.isEmpty()){
            this.lvl.tM.get(0).setTransformationVector(this.lvl.tM.get(0).transformationVector.x+dx*originalTileSize,this.lvl.tM.get(0).transformationVector.y+dy*originalTileSize);
        }
    }
    if (dx>0 || dy >0) test = (transx<(this.lvl.player.posX)*originalTileSize || transy<(this.lvl.player.posY)*originalTileSize);
    else test = (transx>(this.lvl.player.posX)*originalTileSize || transy>(this.lvl.player.posY)*originalTileSize);
    while(test){

        transx +=dx;
        transy +=dy;
        for (int i =0;i<numiter;i++){
            lvl.tM.get(i).one_transition(this.lvl.player,(Graphics2D)getGraphics(),transx,transy,this);
        }

        try {
            Thread.sleep(naptime,0);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        if (dx>0 || dy >0) test = (transx<(this.lvl.player.posX)*originalTileSize || transy<(this.lvl.player.posY)*originalTileSize);
        else test = (transx>(this.lvl.player.posX)*originalTileSize || transy>(this.lvl.player.posY)*originalTileSize);

    }

    TransitionMaker.numOfTrans=0;
    if(this.lvl.tM.size()==2)this.lvl.tM.get(1).isMoving=false;
    TransitionMaker.Levellinks.removeAll(TransitionMaker.Levellinks);
    TransitionMaker.one_transitiondone =false;
    }



public BufferedImage saveLevelImage(Level lvl,int md,ArrayList<String>s,boolean isPlayer){
    BufferedImage levelImage=new BufferedImage(windowSize,windowSize,BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D)levelImage.getGraphics();
    this.paintLevel(g2,lvl,0,0,(double)windowSize / (originalTileSize * lvl.size),md,s,false);
    if (isPlayer){
        double sc =this.lvl.player.world.size;
        float alpha =(float) 0.3;
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha);
        g2.setComposite(ac);
        g2.scale(sc,sc);
        g2.drawImage(Display.links.get(4),0,0,48,48,null);
    }return levelImage;
}

}