package com.dryerzinia.pokemon.ui.editor;
import javax.swing.*;

import java.awt.event.*;
import java.util.*;
import java.awt.*;
import java.lang.reflect.*;

public class UltimateEdit extends JFrame {

    Object o2;
    Class cc2;

    Container c;

    ArrayList<JTextField> fields = new ArrayList<JTextField>();

    public UltimateEdit(Object o) {

        this.o2 = o;

        c = getContentPane();
        c.setLayout(new FlowLayout());

        init(c, o, o.getClass());

        setSize(400, 700);
        setVisible(true);

    }

    public UltimateEdit(Object o, Class cc) {
        this.o2 = o;

        c = getContentPane();
        c.setLayout(new FlowLayout());

        init(c, o, cc);

        setSize(400, 700);
        setVisible(true);
    }

    public void init(Container c, Object o, Class cc) {
        Field f[] = cc.getDeclaredFields();
        cc2 = cc;
        for (int i = 0; i < f.length; i++) {
            try {
                f[i].setAccessible(true);
                if (f[i].getType() == int.class) {
                    int j = f[i].getInt(o);
                    JTextField JTF = new JTextField("" + j);
                    fields.add(JTF);
                    c.add(new JLabel(f[i].getName() + " (Int):"));
                    c.add(JTF);
                } else if (f[i].getType() == boolean.class) {
                    boolean j = f[i].getBoolean(o);
                    JTextField JTF = new JTextField("" + j);
                    fields.add(JTF);
                    c.add(new JLabel(f[i].getName() + " (Boolean):"));
                    c.add(JTF);
                } else if (f[i].getType().getName().equals("java.lang.String")) {
                    String j = (String) f[i].get(o);
                    JTextField JTF = new JTextField("" + outFix(j), 8);
                    fields.add(JTF);
                    c.add(new JLabel(f[i].getName() + " ("
                            + f[i].getType().getName() + "):"));
                    c.add(JTF);
                } else {
                    final Object toEdit = f[i].get(o);
                    final Field fi = f[i];
                    final Object o3 = o;
                    final Class eClass = f[i].getType();
                    JButton editB = new JButton("Edit " + f[i].getName() + " ("
                            + f[i].getType().getName() + ")");
                    editB.addActionListener(new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println();
                            Class[] cs = eClass.getDeclaredClasses();
                            for (int i = 0; i < cs.length; i++) {
                                String cName = cs[i].getName();
                                System.out.println("Cb" + cName);
                                if (cName.substring(cName.indexOf("$") + 1,
                                        cName.length()).equals("Edit")) {
                                    Class eClass2 = cs[i];
                                    Constructor con[] = eClass2
                                            .getDeclaredConstructors();
                                    try {
                                        con[0].newInstance(new Sub(toEdit));
                                    } catch (Exception x) {
                                        x.printStackTrace();
                                    }
                                    return;
                                }
                            }
                            try {
                                int l = Array.getLength(toEdit);
                                new EditableArray(toEdit);
                                return;
                            } catch (Exception x) {
                                x.printStackTrace();
                            }
                            try {
                                if (toEdit != null)
                                    new UltimateEdit(toEdit);
                                else {
                                    Object o2 = eClass.newInstance();
                                    fi.set(o3, o2);
                                    new UltimateEdit(o2);
                                }
                            } catch (Exception x) {
                            }
                        }
                    });
                    c.add(editB);
                }
                c.add(Box.createHorizontalStrut(20000));
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
        JButton superB = new JButton("Super");
        superB.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                new UltimateEdit(o2, cc2.getSuperclass());
            }
        });
        c.add(superB);
        JButton saveB = new JButton("Save");
        saveB.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        c.add(saveB);
    }

    public void save() {
        int fieldCount = 0;
        Field f[] = cc2.getDeclaredFields();
        for (int i = 0; i < f.length; i++) {
            try {
                f[i].setAccessible(true);
                if (f[i].getType() == int.class) {
                    int j = Integer.parseInt(fields.get(fieldCount).getText());
                    f[i].setInt(o2, j);
                } else if (f[i].getType() == boolean.class) {
                    boolean j = Boolean.valueOf(fields.get(fieldCount)
                            .getText());
                    f[i].setBoolean(o2, j);
                } else if (f[i].getType().getName().equals("java.lang.String")) {
                    String j = inFix(fields.get(fieldCount).getText());
                    f[i].set(o2, (Object) j);
                } else {
                    fieldCount--;
                }
                fieldCount++;
            } catch (Exception x) {
                JOptionPane.showMessageDialog(null, "Failed");
                return;
            }
        }
        dispose();
    }

    public static boolean Extends(Object o, String name) {
        Class c;
        if (o instanceof Class)
            c = (Class) o;
        else
            c = o.getClass();
        while (true) {
            if (c == null)
                break;
            else if (c.getName().equals(name)) {
                return true;
            }
            c = c.getSuperclass();
        }
        return false;
    }

    public static String outFix(String s) {
        String o = "";
        if (s == null)
            return "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\n')
                o += "\\n";
            else
                o += c;
        }
        return o;
    }

    public static String inFix(String s) {
        String o = "";
        if (s == null)
            return "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                i++;
                c = s.charAt(i);
                if (c == 'n') {
                    o += '\n';
                }
            } else
                o += c;
        }
        return o;
    }

    public static class SuperCommandLine extends JFrame {

        JTextField tf;
        JTextArea ta;

        public SuperCommandLine() {
            super("Super Shell");

            Container c = getContentPane();

            setLayout(null);

            tf = new JTextField();
            ta = new JTextArea(40, 40);

            ta.setLineWrap(true);

            JScrollPane sc = new JScrollPane(ta);

            tf.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    ta.append(tf.getText() + '\n');
                    tf.setText("");
                }
            });

            tf.setBounds(2, 2, 596, 20);
            sc.setBounds(2, 22, 588, 345);

            c.add(tf);
            c.add(sc);

            setSize(600, 400);
            setVisible(true);

        }

        /*
         * public static void execute(String command){ Stack<String> ss =
         * orderCommands(command); Object o = execute1(ss, null); }
         * 
         * public static Object execute1(Stack<String> s, Object o){ String s1 =
         * s.pop(); if(s1.equals("V")){ String s2 = s.pop(); String s3 =
         * s.pop(); Object o3 = getVar(s2, o); if(s3.equals("D")){ return
         * execute1(s, o3); } else { s.push(s3); return o3; } } else
         * if(s1.equals("E")){ return execute2(s, o); // Exectue this method }
         * return null; }
         * 
         * public static Object execute2(Stack<String> s, Object o){
         * ArrayList<Object> os = new ArrayList<Object>(); String s1 = s.pop();
         * while(true){ if(s.equals("V")){ s.push("V"); o.add(execute1(s,
         * null)); // Get Object From the Variable } else if(s.equals("E")){
         * o.add(execute2(s, o)); // Get Object the calling Method } else
         * if("C"){ } else if("M"){ String s2 = s.pop(); // The method Name //
         * execute with os args and return } } }
         */
        public static Stack orderCommands(String command) {
            String c = removews(command);
            Stack<String> ss = new Stack<String>();

            recurSplit(ss, c);

            return ss;
        }

        public static void recurSplit(Stack s, String c) {
            if (c.length() == 0)
                return;
            ArrayList<String> s1 = split1(c, ',');
            if (s1.size() != 1) {
                for (int i = s1.size() - 1; i >= 0; i--) {
                    if (i != s1.size() - 1)
                        s.add("C");
                    // if(i != s1.size()-1)System.out.println("C");
                    recurSplit(s, s1.get(i));
                }
            } else {
                ArrayList<String> s2 = split1(c, '.');
                if (s2.size() == 1) {
                    String s3 = s2.get(0);
                    if (s3.indexOf("(") != -1) {
                        // System.out.println(s3.substring(0, s3.indexOf("(")));
                        s.add(s3.substring(0, s3.indexOf("(")));
                        // System.out.println("M");
                        s.add("M");
                        // System.out.println("In:"+s3.substring(s3.indexOf("(")+1,
                        // s3.length()-1));
                        recurSplit(s, s3.substring(s3.indexOf("(") + 1,
                                s3.length() - 1));
                        // s.add("E");
                    } else {
                        // System.out.println(s3);
                        s.add(s3);
                        // System.out.println("V");
                        s.add("V");
                    }
                    return;
                } else {
                    for (int i = s2.size() - 1; i >= 0; i--) {
                        if (i != s2.size() - 1)
                            s.add("D");
                        // if(i != s2.size()-1)System.out.println("D");
                        recurSplit(s, s2.get(i));
                    }
                }
            }
        }

        public static ArrayList<String> split1(String s, char split) {
            ArrayList<String> ss = new ArrayList<String>();
            int pCount = 0;
            int lastBreak = 0;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '(') {
                    pCount++;
                } else if (c == ')') {
                    pCount--;
                } else if (c == split) {
                    if (pCount == 0) {
                        ss.add(s.substring(lastBreak, i));
                        lastBreak = i + 1;
                    }
                }
            }
            ss.add(s.substring(lastBreak, s.length()));
            return ss;
        }

        public static String removews(String s) {
            String s2 = "";
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) != ' ')
                    s2 += s.charAt(i);
            }
            return s2;
        }

    }

    public static void main(String args[]) {
        Stack<String> ss = SuperCommandLine
                .orderCommands("test1(this1.test2(test3,bye().hey()),this2.test4).test5()");
        while (!ss.empty())
            System.out.println(ss.pop());
    }

}
