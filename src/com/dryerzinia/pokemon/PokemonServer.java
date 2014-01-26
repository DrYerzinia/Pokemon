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

import com.dryerzinia.pokemon.map.Direction;
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
import com.dryerzinia.pokemon.net.msg.client.act.SendPerson;
import com.dryerzinia.pokemon.net.msg.client.fight.SendFightClientMessage;
import com.dryerzinia.pokemon.net.msg.server.ServerMessage;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.GameState;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.ui.Fight;
import com.dryerzinia.pokemon.util.MysqlConnect;
import com.dryerzinia.pokemon.util.ResourceLoader;

public class PokemonServer {

	// TODO possibly multiple ports with per port mode settings in file
    public static final int PORT_NUM = 53879;

    public static final int CM_TCP = 0;
    public static final int CM_DATAGRAM = 1;
    public static final int CM_ALL = 2;

    private static int connectMode = CM_ALL;

    public static PokemonServer pokes;

    ServerSocket ssock;

    public static ArrayList<PlayerInstanceData> players = new ArrayList<PlayerInstanceData>();

    int playeridcount = 0;

    ArrayList<ObjectOutputStream> oos2;

    Timer server_tasks;

    /**
     * Entry point for Pokemon game server
     * @param args
     */
    public static void main(String args[]) {
        boolean localized = false;
        // TODO better argument parsing
        if (args.length > 0 && args[0].equals("local"))
            localized = true;
        new PokemonServer(localized);
    }
    
    public PokemonServer(boolean localized) {

        MysqlConnect.localized = localized;

        MysqlConnect.username = "pokemon";
        MysqlConnect.password = "UhSz6NKQVhN7ByWuzRug";
        MysqlConnect.dbname = "Pokemon";
        MysqlConnect.server = "localhost";
        MysqlConnect.port = 3306;
        
        oos2 = new ArrayList<ObjectOutputStream>();

        // We have to pretend to have a head for the Pokemon
        // Game instance to be created
        System.setProperty("java.awt.headless", "false");

        // Probably do some other thing for this singleton
        pokes = this;

        // Don't need to load images for headless server
        ResourceLoader.setDoLoad(false);

        // Game Instance Data
        GameState.init();

        // Load Actors
        GameState.loadActors("actors.json");

        System.out.println("Game World Instace Created");

        
        server_tasks = new Timer();

        server_tasks.schedule(new ActTask(), 0, 250);
        server_tasks.scheduleAtFixedRate(new SaveAllTask(), 0, 120000);
        server_tasks.scheduleAtFixedRate(new KickInactiveTask(), 0, 30000);

        listen();

    }

    /**
     * Saves all of the information of connected players every 2 minutes
     * in case server crashes
     */
    public class SaveAllTask extends TimerTask {
    	public void run() {
        	for (PlayerInstanceData pid : players)
        		MysqlConnect.savePlayerData(pid.getPlayer());
        }
    }
    
    /**
     * Kicks any players who we have not received a message from in the last
     * 45 seconds.  Player should send pings every 15 seconds to insure they
     * stay connected.  This helps remove UDP Clients who have disconnected
     * without saying anything.  This taks runs once every 30 seconds so the
     * longest a player can be disconnected and not removed is 1:15
     */
    public class KickInactiveTask extends TimerTask {
        public void run() {
        	kickInactive();
        }
    }

    /**
     * Removes players who have not sent a message in the last 45 seconds
     */
    public synchronized void kickInactive() {

    	Iterator<PlayerInstanceData> pid_iterator = players.iterator();

    	while(pid_iterator.hasNext()) {
            
    		PlayerInstanceData pid = pid_iterator.next();

    		/* Players who have not send a message in the last 45 seconds
    		 * Are removed from the game, They should ping at least once
    		 * every 30 seconds to show they are Connected particularly
    		 * for UDP connections this is important.
    		 */
            if (!pid.hasMessageLast45()) {

            	System.out.println("Player " + pid.getPlayer().getName()
                        + " not responsive");
                
                MysqlConnect.savePlayerData(pid.getPlayer());

                // Remove PlayerInstanceData from server
                pid_iterator.remove();
                // TODO Proper disconnect and killing of all threads
                System.out.println("Player kicked!");

            }
        }
    }

    /**
     * Runs AI for all Actors in the game world and updates players near them
     * of their actions.  AI runs ~4 times per second.
     */
    public class ActTask extends TimerTask {
        public void run() {

        	for(Person person: GameState.people)
       			sendPlayerActorUpdate(person, person.act());

        }
    }

