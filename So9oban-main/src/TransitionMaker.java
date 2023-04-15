import java.util.ArrayList;

import javax.swing.plaf.basic.BasicTreeUI.TreeCancelEditingAction;

import java.awt.image.BufferedImage;
import java.time.OffsetDateTime;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;


public class TransitionMaker implements Cloneable{
   static int dx,dy=0;
   static int numOfTrans = 0;
   double scale ;
   Point2D.Double transformationVector ;
   static boolean one_transitiondone = false;
   //public static ArrayList<BufferedImage> links ;
   public static ArrayList<BufferedImage> Levellinks;
   double deltaX,deltaY;
   Level lvl;
   boolean isMoving =false;
   public TransitionMaker(Level lvl){
      this.lvl = lvl;
      this.scale = 1;
      transformationVector = new Point2D.Double(0,0);
      Levellinks = new ArrayList<>();
   }

   public TransitionMaker(int deltaX,int deltaY){
      
      this.scale = 1;
      transformationVector = new Point2D.Double(0,0);
     this.deltaX = deltaX;
     this.deltaY=deltaY;
   }

   public void one_transition(Player player,Graphics2D g2,int transx,int transy,Display dis){
      
      int originalTileSize = 48;

      g2.scale(this.scale,this.scale); 
      g2.translate(this.transformationVector.x,this.transformationVector.y);
   
      //the background can be an emptyspace or a target 
      BufferedImage buf;
      
      int height,width,offsetx=0;
      int offsety=0;
      height = transy - (player.posY-dy)*originalTileSize;
      width= transx-(player.posX-dx)*originalTileSize;
      if (width==0)width = 48;
      if (width<0) offsetx=48-((player.posX-dx)*originalTileSize-transx);
      if(height ==0)height =48;
      if (height<0)offsety=48-((player.posY-dy)*originalTileSize-transy);
      if (lvl.map[player.posX-dx][player.posY-dy]instanceof Target){
         buf =Display.links.get(3); 
         buf = buf.getSubimage(offsetx, offsety,Math.abs(width),Math.abs(height));
         g2.drawImage(buf,(player.posX-dx)*originalTileSize+Double.valueOf(deltaX).intValue()+offsetx,
         (player.posY-dy)*originalTileSize+Double.valueOf(deltaY).intValue()+offsety,Math.abs(width),Math.abs(height),null);
      }
      else {
         buf =Display.links.get(2); 
         buf = buf.getSubimage(offsetx, offsety,Math.abs(width),Math.abs(height));
         g2.drawImage(buf,(player.posX-dx)*originalTileSize+Double.valueOf(deltaX).intValue()+offsetx,
         (player.posY-dy)*originalTileSize+Double.valueOf(deltaY).intValue()+offsety,Math.abs(width),Math.abs(height),null);
           
      }
      
      int j = 0;
      for(int i =(numOfTrans-1);i>=0;--i){   
         
         if (i==0){
            if(this.lvl.player.world!=null){
            if(!one_transitiondone){
               ArrayList<String> s=new ArrayList<>();
               s.add(this.lvl.player.world.id);
               BufferedImage img=dis.saveLevelImage(this.lvl.player.world,dis.maxDepth-1,s,true);
               double sc =(double)originalTileSize/dis.windowSize;
               Levellinks.add(img);
               
            }
            double sc =(double)originalTileSize/dis.windowSize; 
               g2.scale(sc,sc);
               g2.drawImage(Levellinks.get(Levellinks.size()-1),Double.valueOf(((transx)+dx*(i)*originalTileSize+deltaX)/sc).intValue(),Double.valueOf(((transy)+dy*(i)*originalTileSize+deltaY)/sc).intValue(),(int)(originalTileSize/sc),(int)(originalTileSize/sc),null);
               g2.scale((double)dis.windowSize/originalTileSize,(double)dis.windowSize/originalTileSize);
         }else{
            g2.drawImage(Display.links.get(4), transx+dx*i*originalTileSize+Double.valueOf(deltaX).intValue(),transy+dy*i*originalTileSize+Double.valueOf(deltaY).intValue(),originalTileSize,originalTileSize,null);
         }

         }
         else //two cases WorldBox or Box 
         {
            EmptySpace es =(EmptySpace)lvl.map[player.posX+dx*i][player.posY+dy*i];
             if(es.Content instanceof WorldBox || es.Content instanceof Infinibox ){
               if(es.Content instanceof WorldBox){
                  if(!one_transitiondone){
                     ArrayList<String> s=new ArrayList<>();
                     s.add(((WorldBox)es.Content).inside.id);
                     Levellinks.add(dis.saveLevelImage(((WorldBox)es.Content).inside,dis.maxDepth-1,s,false));
                  }
               }
               if(es.Content instanceof Infinibox){
                  if(!one_transitiondone){
                     ArrayList<String> s=new ArrayList<>();
                     s.add(((Infinibox )es.Content).levelbound);
                     Level ll = Level.LinkedLevel.get(((Infinibox )es.Content).levelbound);
                     Levellinks.add(dis.saveLevelImage(ll,dis.maxDepth-1,s,false));
                  }
               }
               
               
               double sc =(double)originalTileSize/dis.windowSize; 
               g2.scale(sc,sc);
               g2.drawImage(Levellinks.get(j),Double.valueOf(((transx)+dx*(i)*originalTileSize+deltaX)/sc).intValue(),Double.valueOf(((transy)+dy*(i)*originalTileSize+deltaY)/sc).intValue(),(int)(originalTileSize/sc),(int)(originalTileSize/sc),null);
               g2.scale((double)dis.windowSize/originalTileSize,(double)dis.windowSize/originalTileSize);
               ++j;
            }
            else
            g2.drawImage(Display.links.get(1),transx+dx*i*originalTileSize+Double.valueOf(deltaX).intValue(),transy+dy*i*originalTileSize+Double.valueOf(deltaY).intValue(),originalTileSize,originalTileSize,null);
         }
      }
      one_transitiondone =true;
   }
   public static void setDirection(int dx,int dy){
      TransitionMaker.dx=dx;
      TransitionMaker.dy=dy;
   }
   public void setTransformationVector(double x,double y){
      this.transformationVector.x = x;
      this.transformationVector.y = y;
   }
   public void setScale(double sc){
      this.scale = sc;
   }
   public void setDelta(double deltaX,double deltaY){
      this.deltaX = deltaX;
      this.deltaY = deltaY;
   }
   @Override
   public Object clone() {
      TransitionMaker tM2 = null;
		try {
			tM2 = (TransitionMaker) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError("Clone aurait du fonctionner");
		}
      tM2.transformationVector = new Point2D.Double(transformationVector.x,transformationVector.y);
      tM2.lvl = this.lvl;
   return tM2;  
   }
   public static void incrementer(){
      ++TransitionMaker.numOfTrans;
   }
   
}