import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;




/**
 * La classe {@code Display} sert Ã  afficher une instance de{@code Level} dans une interface graphique (une fenetre)
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


public class Display extends JPanel {

    /*attributes */
    private Level lvl;
    private final int originalTileSize=48;
    private int scaledTileSize;
    private final int scale =2;
    private final int windowSize;


    History history = new History();
    KeyHandler keyH= new KeyHandler();
    
    /*constructor */
    public Display(Level lvl){
        super();
        this.scaledTileSize=originalTileSize*scale;
        this.windowSize = lvl.size * scaledTileSize;
        this.lvl = lvl;

        JFrame frame = new JFrame();
        frame.setTitle("So9oban");
        frame.setSize(windowSize,windowSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setPreferredSize(new Dimension(windowSize,windowSize));
        this.setBackground(Color.lightGray);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
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
    public void displayTerminal(Level lvl) {
        for (int i = 0; i < lvl.size; i++) {
            for (int j = 0; j < lvl.size; j++) {
                if (lvl.map[j][i] instanceof Wall) {
                    System.out.print('#');
                } else if (lvl.map[j][i] instanceof Target) {
                    Target tar = (Target) lvl.map[j][i];
                    if (tar.Content==null) {
                        System.out.print('*');
                    } else {
                        if (tar.Content instanceof Player) {
                            System.out.print('@');
                        } else
                        if (tar.Content instanceof WorldBox) {
                            WorldBox wb = (WorldBox) tar.Content;
                            System.out.print(wb.inside.id);
                        } else
                        if (tar.Content instanceof Box) {
                            System.out.print('&');
                        }
                    }
                } else if (lvl.map[j][i] instanceof EmptySpace ) {
                    EmptySpace obj = (EmptySpace) lvl.map[j][i];
                    if (obj.isEmpty()) {
                        System.out.print('.');
                    } else {
                        if (obj.Content instanceof Player) {
                            System.out.print('@');
                        } else
                        if (obj.Content instanceof WorldBox) {
                            WorldBox wb = (WorldBox) obj.Content;
                            System.out.print(wb.inside.id);
                        } else
                        if (obj.Content instanceof Box) {
                            System.out.print('&');
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
     * @param t est le tile Ã  afficher et qu'on cherche son buffer
     * @return un buffer sur l'image de tile ou null si elle Ã©choue
     * @throws NullPointerException si t est null
     */

    public BufferedImage getBufTile(Tile t ){
        BufferedImage buf = null;
        try {
            buf= ImageIO.read(getClass().getResourceAsStream(t.sprite));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error occured");
        }
        return buf;
    }

    /**
     * retourne le buffer sur l'image de {@code Element} spÃ©cifiÃ© si elle rÃ©ussit pas elle retourne null
     * @requires t!=null;
     * @param e est le tile Ã  afficher et qu'on cherche son buffer
     * @return un buffer sur l'image d'element ou null si elle Ã©choue
     * @throws NullPointerException si t est null
     */
    public BufferedImage getBufElement(Element e){
        BufferedImage buf = null;
        try {
            buf= ImageIO.read(getClass().getResourceAsStream(e.sprite));
        } catch (IOException er) {
            er.printStackTrace();
            System.out.println("error occured");

        }
        return buf;
    }
    /**
     * elle dessine l'image dint  x,int y u{@code Tile} spÃ©cifiÃ© dans la position (x,y)passÃ©es en argument
     * @requires x>=0 && y>=0;
     * @requires t!=null;
     * @param g2 instance de {@code Graphics2D} pour utiliser les mÃ©thodes de dessin
     * @param t le tile Ã  afficher
     * @param x abcisse de l'image Ã  afficher
     * @param y ordonnÃ©e de l'image Ã  afficher
     * @throws NullPointerException si t est null
     * @throws IllegalArgumentException si x<0 ou y<0
     */



    public void drawTile(Graphics2D g2,Tile t,int posx,int posy){
        if (t==null){
            throw new NullPointerException("Tile est null");
        }
        if (posx<0||posy<0){
            throw new IllegalArgumentException("une des coordonnÃ©e est nÃ©gative");
        }
        g2.drawImage(getBufTile(t),posx,posy,originalTileSize,originalTileSize,null);
    }


    /**
     * elle dessine l'image du{@code Element} spÃ©cifiÃ© dans la position (x,y)passÃ©es en argument
     * @requires x>=0 && y>=0;
     * @requires e!=null;
     * @param g2 instance de {@code Graphics2D} pour utiliser les mÃ©thodes de dessin
     * @param e l'Ã©lement  Ã  afficher
     * @param x abcisse de l'image Ã  afficher
     * @param y ordonnÃ©e de l'image Ã  afficher
     * @throws NullPointerException si t est null
     * @throws IllegalArgumentException si x<0 ou y<0
     */

    public void drawElement(Graphics2D g2,Element e,int posx,int posy){
        if (e==null){
            throw new NullPointerException("Tile est null");
        }
        if (posx<0||posy<0){
            throw new IllegalArgumentException("une des coordonnÃ©e est nÃ©gative");
        }
        g2.drawImage(getBufElement(e),posx,posy,originalTileSize,originalTileSize,null);
    }

    public void update() {
        updatePlayer(this.lvl.player.posX, this.lvl.player.posY);
    }

    public void updatePlayer(int x, int y) {
        int dx = 0;
        int dy = 0;
        boolean test = false;

        if (keyH.upPressed) {
            dy = -1;
            test = lvl.isMovableHere(x, y, dx, dy, "up");
            if (test) this.lvl.player.posY--;
            keyH.upPressed = false;
        } else if (keyH.downPressed) {
            dy = 1;
            test = lvl.isMovableHere(x, y, dx, dy, "down");
            if (test) this.lvl.player.posY++;
            keyH.downPressed = false;
        } else if (keyH.leftPressed) {
            dx = -1;
            test = lvl.isMovableHere(x, y, dx, dy, "left");
            if (test) this.lvl.player.posX--;
            keyH.leftPressed = false;
        } else if (keyH.rightPressed) {
            dx = 1;
            test = lvl.isMovableHere(x, y, dx, dy, "right");
            if (test) this.lvl.player.posX++;
            keyH.rightPressed = false;
        }
        history.addMove(this.lvl.map);
    }






    /*elle permet de faire des transition */
    public void transition(int x,int y ){
        

    }
    /*elle permet d'actualiser le jeu  */
    public void gamerunner(){
        this.displayTerminal(this.lvl);
         
        while(true){
            update();
            repaint();
            /*             if (keyH.ispress()){
                update();
                repaint();
            }*/    
        }       
    }
 
    /**
     * elle affiche le niveau sur une fenetre
     */



    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 =(Graphics2D)g; 
        //Level lev = ((WorldBox)((EmptySpace)lvl.map[2][1]).Content).inside;
        //double nsc =  (double)windowSize/(originalTileSize*lev.size*this.scale);
        paintLevel(g2,this.lvl,0,0,this.scale);
        if(keyH.MKeyispressed){
            //paintLevel(g2, lev, 0, 0,nsc);
        }    
        g2.dispose();
    }
    public void paintLevel(Graphics2D g,Level l,int dx,int dy,double sc){
           g.scale(sc,sc);
           for (int i = 0;i<l.size;i++){
            for (int j = 0;j<l.size;j++){
                if(l.map[i][j] instanceof EmptySpace){
                    EmptySpace es =(EmptySpace)l.map[i][j];
                   //target,ground
                    if (es.isEmpty()){
                        drawTile(g,es,Double.valueOf(i*originalTileSize+dx/sc).intValue(),Double.valueOf(j*originalTileSize+dy/sc).intValue());
                    }else
                    //player,box or worldbox 
                    {       
                            if (es.Content instanceof WorldBox){
                                Level lev = ((WorldBox)es.Content).inside;
                                paintLevel(g,lev,Double.valueOf(i*originalTileSize+dx/sc).intValue(),Double.valueOf(j*originalTileSize+dy/sc).intValue(),sc/(lev.size*sc)); 
                                g.scale(lev.size, lev.size);
                        }else
                            drawElement(g,es.Content,Double.valueOf(i*originalTileSize+dx/sc).intValue(),Double.valueOf(j*originalTileSize+dy/sc).intValue());

                    }
                }else
                 //wall
                {
                    drawTile(g,l.map[i][j],Double.valueOf(i*originalTileSize+dx/sc).intValue(),Double.valueOf(j*originalTileSize+dy/sc).intValue());
                }
            }
        }
  
    } 

}