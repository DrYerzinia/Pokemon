package com.dryerzinia.pokemon;
/*

By: ???

TODO:
Write CMD line args parser
Add port for remote control window via network
Add a CMD line option to open a control window
In control window enable observer mode

 */

import java.io.*;
import java.net.*;
import java.util.*;

import com.dryerzinia.pokemon.net.ByteInputStream;
import com.dryerzinia.pokemon.net.DatagramSocketStreamer;
import com.dryerzinia.pokemon.net.Streamer;
import com.dryerzinia.pokemon.net.TCPStreamer;
import com.dryerzinia.pokemon.net.msg.client.CMLoad;
import com.dryerzinia.pokemon.net.msg.client.ClientMessage;
import com.dryerzinia.pokemon.net.msg.client.MessageClientMessage;
import com.dryerzinia.pokemon.net.msg.client.PlayerUpdateMessage;
import com.dryerzinia.pokemon.net.msg.client.act.SendActMovedClientMessage;
import com.dryerzinia.pokemon.net.msg.client.act.SendActTalkingToClientMessage;
import com.dryerzinia.pokemon.net.msg.client.fight.SendFightClientMessage;
import com.dryerzinia.pokemon.net.msg.server.ServerMessage;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.ui.Fight;
import com.dryerzinia.pokemon.util.MysqlConnect;

public class PokemonServer {

    public static final int PORT_NUM = 53879;

    public static final int ID_PLAYER = 0;
    public static final int ID_MESSAGE = 1;
    public static final int ID_LOGIN = 2;
    public static final int ID_LOAD = 3;
    public static final int ID_GET_PLAYER = 4;
    public static final int ID_GET_POKEMON = 5;
    public static final int ID_GET_ITEM = 6;
    public static final int ID_SEND_ACT = 7;
    public static final int ID_LOG_OFF = 8;
    public static final int ID_PING = 9;
    public static final int ID_ALL_READY_LOGGED = 10;
    public static final int ID_BAD_PASSWORD = 11;
    public static final int ID_LOGIN_SUCCESS = 12;
    public static final int ID_FIGHT_MESSAGE = 13;

    public static final int CM_TCP = 0;
    public static final int CM_DATAGRAM = 1;
    public static final int CM_ALL = 2;

    private static int connectMode = CM_ALL;

    public static PokemonServer pokes;

    ServerSocket ssock;

    PokemonGame pg;

    public static ArrayList<PlayerInstanceData> players = new ArrayList<PlayerInstanceData>();

    int playeridcount = 0;

    ArrayList<ObjectOutputStream> oos2;

    public PokemonServer(boolean localized) {

        MysqlConnect.localized = localized;

        MysqlConnect.username = "root";
        MysqlConnect.password = "VrvJMUASBqsz8LtA796J";
        MysqlConnect.dbname = "Pokemon";
        MysqlConnect.server = "localhost";
        MysqlConnect.port = 3306;
        
        oos2 = new ArrayList<ObjectOutputStream>();

        System.setProperty("java.awt.headless", "false");

        pokes = this;

        pg = new PokemonGame();
        pg.run = false;
        pg.read = false;
        PokemonGame.images.setDoLoad(false);
        pg.init();
        // pg.save(new File("saveReg.dat"));

        System.out.println("Game loaded...");

        // System.out.println("Size:"+pg.level.get(pg.Char.level).g.g[0].length);
        // System.out.println("Size:"+pg.level.get(pg.Char.level).g.g[0][0].size());
        // System.out.println("Level:"+pg.Char.level);
        // System.out.println("Levels:"+pg.level.size());

        Thread t3 = new Thread(new actLoop());
        t3.start();
        Thread t4 = new Thread(new saveAllLoop());
        t4.start();
        Thread t5 = new Thread(new closeNonResponsive());
        t5.start();
        listen();

    }

    public class saveAllLoop extends Thread {

