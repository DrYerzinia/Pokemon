import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class EditMenu extends JFrame {

    GMenu gm;

    JTextField menutext, xtf, ytf, wtf, htf;
    JLabel xl, yl, wl, hl, msg;
    JButton saveb;

    Container c;

    public EditMenu() {

        new EditMenu(new GMenu("", 0, 6, 10, 3));
        this.dispose();

    }

    public EditMenu(GMenu gm) {

        super("Edit Menu");

        this.gm = gm;

        c = getContentPane();
        setLayout(new FlowLayout());

        menutext = new JTextField(UltimateEdit.outFix(gm.message), 15);
        xtf = new JTextField(gm.x + "");
        ytf = new JTextField(gm.y + "");
        wtf = new JTextField(gm.w + "");
        htf = new JTextField(gm.h + "");
        msg = new JLabel("Message:");
        xl = new JLabel("X:");
        yl = new JLabel("Y:");
        wl = new JLabel("W:");
        hl = new JLabel("H:");
        saveb = new JButton("Save");
        saveb.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 5028779236775465799L;

            public void actionPerformed(ActionEvent e) {
                save();
            }
        });

        c.add(msg);
        c.add(menutext);
        c.add(xl);
        c.add(xtf);
        c.add(yl);
        c.add(ytf);
        c.add(wl);
        c.add(wtf);
        c.add(hl);
        c.add(htf);
        c.add(saveb);

        setSize(320, 250);
        setVisible(true);

    }

    private void save() {
        gm.message = UltimateEdit.inFix(menutext.getText());
        gm.x = Integer.parseInt(xtf.getText());
        gm.y = Integer.parseInt(ytf.getText());
        gm.w = Integer.parseInt(wtf.getText());
        gm.h = Integer.parseInt(htf.getText());
    }
}
