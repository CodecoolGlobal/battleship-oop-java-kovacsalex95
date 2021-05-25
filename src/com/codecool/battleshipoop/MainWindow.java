package com.codecool.battleshipoop;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
    private JPanel panelMain;
    private JButton newGameButton;
    private JButton dontPressMeButton;
    private JButton creditsButton;
    private JButton exitGameButton;
    private JPanel panelBoardContainer;
    private JToolBar toolbarFunctions;

    public static void main(String args[])
    {
        MainWindow window = new MainWindow();

        JFrame frame = new JFrame("Battleship Hokkaido");
        frame.setContentPane(window.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 500));

        FieldPanel panel1 = new FieldPanel();
        window.panelBoardContainer.add(panel1);

        frame.pack();
        frame.setVisible(true);
    }
}
