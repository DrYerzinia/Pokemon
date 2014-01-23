package com.dryerzinia.pokemon.obj;
import java.io.*;
import java.awt.*;
import java.util.*;

import javax.swing.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.ui.editor.Sub;
import com.dryerzinia.pokemon.ui.editor.SubListener;
import com.dryerzinia.pokemon.util.DeepCopy;
import com.dryerzinia.pokemon.util.JSON;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;

import java.awt.event.*;

public class Move implements Serializable, DeepCopy, JSON {

    static final long serialVersionUID = -6011340993523914499L;

    /*
     * Master list of standard moves for when pokemon learn them
     */
    public static HashMap<String, Move> baseMoves;
    
    public transient Image animation[]; // TODO: add animations sprites and code

    public int effmod;

    public String name;
    public String description;

    public String effect;
    public String type;

    public int currentpp;
    public int pp;
    public int dmg;
    public int accuracy;

    public Move() {
    }

    public Move(String name, String description, String effect, String type,
            int anilen, int pp, int dmg, int accuracy) {

        this.name = name;
        this.description = description;

        this.effect = effect;
        this.type = type;

        animation = new Image[effmod];

        for (int i = 0; i < effmod; i++)
            animation[i] = ResourceLoader.getSprite(name + String.format("%02i", i) + ".png");

        this.currentpp = pp;
        this.pp = pp;
        this.dmg = dmg;
        this.accuracy = accuracy;

    }

    public Move(Move m) {
        set(m);
    }

