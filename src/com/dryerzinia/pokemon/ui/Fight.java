package com.dryerzinia.pokemon.ui;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.net.msg.server.fight.FMTSGetNextPokemon;
import com.dryerzinia.pokemon.net.msg.server.fight.FMTSSelectedAttack;
import com.dryerzinia.pokemon.obj.ClientState;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Move;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.ui.menu.AttackSelectionMenu;
import com.dryerzinia.pokemon.ui.menu.GMenu;
import com.dryerzinia.pokemon.ui.menu.ItemMenu;
import com.dryerzinia.pokemon.ui.menu.MenuEvent;
import com.dryerzinia.pokemon.ui.menu.SelectionMenu;
import com.dryerzinia.pokemon.ui.menu.StatDispMenu;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.util.event.AbstractMenuListener;

public class Fight extends Overlay implements Serializable {

    // Server Fight Message Codes
    public static final int FM_ATTACK_SELECTION = 0;
    public static final int FM_POKEMON_SWITCH = 1;
    public static final int FM_USE_ITEM = 2;
    public static final int FM_RUN = 3;
    public static final int FM_START_FIGHT = 4;
    public static final int FM_SPEED_CHECK = 5;
    public static final int FM_POKEMON_SET = 6;
    public static final int FM_DAMAGE_CHECK = 7;
    public static final int FM_GET_NEXT_POKEMON = 8;

    // Control Codes
    public static final int FM_SPEED_CHECK_GO_FIRST = 0;
    public static final int FM_SPEED_CHECK_GO_LAST = 1;

    public static final int FM_POKEMON_SWITCHED = 5;

    // Static Menus
    public static final GMenu OpponentWaitGM = new GMenu(
            "Waiting for\nopponent...", 0, 6, 10, 3);

    public static final Move SwitchPokemon = new Move("Switch pokemon", "", "",
            "", 0, 10, 0, 0);
    public static final Move UseItem = new Move("Use item", "", "", "", 0, 10,
            0, 0);

    public transient Image playerb;
    public transient Image belt[];
    public transient Image pokeballTiny[];

    public transient SelectionMenu fightMenu;
    public transient SelectionMenu yesnoMenu;

    public transient GMenu info = new GMenu("", 0, 6, 10, 3);

    public transient boolean WaitingForServerMessageReturn = false;

    public transient OverlayO ol = new OverlayO();

    public transient boolean ola = false;

    public transient boolean running = false;
    public transient boolean fightMenuActive = false;
    public transient boolean yesnoMenuActive = false;
    public transient boolean waitcompstart = false;
    public transient boolean fail = false;
    public transient boolean switching = false;
    public transient boolean finishswitch = false;
    public transient boolean pokesw = false;
    public transient boolean turn = true;
    public transient boolean animate = false;
    public transient boolean compfinished = true;
    public transient boolean faint = false;
    public transient boolean exp = false;
    public transient boolean end = false;
    public transient boolean blackingout = false;
    public transient boolean capture = false;
    public transient boolean golastcenter = false;
    public transient boolean failcapture = false;

    public transient int runCount = 0;

    public transient Pokemon out = null;

    public transient int attack = -1;

    public transient Move toUseU = null;
    public transient Move toUseE = null;

    public transient int damageU;
    public transient int damageE;

    public transient boolean missU;
    public transient boolean missE;

    private transient Move astoUseE = null;

    public transient Pokemon firstTurn = null;

    private transient boolean isComputer = true;

    // FIGHT MESSAGE DATA
    public transient boolean notChallengerKOed = false;

    public transient int itemToUse = 0;

    public int activePokemonE = -2;
    public int activePokemonC = -2;

    public Player enemyPlayer;
    public Player currentPlayer;

    public int pokemonCountE;
    public int pokemonCountC;

    public Pokemon enemy = null;

    public Fight() {
    }

    public Fight(Pokemon enemy) {

        this.enemy = enemy;

        init();

    }

