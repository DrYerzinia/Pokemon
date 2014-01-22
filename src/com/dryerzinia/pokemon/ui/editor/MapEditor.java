package com.dryerzinia.pokemon.ui.editor;
import javax.swing.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.map.Grid;
import com.dryerzinia.pokemon.obj.Move;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.obj.Tile;

import java.awt.event.*;
import java.util.*;
import java.awt.*;

public class MapEditor extends JFrame implements MouseListener {

    ArrayList<Tile> sector = null;

    Container c;

    JComboBox tiles;
    ImageIcon layers[];
    JLabel lnames[];
    JButton add, addbc, rw;
    JLabel preview;
    ImageIcon pico;

    JPopupMenu rc;

    int lastClick = -1;
    int lastx, lasty;

    boolean abc = false;

    public MapEditor() {
        super("In-Game Map Editor!");

        c = getContentPane();
        setLayout(new FlowLayout());

        rc = new JPopupMenu();
        JMenuItem del = rc.add("Delete");
        del.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        });
        final ArrayList<Tile> mtile = pg.mtile;
        JMenuItem edt = rc.add("Edit");
        edt.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (sector.get(lastClick).getClass().getName().indexOf("Tile") != -1)
                    new EditTile(mtile, sector, lastClick, bs, ms);
                else
                    new UltimateEdit(mtile.get(sector.get(lastClick).id));
            }
        });
        JMenuItem add = rc.add("Add");
        add.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                new AddTile(mtile, sector);
            }
        });
        this.add = new JButton("Add");
        this.add.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                new AddTile(mtile, sector);
            }
        });
        tiles = new JComboBox(pg.mtile.toArray());
        tiles.addItemListener(new AbstractItemListener() {
            public void itemStateChanged(ItemEvent e) {
                setTile((Tile) e.getItem());
            }
        });
        this.addbc = new JButton("Add By Click");
        this.addbc.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                abc = !abc;
            }
        });
        this.rw = new JButton("Remove Level White");
        this.rw.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Grid g = PokemonGame.pokeg.level
                        .get(Player.self.level).g;
                for (int x = 0; x < g.getWidth(); x++) {
                    for (int y = 0; y < g.getHeight(); y++) {
                        for (int i = 0; i < g.g[x][y].size(); i++) {
                            if (g.g[x][y].get(i).toString()
                                    .equals("WhiteTile.png")) {
                                g.g[x][y].remove(i);
                                i--;
                            }
                        }
                    }
                }
            }
        });

        layers = new ImageIcon[6];
        lnames = new JLabel[6];

        for (int i = 0; i < 6; i++) {
            layers[i] = new ImageIcon();
            lnames[i] = new JLabel();
            lnames[i].setIcon(layers[i]);
            c.add(lnames[i]);
            final int i2 = i;
            lnames[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
                        popup(i2, e.getX(), e.getY());
                    }
                }
            });
        }

        c.add(this.add);
        c.add(tiles);
        pico = new ImageIcon(pg.mtile.get(0).getImage());
        preview = new JLabel(pico);
        c.add(preview);
        c.add(addbc);
        c.add(rw);

        final MapEditor met = this;
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                PokemonGame.pokeg.removeMouseListener(met);
                PokemonGame.pokeg.me = null;
                dispose();
            }
        });

        setSize(320, 240);
        setVisible(true);

    }

    public void setTile(Tile t) {
        pico.setImage(t.getImage());
        preview.repaint();
    }

    public void addTile(int x, int y) {
        pg.level.get(pg.Char.level).g.g[x + Player.self.x][y
                + Player.self.y].add((Tile) tiles.getSelectedItem());
    }

    public void popup(int i, int x, int y) {
        lastClick = i;
        rc.show(lnames[i], x, y);
    }

    public void delete() {
        sector.remove(lastClick);
        loadSector(lastx, lasty);
    }

    public void loadSector(int x, int y) {
        lastx = x;
        lasty = y;
        sector = pg.level.get(pg.Char.level).g.g[x + Player.self.x][y
                + Player.self.y];
        for (int i = 0; i < 6; i++) {
            if (i < sector.size()) {
                lnames[i].setText(sector.get(i) + "");
                try {
                    layers[i].setImage(sector.get(i).getImage());
                } catch (NullPointerException npe) {
                }
            } else {
                lnames[i].setText("");
                try {
                    layers[i].setImage(null);
                } catch (Exception c) {
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / 32;
        int y = e.getY() / 32;
        if (!abc)
            loadSector(x, y);
        else
            addTile(x, y);
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
