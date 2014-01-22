package com.dryerzinia.pokemon.ui;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.IOException;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.msg.server.fight.FMTSSendNextPokemon;
import com.dryerzinia.pokemon.obj.Move;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.ui.editor.Sub;
import com.dryerzinia.pokemon.ui.editor.SubListener;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.ui.menu.MenuEvent;
import com.dryerzinia.pokemon.ui.menu.SelectionMenu;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.util.event.AbstractMenuListener;

public class PokemonView extends Overlay {

    public GMenu choosem;

    ArrayList<SubListener> subListeners = new ArrayList<SubListener>();

    public Image arrow;
    public Image harrow;

    public SelectionMenu op;

    public boolean opa = false;
    public boolean inbattle = false;
    public boolean wait = false;
    public boolean fainted = false;

    public int switchto = -1;
    public int currout = -1;

    public int selection = 0;
    public int swSel = -1;

    public int stats = -1;
    public int statsec = 0;

    public PokemonView() {

        choosem = new GMenu("Choose a POKeMON.\n ", 0, 6, 10, 3);

        arrow = ResourceLoader.getSprite("ArrowRight.png");
        harrow = ResourceLoader.getSprite("HoloArrowRight.png");

        op = new SelectionMenu("STATS\nSWITCH\nCANCEL", 6, 5, 4, 4, 1);
        op.addMenuListener(new AbstractMenuListener() {
            public void MenuPressed(MenuEvent e) {
                String val = e.getValue();
                if (val.equals("SWITCH")) {
                    if (inbattle)
                        battleswitch();
                    else
                        switchInit();
                } else if (val.equals("STATS")) {
                    statInit();
                }
            }
        });
    }

    public void addSubListener(SubListener subListener) {
        subListeners.add(subListener);
    }

    public void sendSubEvent() {
        Iterator<SubListener> subit = subListeners.iterator();
        while (subit.hasNext()) {
            subit.next().SubEvent(new Sub(null));
        }
    }

    public void fainted() {
        fainted = true;
        swSel = -1;
        choosem = new GMenu("Bring out which\nPOKeMON?", 0, 6, 10, 3);
    }

    public void switchInit() {
        swSel = selection;
        choosem = new GMenu("Move Pokemon\nwhere?", 0, 6, 10, 3);
        opa = false;
    }

    public void statInit() {
        choosem = new GMenu("Choose a POKeMON.\n ", 0, 6, 10, 3);
        stats = selection;
        statsec = 0;
        opa = false;
    }

    public void draw(Graphics g) {

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 160, 144);

        int stats = this.stats;

        Pokemon[] character_belt = Player.self.poke.belt;

        if (stats == -1) {

            for (int i = 0; i < 6; i++) {
                if (character_belt[i] != null) {

                    g.drawImage(character_belt[i].sprites[1],
                            7, i * 16 + 2, null);
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("monospaced", 0, 10));
                    g.drawString(character_belt[i].getName(),
                            25, i * 16 + 11);
                    g.drawString(":L"
                            + character_belt[i].level, 100,
                            i * 16 + 11);
                    g.drawString(
                    		character_belt[i].currentHP
                                    + "/"
                                    + character_belt[i]
                                            .getTotalHP(), 100, i * 16 + 20);

                    Fight.drawHPBar(
                            g,
                            ((double) character_belt[i].currentHP / (double) character_belt[i]
                                    .getTotalHP()), 29, i * 16 + 15);

                    choosem.draw(g);

                }
            }

            if (swSel != -1)
                g.drawImage(harrow, 2, swSel * 16 + 6, null);
            g.drawImage(arrow, 2, selection * 16 + 6, null);

