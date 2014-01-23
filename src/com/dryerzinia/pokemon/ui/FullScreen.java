package com.dryerzinia.pokemon.ui;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;

import javax.swing.*;

import com.dryerzinia.pokemon.PokemonGame;

public class FullScreen {
//TODO FIX FULLSCREEN CLAS
    public FullScreen() {
/*
        Applet pg = new PokemonGame();
        JFrame win = new JFrame("Pokemon");
        win.getContentPane().add(pg);
        win.addWindowListener((WindowListener) pg);

        win.setSize(400, 400);
        win.setVisible(true);

        pg.init();

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        try {

            System.out.println(gd.isFullScreenSupported());

            gd.setFullScreenWindow(win);

            System.out.println("FS");
            Thread.sleep(20000);

        } catch (Exception x) {

            x.printStackTrace();

        } finally {

            gd.setFullScreenWindow(null);

        }
*/
    }

    public static void main(String args[]) {

        System.out.println(args[0]);

        // new FullScreen();

    }

}
