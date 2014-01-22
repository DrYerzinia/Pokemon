package com.dryerzinia.pokemon.ui.editor;
import javax.swing.*;

import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Tile;

import java.awt.event.*;
import java.util.*;
import java.awt.*;

public class AddLevel extends JFrame {
    
    JTextField wtf, htf;
    JLabel wl, hl;
    JLabel borderl[];
    // JComboBox bordercb[];
    // JComboBox borderoffcb[];

    JButton add;

    Container c;

    public AddLevel() {

        super("Add Level");

        c = getContentPane();
        setLayout(new FlowLayout());

        wtf = new JTextField("0");
        htf = new JTextField("0");
        wl = new JLabel("Width");
        hl = new JLabel("Height");

        add = new JButton("Add Level");
        add.addActionListener(new AbstractAction() {

            private static final long serialVersionUID = -483899693264284784L;
            
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });

        c.add(wl);
        c.add(wtf);
        c.add(hl);
        c.add(htf);

        c.add(add);

        /*
         * 
         * Grid g = new Grid(w, h); for(int x = 0; x < w; x++) for(int y = 0; y
         * < h; y++) g.add(x,y,mtile.get(29));
         * 
         * level[6] = new Level(-2, 0, g, 1, Char); level[6].id = 6; g.l =
         * level[6];
         * 
         * level[5].borderL[1] = level[6]; level[5].borders[1] = 6;
         * level[5].borderoffset[1] = 5;
         * 
         * level[6].borderL[7] = level[5]; level[6].borders[7] = 5;
         * level[6].borderoffset[7] = -5;
         */

        setSize(320, 250);
        setVisible(true);

    }

    private void add() {
        int w = Integer.parseInt(wtf.getText());
        int h = Integer.parseInt(htf.getText());
        Grid g = new Grid(w, h);
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                g.add(x, y, mtiles.get(29));

        Level level = new Level(g);
        level.id = levels.size();
        g.l = level;

        System.out.println("lvlid" + level.id);

        levels.add(level);

    }

}
