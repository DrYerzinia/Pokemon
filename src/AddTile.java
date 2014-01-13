import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.*;

public class AddTile extends JFrame {

    JComboBox tiles;
    JComboBox layer;
    JButton add;

    ArrayList<Tile> mtiles;
    ArrayList<Tile> sector;

    ArrayList<Class> classList;

    Class setClass;

    Container c;

    int curlay = 0;

    public AddTile(ArrayList<Tile> mtiles, ArrayList<Tile> sector) {

        super("Add Tile");

        c = getContentPane();
        setLayout(new FlowLayout());

        this.mtiles = mtiles;
        this.sector = sector;

        classList = readClasses("Tile");
        setClass = classList.get(0);

        tiles = new JComboBox(classList.toArray());
        tiles.addItemListener(new AbstractItemListener() {
            public void itemStateChanged(ItemEvent e) {
                setClass = (Class) e.getItem();
            }
        });
        c.add(tiles);
        layer = new JComboBox(new String[] { "0", "1", "2", "3", "4", "5" });
        layer.addItemListener(new AbstractItemListener() {
            public void itemStateChanged(ItemEvent e) {
                setLayer(Integer.parseInt((String) e.getItem()));
            }
        });
        c.add(layer);
        add = new JButton("Add");
        add.addActionListener(new AbstractAction() {
            
            private static final long serialVersionUID = -7534235305332006798L;

            public void actionPerformed(ActionEvent e) {
                add();
            }
        });
        c.add(add);

        setSize(320, 250);
        setVisible(true);

    }

    public static ArrayList<Class> readClasses(String ext) {
        ArrayList<Class> cs = new ArrayList<Class>();
        try {
            for (int i = 0; i < PokemonGame.allClasses.size(); i++) {
                Class c = PokemonGame.allClasses.get(i);
                System.out.println(c);
                if (UltimateEdit.Extends(c, ext)) {
                    System.out.println(c);
                    cs.add(c);
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return cs;
    }

    public void setLayer(int i) {
        curlay = i;
    }

    public void add() {
        try {
            Tile t = (Tile) setClass.newInstance();
            t.id = mtiles.size();
            new UltimateEdit(t);
            mtiles.add(t);
            sector.add(curlay, t);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

}