    /**
     * Synchronized because PlayerInstanceData iteration
     * @param person Actor that changed and needs to be updated
     * TODO all actors are persons???
     */
    public synchronized static void sendPlayerActorUpdate(Person person, boolean changed){

		for(PlayerInstanceData pid : players) {
            if (pid.getPlayer().getLevel() == person.level)
				try {

					if(!pid.hasSeen(person)){
						pid.saw(person);
						pid.writeClientMessage(new SendPerson(person));
					}

					if(changed)
						pid.sendActor(person, Person.A_MOVED);

				} catch (IOException ioe) {
					System.err.println("Could not send actor update to player: " + ioe.getMessage());
					// TODO likely cause client is disconnected we should probably remove them here
				}
        }

    }

    /**
     * TODO figure out what the fuck this is for
     * @param x
     * @param y
     * @param level
     * @return
     */
    public synchronized static boolean isPlayer(int x, int y, int level) {
       for(PlayerInstanceData pid : players) {
            Player player = pid.getPlayer();
            if(player.x + 4 == x
            && player.y + 4 == y
            && player.level == level)
                return true;
        }
        return false;
    }

    /**
     * Binds TCP to the server socket and starts accepting
     * connections from clients
     */
    public void startTCPListen() {

        try {

            ssock = new ServerSocket();
            ssock.bind(new InetSocketAddress(PORT_NUM));

            (new TCPListener()).start();

        } catch (BindException be) {

        	System.err.println("Unable to bind to port: " + be.getMessage());
        	System.err.println("Not listening on TCP!");

        } catch (IOException ioe){

        	ioe.printStackTrace();
        }

    }

    /**
     * Binds UDP to the Server Socket and starts listening for 
     * packets from clients
     */
    public void startDatagramListen() {

        try {

            (new DatagramListener(new DatagramSocket(PORT_NUM))).start();

        } catch (SocketException se) {

        	System.err.println("Could not attach to UDP Socket: " + se.getMessage());
        	System.err.println("Not listening on UDP!");

        }

    }


    /**
     * Thread for communicating with Client
     */
    public class MessageListener extends Thread {

        InputStream is;
        OutputStream os;

        Streamer streamer;

        PlayerInstanceData pid;

        boolean kill = false;

        public MessageListener(Streamer streamer, PlayerInstanceData pid) {

        	this.streamer = streamer;

            try {

            	this.is = streamer.getInputStream();
                this.os = streamer.getOutputStream();

            } catch (IOException ioe) {

            	System.err.println("Could not open Stream to/from client.");

            }

            this.pid = pid;

        }

        /**
         * Closes the socket and ends this thread
         */
        public void stop_listening(){

        	try {

        		streamer.close();

        	} catch(IOException ioe){

        		System.err.println("Unable to close listening thread: " + ioe.getMessage());

        	}

        }
        
        /**
         * Run loop for listening to messages from client and processing them
         */
        public void run() {

            try (ObjectInputStream ois = new ObjectInputStream(is)) {

            	while(true){

            		ServerMessage receivedMessage = (ServerMessage) ois.readObject();
                    pid.recivedMessage();
                    receivedMessage.proccess(ois, pid);

            	}

            } catch(SocketException se) {

            	// Safe disconnect should be an socket close interupt

            } catch (EOFException eofe) {

            	System.out.println("EOF Disconnect, thats not very nice!");

            } catch (IOException ioe) {

            	System.err.println("IOException occured!");
            	System.err.println("Client " + pid.getPlayer().getName() + "disconnected!");

            	ioe.printStackTrace();

            } catch (ClassNotFoundException cnfe) { 

            	System.err.println("Received unknown message from Client:");

            	cnfe.printStackTrace();

            } finally {

            	System.out.println("Client " + pid.getPlayer().getName() + " disconnected");
            	
            	// Remove player from list of connected players
                if (pid.isLoggedIn())
                	// Save player
                    remove(pid.getPlayer());
                else
                	// Don't save anything for players who never logged in successfully
                    removeNoSave(pid);
            }
        }
    }
    
    /**
     * This thread waits for a socket connections on TCP
     * then splits off a new socket acceptor and starts handling
     * messages from the client
     */
    public class TCPListener extends Thread {

    	Streamer streamer;

        public TCPListener() {
        }

