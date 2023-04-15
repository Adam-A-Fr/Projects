public class Infinibox extends Box implements Cloneable{

    public String levelbound; 
    public String actualevel; 

    public int grade;
    public int x,y;

    public Infinibox(String levelbound, String actualevel, int grade, int x, int y) {
        this.levelbound = levelbound;
        this.actualevel = actualevel;
        this.grade = grade;
        this.x = x;
        this.y = y;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
