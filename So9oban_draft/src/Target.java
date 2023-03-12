


public class Target extends EmptySpace{

    private boolean filled;
    public Target(){
        this.sprite="assets/target.png";
        this.Content =null;
    }
/* 
    public Target(Element e){
        this.sprite="assets/target.png";
        this.Content =e;
    }
*/

    public boolean isFilled() {
        return this.Content != null;
    }

}