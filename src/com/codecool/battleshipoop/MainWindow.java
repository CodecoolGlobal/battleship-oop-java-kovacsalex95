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
    private JPanel panelBoard1;
    private JPanel panelBoard2;
    private JToolBar toolbarFunctions;

    public static void main(String args[])
    {
        JFrame frame = new JFrame("Battleship Hokkaido");
        frame.setContentPane(new MainWindow().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 500));
        frame.pack();
        frame.setVisible(true);
    }
}
