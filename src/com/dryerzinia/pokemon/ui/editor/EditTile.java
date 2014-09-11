package com.dryerzinia.pokemon.ui.editor;
import javax.swing.*;

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.obj.Move;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.obj.RandomFight;
import com.dryerzinia.pokemon.obj.tiles.Tile;
import com.dryerzinia.pokemon.obj.RandomFight.Edit;
import com.dryerzinia.pokemon.ui.menu.EditMenu;
import com.dryerzinia.pokemon.ui.menu.GMenu;

import java.awt.event.*;
import java.util.*;
import java.awt.*;

public class EditTile extends JFrame {

    JComboBox layer;
    JButton save;
    JButton editMenu;
    JButton rfMenu;
    JLabel imgnamelb;
    JTextField imgname;
    JCheckBox cbso;
    JTextField ctltf, ldtf, edtf, xntf, yntf, poxtf, poytf;
    JLabel ctll, ldl, edl, xnl, ynl, poxl, poyl;

    ArrayList<Tile> mtiles;
    ArrayList<Tile> sector;

    Container c;

    GMenu editg;

    Sub sub = new Sub(null);

    int curlay = 0;
    int editnum;

    ArrayList<Pokemon> bs;
    ArrayList<Move> ms;

    public EditTile(ArrayList<Tile> mtiles, ArrayList<Tile> sector,
            int editnum) {

        super("Edit Tile");

        c = getContentPane();
        setLayout(new FlowLayout());

        this.mtiles = mtiles;
        this.sector = sector;
        this.editnum = editnum;

        Tile t = sector.get(editnum);

        imgnamelb = new JLabel("ImgName:");
        imgname = new JTextField(sector.get(editnum).toString());
        cbso = new JCheckBox("Can be stepped on",
                sector.get(editnum).canBeSteppedOn);
        c.add(imgnamelb);
        c.add(imgname);
        c.add(cbso);

        layer = new JComboBox(new String[] { "0", "1", "2", "3", "4", "5" });
        layer.setSelectedIndex(editnum);
        layer.addItemListener(new AbstractItemListener() {
            public void itemStateChanged(ItemEvent e) {
                setLayer(Integer.parseInt((String) e.getItem()));
            }
        });
        c.add(layer);
      //  if (sector.get(editnum).onClick != null)
      //      editMenu = new JButton("Edit Menu");
      //  else
      //      editMenu = new JButton("Add Menu");
        GMenu gg = null;
        //if (sector.get(editnum).onClick != null)
        //    gg = (GMenu) sector.get(editnum).onClick.deepCopy();
        if (gg == null)
            gg = new GMenu("", 0, 6, 10, 3);
        final GMenu g = gg;
        editg = g;
        // final ImageLoader il = sector.get(editnum).il;
        editMenu.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // if(g == null) setEditG(new EditMenu(g, il));
                // else
                new EditMenu(g);
            }
        });
        c.add(editMenu);
        if (sector.get(editnum).rf != null) {
            sub.s = sector.get(editnum).rf;
            rfMenu = new JButton("Edit RandomFight");
        } else {
            rfMenu = new JButton("Add RandomFight");
        }
        rfMenu.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                new RandomFight.Edit(sub);
            }
        });
        c.add(rfMenu);
        save = new JButton("Save");
        save.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });

        ctltf = new JTextField("" + t.changeToLevel);
        ldtf = new JTextField("" + t.leaveDirection);
        edtf = new JTextField("" + t.exitDir);
        xntf = new JTextField("" + t.xnew);
        yntf = new JTextField("" + t.ynew);
        poxtf = new JTextField("" + t.pixelOffsetX);
        poytf = new JTextField("" + t.pixelOffsetY);
        ctll = new JLabel("Change To Level:");
        ldl = new JLabel("Leave Direction:");
        edl = new JLabel("Exit Direction:");
        xnl = new JLabel("New X:");
        ynl = new JLabel("New Y:");
        poxl = new JLabel("POX:");
        poyl = new JLabel("POY:");
        c.add(ctll);
        c.add(ctltf);
        c.add(ldl);
        c.add(ldtf);
        c.add(edl);
        c.add(edtf);
        c.add(xnl);
        c.add(xntf);
        c.add(ynl);
        c.add(yntf);
        c.add(poxl);
        c.add(poxtf);
        c.add(poyl);
        c.add(poytf);

        c.add(save);

        setSize(320, 250);
        setVisible(true);

    }

    public void setLayer(int i) {
        curlay = i;
    }

    public void save() {
        int n = JOptionPane.showConfirmDialog(null,
                "Modify master tile (Yes) Create new master tile (no)",
                "In what way?", JOptionPane.YES_NO_CANCEL_OPTION);
        if (n == 0) {
            Tile t = sector.remove(editnum);
            t.setImageName(imgname.getText());
            t.canBeSteppedOn = cbso.isSelected();

            t.changeToLevel = Integer.parseInt(ctltf.getText());
            t.leaveDirection = Direction.get(Integer.parseInt(ldtf.getText()));
            t.exitDir = Direction.get(Integer.parseInt(edtf.getText()));
            t.xnew = Integer.parseInt(xntf.getText());
            t.ynew = Integer.parseInt(yntf.getText());
            t.pixelOffsetX = Integer.parseInt(poxtf.getText());
            t.pixelOffsetY = Integer.parseInt(poytf.getText());

            //if (editg != null && !editg.message.equals(""))
            //    t.onClick = editg;
            sector.add(curlay, t);
            t.loadImage();
        } else if (n == 1) {
            Tile t = (Tile) sector.remove(editnum).deepCopy();
            t.setImageName(imgname.getText());
            t.canBeSteppedOn = cbso.isSelected();

            t.changeToLevel = Integer.parseInt(ctltf.getText());
            t.leaveDirection = Direction.get(Integer.parseInt(ldtf.getText()));
            t.exitDir = Direction.get(Integer.parseInt(edtf.getText()));
            t.xnew = Integer.parseInt(xntf.getText());
            t.ynew = Integer.parseInt(yntf.getText());
            t.pixelOffsetX = Integer.parseInt(poxtf.getText());
            t.pixelOffsetY = Integer.parseInt(poytf.getText());

            //if (editg != null && !editg.message.equals(""))
            //    t.onClick = editg;
            t.id = mtiles.size();
            mtiles.add(t);
            sector.add(curlay, t);
            t.loadImage();
        }
        dispose();
    }

}