        public saveAllLoop() {
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(120000);
                    for (int i = 0; i < players.size(); i++) {
                        MysqlConnect.savePlayerData(players.get(i)
                                .getPlayer());
                    }
                } catch (Exception x) {
                }
            }
        }

    }

    public class closeNonResponsive extends Thread {

        public closeNonResponsive() {
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(30000);
                    closeNonResponse();
                } catch (Exception x) {
                }
            }
        }

    }

    public synchronized void closeNonResponse() {
        Iterator<PlayerInstanceData> itp = players.iterator();
        while (itp.hasNext()) {
            PlayerInstanceData p = itp.next();
            if (!p.hasMessageLast45()) {
                System.out.println("Player " + p.getPlayer().getName()
                        + " not responsive");
                MysqlConnect.savePlayerData(p.getPlayer());
                itp.remove();
                System.out.println("Player REMOVED!!!");
            }
        }
    }

    public class actLoop implements Runnable {
        public void run() {
            while (true) {
                Iterator<Actor> act = PokemonGame.actors.iterator();
                while (act.hasNext()) {
                    Actor a = act.next();
                    if (a.act(-256, -256)) {
                        try {
                            Iterator<PlayerInstanceData> itp = PokemonServer.players
                                    .iterator();
                            while (itp.hasNext()) {
                                PlayerInstanceData p = itp.next();
                                Person pe = (Person) a;
                                if (p.getPlayer().getLevel() == pe.level) {
                                    try {
                                        p.sendActor(pe, Person.A_MOVED);
                                    } catch (IOException ioe) {
                                        System.err
                                                .println("Send actor got IO error...");
                                    }
                                    ;
                                }
                            }
                        } catch (ConcurrentModificationException cme) {
                            System.err.println("Act had a CME");
                        }
                    }
                }
                try {
                    Thread.sleep(250);
                } catch (Exception x) {
                }
            }
        }
    }

    public synchronized static boolean isPlayer(int x, int y, int level) {
        Iterator<PlayerInstanceData> itp = players.iterator();
        while (itp.hasNext()) {
            Player p = itp.next().getPlayer();
            if (p.x + 4 == x && p.y + 4 == y && p.level == level)
                return true;
        }
        return false;
    }

    public static void main(String args[]) {
        boolean localized = false;
        if (args.length > 0 && args[0].equals("local"))
            localized = true;
        new PokemonServer(localized);
    }

    public void startTcpListen() {

        try {

            ssock = new ServerSocket();
            ssock.bind(new InetSocketAddress(PORT_NUM));

        } catch (Exception x) {
            x.printStackTrace();
        }

        (new TcpListener()).start();

    }

    public void startDatagramListen() {

        try {

            (new DatagramListener(new DatagramSocket(PORT_NUM))).start();

        } catch (Exception x) {
            System.out.println("Datagram Listener failed to start");
        }

    }

    public class TcpListener extends Thread {

        public TcpListener() {
        }

        public void run() {
            try {

                Socket sock = ssock.accept();

                Thread t = new TcpListener();
                t.start();

                Player p = getNextPlayer();

                Streamer s = new TCPStreamer(sock);

                OutputStream os = s.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);

                PlayerInstanceData pid = new PlayerInstanceData();
                pid.setPlayer(p);
                pid.setOutputStream(oos);

                Thread t2 = new listener(s, pid);
                t2.start();

                pid.setListener(t2);

            } catch (Exception x) {
                x.printStackTrace();
            }
        }

    }

    public synchronized Player getNextPlayer() {
        Player p = new Player(playeridcount, 5, 5, 0, 9, "Player");
        playeridcount++;
        return p;
    }

    public class DatagramListener extends Thread {

        public DatagramSocket ds;
        private HashMap<Integer, DatagramSocketStreamer> dss = new HashMap<Integer, DatagramSocketStreamer>();

        public DatagramListener(DatagramSocket ds) {
            this.ds = ds;
        }

        public void run() {
            DatagramPacket dp = new DatagramPacket(new byte[10000], 10000);

            try {
                while (true) {
                    ds.receive(dp);
                    ByteInputStream bis = new ByteInputStream(dp.getData());
                    int id = bis.readInt();
                    int len = bis.readInt();
                    byte[] data = new byte[len];
                    for (int i = 0; i < len; i++)
                        data[i] = (byte) bis.read();
                    DatagramSocketStreamer dssi = dss.get(new Integer(id));
                    if (dssi == null) {
                        InetAddress from = dp.getAddress();
                        int fport = dp.getPort();
                        Player p = getNextPlayer();
                        int id2 = p.getID();
                        PlayerInstanceData pid = new PlayerInstanceData();
                        pid.setPlayer(p);
                        dssi = new DatagramSocketStreamer(ds,
                                new InetSocketAddress(from, fport), id2);
                        ObjectOutputStream oos = new ObjectOutputStream(
                                dssi.getOutputStream());
                        pid.setOutputStream(oos);
                        pid.setSockID(id2);
                        sendID(dssi, oos);
                        Thread t = new listener(dssi, pid);
                        t.start();
                        pid.setListener(t);
                        dss.put(new Integer(id2), dssi);
                    } else
                        dssi.addToByteArray(data);
                }
            } catch (Exception x) {
                System.err.println("Failed to recive packet.");
                x.printStackTrace();
            }
        }

        public void sendID(DatagramSocketStreamer dss, ObjectOutputStream oos)
                throws IOException {
            oos.writeInt(dss.getID());
            oos.flush();
            System.out.println("IDSent");
        }

    }

    public void listen() {

        switch (connectMode) {
        case CM_TCP:
            startTcpListen();
            break;
        case CM_DATAGRAM:
            startDatagramListen();
            break;
        case CM_ALL:
            startTcpListen();
            startDatagramListen();
            break;
        }

    }

    public static int arrayListRemove(ArrayList al, Object o) {
        int removed = 0;
        Iterator it = al.iterator();
        while (it.hasNext()) {
            Object o2 = it.next();
            if (o2.equals(o)) {
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    public synchronized void replace(Player p) {
        boolean levelchange = false;
        Player p4 = null;
        Iterator<PlayerInstanceData> i = players.iterator();
        while (i.hasNext()) {
            Player p2 = i.next().getPlayer();
            if (p.id == p2.id) {
                if (p2.level != p.level)
                    levelchange = true;
                p2.set(p);
                Iterator<PlayerInstanceData> i2 = players.iterator();
                while (i2.hasNext()) {
                    PlayerInstanceData pid = i2.next();
                    Player p3 = pid.getPlayer();
                    if (p2 != p3 && localized(p2, p3)) { // Add localization for
                                                         // updates
                        try {
                            pid.sendPlayerUpdate(p2, false);
                        } catch (IOException x) {
                            System.err.println("Failed to Update Player");
                        }
                    } else {
                        if (levelchange) {
                            p4 = new Player();
                            p4.set(p2);
                            p4.level = -1;
                            try {
                                pid.sendPlayerUpdate(p4, false);
                            } catch (IOException x) {
                                System.err.println("Failed to Update Player");
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    public synchronized void addPlayer(PlayerInstanceData p) {
        players.add(p);
    }

    public static boolean localized(Player p1, Player p2) { // TODO: Increase
                                                            // Accuracy

        return p1.level == p2.level;

    }

    public synchronized void sendMessage(String s, PlayerInstanceData p2id) {
        Player p2 = p2id.getPlayer();
        if (s.charAt(0) == '/') {
            switch (s.charAt(1)) {
            case 'w':
                try {
                    int sp = s.indexOf(' ', 3);
                    String name = s.substring(3, sp);
                    String message = s.substring(sp, s.length());
                    Iterator<PlayerInstanceData> i = players.iterator();
                    while (i.hasNext()) {
                        PlayerInstanceData pid = i.next();
                        Player p = pid.getPlayer();
                        if (p.name.equals(name)) {
                            pid.sendMessage(p2.name + " whispers: " + message);
                        }
                    }
                } catch (Exception x) {
                }
                break;
            case 'f':
                try {
                    int sp = s.indexOf(' ', 3);
                    String name = s.substring(3, s.length());
                    if (name.equals(p2.getName()))
                        break;
                    Iterator<PlayerInstanceData> i = players.iterator();
                    while (i.hasNext()) {
                        PlayerInstanceData pid = i.next();
                        Player p = pid.getPlayer();
                        if (p.name.equals(name) && localized(p, p2)) {

                            Fight f;

                            // Confirming/Starting Fight
                            if ((f = pid.getFight()) != null) {
                                pid.isChallenger = true;
                                p2id.isChallenger = false;

                                // Set up server part of the fight
                                f.currentPlayer = p2id.getPlayer();
                                f.enemyPlayer = pid.getPlayer();
                                f.setOutPokemon(p2id.getPlayer().getFirstOut());
                                f.setEnemyPokemon(pid.getPlayer().getFirstOut());

                                f.activePokemonE = f.enemyPlayer.poke
                                        .getFirstHealthy();
                                f.activePokemonC = f.currentPlayer.poke
                                        .getFirstHealthy();

                                p2id.getPlayer()
                                        .getFirstOut()
                                        .getBase(PokemonGame.pokeg.basePokemon,
                                                PokemonGame.pokeg.baseMoves);
                                pid.getPlayer()
                                        .getFirstOut()
                                        .getBase(PokemonGame.pokeg.basePokemon,
                                                PokemonGame.pokeg.baseMoves);

                                pid.sendFightStart(p2id.getPlayer());
                                p2id.sendFightStart(pid.getPlayer());

                                // Asking to fight
                            } else {

                                f = new Fight();
                                p2id.setFight(f);
                                pid.setFight(f);

                                pid.sendMessage(p2.name + " wants to fight...");

                            }
                        }
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                    System.err.println("Failed to get player to Fight...");
                }
                break;
            }
        } else {
            Iterator<PlayerInstanceData> i = players.iterator();
            while (i.hasNext()) {
                PlayerInstanceData pid = i.next();
                Player p = pid.getPlayer();
                if (p.id != p2.id && localized(p, p2)) {
                    try {
                        pid.sendMessage(p2.name + ": " + s);
                    } catch (IOException x) {
                        System.out.println("Failed to send Message");
                    }
                }
            }
        }
    }

    public synchronized boolean isLoggedIn(Player p) {
        Iterator<PlayerInstanceData> itp = players.iterator();
        while (itp.hasNext()) {
            Player p2 = itp.next().getPlayer();
            if (p2.equals(p))
                return true;
        }
        return false;
    }

    public class listener extends Thread {

        InputStream is;
        OutputStream os;

        Streamer s;

        PlayerInstanceData p;

        boolean kill = false;

        public listener(Streamer s, PlayerInstanceData p) {
            this.s = s;
            try {
                this.is = s.getInputStream();
                this.os = s.getOutputStream();
            } catch (Exception x) {
            }
            this.p = p;
        }

        public void run() {
            Player p2 = null;
            ObjectInputStream ois = null;
            boolean loggedIn = false;
            try {
                ois = new ObjectInputStream(is);
                while (true) {
                    ServerMessage receivedMessage = (ServerMessage) ois
                            .readObject();
                    p.recivedMessage();
                    receivedMessage.proccess(ois, p);
                }
            } catch (Exception x) {
                x.printStackTrace();
            } finally {
                try {
                    // ois.close();
                    // s.close();
                } catch (Exception x) {
                }
                if (loggedIn)
                    remove(p.getPlayer());
                else
                    removeNoSave(p);
            }
        }
    }

    public synchronized void removeNoSave(PlayerInstanceData p) {
        Iterator<PlayerInstanceData> i = players.iterator();
        while (i.hasNext()) {
            PlayerInstanceData p2 = i.next();
            if (p2 == p) {
                i.remove();
                break;
            }
        }
    }

    public synchronized void remove(Player p) {
        if (p.level != -1)
            MysqlConnect.savePlayerData(p);
        Iterator<PlayerInstanceData> i = players.iterator();
        while (i.hasNext()) {
            Player p2 = i.next().getPlayer();
            if (p.getID() == p2.getID()) {
                Player p3 = new Player();
                p3.set(p2);
                p3.level = -1;
                replace(p3);
                i.remove();
                break;
            }
        }
    }

    public static class PlayerInstanceData {

        public Player p;
        private int sockID = -1;
        private long lastRecivedMessageTime = 0;
        private Thread listeningThread;
        private ObjectOutputStream oos;
        private boolean loggedIn = false;
        private Fight f = null;
        private boolean isChallenger = false;

        public PlayerInstanceData() {
        }

        public PlayerInstanceData(Player p, long l, Thread t) {
            this.p = p;
            lastRecivedMessageTime = l;
            Thread listeningThread = t;
        }

        boolean hasMessageLast45() {
            if (System.currentTimeMillis() - 45000 > lastRecivedMessageTime)
                return false;
            return true;
        }

        public void stop() {
            listeningThread.stop();
        }

        public Player getPlayer() {
            return p;
        }

        public void setPlayer(Player p) {
            this.p = p;
        }

        public boolean isLoggedIn() {
            return loggedIn;
        }

        public boolean isChallenger() {
            return isChallenger;
        }

        public void setSockID(int id) {
            int sockID = id;
        }

        public int getSockID() {
            return sockID;
        }

        public void setFight(Fight f) {
            this.f = f;
        }

        public Fight getFight() {
            return f;
        }

        public void setListener(Thread t) {
            listeningThread = t;
        }

        public void setLoggedIn(boolean li) {
            loggedIn = li;
        }

        public void recivedMessage() {
            lastRecivedMessageTime = System.currentTimeMillis();
        }

        public void setOutputStream(ObjectOutputStream oos) {
            this.oos = oos;
        }

        public void setIsChallenger(boolean chal) {
            isChallenger = chal;
        }

        public synchronized void writeClientMessage(ClientMessage cm) {
            try {
                oos.writeObject(cm);
                oos.flush();
                oos.reset();
            } catch (Exception x) {
                x.printStackTrace();
            }
        }

        public synchronized void sendActor(Person p, int activity)
                throws IOException {
            if (activity == Person.A_TALKING_TO)
                oos.writeObject(new SendActTalkingToClientMessage(p.id, p.x,
                        p.y, p.dir, p.level, p.onClick.getActive()));
            else
                oos.writeObject(new SendActMovedClientMessage(p.id, p.x, p.y,
                        p.dir, p.level));
            oos.flush();
            oos.reset();
        }

        public synchronized void sendPlayerUpdate(Player p, boolean self)
                throws IOException {
            oos.writeObject(new PlayerUpdateMessage(p, self));
            oos.flush();
            oos.reset();
        }

        public synchronized void sendMessage(String message) throws IOException {
            oos.writeObject(new MessageClientMessage(message));
            oos.flush();
            oos.reset();
        }

        public synchronized void sendCommand(int command) throws IOException {
            oos.writeInt(command);
            oos.flush();
        }

        public synchronized void sendLoad() throws IOException {
            int ids[] = new int[6];
            for (int i = 0; i < 6; i++) {
                if (p.poke.belt[i] != null)
                    ids[i] = p.poke.belt[i].idNo;
                else
                    ids[i] = -1;
            }

            oos.writeObject(new CMLoad(ids, p.poke.box, p.items));
            oos.flush();
            oos.reset();

        }

        public synchronized void sendFightStart(Player enemyPlayer)
                throws IOException {

            // Create a new fight
            Fight f = new Fight();

            // Set the other guy to the enemy player
            f.enemyPlayer = enemyPlayer;

            // set the number of pokemon and the enemy
            f.pokemonCountE = 0;
            f.activePokemonE = -1;
            // Count pokemon
            for (int i = 0; i < 6; i++) {
                if (enemyPlayer.poke.belt[i] == null)
                    break;
                if (f.activePokemonE == -1
                        && enemyPlayer.poke.belt[i].currentHP > 0) {
                    f.activePokemonE = i; // Set first slot
                    f.enemy = enemyPlayer.poke.belt[i]; // Set Enemy Pokemon
                }
                f.pokemonCountE++;
            }

            // Set self to Well self
            f.currentPlayer = p;

            // Send the fight to the opponent
            oos.writeObject(new SendFightClientMessage(f));

            oos.flush();
            oos.reset();
        }

        public synchronized void SendAttackSelection(int sel)
                throws IOException {
            oos.writeInt(ID_FIGHT_MESSAGE);
            oos.writeInt(Fight.FM_ATTACK_SELECTION);
            oos.writeInt(sel);
            oos.flush();
        }

        public synchronized void SendFightMessage(int type, int message)
                throws IOException {
            oos.writeInt(ID_FIGHT_MESSAGE);
            oos.writeInt(type);
            oos.writeInt(message);
            oos.flush();
        }

        public synchronized void SendDamageCheck(int outDamage, int enemyDamage)
                throws IOException {
            oos.writeInt(ID_FIGHT_MESSAGE);
            oos.writeInt(Fight.FM_DAMAGE_CHECK);
            oos.writeInt(outDamage);
            oos.writeInt(enemyDamage);
            oos.flush();
        }

        public synchronized void SendPokemonSwitch(Pokemon p)
                throws IOException {
            oos.writeInt(ID_FIGHT_MESSAGE);
            oos.writeInt(Fight.FM_POKEMON_SWITCH);
            oos.writeObject(p);
            oos.flush();
        }

    }
}
