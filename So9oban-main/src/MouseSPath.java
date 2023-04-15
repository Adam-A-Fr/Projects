import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.MouseInfo;

public class MouseSPath extends MouseAdapter {
    Point coordinates;
    boolean pressed = false;
    public MouseSPath(){
        coordinates = new Point(0,0);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        coordinates =  MouseInfo.getPointerInfo().getLocation();
        pressed = true;
    }   
    public Point getTilePosition(Display dis,double scale,double marge){
        //si le niveau est dans toute la fenetre scale is Default scale and marge est nulle
        int x,y;
            double Levelsize = dis.lvl.size*Display.originalTileSize*scale;
            //the cast won't affect the result
            int mousePointerX =coordinates.x-dis.getLocationOnScreen().x-(int)marge;
            int mousePointerY =coordinates.y-dis.getLocationOnScreen().y-(int)marge;
            int scaledTileSize = (int)(Display.originalTileSize*scale);
            if (mousePointerX<0||mousePointerY<0||mousePointerX>Levelsize||mousePointerY>Levelsize)return new Point(-1, -1);
            x= (int)mousePointerX/scaledTileSize;
            y = (int)mousePointerY/scaledTileSize;
        return new Point(x, y);
    } 

}
