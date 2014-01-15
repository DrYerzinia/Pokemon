package com.dryerzinia.pokemon.obj;

import java.io.*;
import java.awt.*;
import java.util.*;

import javax.swing.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.msg.server.fight.FMTSSelectedAttack;
import com.dryerzinia.pokemon.ui.Fight;
import com.dryerzinia.pokemon.ui.editor.Sub;
import com.dryerzinia.pokemon.ui.editor.SubListener;
import com.dryerzinia.pokemon.util.DeepCopy;
import com.dryerzinia.pokemon.util.JSON;
import com.dryerzinia.pokemon.util.JSONArray;
import com.dryerzinia.pokemon.util.JSONObject;

import java.awt.event.*;

public class Pokemon implements Serializable, DeepCopy, JSON {

    static final long serialVersionUID = 6487354042212056227L;

    public transient Image sprites[];

    public String name;
    public String nickName;

    public String Species;

    public int level;
    public int currentHP;
    public int hpSE;
    public int attackSE;
    public int defenseSE;
    public int speedSE;
    public int specialSE;

    public int idNo;
    public int EXP;

    public String status;
    public String ot;

    public Move moves[] = null;

    public transient BaseStats pokeBase;

    public transient boolean added = false;
    public transient boolean used = false;
    public transient boolean gainedLevel = false;

    public int location = 6;

    public int remoteSetDamage = -1;

    public Pokemon() {
    }

    public Pokemon(String name, String nickName, String description, int level,
            int totalHP, int currentHP, int attack, int defense, int speed,
            int special, int idNo, int no, int EXP, String status, String type,
            String type2, String ot, Move moves[]) {

        this.name = name;
        this.nickName = nickName;

        this.level = level;
        this.currentHP = currentHP;
        this.attackSE = attack;
        this.defenseSE = defense;
        this.speedSE = speed;
        this.specialSE = special;
        this.idNo = idNo;

        this.status = status;
        this.ot = ot;

        loadImg();

        this.moves = moves;

    }

    public Pokemon(String name, String nickName, String description, int level,
            int totalHP, int currentHP, int attack, int defense, int speed,
            int special, int idNo, int no, int EXP, String status, String type,
            String type2, String ot, Move moves[], BaseStats bs) {

        this.name = name;
        this.nickName = nickName;

        this.level = level;
        this.currentHP = currentHP;
        this.attackSE = attack;
        this.defenseSE = defense;
        this.speedSE = speed;
        this.specialSE = special;
        this.idNo = idNo;

        this.status = status;
        this.ot = ot;

        this.pokeBase = bs;

        loadImg();

        this.moves = moves;

    }

    public int numberOfMoves() {
        int i = 0;
        while (i != 4 && moves[i] != null)
            i++;
        return i--;
    }

    public Move getMove(int m) {
        if (m == Fight.FM_POKEMON_SWITCHED)
            return Fight.SwitchPokemon;
        return moves[m];
    }

    public int getMove(Move m) {
        if (m.getName().equals(Fight.SwitchPokemon.getName())) {
            return FMTSSelectedAttack.CHANGE_POKEMON_MOVE;
        }
        for (int i = 0; i < 4; i++)
            if (moves[i] != null && moves[i].name.equals(m.name))
                return i;
        return -1;
    }

    public int getLocation() {
        return location;
    }

    public int getLevel() {
        return level;
    }

    public int getAttack() {
        int att = calcStat(10, 0);
        if (status != null && status.indexOf("brn") != -1)
            att /= 2;
        return att;
    }

    public int getSpeed() {
        return calcStat(5, 1);
    }

    public int getDefense() {
        return calcStat(5, 2);
    }

    public int getSpecial() {
        return calcStat(5, 3);
    }

