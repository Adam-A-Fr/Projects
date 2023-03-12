
public class EmptySpace extends Tile{

    //What's on the Tile (Null if there is nothing)
    public Element Content;


    public boolean isEmpty(){
        return this.Content==null;
    }

    public EmptySpace(){
        this.sprite="assets/ground.png";
        this.Content =null;
    }

    public EmptySpace(Element e){
        this.sprite="assets/ground.png";
        this.Content =e;
    }
}