    public static void init_base_moves(){

    	baseMoves = new HashMap<String, Move>();

    	// * - An accuracy check is performed for this attack. Thus,
        // Brightpowder, if held by the opponent, can decrease this attack's
        // accuracy by 7.8%.
        // ^ - If King's Rock is attached to the user, there is an 11.7% chance
        // that the opponent will flinch if the opponent lost HP due to, and the
        // user struck first with, this attack.
        // 00: *^ Deal Damage

        Move m = new Move("Pound", "", "", "Normal", 0, 35, 40, 255);
        baseMoves.put("Pound", m);
        m = new Move("Mega Punch", "", "", "Normal", 0, 20, 80, 216);
        baseMoves.put("Mega Punch", m );
        m = new Move("Scratch", "", "", "Normal", 0, 35, 40, 255);
        baseMoves.put("Scratch", m);
        m = new Move("Vicegrip", "", "", "Normal", 0, 30, 55, 255);
        baseMoves.put("Vicegrip", m);
        m = new Move("Cut", "", "", "Normal", 0, 30, 50, 242);
        baseMoves.put("Cut", m);
        m = new Move("Wing Attack", "", "", "Flying", 0, 35, 60, 255);
        baseMoves.put("Wing Attack", m);
        m = new Move("Slam", "", "", "Normal", 0, 20, 80, 191);
        baseMoves.put("Slam", m);
        m = new Move("Vine Whip", "", "", "Grass", 0, 20, 80, 191);
        baseMoves.put("Vine Whip", m);
        m = new Move("Mega Kick", "", "", "Normal", 0, 5, 120, 191);
        baseMoves.put("Mega Kick", m);
        m = new Move("Horn Attack", "", "", "Normal", 0, 25, 65, 255);
        baseMoves.put("Horn Attack", m);
        m = new Move("Tackle", "", "", "Normal", 0, 35, 35, 242);
        baseMoves.put("Tackle", m);
        m = new Move("Water Gun", "", "", "Water", 0, 25, 40, 255);
        baseMoves.put("Water Gun", m);
        m = new Move("Hydro Pump", "", "", "Water", 0, 5, 120, 204);
        baseMoves.put("Hydro Pump", m);
        m = new Move("Surf", "", "", "Water", 0, 15, 95, 255);
        baseMoves.put("Surf", m);
        m = new Move("Peck", "", "", "Flying", 0, 35, 35, 255);
        baseMoves.put("Peck", m);
        m = new Move("Drill Peck", "", "", "Flying", 0, 20, 80, 255);
        baseMoves.put("Drill Peck", m);
        m = new Move("Strength", "", "", "Normal", 0, 15, 80, 255);
        baseMoves.put("Strength", m);
        m = new Move("Rock Throw", "", "", "Rock", 0, 15, 50, 229);
        baseMoves.put("Rock Throw", m);
        m = new Move("Egg Bomb", "", "", "Normal", 0, 10, 100, 191);
        baseMoves.put("Egg Bomb", m);
        m = new Move("Waterfall", "", "", "Water", 0, 15, 80, 255);
        baseMoves.put("Waterfall", m);
        m = new Move("Megahorn", "", "", "Bug", 0, 10, 120, 216);
        baseMoves.put("Megahorn",m);

        // Good Chance for critical hit

        m = new Move("Karate Chop", "", "", "Fighting", 0, 25, 50, 255);
        baseMoves.put("Karate Chop", m);
        m = new Move("Razor Leaf", "", "", "Grass", 0, 25, 55, 242);
        baseMoves.put("Razor Leaf", m);
        m = new Move("Crabhammer", "", "", "Water", 0, 10, 90, 216);
        baseMoves.put("Crabhammer", m);
        m = new Move("Slash", "", "", "Normal", 0, 20, 70, 255);
        baseMoves.put("Slash", m);
        m = new Move("Aeroblast", "", "", "Flying", 0, 5, 100, 242);
        baseMoves.put("Aeroblast", m);
        m = new Move("Cross Chop", "", "", "Fighting", 0, 5, 100, 204);
        baseMoves.put("Cross Chop", m);

        // 01: * Puts Opponent to Sleep

        m = new Move("Sing", "", "", "Normal", 0, 15, 0, 140);
        baseMoves.put("Sing", m);
        m = new Move("Sleep Powder", "", "", "Grass", 0, 15, 0, 191);
        baseMoves.put("Sleep Powder", m);
        m = new Move("Hypnosis", "", "", "Phychic", 0, 20, 0, 153);
        baseMoves.put("Hypnosis", m);
        m = new Move("Lovely Kiss", "", "", "Normal", 0, 10, 0, 191);
        baseMoves.put("Lovely Kiss", m);
        m = new Move("Spore", "", "", "Grass", 0, 15, 0, 255);
        baseMoves.put("Spore", m);

        // 02: * May Poison Opponent

        m = new Move("Poison Sting", "", "", "Poison", 76, 15, 35, 255);
        baseMoves.put("Poison Sting", m);
        m = new Move("Smog", "", "", "Poison", 102, 20, 20, 178);
        baseMoves.put("Smog", m);
        m = new Move("Sludge", "", "", "Poison", 76, 20, 65, 255);
        baseMoves.put("Sludge", m);
        m = new Move("Sludge Bomb", "", "", "Poison", 76, 10, 90, 255);
        baseMoves.put("Sludge Bomb", m);

        // 03: *^ Recovers to user half of HP lost by opponent due to this
        // attack

        m = new Move("Absorb", "", "", "Grass", 0, 20, 20, 255);
        baseMoves.put("Absorb", m);
        m = new Move("Mega Drain", "", "", "Grass", 0, 10, 40, 255);
        baseMoves.put("Mega Drain", m);
        m = new Move("Leech Life", "", "", "Bug", 0, 15, 20, 255);
        baseMoves.put("Leech Life", m);
        m = new Move("Giga Drain", "", "", "Grass", 5, 10, 60, 255);
        baseMoves.put("Giga Drain", m);

        // 04: * May burn opponent

        m = new Move("Fire Punch", "", "", "Fire", 25, 15, 75, 255);
        baseMoves.put("Fire Punch", m);
        m = new Move("Ember", "", "", "Fire", 25, 25, 40, 255);
        baseMoves.put("Ember", m);
        m = new Move("Flamethrower", "", "", "Fire", 25, 15, 95, 255);
        baseMoves.put("Flamethrower", m);
        m = new Move("Fire Blast", "", "", "Fire", 25, 5, 120, 216);
        baseMoves.put("Fire Blast", m);

        // 05: * May freeze opponent

        m = new Move("Ice Punch", "", "", "Ice", 25, 15, 75, 255);
        baseMoves.put("Ice Punch", m);
        m = new Move("Ice Beam", "", "", "Ice", 25, 10, 95, 255);
        baseMoves.put("Ice Beam", m);
        m = new Move("Blizzard", "", "", "Ice", 25, 5, 120, 178);
        baseMoves.put("Blizzard", m);
        m = new Move("Powder Snow", "", "", "Ice", 25, 25, 40, 255);
        baseMoves.put("Powder Snow", m);

        // 06: * May paralyze opponent

        m = new Move("Thunderpunch", "", "", "Eletric", 25, 15, 75, 255);
        baseMoves.put("Thunderpunch", m);
        m = new Move("Body Slam", "", "", "Normal", 76, 15, 85, 255);
        baseMoves.put("Body Slam", m);
        m = new Move("Thunder Shock", "", "", "Eletric", 25, 30, 40, 255);
        baseMoves.put("Thunder Shock", m);
        m = new Move("Thunderbolt", "", "", "Eletric", 25, 15, 95, 255);
        baseMoves.put("Thunderbolt", m);
        m = new Move("Lick", "", "", "Ghost", 76, 20, 30, 255);
        baseMoves.put("Lick", m);
        m = new Move("Zap Cannon", "", "", "Eletric", 255, 5, 100, 127);
        baseMoves.put("Zap Cannon", m);
        m = new Move("Spark", "", "", "Eletric", 76, 20, 65, 255);
        baseMoves.put("Spark", m);
        m = new Move("Dragonbreath", "", "", "Dragon", 76, 20, 60, 255);
        baseMoves.put("Dragonbreath", m);

        // 07: * User faints as part of this attack's use. Opponent's Defense is
        // temporarily halved in damage calculation. Fainting will not fail,
        // though attack may miss or fail for other reasons. If opponent also
        // faints, opponent's fainting message is shown first.

        m = new Move("Selfdestruct", "", "", "Normal", 0, 5, 200, 255);
        baseMoves.put("Selfdestruct", m);
        m = new Move("Explosion", "", "", "Normal", 0, 5, 250, 255);
        baseMoves.put("Explosion", m);

        // 08: * Only effective while opponent is asleep. Recovers to user half
        // of HP lost by opponent due to this attack

        m = new Move("Dream Eater", "", "", "Psychic", 0, 15, 100, 255);
        baseMoves.put("Dream Eater", m);

        // 09: Uses last move opponent used. Fails if opponent had used Mirror
        // Move, Sketch, Sleep Talk, Transform, Mimic, Metronome, or any attack
        // user knows.

        m = new Move("Mirror Move", "", "", "Flying", 0, 20, 100, 255);
        baseMoves.put("Mirror Move", m);

        // 0A: Increases user's Attack by 1 stage

        m = new Move("Mediate", "", "", "Psychic", 0, 0, 40, 255);
        baseMoves.put("Mediate", m);
        m = new Move("Sharpen", "", "", "Normal", 0, 0, 30, 255);
        baseMoves.put("Sharpen", m);

        // 0B: Increases user's Defense by 1 stage

        m = new Move("Harden", "", "", "Psychic", 0, 0, 30, 255);
        baseMoves.put("Harden", m);
        m = new Move("Withdraw", "", "", "Normal", 0, 0, 40, 255);
        baseMoves.put("Withdraw", m);

        // 0D: Increases user's Special Attack by 1 stage

        m = new Move("Growth", "", "", "Normal", 0, 0, 40, 255);
        baseMoves.put("Growth", m);

        // 10: Increases user's evasion by 1 stage

        m = new Move("Double Team", "", "", "Normal", 0, 0, 15, 255);
        baseMoves.put("Double Team", m);

        // After a successful use of this attack by the user, every use of Stomp
        // by the opponent deals double base damage, even when the opponent uses
        // other attacks in between. This effect ends when the user is switched.

        m = new Move("Minimize", "", "", "Normal", 0, 0, 20, 255);
        baseMoves.put("Minimize", m);

        // 11: ^ Hits without fail. Fails if opponent is using Dig or Fly

        m = new Move("Swift", "", "", "Normal", 0, 20, 60, 255);
        baseMoves.put("Swift", m);
        m = new Move("Faint Attack", "", "", "Dark", 0, 20, 60, 255);
        baseMoves.put("Faint Attack", m);

        // Fourth Priority

        m = new Move("Vital Throw", "", "", "Fighting", 0, 10, 70, 255);
        baseMoves.put("Vital Throw", m);

        // 12: * Decreases opponent's Attack by 1 stage

        m = new Move("Growl", "", "", "Normal", 0, 40, 0, 255);
        baseMoves.put("Growl", m);

        // 13: * Decreases opponent's Defense by 1 stage.

        m = new Move("Tail Whip", "", "", "Normal", 0, 30, 0, 255);
        baseMoves.put("Tail Whip", m);
        m = new Move("Leer", "", "", "Normal", 0, 30, 0, 255);
        baseMoves.put("Leer", m);

        // 14: * Decreases opponent's Speed by 1 stage.

        m = new Move("String Shot", "", "", "Bug", 0, 30, 0, 242);
        baseMoves.put("String Shot", m);

        // 17: * Decreases opponent's Accuracy by 1 stage.

        m = new Move("Sand-Attack", "", "", "Ground", 0, 15, 0, 255);
        baseMoves.put("Sand-Attack", m);
        m = new Move("Smokescreen", "", "", "Normal", 0, 20, 0, 255);
        baseMoves.put("Smokescreen", m);
        m = new Move("Kinesis", "", "", "Psychic", 0, 15, 0, 204);
        baseMoves.put("Kinesis", m);
        m = new Move("Flash", "", "", "Normal", 0, 20, 0, 178);
        baseMoves.put("Flash", m);

        // 18: * Decreases opponent's evasion by 1 stage.

        m = new Move("Sweet Scent", "", "", "Normal", 0, 20, 0, 255);
        baseMoves.put("Sweet Scent", m);

        // 19: Resets the stat stages for all stats (including evasion and
        // Accuracy) on both active Pokemon to zero.

        m = new Move("Haze", "", "", "Ice", 0, 30, 0, 255);
        baseMoves.put("Haze", m);

        // 1A: *^ When this attack is used, the effect begins and X is set to 0.
        // During effect, user uses this attack during each of its turns and
        // cannot use any other attack, but may switch, and whenever the user is
        // hit by an attack by the opponent (other than Pain Split), the HP lost
        // by user due to that attack is added to X. After the second or third
        // use of this attack after this one, returns to opponent X times 2 and
        // ends effect. Accuracy check is performed only when attack is
        // returned. Counts all hits of multi-hit attacks. Attack returned is
        // affected by type immunities. Returned attack is considered an attack
        // by the user. If user is replaced, the user finishes its turn by doing
        // something other than attack, or this attack is prevented from being
        // used during effect, effect ends without returning an attack.

        m = new Move("Bide", "", "", "Normal", 0, 10, 0, 255);
        baseMoves.put("Bide", m);

        // 1B: *^ User uses this attack (even if the attack misses) for two or
        // three turns (including this turn), during which user cannot use any
        // other attack or switch, and after which user becomes confused (even
        // if it was already confused). If user is prevented from using this
        // attack or is replaced, effect ends without causing confusion. If user
        // is asleep, this attack deals damage and lasts one turn (doesn't cause
        // confusion).

        m = new Move("Thrash", "", "", "Normal", 0, 20, 90, 255);
        baseMoves.put("Thrash", m);
        m = new Move("Pedal Dance", "", "", "Grass", 0, 20, 70, 255);
        baseMoves.put("Pedal Dance", m);
        m = new Move("Outrage", "", "", "Dragon", 0, 15, 90, 255);
        baseMoves.put("Outrage", m);

        m = new Move("Gust", "", "", "Flying", 0, 0, 40, 255);
        baseMoves.put("Gust", m);
        m = new Move("Thunder Wave", "", "", "Eletric", 0, 30, 0, 255);
        baseMoves.put("Thunder Wave", m);

    }
    
