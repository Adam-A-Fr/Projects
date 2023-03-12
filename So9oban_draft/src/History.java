import java.util.LinkedList;

public class History {
    private final int MAX_LENGTH = 99;
    LinkedList <Tile[][]> moves ;

    public History() {
        this.moves = new LinkedList<Tile[][]>();
    }

    public void addMove (Tile[][] map) {
        this.moves.addFirst(map);
        if (this.moves.size()> MAX_LENGTH) this.moves.removeLast();
    }

    public Tile[][] returnMove () {
        return this.moves.removeFirst();
    }
}
