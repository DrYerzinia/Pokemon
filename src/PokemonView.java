import java.awt.*;
import java.util.*;
import java.awt.event.*;

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

        arrow = PokemonGame.images.getSprite("ArrowRight.png");
        harrow = PokemonGame.images.getSprite("HoloArrowRight.png");

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

        if (stats == -1) {

            for (int i = 0; i < 6; i++) {
                if (PokemonGame.pokeg.Char.poke.belt[i] != null) {

                    g.drawImage(PokemonGame.pokeg.Char.poke.belt[i].sprites[1],
                            7, i * 16 + 2, null);
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("monospaced", 0, 10));
                    g.drawString(PokemonGame.pokeg.Char.poke.belt[i].getName(),
                            25, i * 16 + 11);
                    g.drawString(":L"
                            + PokemonGame.pokeg.Char.poke.belt[i].level, 100,
                            i * 16 + 11);
                    g.drawString(
                            PokemonGame.pokeg.Char.poke.belt[i].currentHP
                                    + "/"
                                    + PokemonGame.pokeg.Char.poke.belt[i]
                                            .getTotalHP(), 100, i * 16 + 20);

                    Fight.drawHPBar(
                            g,
                            ((double) PokemonGame.pokeg.Char.poke.belt[i].currentHP / (double) PokemonGame.pokeg.Char.poke.belt[i]
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

            g.drawImage(PokemonGame.pokeg.Char.poke.belt[stats].sprites[10], 0,
                    0, null);

            g.setColor(Color.BLACK);
            g.setFont(new Font("monospaced", Font.BOLD, 10));
            g.drawString("No. "
                    + PokemonGame.pokeg.Char.poke.belt[stats].pokeBase.no, 5,
                    64);

            g.setFont(new Font("monospaced", 0, 12));
            g.drawString(PokemonGame.pokeg.Char.poke.belt[stats].getName(), 80,
                    15);

            if (statsec == 0) {

                g.setFont(new Font("monospaced", Font.BOLD, 11));
                g.drawString(":L"
                        + (PokemonGame.pokeg.Char.poke.belt[stats].level), 100,
                        25);
                g.setFont(new Font("monospaced", Font.BOLD, 11));
                g.drawString(
                        PokemonGame.pokeg.Char.poke.belt[stats].currentHP
                                + "/ "
                                + PokemonGame.pokeg.Char.poke.belt[stats]
                                        .getTotalHP(), 85, 40);
                g.setFont(new Font("monospaced", 0, 12));
                g.drawString("STATUS/"
                        + PokemonGame.pokeg.Char.poke.belt[stats].status, 80,
                        65);
                if (PokemonGame.pokeg.Char.poke.belt[stats].getType() != null) {
                    g.drawString("TYPE1/", 80, 75);
                    g.drawString(
                            PokemonGame.pokeg.Char.poke.belt[stats].getType(),
                            100, 85);
                }
                if (PokemonGame.pokeg.Char.poke.belt[stats].getType2() != null) {
                    g.drawString("TYPE2/", 80, 95);
                    g.drawString(
                            PokemonGame.pokeg.Char.poke.belt[stats].getType2(),
                            100, 105);
                }
                g.drawString("IDNo/", 80, 115);
                g.drawString("" + PokemonGame.pokeg.Char.poke.belt[stats].idNo,
                        100, 125);
                if (PokemonGame.pokeg.Char.poke.belt[stats].ot != null) {
                    g.drawString("OT/", 80, 135);
                    g.drawString(PokemonGame.pokeg.Char.poke.belt[stats].ot,
                            100, 145);
                }

                g.drawString("ATTACK", 5, 75);
                g.drawString("DEFENSE", 5, 95);
                g.drawString("SPEED", 5, 115);
                g.drawString("SPECIAL", 5, 135);
                g.setFont(new Font("monospaced", Font.BOLD, 12));
                g.drawString(
                        ""
                                + PokemonGame.pokeg.Char.poke.belt[stats]
                                        .getAttack(), 45, 85);
                g.drawString(
                        ""
                                + PokemonGame.pokeg.Char.poke.belt[stats]
                                        .getDefense(), 45, 105);
                g.drawString(
                        "" + PokemonGame.pokeg.Char.poke.belt[stats].getSpeed(),
                        45, 125);
                g.drawString(
                        ""
                                + PokemonGame.pokeg.Char.poke.belt[stats]
                                        .getSpecial(), 45, 145);

                Fight.drawHPBar(
                        g,
                        ((double) PokemonGame.pokeg.Char.poke.belt[stats].currentHP / (double) PokemonGame.pokeg.Char.poke.belt[stats]
                                .getTotalHP()), 75, 27);

            } else {

                g.drawString("EXP POINTS", 80, 30);
                g.drawString("" + PokemonGame.pokeg.Char.poke.belt[stats].EXP,
                        100, 40);
                g.setFont(new Font("monospaced", Font.BOLD, 9));
                g.drawString("LEVEL UP", 80, 50);
                g.drawString(
                        PokemonGame.pokeg.Char.poke.belt[stats]
                                .expToNextLevel()
                                + " to :L"
                                + (PokemonGame.pokeg.Char.poke.belt[stats].level + 1),
                        85, 60);

                for (int i = 0; i < 4; i++) {
                    Move m = PokemonGame.pokeg.Char.poke.belt[stats].moves[i];
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
                    PokemonGame.pokeg.Char.poke.belt[selection].name
                            + " is\nalready out!", 0, 6, 10, 3);
            opa = false;
            wait = true;
        } else if (PokemonGame.pokeg.Char.poke.belt[selection].currentHP == 0) {
            choosem = new GMenu("There's no will\nto fight!", 0, 6, 10, 3);
            opa = false;
            wait = true;
        } else {
            currout = selection;
            switchto = selection;
            active = false;
            PokemonGame.pokeg.writeServerMessage(new FMTSSendNextPokemon(switchto));
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
                        && PokemonGame.pokeg.Char.poke.belt[selection + 1] != null
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
                                    PokemonGame.pokeg.Char.poke.belt, swSel,
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