    public void init() {    // TODO: check only pokemon that are not NULL!!!
        for (int i = 0; i < 6; i++) {
            if (ClientState.player.poke.belt[i].currentHP != 0) {
                out = ClientState.player.poke.belt[i];
                activePokemonC = i;
                break;
            }
        }
        out.used = true;

        belt = new Image[2];
        pokeballTiny = new Image[2];

        playerb = ResourceLoader.getSprite("MeBack.png");
        belt[0] = ResourceLoader.getSprite("PokeballBarL.png");
        belt[1] = ResourceLoader.getSprite("PokeballBarR.png");
        pokeballTiny[0] = ResourceLoader.getSprite("PokeballFull.png");
        pokeballTiny[1] = ResourceLoader.getSprite("PokeballEmpty.png");

        setMainMenu();
        final GMenu info2 = info;
        fightMenu = new SelectionMenu("FIGHT\nPkMn\nITEM\nRUN", null, 4, 6, 6,
                3, 2);
        yesnoMenu = new SelectionMenu("Yes\nNo", null, 6, 4, 3, 3, 1);

        yesnoMenu.exitOnLast = false;

        Overlay ol[] = new Overlay[4];

        ol[0] = null;
        ol[1] = new PokemonView();
        ol[2] = null;
        ol[3] = null;

        this.ol.o = ol[1];// new PokemonView(PokemonGame.pokeg);
        ((PokemonView) ol[1]).inbattle = true;
        ((PokemonView) ol[1]).currout = 0;

        fightMenu.addMenuListener(new AbstractMenuListener() {
            public void MenuPressed(MenuEvent e) {
                int i = e.getSelection();
                if (i == 3) {
                    if (canRun()) {
                        running = true;
                        // fightMenuActive = false;
                        info2.set(new GMenu("Got away safely!\n ", 0, 6, 10, 3));
                        turn = false;
                        fightMenuActive = false;
                    } else {
                        if (isTrainer()) {
                            info2.set(new GMenu(
                                    "You can't run \nfrom a Trainer.", 0, 6,
                                    10, 3));
                            System.out.println("Cant Run");
                            turn = false;
                            setFirstTurnEnemy();
                            fightMenuActive = false;
                        } else {
                            info2.set(new GMenu("Can't escape!\n ", 0, 6, 10, 3));
                            waitcompstart = true;
                            turn = false;
                            fightMenuActive = false;
                        }
                        // compfinished = false;
                    }
                }
            }
        });

        final Overlay ot = this.ol.o;
        yesnoMenu.addMenuListener(new AbstractMenuListener() {
            public void MenuPressed(MenuEvent e) {
                int i = e.getSelection();
                if (i == 0) {
                    yesnoMenuActive = false;
                    ot.active = true;
                    ((PokemonView) ot).fainted();
                } else {
                    if (faintRun()) {
                        running = true;
                        info2.set(new GMenu("Got away safely!\n ", 0, 6, 10, 3));
                    } else {
                        info2.set(new GMenu("Couldent run...\n ", 0, 6, 10, 3));
                        waitcompstart = true;
                        // fix
                    }
                    yesnoMenuActive = false;
                    System.out.println("?");
                }
                System.out.println("p");
            }
        });

        fightMenu.overlays = ol;
        fightMenu.toset = this.ol;
        fightMenu.exitOnLast = false;

        fightMenu.submenus = new GMenu[4];

        fightMenu.submenus[0] = null;
        fightMenu.submenus[1] = null;
        fightMenu.submenus[2] = new ItemMenu(ClientState.player.items, 2,
                1, 8, 5, 1);
        fightMenu.submenus[3] = null;

        ((SelectionMenu) fightMenu.submenus[2])
                .addMenuListener(new AbstractMenuListener() {
                    public void MenuPressed(MenuEvent e) {
                        int i = e.getSelection();
                        if (ClientState.player.items.size() > i) {
                            Item it = ClientState.player.items.get(i);
                            if (e.getButton() == MenuEvent.Z) {
                                it.number--;
                                itemToUse = i;
                                toUseU = Fight.UseItem;

                                fightMenuActive = false;
                                info2.set(OpponentWaitGM);
                                WaitingForServerMessageReturn = true;
                                System.out.println("PokemonGame.pokeg.writeServerMessage(new FMTSSelectedAttack(FMTSSelectedAttack.ITEM_MOVE,i));");

                                try {
                                	Client.writeServerMessage(new FMTSSelectedAttack(
                                                FMTSSelectedAttack.ITEM_MOVE,
                                                i));
                                } catch(IOException ioe) {
                                	System.err.println("Failed to write ItemMove to server: " + ioe.getMessage());
                                	// TODO connection error handling
                                }
                            }
                        }
                    }
                });
    }

    public void setMainMenu() {
        if (isTrainer())
            info.set(new GMenu(enemyPlayer + " sent\nout " + enemy
                    + "...\n  \nGo! " + out + "!\n ", 0, 6, 10, 3));
        else
            info.set(new GMenu("Wild " + enemy + "\nappeared!\n  \nGo! " + out
                    + "!\n ", 0, 6, 10, 3));
    }