    public String getName() {
        return name;
    }

    public static int getSpeedPriority(Move m) {
        // Priority: Endure, Protect, Detect -> Quick Attack, Mach Punch,
        // Extremespeed -> All other attacks -> Counter, Mirror Coat, Whirlwind,
        // Roar, Vital Throw
        String name = m.getName();
        if (name.equals("Endure") || name.equals("Protect")
                || name.equals("Detect"))
            return 1;
        if (name.equals("Quick Attack") || name.equals("Mach Punch")
                || name.equals("Extremespeed"))
            return 2;
        if (name.equals("Counter") || name.equals("Mirror Coat")
                || name.equals("Whirlwind") || name.equals("Roar")
                || name.equals("Vital Throw"))
            return 4;
        return 3;
    }

    public void set(Move m) {

        this.name = m.name;
        this.description = m.description;

        this.effect = m.effect;
        this.type = m.type;
        this.effmod = m.effmod;

        this.currentpp = m.pp;
        this.pp = m.pp;
        this.dmg = m.dmg;
        this.accuracy = m.accuracy;
    }

    public void getBase() {

    	Move move = baseMoves.get(name);
 
    	if(move != null){
    		dmg = move.dmg;
    		accuracy = move.accuracy;
    		effect = move.effect;
    	} else
    		System.err.println("Move " + name + " not found!");

    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    public String toString() {

        return "effmod: " + effmod + "\nName: " + name + "\nDescrip: "
                + description + "\nEffect: " + effect + "\nType: " + type
                + "\nCurPP: " + currentpp + "\npp: " + pp + "\nDmg: " + dmg
                + "\nAcc: " + accuracy;

    }

    public Object deepCopy() {
        return new Move(new String(name + ""), new String(description + ""),
                new String(effect + ""), new String(type + ""), effmod, pp,
                dmg, accuracy);
    }

    public static class Edit extends JFrame {

        ArrayList<SubListener> subListeners = new ArrayList<SubListener>();

        Sub sub;

        JTextField nameTF, typeTF, effectTF, ppTF;

        boolean editing = false;

        public Edit(Sub s) {
            super("Edit Move");

            sub = s;

            Container c = getContentPane();
            setLayout(new FlowLayout(FlowLayout.LEFT));

            String name = "", type = "", effect = "", pp = "";

            if (s.s != null) {
                editing = true;
                Move m = (Move) s.s;
                name += m.name;
                type += m.type;
                effect += m.effect;
                pp += m.pp;
            }

            c.add(new JLabel("Name:"));
            c.add(nameTF = new JTextField(name, 10));
            c.add(Box.createHorizontalStrut(20000));
            c.add(new JLabel("Type:"));
            c.add(typeTF = new JTextField(type, 10));
            c.add(Box.createHorizontalStrut(20000));
            c.add(new JLabel("Effect:"));
            c.add(effectTF = new JTextField(effect, 10));
            c.add(Box.createHorizontalStrut(20000));
            c.add(new JLabel("PP:"));
            c.add(ppTF = new JTextField(pp, 10));
            c.add(Box.createHorizontalStrut(20000));

            JButton saveB = new JButton("Save");
            saveB.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    save();
                }
            });
            c.add(saveB);

