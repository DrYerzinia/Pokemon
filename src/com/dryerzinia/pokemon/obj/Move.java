package com.dryerzinia.pokemon.obj;
import java.io.*;
import java.awt.*;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.ui.editor.Sub;
import com.dryerzinia.pokemon.ui.editor.SubListener;
import com.dryerzinia.pokemon.util.DeepCopy;
import com.dryerzinia.pokemon.util.JSON;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.util.StringStream;

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

    public static void writeMoveBase(){

    	// Write Pokemon Base stat's to File
        File file = new File("MoveBase.json");
        StringBuilder json = new StringBuilder("[");

        try {

	        Iterator<Entry<String, Move>> it = baseMoves.entrySet().iterator();
	        while(it.hasNext()){
	        	Move move = it.next().getValue();
	
	        	json.append(move.toJSON());
	
	        }
	        
	        json.append("]"); // Replace last extra , with array closure

	        // Attempt to write stats to the file
	        try (BufferedWriter json_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
	
	        	json_writer.write(json.toString());
	
	        } catch(IOException ioe) {
	
	        	System.err.println("Move Base not written to file: " + file.getAbsolutePath());
	
	        	ioe.printStackTrace();
	        	
	        }

        } catch(IllegalAccessException e){

        	System.err.println("Move Base not written a file because we have no reflection access: " + e.getLocalizedMessage());

        }

    }

    public static void readMoveBase(){
    	
    	try (BufferedReader json_reader = new BufferedReader(new InputStreamReader(
                PokemonGame.class.getClassLoader().getResourceAsStream("MoveBase.json")))) {
    		
    		String json = json_reader.readLine();

    		baseMoves = new HashMap<String, Move>();

    		Object baseMovesArray[] = JSONObject.JSONToArray(new StringStream(json));

    		for(Object moveObj : baseMovesArray){

    			Move move = (Move)moveObj;
    			baseMoves.put(move.getName(), move);

    		}

    	} catch(IOException ioe) {

    		System.err.println("Failed to load Move Base!");
    		ioe.printStackTrace();
    		
    		// TODO Terminate program

    	}

    }

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

    	JSONObject.defaultToObject(json, this);

	}
    
}