        public void run() {
        	
        	try {

                Socket sock = ssock.accept();

                Thread tcp_client_listener = new TCPListener();
                tcp_client_listener.start();

                streamer = new TCPStreamer(sock);

                try {

                    /* Create's new player instance data and sets a player object
                	 * for it and the output stream to send messages on
                	 */
                    PlayerInstanceData pid = new PlayerInstanceData();
                    pid.setPlayer(getNextPlayer());
                    pid.setOutputStream(new ObjectOutputStream(streamer.getOutputStream()));

                    /* Starts listening for messages from the client
                     */
                    MessageListener message_listener = new MessageListener(streamer, pid);
                    message_listener.start();

                    /* sets a reference to the message listener so it can be
                     * killed when we need to remove this player
                     */
                    pid.setListener(message_listener);
                    
                } catch (IOException ioe) {
                    /* We likely lost connection to player during accept
                     * player is added during login messages so no need to
                     * clean player up
                     */
                    System.err.println("Failed to open streams to new client: " + ioe.getMessage());
                }
                
            } catch(IOException ioe){

            	System.err.println("Error occured while waiting for connection.");
            	System.err.println("We are no longer listening on TCP.");

            } catch(SecurityException se){

            	System.err.println("Not allowed to accept on this socket.");
            	System.err.println("We are no longer listening on TCP.");

            }

        }

    }

    /**
     * Thread listens for datagram packets and routes them to the
     * ObjectInputStream of the appropriate client
     * TODO clients can easily hijack other datagram clients connections
     * crashing them.  Add some kind of authentication
     */
    public class DatagramListener extends Thread {

    	/* Socket to listen for UPD packets on
    	 * TODO why public?
    	 */
        public DatagramSocket ds;

        /* HashMap of connected clients for routing incoming packets
         */
        private HashMap<Integer, DatagramSocketStreamer> dss = new HashMap<Integer, DatagramSocketStreamer>();

        public DatagramListener(DatagramSocket ds) {

        	this.ds = ds;

        }

        public void run() {

        	DatagramPacket dp = new DatagramPacket(new byte[10000], 10000);

        	while(true) {

        		try {
        		
        			ds.receive(dp);

	        		try (ByteInputStream bis = new ByteInputStream(dp.getData())) {
	
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
	
	                        ObjectOutputStream oos = new ObjectOutputStream(dssi.getOutputStream());
	
	                        pid.setOutputStream(oos);
	                        pid.setSockID(id2);
	
	                        sendID(dssi.getID(), oos);
	
	                        MessageListener message_listener = new MessageListener(dssi, pid);
	                        message_listener.start();
	                        pid.setListener(message_listener);
	
	                        dss.put(new Integer(id2), dssi);
	
	                    } else {
	                     
	                    	dssi.addToByteArray(data);
	                    
	        			}
	        		
	        		} catch(IOException ioe){
	        			
	        			System.err.println("Error while reading Packet: " + ioe.getMessage());
	
	        		}

        		} catch(IOException ioe){

        			System.err.println("Failed to read datagram packet: " + ioe.getMessage());
        			// TODO we may have lost the socket here and need to do some resetting

        		}
        	}
        }
        
