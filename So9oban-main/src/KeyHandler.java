import java.awt.event.KeyListener; 
import java.awt.event.KeyEvent;

/*
* Class de lecture de données clavier avec un systeme ici de "press and release"
* */
public class KeyHandler implements KeyListener {

    public boolean upPressed,downPressed,leftPressed,rightPressed;
    public boolean MKeyispressed =false;
    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_S){
            if (!ispress())downPressed= true;
        }

        if(code == KeyEvent.VK_Z){
            if (!ispress())upPressed=true;
        }

        if(code == KeyEvent.VK_Q){
            if (!ispress())leftPressed= true;
        }

        if(code == KeyEvent.VK_D){
            if (!ispress())rightPressed= true;
        }
        if(code == KeyEvent.VK_M){
            if (!ispress())MKeyispressed= !MKeyispressed;
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
       /* // TODO Auto-generated method stub
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_S){
            downPressed= false;
        }

        if(code == KeyEvent.VK_Z){
            upPressed=false;
        }


        if(code == KeyEvent.VK_Q){
            leftPressed= false;
        }

        if(code == KeyEvent.VK_D){
            rightPressed= false;
        }*/
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public boolean ispress(){
        return upPressed||downPressed||leftPressed||rightPressed;
    }
}