            setSize(360, 340);
            setVisible(true);

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

        public void save() {
            try {
                Move m = null;
                if (sub.s == null)
                    m = new Move();
                else
                    m = (Move) sub.s;
                m.name = nameTF.getText();
                m.type = typeTF.getText();
                m.effect = effectTF.getText();
                m.pp = Integer.parseInt(ppTF.getText());
                m.currentpp = m.pp;
                sub.s = m;
                if (editing)
                    ((Move) sub.s).set(m);
                else
                    sendSubEvent();
                dispose();
            } catch (Exception x) {
                x.printStackTrace();
            }
        }

    }

	@Override
	public String toJSON() {

		String json = "{'class':'" + this.getClass().getName() + "'";

	    json += ",'effmod':" + effmod;

	    json += ",'name':" + JSONObject.stringToJSON(name);
	    json += ",'description':" + JSONObject.stringToJSON(description);

	    json += ",'effect':" + JSONObject.stringToJSON(effect);
	    json += ",'type':" + JSONObject.stringToJSON(type);

	    json += ",'currentpp':" + currentpp;
	    json += ",'pp':" + pp;
	    json += ",'dmg':" + dmg;
	    json += ",'accuracy':" + accuracy;
	    
	    json += "}";
	    
	    return json;
	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {
		// TODO Auto-generated method stub
		
	}
    
}