    public int getTotalHP() {
        return calcStat(10, 4);
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getStatPoints(int n) {
        int statExp = 0;
        switch (n) {
        case 0:
            statExp = attackSE;
            break;
        case 1:
            statExp = speedSE;
            break;
        case 2:
            statExp = defenseSE;
            break;
        case 3:
            statExp = specialSE;
            break;
        case 4:
            statExp = hpSE;
            break;
        }
        return (int) ((Math.sqrt(((double) statExp) - 1.0) + 1.0) / 4.0);
    }

    public int calcStat(int additive, int n) {
        // change the zero do a random dv
        return (int) ((((pokeBase.attack + 0) * 2 + getStatPoints(n)) * level / 100) + additive);
    }

    public void loadImg() {

        sprites = new Image[11];

        sprites[0] = PokemonGame.images.getSprite(name + "U.png");
        sprites[1] = PokemonGame.images.getSprite(name + "D.png");
        sprites[2] = PokemonGame.images.getSprite(name + "L.png");
        sprites[3] = PokemonGame.images.getSprite(name + "R.png");
        sprites[4] = PokemonGame.images.getSprite(name + "MU.png");
        sprites[5] = PokemonGame.images.getSprite(name + "MD.png");
        sprites[6] = PokemonGame.images.getSprite(name + "ML.png");
        sprites[7] = PokemonGame.images.getSprite(name + "MR.png");
        sprites[8] = PokemonGame.images.getSprite(name + "P.png");
        sprites[9] = PokemonGame.images.getSprite(name + "B.png");
        sprites[10] = PokemonGame.images.getSprite(name + "F.png");
        try {
            sprites[1] = PokemonGame.images.getSprite(pokeBase.smallImageName
                    + "D.png");
        } catch (Exception x) {
        }

    }

    public Pokemon(Pokemon p) {

        set(p);

    }

    public void getBase(ArrayList<Pokemon> poke, ArrayList<Move> moves) {
        Iterator<Pokemon> p = poke.iterator();
        while (p.hasNext()) {
            Pokemon po = p.next();
            if (po.Species.equals(name) || po.Species.equals(Species)) {
                pokeBase = po.pokeBase;
                try {
                    sprites[1] = PokemonGame.images
                            .getSprite(pokeBase.smallImageName + "D.png");
                } catch (Exception x) {
                }
                break;
            }
        }
        if (this.moves == null)
            return;
        for (int i = 0; i < 4; i++) {
            if (this.moves[i] == null)
                break;
            this.moves[i].setBase(moves);
        }
    }

    public void set(Pokemon p) {

        this.name = p.name;
        this.nickName = p.nickName;

        this.level = p.level;
        this.hpSE = p.hpSE;
        this.currentHP = p.currentHP;
        this.attackSE = p.attackSE;
        this.defenseSE = p.defenseSE;
        this.speedSE = p.speedSE;
        this.specialSE = p.specialSE;
        this.idNo = p.idNo;
        this.EXP = p.EXP;

        this.Species = p.Species;

        this.status = p.status;
        this.ot = p.ot;

        this.location = p.location;

        if (p.sprites != null)
            sprites = p.sprites;

        this.moves = p.moves;

        this.pokeBase = p.pokeBase;

    }

    public boolean addEXP(Pokemon enemy, int n) {
        int winxp = enemy.getWinExp(false);
        EXP += winxp / n;
        speedSE += enemy.pokeBase.speed / n;
        attackSE += enemy.pokeBase.attack / n;
        defenseSE += enemy.pokeBase.defense / n;
        specialSE += enemy.pokeBase.special / n;
        if (EXP > nextLevelExp()) {
            level++;

            currentHP = getTotalHP();

            gainedLevel = true;
            return true;
        }
        return false;
    }

    public int expToNextLevel() {
        return nextLevelExp() - EXP;
    }

    public int nextLevelExp() {
        switch (pokeBase.growthRate) {
        case 0:
            return (int) (Math.pow(level + 1, 3) * 0.8);
        case 1:
            return (int) (Math.pow(level + 1, 3));
        case 2:
            return (int) (Math.pow(level + 1, 3) * 1.25);
        case 3:
            return (int) (Math.pow(level + 1, 3) * 1.25);
            // return
            // (int)((1.2*Math.pow(level+1,3))-(15*Math.pow(level+1,2))-(100*level+1)-140);
        }
        return -1;
    }

    public int getDamage(Move attack, int defense, boolean crit) {
        if (remoteSetDamage != -1) {
            System.out.println("Remote Damage Set: " + remoteSetDamage);
            return remoteSetDamage;
        }
        int damage = attack.dmg;
        if (damage == 0)
            return 0;
        int rand = new Random().nextInt(38) + 217;
        System.out.println("Attack: " + getAttack() + "\nDamage: " + damage
                + "\nDefense: " + defense + "\nLevel: " + level + "\nRandom: "
                + rand);
        double ret = (int) (((2.0 * ((double) level) / 5.0 + 2.0)
                * ((double) getAttack()) * ((double) damage) / ((double) defense)) / 50.0);
        System.out.println("ret" + ret);
        if (ret < 768)
            ret = ret * ((double) rand) / 255.0;
        System.out.println("ret" + ret);
        if (ret > 997)
            ret = 997;
        ret += 2;
        if ((pokeBase.type != null && attack.type.equals(pokeBase.type))
                || (pokeBase.type2 != null && attack.type
                        .equals(pokeBase.type2))) {
            ret *= 1.5;
            System.out.println("workd");
        }
        System.out.println("Dmg:" + ret);
        return (int) ret;
    }

    public int getWinExp(boolean wild) {
        double battletype = 1;
        if (wild)
            battletype = 1.5;
        System.out.println(Species + pokeBase.baseExp);
        return (int) (((((double) pokeBase.baseExp) * ((double) level)) * battletype) / 7.0d);
    }

    public String getName() {
        if (nickName != null)
            return nickName;
        return name;
    }

    public String toString() {
        return getName();
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {

        ois.defaultReadObject();
        getBase(PokemonGame.pokeg.basePokemon, PokemonGame.pokeg.baseMoves);
        loadImg();

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    public Object deepCopy() {
        Move moves2[] = new Move[moves.length];
        for (int i = 0; i < moves.length; i++) {
            if (moves[i] != null) {
                moves2[i] = (Move) moves[i].deepCopy();
            } else {
                moves2[i] = null;
            }
        }
        return new Pokemon(new String(name + ""), new String(nickName + ""),
                "", level, 0, currentHP, attackSE, defenseSE, speedSE,
                specialSE, idNo, 0, EXP, new String(status + ""), "", "",
                new String(ot + ""), moves2);
    }

    public String getType() {
        return pokeBase.type;
    }

    public String getType2() {
        return pokeBase.type2;
    }

    public int getRareness() {
        return pokeBase.rareness;
    }

    public int evolvesAt() {
        return pokeBase.evolvesAt;
    }

    public Pokemon evolvesTo() {
        return pokeBase.evolvesTo;
    }

    public String getSpecies() {
        return Species;
    }

    public static class BaseStats {

        public int height;
        public int weight;
        public int no;

        public String type;
        public String type2;
        public String description;

        public String smallImageName;

        public int attack;
        public int defense;
        public int speed;
        public int special;
        public int hp;

        public int baseExp;
        public int growthRate;

        public int rareness;

        public int evolvesAt;
        public Pokemon evolvesTo;

        ArrayList<Move> leanedMoves;

        public BaseStats() {

        }

        public String toString() {
            return "att:" + attack + ",def:" + defense + ",spd:" + speed
                    + ",spe:" + special;
        }

    }

    public static class Edit extends JFrame {

        ArrayList<SubListener> subListeners = new ArrayList<SubListener>();

        Container c, moveC;

        JPopupMenu rc;

        int lastClick;

        JTextField SpeciesTF, levelTF, hpTF, attackTF, defenseTF, speedTF,
                specialTF, EXPTF;
        JLabel SpeciesLB, levelLB, hpLB, attackLB, defenseLB, speedLB,
                specialLB, EXPLB;
        JButton addMoveB, saveB;

        boolean editing = false;

        String Species = "", level = "", hp = "", attack = "", defense = "",
                speed = "", special = "", EXP = "";

        Sub sub;

        public Edit(Sub s) {
            super("Add Pokemon");
            this.sub = s;

            rc = new JPopupMenu();
            JMenuItem edt = rc.add("Edit");
            edt.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    new Move.Edit(new Sub(((Pokemon) sub.s).moves[lastClick]));
                }
            });

            c = getContentPane();
            setLayout(new FlowLayout(FlowLayout.LEFT));

            moveC = new JPanel();
            moveC.setLayout(new FlowLayout());

            c.add(moveC);
            c.add(Box.createHorizontalStrut(20000));

            if (sub.s != null) {
                Pokemon p = (Pokemon) sub.s;
                for (int i = 0; i < 4; i++) {
                    if (p.moves == null || p.moves[i] == null)
                        break;
                    final int ii = i;
                    final JLabel mLB = new JLabel("" + p.moves[i].name);
                    mLB.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
                                popup(mLB, ii, e.getX(), e.getY());
                            }
                        }
                    });
                    // moveLB.add(pLB);
                    moveC.add(mLB);
                }
            }

            if (sub.s != null && sub.s instanceof Pokemon) {
                editing = true;
                Pokemon p = (Pokemon) sub.s;
                Species += p.Species;
                level += p.level;
                hp += p.hpSE;
                attack += p.attackSE;
                defense += p.defenseSE;
                speed += p.speedSE;
                special += p.specialSE;
                EXP += p.EXP;

            }

            SpeciesLB = new JLabel("Species: ");
            SpeciesTF = new JTextField(Species, 15);
            levelLB = new JLabel("Level: ");
            levelTF = new JTextField(level, 15);
            hpLB = new JLabel("HP: ");
            hpTF = new JTextField(hp, 15);
            attackLB = new JLabel("Attack: ");
            attackTF = new JTextField(attack, 15);
            defenseLB = new JLabel("Defense: ");
            defenseTF = new JTextField(defense, 15);
            speedLB = new JLabel("Speed: ");
            speedTF = new JTextField(speed, 15);
            specialLB = new JLabel("Special: ");
            specialTF = new JTextField(special, 15);
            EXPLB = new JLabel("EXP: ");
            EXPTF = new JTextField(EXP, 15);

            c.add(SpeciesLB);
            c.add(SpeciesTF);
            c.add(Box.createHorizontalStrut(20000));
            c.add(levelLB);
            c.add(levelTF);
            c.add(Box.createHorizontalStrut(20000));
            c.add(hpLB);
            c.add(hpTF);
            c.add(Box.createHorizontalStrut(20000));
            c.add(attackLB);
            c.add(attackTF);
            c.add(Box.createHorizontalStrut(20000));
            c.add(defenseLB);
            c.add(defenseTF);
            c.add(Box.createHorizontalStrut(20000));
            c.add(speedLB);
            c.add(speedTF);
            c.add(Box.createHorizontalStrut(20000));
            c.add(specialLB);
            c.add(specialTF);
            c.add(Box.createHorizontalStrut(20000));
            c.add(EXPLB);
            c.add(EXPTF);

            c.add(Box.createHorizontalStrut(20000));

            addMoveB = new JButton("Add Move");
            addMoveB.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    new Move.Edit(new Sub(null))
                            .addSubListener(new SubListener() {
                                public void SubEvent(Sub s) {
                                    addSub((Move) s.s);
                                }
                            });
                }
            });
            c.add(addMoveB);

            saveB = new JButton("Save");
            saveB.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    save();
                }
            });
            c.add(saveB);

            setSize(360, 350);
            setVisible(true);

        }

        public void popup(JLabel l, int i, int x, int y) {
            lastClick = i;
            rc.show(l, x, y);
        }

        public void addSubListener(SubListener subListener) {
            subListeners.add(subListener);
        }

        public void sendSubEvent() {
            Iterator<SubListener> subit = subListeners.iterator();
            while (subit.hasNext()) {
                subit.next().SubEvent(sub);
            }
        }

        public void addSub(Move m) {
            Pokemon p = (Pokemon) sub.s;
            for (int i = 0; i < 4; i++) {
                if (p.moves[i] == null) {
                    p.moves[i] = m;
                    break;
                }
            }
        }

        public void save() {
            try {
                Pokemon p;
                if (sub.s == null || !(sub.s instanceof Pokemon))
                    p = new Pokemon();
                else
                    p = (Pokemon) sub.s;
                p.Species = SpeciesTF.getText();
                p.getBase(PokemonGame.pokeg.basePokemon,
                        PokemonGame.pokeg.baseMoves);
                if (p.moves == null)
                    p.moves = new Move[4];
                p.name = p.Species;
                p.nickName = p.Species;
                p.level = Integer.parseInt(levelTF.getText());
                p.hpSE = Integer.parseInt(hpTF.getText());
                p.currentHP = p.getTotalHP();
                p.attackSE = Integer.parseInt(attackTF.getText());
                p.defenseSE = Integer.parseInt(defenseTF.getText());
                p.speedSE = Integer.parseInt(speedTF.getText());
                p.specialSE = Integer.parseInt(specialTF.getText());
                p.EXP = Integer.parseInt(EXPTF.getText());
                sub.s = p;
                if (editing)
                    ((Pokemon) sub.s).set(p);
                else
                    sendSubEvent();
            } catch (Exception x) {
                x.printStackTrace();
            }
            dispose();
        }

    }

	@Override
	public String toJSON() {

    	String json = "{'class':'" + this.getClass().getName() + "'";

    	json += ",'name':" + JSONObject.stringToJSON(name);
    	json += ",'nickName':" + JSONObject.stringToJSON(nickName);

    	json += ",'Species':" + JSONObject.stringToJSON(Species);

    	json += ",'level':" + level;
    	json += ",'currentHP':" + currentHP;
    	json += ",'hpSE':" + hpSE;
    	json += ",'attackSE':" + attackSE;
    	json += ",'defenseSE':" + defenseSE;
    	json += ",'speedSE':" + speedSE;
    	json += ",'specialSE':" + specialSE;

    	json += ",'idNo':" + idNo;
    	json += ",'EXP':" + EXP;

    	json += ",'status':" + JSONObject.stringToJSON(status);
    	json += ",'ot':" + JSONObject.stringToJSON(ot);

    	json += ",'moves':" + JSONArray.objectArrayToJSON(moves);

	    json += ",'location':" + location;

	    json += ",'remoteSetDamage':" + remoteSetDamage;

        json += "}";

        return json;
	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		getBase(PokemonGame.pokeg.basePokemon, PokemonGame.pokeg.baseMoves);
        loadImg();

	}

}
