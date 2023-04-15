import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class Menu extends JPanel {
    private int size;
    public Menu() {
        super();
        this.setBackground(Color.black);
        this.setLayout(null);
        // to have the window size from the config File
        BufferedImage Titleimage = null;
        BufferedImage Playimage = null;
        try {
            FileReader fr = new FileReader(System.getProperty("user.dir")   + "\\src\\assets\\config.txt");
            BufferedReader br = new BufferedReader(fr);
            size = Integer.parseInt(br.readLine());
            Playimage = ImageIO.read(new File(System.getProperty("user.dir")   + "\\src\\assets\\Playbtn.png"));
                    Titleimage = ImageIO.read(new File(System.getProperty("user.dir")   + "\\src\\assets\\Title.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageIcon image = new ImageIcon(Titleimage);
        JLabel timage = new JLabel(image);
        timage.setBounds(size / 2 - 75, 0, 150, 50);

        this.add(timage);


        JButton NGbutton = new JButton(new ImageIcon(Playimage));
        NGbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "\\src\\assets\\outlevels"));
                int result = fileChooser.showOpenDialog(getParent());
                File selectedFile = null;
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getName());
                }
                Level lvl = new Level(Level.filetolvl(selectedFile.getName()));
                Display disp = new Display(lvl);
                disp.startGame();

                // Launch a game

            }
        });
        NGbutton.setBackground(Color.darkGray);
        NGbutton.setBounds(size / 2 - 85, timage.getY() + 60, 170, 45);
        this.add(NGbutton);

        JButton Setbutton = new JButton("Res"+size+"x"+size);
        Setbutton.setBounds(size/2 -85,NGbutton.getY() + 50 , 170,45);
        Setbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(size ==512) size =1024;
                else size = 512;
                try {
                    FileWriter writer = new FileWriter(System.getProperty("user.dir")   + "\\src\\assets\\config.txt");
                    writer.write(Integer.toString(size));
                    writer.close();

                    Setbutton.setText("Res"+size+"x"+size);

                } catch (IOException ex) {
                    System.out.println("An error occurred while overriding the file: " + ex.getMessage());
                }




            }
        });
        this.add(Setbutton);



        JButton Quit =new JButton("Quit");
        Quit.setBounds(size/2 -85,Setbutton.getY() + 50 , 170,45);
        Quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        this.add(Quit);

    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        Menu m =new Menu();
        f.add(m);
        f.setSize(m.size,m.size);
        f.setVisible(true);
        f.setResizable(false);



    }







}



