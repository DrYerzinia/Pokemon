import java.io.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.util.*;

public class RandomFight implements Serializable, DeepCopy, JSON {

    static final long serialVersionUID = -7322792374197388454L;

    Pokemon pokemon[];
    int chance[];

    int total;

    public RandomFight() {
    }

    public RandomFight(int chance[], Pokemon pokemon[]) {
        this.chance = chance;
        this.pokemon = pokemon;

        total = 0;

        for (int i = 0; i < chance.length; i++)
            total += chance[i];

    }

    public String toString() {
        String s = "";
        for (int i = 0; i < pokemon.length; i++) {
            s += "Pokemon:" + pokemon[i].name + " Chance:" + chance[i] + ",";
        }
        return s;
    }

    public Pokemon getAttack() {
        if (Math.random() * 10 > 9) {
            int ran = (int) (Math.random() * total);
            int curr = 0;
            System.out.println("Tot:" + total);
            System.out.println("Rand:" + ran);
            for (int i = 0; i < chance.length; i++) {
                curr += chance[i];
                System.out.println("Curr:" + curr);
                if (curr > ran) {
                    ;
                    // System.out.println("AHH2:"+pokemon[i].baseExp);
                    return pokemon[i];
                }
            }
            return pokemon[pokemon.length - 1];
        }
        return null;
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    @Override
    public String toJSON(){

    	String json = "{'class':'RandomFight'";
    	
        json += ",'pokemon':" + JSONArray.objectArrayToJSON(pokemon);

        json += ",'chance':" + JSONArray.intArrayToJSON(chance);

        json += ",'total':" + total;

        json += "}";

        return json;

    }

    @Override
    public void fromJSON(HashMap<String, Object> json){
    	// TODO Auto-generated method stub
    }

    public Object deepCopy() {
        Pokemon poke2[] = new Pokemon[pokemon.length];
        int chance2[] = new int[chance.length];
        for (int i = 0; i < pokemon.length; i++) {
            poke2[i] = (Pokemon) pokemon[i].deepCopy();
            chance2[i] = chance[i];
        }
        return new RandomFight(chance2, poke2);
    }

    public void addPokemon(Pokemon p, int c) {
        Pokemon poke[] = new Pokemon[pokemon.length + 1];
        int chan[] = new int[pokemon.length + 1];
        for (int j = 0; j < pokemon.length; j++) {
            poke[j] = pokemon[j];
            chan[j] = chance[j];
        }
        poke[pokemon.length] = p;
        chan[pokemon.length] = c;
        pokemon = poke;
        chance = chan;
        total = 0;
        for (int i = 0; i < chance.length; i++)
            total += chance[i];
    }

    public void remove(int n) {
        Pokemon poke[] = new Pokemon[pokemon.length - 1];
        int chan[] = new int[pokemon.length - 1];
        int k = 0;
        System.out.println("Removing");
        for (int j = 0; j < pokemon.length; j++) {
            if (j != n) {
                poke[k] = pokemon[j];
                chan[k] = chance[j];
                k++;
            }
        }
        pokemon = poke;
        chance = chan;
    }

    public static class Edit extends JFrame {

        private static final int W = 320;
        private static final int H = 280;

        Container c, pokemonC;

        JPopupMenu rc;

        int lastClick;

        JButton addPokemon;

        ArrayList<JLabel> pokemonLB = new ArrayList<JLabel>();
        ArrayList<JTextField> pokemonChanceTF = new ArrayList<JTextField>();

        Sub sub;

        public Edit(Sub subi) {
            super("Random Fight Edit");

            sub = subi;

            rc = new JPopupMenu();
            JMenuItem edt = rc.add("Edit");
            edt.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    new Pokemon.Edit(new Sub(
                            ((RandomFight) sub.s).pokemon[lastClick]));
                }
            });
            JMenuItem rem = rc.add("Remove");
            rem.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    ((RandomFight) sub.s).remove(lastClick);
                }
            });

            c = getContentPane();
            setLayout(new FlowLayout());
            pokemonC = new JPanel();
            // pokemonC.setLayout(new FlowLayout());
            // pokemonC.setMaximumSize(new Dimension(W,60));

            // c.add(pokemonC);

            if (sub.s != null) {
                RandomFight rf = (RandomFight) sub.s;
                for (int i = 0; i < rf.pokemon.length; i++) {
                    final int ii = i;
                    final JLabel pLB = new JLabel("N:" + rf.pokemon[i].name
                            + ",L:" + rf.pokemon[i].level);
                    pLB.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
                                popup(pLB, ii, e.getX(), e.getY());
                            }
                        }
                    });
                    pokemonLB.add(pLB);
                    c.add(pLB);
                    JTextField pTF = new JTextField("" + rf.chance[i]);
                    pokemonChanceTF.add(pTF);
                    c.add(pTF);
                }
            }

            addPokemon = new JButton("Add Pokemon");
            addPokemon.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    new Pokemon.Edit(new Sub(null))
                            .addSubListener(new SubListener() {
                                public void SubEvent(Sub s) {
                                    addSub((Pokemon) s.s);
                                }
                            });
                }
            });
            c.add(addPokemon);

            setSize(W, H);
            setVisible(true);

        }

        public void popup(JLabel l, int i, int x, int y) {
            lastClick = i;
            rc.show(l, x, y);
        }

        public void addSub(Pokemon p) {
            System.out.println("Sub Added");
            RandomFight rf = null;
            System.out.println("rar");
            if (sub.s == null) {
                System.out.println("subs");
                rf = new RandomFight();
            } else {
                System.out.println("realy");
                try {
                    rf = (RandomFight) sub.s;
                } catch (Exception x) {
                    x.printStackTrace();
                }
                System.out.println("not null");
            }
            System.out.println("1");
            rf.addPokemon(p, 10);
            System.out.println("1");
            sub.s = rf;
            System.out.println("SubAdded...");
            final JLabel pLB = new JLabel("N:" + p.name + ",L:" + p.level);
            pLB.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
                        popup(pLB, pokemonLB.size() - 1, e.getX(), e.getY());
                    }
                }
            });
            pokemonLB.add(pLB);
            pokemonC.add(pLB);
            JTextField pTF = new JTextField("" + 10);
            pokemonChanceTF.add(pTF);
            pokemonC.add(pTF);
            setSize(new Dimension(0, 0));
            setSize(new Dimension(W, H));
            System.out.println("SubAdded");
        }

    }
}
