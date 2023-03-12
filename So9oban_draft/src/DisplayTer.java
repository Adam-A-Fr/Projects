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
 * La classe {@code Display} sert à afficher une instance de{@code Level} dans une interface graphique (une fenetre) 
 * de taille fixe et vous pouvez la fermer en cliquant sur la bouton X .Cette interface affiche les Objets de jeu 
 * (élements et le monde de jeu )en affichant des images.Elle a comme atributs des paramètres d'affichage
 * 
 * Dans cette version on suppose que la map est carrée 
 * Scale c'est pour zommer ,il faut respecter toujours la taille maximale de l'écran 
 * les sizes sont donnés en pixel
 * on imagine que cette window est une matrice dont:
 * maxcol est le nombre de colonnes dans cette fenetre
 * maxRow est le nombre de lignes dans cette fenetre
 * @author Boutrik Mohamed 
 * @version 1 (1/12/2022)
 * 
 */
public class DisplayTer extends JPanel {
   
   private Level lvl;
   private final int originalTileSize=48;
   private final int scaledTileSize;
   private final int scale =2;
   private final int windowSize;
   private final int maxCol;
   private final int maxRow;
   
public DisplayTer(Level lvl){
   super();
   this.scaledTileSize=originalTileSize*scale;
   this.windowSize = lvl.size * scaledTileSize;
   this.maxCol = lvl.size;
   this.maxRow = lvl.size;
   this.lvl = lvl;

   JFrame frame = new JFrame(); 
   frame.setTitle("So9oban");
   frame.setSize(windowSize,windowSize);
   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

   this.setPreferredSize(new Dimension(windowSize,windowSize));
   this.setBackground(Color.lightGray);
   this.setDoubleBuffered(true);
   this.setFocusable(true);   
   
   frame.setVisible(true); 
   frame.setResizable(false);
   frame.add(this);
   frame.pack();
}
/**
 * retourne le buffer sur l'image de {@code Tile} spécifié si elle réussit pas elle retourne null
 * @requires t!=null;
 * @param t est le tile à afficher et qu'on cherche son buffer 
 * @return un buffer sur l'image de tile ou null si elle échoue 
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
 * retourne le buffer sur l'image de {@code Element} spécifié si elle réussit pas elle retourne null
 * @requires t!=null;
 * @param t est le tile à afficher et qu'on cherche son buffer 
 * @return un buffer sur l'image d'element ou null si elle échoue 
 * @throws NullPointerException si t est null 
 */
public BufferedImage getBufElement(Element e ){
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
 * elle dessine l'image du{@code Tile} spécifié dans la position (x,y)passées en argument
 * @requires x>=0 && y>=0;
 * @requires t!=null;
 * @param g2 instance de {@code Graphics2D} pour utiliser les méthodes de dessin 
 * @param t le tile à afficher 
 * @param x abcisse de l'image à afficher 
 * @param y ordonnée de l'image à afficher 
 * @throws NullPointerException si t est null
 * @throws IllegalArgumentException si x<0 ou y<0
 */
public void drawTile(Graphics2D g2,Tile t,int x,int y){
   if (t==null){
      throw new NullPointerException("Tile est null");
   }
   if (x<0||y<0){
      throw new IllegalArgumentException("une des coordonnée est négative");
   }
   g2.drawImage(getBufTile(t),x,y,scaledTileSize,scaledTileSize,null);
}
/**
 * elle dessine l'image du{@code Element} spécifié dans la position (x,y)passées en argument
 * @requires x>=0 && y>=0;
 * @requires e!=null;
 * @param g2 instance de {@code Graphics2D} pour utiliser les méthodes de dessin 
 * @param t l'élement  à afficher 
 * @param x abcisse de l'image à afficher 
 * @param y ordonnée de l'image à afficher 
 * @throws NullPointerException si t est null
 * @throws IllegalArgumentException si x<0 ou y<0
 */
public void drawElement(Graphics2D g2,Element e,int x,int y ){
   if (e==null){
      throw new NullPointerException("Tile est null");
   }
   if (x<0||y<0){
      throw new IllegalArgumentException("une des coordonnée est négative");
   }
   g2.drawImage(getBufElement(e),x,y,scaledTileSize,scaledTileSize,null);
}
/**
 * elle affiche le niveau sur une fenetre 
 */
public void paintComponent(Graphics g){
   super.paintComponent(g);
   Graphics2D g2 =(Graphics2D)g;
   for (int i = 0;i<this.maxCol;i++){
      for (int j = 0;j<this.maxRow;j++){
         if(this.lvl.map[i][j] instanceof EmptySpace){
            EmptySpace es =(EmptySpace)this.lvl.map[i][j]; 
            if (es.isEmpty()){
               drawTile(g2,es,i*scaledTileSize,j*scaledTileSize);
            }else{
               drawElement(g2,es.Content,i*scaledTileSize,j*scaledTileSize);
            }
        
         }else{
            drawTile(g2,this.lvl.map[i][j],i*scaledTileSize,j*scaledTileSize);
         }
      }
   }
   g2.dispose();
}





    public static void displayTerminal(Level lvl) {
        for (int i = 0; i < lvl.size; i++) {
            for (int j = 0; j < lvl.size; j++) {
                // System.out.println(lvl.map[j][i]);
                if (lvl.map[j][i] instanceof Wall) {
                    System.out.print('#');
                } else if (lvl.map[j][i] instanceof Target) {
                    System.out.print("*");
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
}