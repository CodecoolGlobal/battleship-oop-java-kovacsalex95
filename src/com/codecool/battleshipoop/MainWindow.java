package com.codecool.battleshipoop;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
    private JPanel panel_main;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JPanel panel_board_container;
    private JPanel panel_board_1;
    private JPanel panel_board_2;
    private JToolBar toolbar_functions;

    public static void main(String args[])
    {
        JFrame frame = new JFrame("Battleship Hokkaido");
        frame.setContentPane(new MainWindow().panel_main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 500));
        frame.pack();
        frame.setVisible(true);
    }
}