            if (opa)
                op.draw(g);

        } else {

            g.drawImage(character_belt[stats].sprites[10], 0,
                    0, null);

            g.setColor(Color.BLACK);
            g.setFont(new Font("monospaced", Font.BOLD, 10));
            g.drawString("No. "
                    + character_belt[stats].pokeBase.no, 5,
                    64);

            g.setFont(new Font("monospaced", 0, 12));
            g.drawString(character_belt[stats].getName(), 80,
                    15);

            if(statsec == 0) {

                g.setFont(new Font("monospaced", Font.BOLD, 11));
                g.drawString(":L"
                        + (character_belt[stats].level), 100,
                        25);
                g.setFont(new Font("monospaced", Font.BOLD, 11));
                g.drawString(
                		character_belt[stats].currentHP
                                + "/ "
                                + character_belt[stats]
                                        .getTotalHP(), 85, 40);
                g.setFont(new Font("monospaced", 0, 12));
                g.drawString("STATUS/"
                        + character_belt[stats].status, 80,
                        65);
                if(character_belt[stats].getType() != null) {
                    g.drawString("TYPE1/", 80, 75);
                    g.drawString(
                    		character_belt[stats].getType(),
                            100, 85);
                }
                if (character_belt[stats].getType2() != null) {
                    g.drawString("TYPE2/", 80, 95);
                    g.drawString(
                    		character_belt[stats].getType2(),
                            100, 105);
                }
                g.drawString("IDNo/", 80, 115);
                g.drawString("" + character_belt[stats].idNo,
                        100, 125);
                if (character_belt[stats].nickName != null) {
                    g.drawString("OT/", 80, 135);
                    g.drawString(character_belt[stats].nickName,
                            100, 145);
                }

                g.drawString("ATTACK", 5, 75);
                g.drawString("DEFENSE", 5, 95);
                g.drawString("SPEED", 5, 115);
                g.drawString("SPECIAL", 5, 135);
                g.setFont(new Font("monospaced", Font.BOLD, 12));
                g.drawString(
                        ""
                                + character_belt[stats]
                                        .getAttack(), 45, 85);
                g.drawString(
                        ""
                                + character_belt[stats]
                                        .getDefense(), 45, 105);
                g.drawString(
                        "" + character_belt[stats].getSpeed(),
                        45, 125);
                g.drawString(
                        ""
                                + character_belt[stats]
                                        .getSpecial(), 45, 145);

                Fight.drawHPBar(
                        g,
                        ((double) character_belt[stats].currentHP / (double) character_belt[stats]
                                .getTotalHP()), 75, 27);

            } else {

                g.drawString("EXP POINTS", 80, 30);
                g.drawString("" + character_belt[stats].EXP,
                        100, 40);
                g.setFont(new Font("monospaced", Font.BOLD, 9));
                g.drawString("LEVEL UP", 80, 50);
                g.drawString(
                		character_belt[stats]
                                .expToNextLevel()
                                + " to :L"
                                + (character_belt[stats].level + 1),
                        85, 60);

                for (int i = 0; i < 4; i++) {
                    Move m = character_belt[stats].moves[i];
                    if (m == null)
                        break;

                    g.setFont(new Font("monospaced", 0, 11));
                    g.drawString(m.name, 10, 70 + i * 20);
                    g.setFont(new Font("monospaced", Font.BOLD, 11));
                    g.drawString("pp " + m.currentpp + "/" + m.pp, 85,
                            80 + i * 20);

                }

            }
        }
    }

    public void set(Overlay o) {

        PokemonView pv = (PokemonView) o;

        choosem = pv.choosem;

        arrow = pv.arrow;
        harrow = pv.harrow;

        selection = pv.selection;

        active = true;

    }

    public void battleswitch() {
        if (currout == selection) {
            choosem = new GMenu(
                    Player.self.poke.belt[selection].nickName
                            + " is\nalready out!", 0, 6, 10, 3);
            opa = false;
            wait = true;
        } else if (Player.self.poke.belt[selection].currentHP == 0) {
            choosem = new GMenu("There's no will\nto fight!", 0, 6, 10, 3);
            opa = false;
            wait = true;
        } else {
            currout = selection;
            switchto = selection;
            active = false;
            try {
            	PokemonGame.pokeg.writeServerMessage(new FMTSSendNextPokemon(switchto));
            } catch(IOException ioe){
            	System.err.println("Failed to send Pokemon Switch: " + ioe.getMessage());
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();
        if (wait) {
            if (c == KeyEvent.VK_X || c == KeyEvent.VK_Z) {
                if (fainted)
                    choosem = new GMenu("Bring out which\nPOKeMON?", 0, 6, 10,
                            3);
                else
                    choosem.set(new GMenu("Choose a POKeMON.\n ", 0, 6, 10, 3));
                wait = false;
            }
        } else {
            if (c == KeyEvent.VK_UP) {
                if (opa)
                    op.pressUp();
                else if (selection > 0 && stats == -1)
                    selection--;
            } else if (c == KeyEvent.VK_LEFT) {
                if (opa)
                    op.pressLeft();
            } else if (c == KeyEvent.VK_RIGHT) {
                if (opa)
                    op.pressRight();
            } else if (c == KeyEvent.VK_DOWN) {
                if (opa)
                    op.pressDown();
                else if (selection < 6
                        && Player.self.poke.belt[selection + 1] != null
                        && stats == -1)
                    selection++;
            } else if (c == KeyEvent.VK_X) {
                if (!fainted) {
                    if (stats != -1) {
                        statsec++;
                        if (statsec == 2)
                            stats = -1;
                    } else if (swSel != -1) {
                        swSel = -1;
                        choosem = new GMenu("Choose a POKeMON.\n ", 0, 6, 10, 3);
                    } else if (opa) {
                        if (op.pushB())
                            opa = false;
                    } else
                        active = false;
                }
            } else if (c == KeyEvent.VK_Z) {
                if (fainted) {
                    battleswitch();
                } else {
                    if (stats != -1) {
                        statsec++;
                        if (statsec == 2)
                            stats = -1;
                    } else if (swSel != -1) {
                        if (inbattle) {
                            battleswitch();
                        } else {
                            PokemonGame.switchPokemon(
                            		Player.self.poke.belt, swSel,
                                    selection);
                            sendSubEvent();
                            System.out.println("sew");
                        }

                        swSel = -1;
                        choosem = new GMenu("Choose a POKeMON.\n ", 0, 6, 10, 3);
                    } else if (opa) {
                        if (op.push())
                            opa = false;
                    } else
                        opa = true;
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

}
