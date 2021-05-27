package com.codecool.battleshipoop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow {
    private JPanel panelMain;
    private JButton newGameButton;
    private JButton dontPressMeButton;
    private JButton creditsButton;
    private JButton exitGameButton;
    private JPanel panelBoardContainer;
    private JToolBar toolbarFunctions;

    public FieldPanel fieldPanel = null;
    private Game game = null;

    public static void main(String args[])
    {
        MainWindow window = new MainWindow();

        window.game = new Game(window);

        JFrame frame = new JFrame("Battleship Hokkaido");
        frame.setContentPane(window.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1280, 800));

        window.fieldPanel = new FieldPanel();
        window.fieldPanel.init(window.game);
        window.panelBoardContainer.add(window.fieldPanel);

        window.newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                window.game.Start();
            }
        });

        window.dontPressMeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.fieldPanel.madness = !window.fieldPanel.madness;
                window.fieldPanel.madnessAmount = 0;
            }
        });

        window.creditsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.fieldPanel.creditsRows = null;
                window.fieldPanel.creditsMode = !window.fieldPanel.creditsMode;
            }
        });

        window.exitGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(4);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}
