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

    public FieldPanel fieldDrawer = null;
    private Game game = null;

    public static void main(String args[])
    {
        MainWindow window = new MainWindow();

        window.game = new Game(window);

        JFrame frame = new JFrame("Battleship Hokkaido");
        frame.setContentPane(window.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 500));

        window.fieldDrawer = new FieldPanel();
        window.fieldDrawer.init(window.game);
        window.panelBoardContainer.add(window.fieldDrawer);
        //window.fieldDrawer.set

        window.newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                window.game.Start();
            }
        });
        frame.pack();
        frame.setVisible(true);
    }
}