        /**
         * sends the client its Assigned ID when it first connects
         * @param id ID of new client
         * @param oos Output stream to send ID over
         * @throws IOException
         */
        public void sendID(int id, ObjectOutputStream oos) throws IOException {
            oos.writeInt(id);
            oos.flush();
        }

    }

    /**
     * starts listening for connections to server
     */
    public void listen() {

        switch (connectMode) {
        case CM_TCP:
            startTCPListen();
            break;
        case CM_DATAGRAM:
            startDatagramListen();
            break;
        case CM_ALL:
            startTCPListen();
            startDatagramListen();
            break;
        }

    }

    /**
     * Creates new player with instance ID auto incremented
     * but dosen't add to master list
     */
    public synchronized Player getNextPlayer() {
        playeridcount++;
        return new Player(playeridcount, 5, 5, Direction.UP, 9, "Player");
    }
    
    /**
     * Adds player instance data to master list for client management
     * @param pid PlayerInstanceData of new player to add to master list
     */
    public synchronized void addPlayer(PlayerInstanceData pid) {
        players.add(pid);
    }

    /**
     * Sends update information for a player to all clients
     * if the player is near them or was near them and has moved
     * @param to_replace player to change
     * TODO as of right now this function is only called to remove 
     * players figure out what other usage it should have
     * we are likely updating players in a message in a non thread
     * safe way!!!
     */
    public synchronized void replace(Player to_replace) {

    	boolean levelchange = false;

        for(PlayerInstanceData pid : players) {

        	Player player = pid.getPlayer();

        	// Found the player to replace
        	if (player.getID() == to_replace.getID()) {

        		// If there level changed we make a note of that
        		if (player.level != to_replace.level)
                    levelchange = true;

        		// set the player in the master list equal to the replace player
        		player.set(to_replace);

        		// for all the other players
                for(PlayerInstanceData pid2 : players) {

                	// If the player is near them update
                    Player p3 = pid2.getPlayer();
                    if (player != p3 && localized(player, p3)) { // Add localization for
                                                         // updates
                        try {
                            pid.sendPlayerUpdate(player, false);
                        } catch (IOException x) {
                            System.err.println("Failed to Update Player");
                        }

                    // If he left where they where update them that he is gone
                    } else if (levelchange) {

                        Player p4 = new Player();

                        p4.set(player);
                        p4.level = -1;

                        try {
                            pid.sendPlayerUpdate(p4, false);
                        } catch (IOException x) {
                            System.err.println("Failed to Update Player");
                        }

                    }
                }
                break;
            }
        }
    }

    /**
     * Checks to see if two players are in the same area
     * @param p1 first player
     * @param p2 second player
     * @return if they are in same area returns true else false
     * TODO Increase the accuracy of this method to an actual Manhattan
     * distance
     */
    public static boolean localized(Player p1, Player p2) {

        return p1.level == p2.level;

    }

    /**
     * Thread safe method called from MesageServerMessage to send 
     * any messages to the appropriate players based on there relative
     * locations and the message type 
     * @param s the message
     * @param p2id the player who sent the message
     */
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
                                        .getBase();
                                pid.getPlayer()
                                        .getFirstOut()
                                        .getBase();

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

    /**
     * Checks to see if a player is already logged in via
     * username comparison
     * @param p
     * @return
     */
    public synchronized boolean isLoggedIn(Player logged_in) {
        for(PlayerInstanceData pid : players) {
        	if(pid.getPlayer().equals(logged_in))
                return true;
        }
        return false;
    }

    /**
     * Removes kicked or disconnected player from master list
     * @param to_remove player to remove from master list
     */
    public synchronized void remove(Player to_remove) {
    	/* If the player is not logged in there level will be -1
    	 * so we check for this, we only want to save changes if
    	 * they where logged in
    	 */
        if (to_remove.level != -1)
            MysqlConnect.savePlayerData(to_remove);

        Iterator<PlayerInstanceData> pid_iterator = players.iterator();

        while(pid_iterator.hasNext()){

        	Player player = pid_iterator.next().getPlayer();

        	/* If the ID's are equal we found the player we are
        	 * removing from the master list
        	 */
        	if (player.getID() == to_remove.getID()) {

        		Player p3 = new Player();
                p3.set(player);
                p3.level = -1;

                replace(p3);

                pid_iterator.remove(); // Remove player from master list

                break;

        	}
        }
    }
    
    /**
     * Removes a player from the master list without saving there status
     * @param pid player to remove
     */
    public synchronized void removeNoSave(PlayerInstanceData pid) {

    	Iterator<PlayerInstanceData> pid_iterator = players.iterator();

    	while(pid_iterator.hasNext()) {
        
    		PlayerInstanceData pid2 = pid_iterator.next();

    		// Removes the PlayerInstanceData for the player
            if (pid == pid2) {
                pid_iterator.remove();
                break;
            }

    	}
    }
    
    /**
     * Contains everything the server needs to keep track of players and
     * send messages to them
     */
    public static class PlayerInstanceData {

        public Player p;
        private int sockID = -1;
        private long lastRecivedMessageTime = 0;
        private MessageListener message_listener;
        private ObjectOutputStream oos;
        private boolean loggedIn = false;
        private Fight f = null;
        private boolean isChallenger = false;

        private HashMap<Integer, Person> peopleSeen = new HashMap<Integer, Person>();

        public PlayerInstanceData() {
        }

        public PlayerInstanceData(Player player, long last_message_recived_time, MessageListener message_listener) {
            this.p = player;
            this.lastRecivedMessageTime = last_message_recived_time;
            this.message_listener = message_listener;
        }

        boolean hasMessageLast45() {
            if (System.currentTimeMillis() - 45000 > lastRecivedMessageTime)
                return false;
            return true;
        }

        public void stop() {
            message_listener.stop_listening();
        }

        public boolean hasSeen(Person person){
        	return peopleSeen.containsKey(person.id);
        }

        public void saw(Person person){
        	peopleSeen.put(person.id, person);
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
            sockID = id;
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

        public void setListener(MessageListener message_listener) {
            this.message_listener = message_listener;
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
                oos.writeObject(new SendActTalkingToClientMessage(p.id, (int)p.x,
                		(int)p.y, p.dir, p.level, p.onClick.getActive()));
            else
                oos.writeObject(new SendActMovedClientMessage(p.id, (int)p.x, (int)p.y,
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

    }
}
