import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import java.lang.reflect.*;

public class EditableArray extends JFrame {

    Object[] oa;

    Container c;

    public EditableArray(Object array) {
        c = getContentPane();
        c.setLayout(new FlowLayout());

        int l = Array.getLength(array);
        oa = new Object[l];
        for (int k = 0; k < l; k++) {
            oa[k] = Array.get(array, k);
            final Object toEdit = oa[k];
            JButton editB = new JButton("Edit " + oa[k]);
            editB.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        int l = Array.getLength(toEdit);
                        new EditableArray(toEdit);
                        return;
                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                    if (toEdit != null)
                        new UltimateEdit(toEdit);
                }
            });
            c.add(editB);
        }
        JButton addB = new JButton("Add");
        addB.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        JButton removeB = new JButton("Remove");
        removeB.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        c.add(addB);
        c.add(removeB);
        setSize(400, 700);
        setVisible(true);
    }

}
