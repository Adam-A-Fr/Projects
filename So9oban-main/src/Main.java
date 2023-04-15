import java.util.Scanner;

/*
 * Class de test permettant de jouer basiquement avec un affichage "Système"
 * */
public class Main{

    public static void main(String[] args)
    {
        String testlevel = "C 9\n" +
                "#########\n" +
                "##   ##@#\n" +
                "## A    #\n" +
                "## F    #\n" +
                "## C    #\n" +
                "##      #\n" +
                "##@B    #\n" +
                "#       #\n" +
                "         \n" +
                "\n" +
                "F 5\n" +
                "#####\n" +
                "#   #\n" +
                "    #\n" +
                "#   #\n" +
                "#####\n" +
                "\n" +
                "A 5\n" +
                "#####\n" +
                "    #\n" +
                "    #\n" +
                "    #\n" +
                "#####";
        Level lvl= new Level(testlevel);
        Display disp= new Display(lvl);
        disp.startGame();
    }
}