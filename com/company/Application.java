package com.company;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Application extends JFrame {

    public Application() {

        initUI();
    }

    private void initUI() {

        add(new swingBoardExample_workinprogress());

        setSize(1000, 850);

        setTitle("Breakthru");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            Application ex = new Application();
            ex.setVisible(true);
        });
    }

    public void draw(){
        LastMove();
    }

    public void LastMove(){
        // UPDATE THIS IN EVERY MOVE
        // DRAW A LINE BETWEEN TWO COORDINATES IN THE MOVE
    }
}