    public void capture(boolean success) {
        info.set(new GMenu(ClientState.player.getName() + " used\n"
                + "\nPokeball!", 0, 6, 10, 3));
        if (success) {
            capture = true;
            addPokemon(ClientState.player, enemy);
        } else {
            failcapture = true;
        }
    }

    public void addPokemon(Player player, Pokemon p) {
        p.added = true;
        player.poke.box.add(p);
        for (int i = 0; i < 6; i++) {
            if (player.poke.belt[i] == null) {
                player.poke.belt[i] = p;
                p.location = i;
                p.nickName = p.Species;
                p.Species = p.Species;
                return;
            }
        }

    }

    public Player getPlayer() {
        return enemyPlayer;
    }

    private void setFirstTurnEnemy() {
        firstTurn = enemy;
    }

    public void setOutPokemon(Pokemon p) {
        out = p;
    }

    public boolean canRun() {
        if (isTrainer())
            return false;
        int l1 = ClientState.player.poke.belt[activePokemonC].level;
        int l2 = enemy.level;
        int X = (int) ((out.getSpeed() * 32 / ((enemy.getSpeed() / 4) % 255)) + (30 * runCount));
        int r = (int) (Math.random() * 255.0);
        if (r <= X)
            return true;
        runCount++;
        return false;
    }

    public boolean faintRun() {// fake
        return true;
    }

    public static Color getPercentageColor(double per) {
        if (per >= .5)
            return Color.GREEN;
        if (per >= .25)
            return Color.ORANGE;
        return Color.RED;
    }

    public static void drawHPBar(Graphics g, double per, int x, int y) {
        g.drawString("HP:", x, y + 5);
        g.drawLine(x + 16, y, x + 66, y);
        g.drawLine(x + 16, y + 3, x + 66, y + 3);
        g.drawLine(x + 15, y + 1, x + 15, y + 2);
        g.drawLine(x + 67, y + 1, x + 67, y + 2);
        g.setColor(Fight.getPercentageColor(per));
        g.drawLine(x + 16, y + 1, (int) (x + 16 + 50.0 * per), y + 1);
        g.drawLine(x + 16, y + 2, (int) (x + 16 + 50.0 * per), y + 2);
    }

