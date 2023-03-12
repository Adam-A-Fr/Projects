import java.util.Scanner;

/*
 * Class de test permettant de jouer basiquement avec un affichage "Système"
 * */
public class Main{

    public static void main(String[] args)
    {
        String testlevel = "L 9 #########" +
                "#.......#" +
                "#...@.&.#" +
                "#.&.....#" +
                "#...&...#" +
                "#.....&.#" +
                "#.&.*A..#" +
                "#.......#" +
                "#########" +
                " A 9 #.########.......##.....&.##.&.....##...&...##.....&.##.&.*B..##.......########## B 3 ###..####";
        Level lvl= new Level(testlevel);
        /*String testlevel2 = "J 3 #.#" +
                "#.#" +
                "###" ;
        Level lvl2= new Level(testlevel2);
        ((WorldBox) lvl.map[5][6].Content).inside = lvl2;*/

        Display disp= new Display(lvl);
        disp.startGame();
    }
}