import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.*;

public class EditLevel extends JFrame {

    public ArrayList<Level> level;

    Level l;

    Container c;

    ArrayList<Tile> mtiles;

    JTextField offsetTF[] = new JTextField[9];
    JTextField levelTF[] = new JTextField[9];
    JTextField xTF, yTF, xoTF, yoTF;

    public EditLevel(ArrayList<Level> level, int n, ArrayList<Tile> mtiles) {

        super("Edit Level");

        c = getContentPane();
        setLayout(new FlowLayout());

        this.level = level;
        this.mtiles = mtiles;

        l = level.get(n);

        JButton saveB;

        xTF = new JTextField("" + l.g.g.length, 5);
        yTF = new JTextField("" + l.g.g[0].length, 5);
        xoTF = new JTextField("0", 5);
        yoTF = new JTextField("0", 5);

        c.add(xTF);
        c.add(yTF);
        c.add(xoTF);
        c.add(yoTF);
        c.add(Box.createHorizontalStrut(20000));
        for (int i = 0; i < 9; i++) {
            offsetTF[i] = new JTextField("" + l.borderoffset[i], 8);
            levelTF[i] = new JTextField("" + l.borders[i], 8);
            c.add(offsetTF[i]);
            c.add(levelTF[i]);
            c.add(Box.createHorizontalStrut(20000));
        }
        saveB = new JButton("Save");
        saveB.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = -5737967531863447171L;

            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        c.add(saveB);

        setSize(320, 370);
        setVisible(true);

    }

    public void save() {
        for (int i = 0; i < 9; i++) {
            int offset = Integer.parseInt(offsetTF[i].getText());
            int level = Integer.parseInt(levelTF[i].getText());
            System.out.println("off" + offset + "lve" + level);
            if (level != -1) {
                l.borders[i] = level;
                l.borderL[i] = this.level.get(level);
                l.borderoffset[i] = offset;
                int x = Integer.parseInt(xTF.getText());
                int y = Integer.parseInt(yTF.getText());
                int xo = Integer.parseInt(xoTF.getText());
                int yo = Integer.parseInt(yoTF.getText());
                if (x != l.g.g.length || y != l.g.g[0].length) {
                    Grid g = new Grid(x, y);
                    Grid g2 = l.g;
                    for (int x2 = 0; x2 < g2.g.length; x2++) {
                        for (int y2 = 0; y2 < g2.g[0].length; y2++) {
                            g.g[x2 + xo][y2 + yo] = new ArrayList<Tile>();
                            for (int i2 = 0; i2 < g2.g[x2][y2].size(); i2++) {
                                g.g[x2 + xo][y2 + yo].add(g2.g[x2][y2].get(i2));
                            }
                        }
                    }
                    for (int x2 = 0; x2 < g.g.length; x2++) {
                        for (int y2 = 0; y2 < g.g[0].length; y2++) {
                            if (g.g[x2][y2] == null) {
                                g.g[x2][y2] = new ArrayList<Tile>();
                                g.g[x2][y2].add(mtiles.get(29));
                            }
                        }
                    }
                    l.g = g;
                    g.l = l;
                }
            }
        }
    }

}