    public void draw(Graphics g) {
        if (ol.o.active) {
            ol.o.draw(g);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 160, 144);

            g.drawImage(belt[1], 5, 16, null);

            g.drawImage(enemy.sprites[10], 102, 0, null);
            g.setColor(Color.BLACK);
            g.setFont(new Font("monospaced", 0, 12));
            g.drawString(enemy.getName(), 5, 10);
            g.setFont(new Font("monospaced", Font.BOLD, 11));
            g.drawString(":L" + enemy.level, 26, 20);
            g.setFont(new Font("monospaced", Font.BOLD, 8));
            Fight.drawHPBar(g,
                    ((double) enemy.currentHP / (double) enemy.getTotalHP()),
                    15, 22);
            g.drawImage(belt[0], 70, 80, null);

            if (activePokemonC == -2) {
                g.drawImage(playerb, 0, 32, null);
                for (int i = 0; i < 6; i++) {
                    if (ClientState.player.poke.belt[i] != null)
                        g.drawImage(pokeballTiny[0], 85 + i * 8, 80, null);
                    else
                        g.drawImage(pokeballTiny[1], 85 + i * 8, 80, null);
                }
            } else if (activePokemonC == -1)
                ; // TODO: WTF IS THIS FOR???
            else {
                g.drawImage(
                        ClientState.player.poke.belt[activePokemonC].sprites[9],
                        0, 32, null);
                g.setColor(Color.BLACK);
                g.setFont(new Font("monospaced", 0, 12));
                g.drawString(ClientState.player.poke.belt[activePokemonC]
                        .getName(), 80, 65);
                g.setFont(new Font("monospaced", Font.BOLD, 11));
                g.drawString(
                        ":L"
                                + ClientState.player.poke.belt[activePokemonC].level,
                        100, 75);
                g.setFont(new Font("monospaced", Font.BOLD, 11));
                g.drawString(out.currentHP + "/ " + out.getTotalHP(), 85, 90);
                g.setFont(new Font("monospaced", Font.BOLD, 8));
                Fight.drawHPBar(
                        g,
                        ((double) ClientState.player.poke.belt[activePokemonC].currentHP / (double) out
                                .getTotalHP()), 75, 77);

            }

            if (animate) {
                animate = animation();
                if (!animate) {
                    turn = false;
                    compfinished = false;
                }
            }

            if (info != null)
                info.draw(g);
            if (fightMenu != null && fightMenuActive)
                fightMenu.draw(g);
            if (yesnoMenu != null && yesnoMenuActive)
                yesnoMenu.draw(g);
        }

    }

    public void ai() {
        System.out.println("AIExe");
        Move m = toUseE;
        if (m.toString().equals("Switch pokemon")) {
            enemySwitch();
            return;
        }
        toUseE = null;

        // TODO: THIS IS THE ACCURACY CHECK: !!! //if(Math.random()*255 <
        // m.accuracy){
        if (!missE) {
            out.currentHP -= damageE;// enemy.getDamage(m, out.getDefense(),
                                     // false);
            System.out.println("ENEMY DAMAGE!!!");
            if (out.currentHP < 0) {
                out.currentHP = 0;
            }
            // TODO: move animation
        } else {
            System.out.println("Missed");
        }
        info.set(new GMenu("Enemy " + enemy.getName() + "\nUsed " + m.name
                + "!", 0, 6, 10, 3));
        compfinished = true;
    }

    private void enemySwitch() {
        info.set(new GMenu("Switching...", 0, 6, 10, 3));
    }

    public int getEnemyDamage() {
        return enemy.getDamage(toUseE, out.getDefense(), false);
    }

    public int getOutDamage() {
        return out.getDamage(toUseU, enemy.getDefense(), false);
    }

    public void leaveInfo() {
        final GMenu info2 = info;
        final SelectionMenu fightMenu2 = fightMenu;
        fightMenu.submenus[0] = new AttackSelectionMenu(out, 2, 6, 8, 3);
        ((SelectionMenu) fightMenu).exitOnLast = false;
        ((SelectionMenu) fightMenu.submenus[0]).exitOnLast = false;
        ((SelectionMenu) fightMenu.submenus[0])
                .addMenuListener(new AbstractMenuListener() {
                    public void MenuPressed(MenuEvent e) {
                        int i = e.getSelection();
                        if (e.getButton() == MenuEvent.Z) {
                            attack = i;
                            if (ClientState.player.poke.belt[activePokemonC].moves[i] != null) {
                                fightMenuActive = false;
                                fightMenu2.submenu = null;
                                toUseU = out.moves[i];

                                System.out.println("Attack selection sent...");
                                info2.set(OpponentWaitGM);
                                WaitingForServerMessageReturn = true;
                                System.out.println("PokemonGame.pokeg.writeServerMessage(new FMTSSelectedAttack(i, 0));");

                                try {
                                	Client.writeServerMessage(new FMTSSelectedAttack(
                                                i, 0));
                                } catch(IOException ioe){
                                	System.err.println("Failed to write attack to server: " + ioe.getMessage());
                                }

                            }
                        }
                    }
                });
        info.set(new GMenu(" \n ", 0, 6, 10, 3));
        // activePokemonC = 0;
        fightMenuActive = true;
    }

    public Pokemon getEnemy() {
        return enemy;
    }

    public void setEnemyAttack(Move m) {
        astoUseE = m;
    }

    public boolean attacksSet() {
        if (toUseU != null && toUseE != null)
            return true;
        return false;
    }

    public void resetMoves() {
        toUseU = null;
        toUseE = null;
    }

    public void setToUseEnemy(Move m) {
        toUseE = m;
    }

    public void setToUseCurrent(Move m) {
        toUseU = m;
    }

    public int speedCheck() {
        // set First Turn to the pokemon who attacks faster

        // Out move is always fastest
        if (toUseU.getName().equals(Fight.SwitchPokemon.getName())
                || toUseU.getName().equals(Fight.UseItem.getName())) {
            firstTurn = out;
            return FM_SPEED_CHECK_GO_FIRST;
        }

        int p1 = Move.getSpeedPriority(toUseU);
        int p2 = Move.getSpeedPriority(toUseE);
        if (p1 > p2)
            firstTurn = out;
        else if (p1 == p2) {
            int s1 = out.getSpeed();
            int s2 = enemy.getSpeed();
            if (s1 > s2)
                firstTurn = out;
            else if (s1 == s2) {
                int r = (int) (Math.random() * 2);
                if (r == 1) {
                    firstTurn = out;
                    System.out.println("Out");
                } else {
                    firstTurn = enemy;
                    System.out.println("Enemy");
                }
            } else
                firstTurn = enemy;
        } else
            firstTurn = enemy;
        if (firstTurn == enemy)
            return FM_SPEED_CHECK_GO_LAST;
        return FM_SPEED_CHECK_GO_FIRST;
    }

    public boolean animation() {
        return false;
    }

    public void attack() {
        Move m = toUseU;
        toUseU = null;
        if (m.currentpp == 0) { // TODO: THIS CHECK SHOULD BE IN THE ATTACK
                                // SELECTION MENU!!!
            info.set(new GMenu("No PP left\nfor this move!", 0, 6, 10, 3));
            fightMenuActive = false;
            turn = false;
            return;
        }
        // DIFFERENT ACCURACY ???? TODO: Figure out this accuracy thing this is
        // different then AI version //if(Math.random()*100 < m.accuracy){
        if (!missU) {
            m.currentpp--; // TODO: subtract PP server side also add to the AI
            enemy.currentHP -= damageU;// out.getDamage(m, enemy.getDefense(),
                                       // false); // TODO: double check correct
                                       // use of getDamage server side
            System.out.println("att:" + attack + ", def:" + enemy.getDefense());
            runCount = 0; // TODO: WHY IS THIS HERE ???
            if (enemy.currentHP <= 0) {
                enemy.currentHP = 0;
                faint = true;
                return;
            }
            animate = true;
        } else {
            System.out.println("YOU Missed!");
        }
    }

    public Move aiAttackSelection() { // TODO: USE this function server side

        // TODO: make computer smart on condition
        // Change to if computer is smart
        // Write code to find optimum damage attack
        // Dumb computer choose attack at random
        // also needs a chance of swapping pokemon for no reason

        int n = enemy.numberOfMoves();
        int r = (int) (Math.random() * n);
        toUseE = enemy.getMove(r);

        return toUseE;

    }

    public void setEnemyPokemon(Pokemon p) {
        enemy = p;
    }

    public void set(Overlay o) {
        Fight f = (Fight) o;

        playerb = f.playerb;
        belt = f.belt;
        pokeballTiny = f.pokeballTiny;

        info = f.info;
        fightMenu = f.fightMenu;

        ol = f.ol;

        ola = f.ola;

        running = f.running;
        fightMenuActive = f.fightMenuActive;

        enemy = f.enemy;

        activePokemonC = f.activePokemonC;
        attack = f.attack;

        turn = f.turn;
        animate = f.animate;
        compfinished = f.compfinished;
        faint = f.faint;
        exp = f.exp;
        end = f.end;

    }

    public void isNotComputer() {
        isComputer = false;
    }

    public void buttondown() {

        System.out.println("end: " + end);
        System.out.println("running: " + running);
        System.out.println("golastcenter: " + golastcenter);
        System.out.println("blackingout: " + blackingout);
        System.out.println("pokesw: " + pokesw);
        System.out.println("switching: " + switching);
        System.out.println("WaitingForServerMessageReturn: " + WaitingForServerMessageReturn);
        
        if (WaitingForServerMessageReturn) {
        } else if (end || running) {
            if (info.nextmenu != null) {
                if (info.push())
                    info.set(info.nextmenu);
            } else if (blackingout) {
                end = false;
                golastcenter = true;
            } else {
                boolean found = false;
                for (int i = 0; i < 6; i++) {
                    if (ClientState.player.poke.belt[i] == null)
                        break;
                    if (ClientState.player.poke.belt[i].level == ClientState.player.poke.belt[i]
                            .evolvesAt()) {
                        info.set(new GMenu("What? "
                                + ClientState.player.poke.belt[i].nickName
                                + "\nis evolving!", 0, 6, 10, 3).nextmenu = new GMenu(
                                ClientState.player.poke.belt[i].nickName
                                        + " evolved\ninto "
                                        + ClientState.player.poke.belt[i]
                                                .evolvesTo().Species + "!", 0, 6,
                                10, 3));
                        Pokemon p = ClientState.player.poke.belt[i];
                        ClientState.player.poke.belt[i] = new Pokemon(
                                p.evolvesTo());
                        if (!p.nickName.equals(p.getSpecies()))
                            ClientState.player.poke.belt[i].nickName = p.nickName;
                        else
                            ClientState.player.poke.belt[i].nickName = ClientState.player.poke.belt[i]
                                    .getSpecies();
                        System.out.println("nic" + p.nickName + "nam" + p.Species);
                        ClientState.player.poke.belt[i].nickName = p.nickName;
                        ClientState.player.poke.belt[i].speedSE = p.speedSE;
                        ClientState.player.poke.belt[i].attackSE = p.attackSE;
                        ClientState.player.poke.belt[i].defenseSE = p.defenseSE;
                        ClientState.player.poke.belt[i].specialSE = p.specialSE;
                        ClientState.player.poke.belt[i].hpSE = p.hpSE;
                        ClientState.player.poke.belt[i].currentHP = ClientState.player.poke.belt[i]
                                .getTotalHP();
                        ClientState.player.poke.belt[i].location = i;
                        ClientState.player.poke.belt[i].EXP = p.EXP;
                        ClientState.player.poke.belt[i].idNo = p.idNo;
                        ClientState.player.poke.belt[i].status = p.status;
                        ClientState.player.poke.belt[i].level = p.level;
                        ClientState.player.poke.belt[i].moves = new Move[4];
                        System.out
                                .println(ClientState.player.poke.belt[i].pokeBase);
                        for (int j = 0; j < 4; j++) {
                            if (p.moves[i] == null)
                                break;
                            ClientState.player.poke.belt[i].moves[i] = new Move(
                                    p.moves[i]);
                        }

                        found = true;
                    }
                }
                if (!found) {
                    // Check server for next pokemon
                    // boolean found2 = false;
                    if (enemyPlayer != null) {
                        WaitingForServerMessageReturn = true;
                        info.set(OpponentWaitGM);
                        try {
                        	Client.writeServerMessage(new FMTSGetNextPokemon());
                        } catch(IOException ioe) {
                        	System.err.println("Failed to write Get Next Pokemon Message: " + ioe.getMessage());
                        }
                        System.out.println("PokemonGame.pokeg.writeServerMessage(new FMTSGetNextPokemon());");

                    }

                }
            }
        } else if (golastcenter) {
            golastcenter = false;
            ClientState.player.level = ClientState.player.lpclevel;
            ClientState.player.x = ClientState.player.lpcx;
            ClientState.player.y = ClientState.player.lpcy;
            ClientState.player.facing = 0;
            for (int i = 0; i < 6; i++) {
                if (ClientState.player.poke.belt[i] == null)
                    break;
                ClientState.player.poke.belt[i].currentHP = ClientState.player.poke.belt[i]
                        .getTotalHP();
                for (int j = 0; j < 4; j++) {
                    if (ClientState.player.poke.belt[i].moves[j] == null)
                        break;
                    ClientState.player.poke.belt[i].moves[j].currentpp = ClientState.player.poke.belt[i].moves[j].pp;
                }
            }
            // sendNowPokemon();
            active = false;
        } else if (blackingout) {
            GMenu c2men = new GMenu(ClientState.player.name
                    + " blacked\nout!", 0, 6, 10, 3);
            GMenu cmen = c2men;
            if (!isComputer) {
                cmen = new GMenu("You were defeated\nby " + enemyPlayer + "!",
                        0, 6, 10, 3);
                cmen.nextmenu = c2men;
            }
            info.set(cmen);
            end = true;
        } else if (pokesw) {
            boolean dead = true;
            for (int i = 0; i < 6; i++) {
                if (ClientState.player.poke.belt[i] == null)
                    break;
                if (ClientState.player.poke.belt[i].currentHP != 0) {
                    dead = false;
                    break;
                }
            }
            if (dead) {
                info.set(new GMenu(ClientState.player.name
                        + " is out of\nuseable POKeMON!", 0, 6, 10, 3));
                blackingout = true;
            } else {
                turn = true;
                info.set(new GMenu("Use next POKeMON?", 0, 6, 10, 3));
                yesnoMenuActive = true;
                pokesw = false;
            }
        } else if (switching) {
            activePokemonC = ((PokemonView) ol.o).switchto;
            out = ClientState.player.poke.belt[activePokemonC];
            out.used = true;
            info.set(new GMenu("Go! " + out.nickName + "!", 0, 6, 10, 3));
            switching = false;
            waitcompstart = true;
            turn = false;
        } else if (capture) {
            capture = false;
            info.set(new GMenu("All right!\n" + enemy.getName()
                    + " was\ncaught!\n  \nDo you want to\ngive a nickname\nto "
                    + enemy.getName() + "?", 0, 6, 10, 3));
            end = true;
        } else if (failcapture) {
            failcapture = false;
            info.set(new GMenu("Aww! It appeared\nto be caught!", 0, 6, 10, 3));
            waitcompstart = true;
            turn = false;
        } else if (exp) {
            int winxp = enemy.getWinExp(isTrainer());
            int numused = 0;
            for (int i = 0; i < 6; i++) {
                if (ClientState.player.poke.belt[i] == null)
                    break;
                if (ClientState.player.poke.belt[i].used
                        && ClientState.player.poke.belt[i].currentHP != 0)
                    numused++;
            }
            GMenu cmen = null;
            for (int i = 0; i < 6; i++) {
                if (ClientState.player.poke.belt[i] == null)
                    break;
                System.out.println("Po"
                        + ClientState.player.poke.belt[i].used);
                if (ClientState.player.poke.belt[i].used
                        && ClientState.player.poke.belt[i].currentHP != 0) {
                    ClientState.player.poke.belt[i].addEXP(enemy, numused);
                    if (cmen == null) {
                        info.set(new GMenu(ClientState.player.poke.belt[i]
                                .getName()
                                + " gained\n"
                                + (winxp / numused)
                                + " EXP. Points!", 0, 6, 10, 3));
                        cmen = info;
                        if (ClientState.player.poke.belt[i].gainedLevel) {
                            cmen.nextmenu = new GMenu(
                                    ClientState.player.poke.belt[i].getName()
                                            + " grew\nto level "
                                            + ClientState.player.poke.belt[i].level
                                            + "!", 0, 6, 10, 3);
                            cmen.nextmenu.extramenu = new StatDispMenu(
                                    ClientState.player.poke.belt[i]);
                            cmen = cmen.nextmenu;
                            ClientState.player.poke.belt[i].gainedLevel = false;
                        }
                    } else {
                        cmen.nextmenu = new GMenu(
                                ClientState.player.poke.belt[i].getName()
                                        + " gained\n" + (winxp / numused)
                                        + " EXP. Points!", 0, 6, 10, 3);
                        cmen = cmen.nextmenu;
                        if (ClientState.player.poke.belt[i].gainedLevel) {
                            cmen.nextmenu = new GMenu(
                                    ClientState.player.poke.belt[i].getName()
                                            + " grew\nto level "
                                            + ClientState.player.poke.belt[i].level
                                            + "!", 0, 6, 10, 3);
                            cmen.nextmenu.extramenu = new StatDispMenu(
                                    ClientState.player.poke.belt[i]);
                            cmen = cmen.nextmenu;
                            ClientState.player.poke.belt[i].gainedLevel = false;
                        }
                    }
                    ClientState.player.poke.belt[i].used = false;
                }
            }
            if (!isComputer) {
                cmen.nextmenu = new GMenu(
                        "You defeated \n" + enemyPlayer + "!", 0, 6, 10, 3);
            }
            end = true;
        } else if (faint) {
            info.set(new GMenu("Enemy " + enemy.getName() + "\nfainted!", 0, 6,
                    10, 3));
            exp = true;
        } else if (waitcompstart) {
            if (isComputer
                    || (astoUseE != null && astoUseE.toString().equals(
                            "Switch pokemon"))) {
                aiAttackSelection();
                ai();
            } else {
                info.set(OpponentWaitGM);
                // TODO: Fix switching!
                //PokemonGame.pokeg.writeFightMessage(FM_ATTACK_SELECTION,
                //        FM_POKEMON_SWITCHED);
/*                try {
                    PokemonGame.SendPokemonSwitch(out);
                } catch (Exception x) {
                    x.printStackTrace();
                }*/
            }
            firstTurn = enemy;
            waitcompstart = false;
            compfinished = false;
        } else if (!turn) {
            if (out.currentHP == 0) {
                info.set(new GMenu(out.nickName + "\nfainted!", 0, 6, 10, 3));
                pokesw = true;
            } else {
                if (firstTurn == enemy) {
                    info.set(new GMenu(" \n ", 0, 6, 10, 3));
                    fightMenuActive = true;
                    turn = true;
                    System.out.println("Reset");
                } else {
                    ai();
                    firstTurn = enemy;
                    System.out.println("Ai");
                }
            }
        } else if (attack != -1) {
            if (out.currentHP == 0) {
                info.set(new GMenu(out.nickName + "\nfainted!", 0, 6, 10, 3));
                pokesw = true;
            } else if (firstTurn == enemy) {
                info.set(new GMenu(out.getName() + "\nused " + toUseU.getName()
                        + "!", 0, 6, 10, 3));
                WaitingForServerMessageReturn = false;
                attack();
                System.out.println("ene");
            }
            System.out.println("att-1");
        } else {
            leaveInfo();
        }
    }

    public boolean isTrainer() {
        if (enemyPlayer == null || enemyPlayer.id == -1)
            return false;
        return true;
    }

    public void keyPressed(KeyEvent e) {
        if (ol.o.active) {
            ol.o.keyPressed(e);
            if (ol.o instanceof PokemonView) {
                PokemonView pv = (PokemonView) ol.o;
                if (pv.switchto != -1
                        && out != ClientState.player.poke.belt[pv.switchto]) {
                    if (out.currentHP == 0) {
                        fightMenuActive = true;
                        activePokemonC = ((PokemonView) ol.o).switchto;
                        out = ClientState.player.poke.belt[activePokemonC];
                        out.used = true;
                        info.set(new GMenu("", 0, 6, 10, 3)); 
                        // info.set(new GMenu("Go! "+out.name+"!", 0, 6, 10,
                        // 3)); | TODO: moved this to ReturnFightMessage delete
                        // this comment if it works
                        //waitcompstart = true;
                        //turn = false;

                        // Set next move to use to be switch
                        toUseU = Fight.SwitchPokemon;

                        // Send switching message
                        //WaitingForServerMessageReturn = true;
                        // System.out.println(" PokemonGame.pokeg.writeServerMessage(new FMTSSelectedAttack(FMTSSelectedAttack.CHANGE_POKEMON_MOVE,activePokemonC));");
                        //PokemonGame.pokeg
                        //      .writeServerMessage(new FMTSSelectedAttack(
                        //            FMTSSelectedAttack.CHANGE_POKEMON_MOVE,
                        //          activePokemonC));

                    } else {
                        info.set(new GMenu(out.nickName + " enough!\nCome back!",
                                0, 6, 10, 3));
                        switching = true;
                        fightMenuActive = false;
                        // out = ClientState.player.poke.belt[pv.switchto];
                    }
                }
            }
            return;
        } else {
            int c = e.getKeyCode();
            if (c == KeyEvent.VK_UP) {
                if (yesnoMenuActive)
                    yesnoMenu.pressUp();
                else
                    fightMenu.pressUp();
            } else if (c == KeyEvent.VK_LEFT) {
                if (yesnoMenuActive)
                    yesnoMenu.pressLeft();
                else
                    fightMenu.pressLeft();
            } else if (c == KeyEvent.VK_RIGHT) {
                if (yesnoMenuActive)
                    yesnoMenu.pressRight();
                else
                    fightMenu.pressRight();
            } else if (c == KeyEvent.VK_DOWN) {
                if (yesnoMenuActive)
                    yesnoMenu.pressDown();
                else
                    fightMenu.pressDown();
            } else if (c == KeyEvent.VK_Z) {
                if (info != null && !fightMenuActive && !yesnoMenuActive) {
                    if (info.push()) {
                        buttondown();
                    }
                } else if (!yesnoMenuActive) {
                    ((AttackSelectionMenu) fightMenu.submenus[0])
                            .setPokemon(out);
                    fightMenu.push();
                } else {
                    yesnoMenu.push();
                }
            } else if (c == KeyEvent.VK_X) {
                if (info != null && !fightMenuActive && !yesnoMenuActive) {
                    if (info.push()) {
                        buttondown();
                    }
                } else if (!yesnoMenuActive) {
                    fightMenu.pushB();
                } else {
                    yesnoMenu.pushB();
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {

        ois.defaultReadObject();

        info = new GMenu("", 0, 6, 10, 3);
        WaitingForServerMessageReturn = false;
        ol = new OverlayO();
        ola = false;
        running = false;
        fightMenuActive = false;
        yesnoMenuActive = false;
        waitcompstart = false;
        fail = false;
        switching = false;
        finishswitch = false;
        pokesw = false;
        runCount = 0;
        activePokemonE = -2;
        activePokemonC = -2;
        out = null;
        attack = -1;
        toUseU = null;
        toUseE = null;
        astoUseE = null;
        firstTurn = null;
        isComputer = true;
        turn = true;
        animate = false;
        compfinished = true;
        faint = false;
        exp = false;
        end = false;
        blackingout = false;
        capture = false;
        golastcenter = false;
        failcapture = false;

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

}
