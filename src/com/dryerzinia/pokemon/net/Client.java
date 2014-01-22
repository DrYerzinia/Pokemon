package com.dryerzinia.pokemon.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import com.dryerzinia.pokemon.PokemonServer;
import com.dryerzinia.pokemon.net.msg.client.ClientMessage;
import com.dryerzinia.pokemon.net.msg.server.GetItemServerMessage;
import com.dryerzinia.pokemon.net.msg.server.GetPokemonServerMessage;
import com.dryerzinia.pokemon.net.msg.server.MessageServerMessage;
import com.dryerzinia.pokemon.net.msg.server.PingServerMessage;
import com.dryerzinia.pokemon.net.msg.server.PlayerServerMessage;
import com.dryerzinia.pokemon.net.msg.server.SMLoad;
import com.dryerzinia.pokemon.net.msg.server.SMLogOff;
import com.dryerzinia.pokemon.net.msg.server.SMLogin;
import com.dryerzinia.pokemon.net.msg.server.ServerMessage;
import com.dryerzinia.pokemon.net.msg.server.act.SendActMovedServerMessage;
import com.dryerzinia.pokemon.net.msg.server.act.SendActTalkingToServerMessage;
import com.dryerzinia.pokemon.obj.Actor;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Person;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.dryerzinia.pokemon.ui.Login;

public final class Client {

    private static int connectMode = PokemonServer.CM_TCP;

    private static ObjectOutputStream object_output_stream_to_server;
    private static Socket socket_to_server;

    public static Timer pinger;

    /*
     * Don't let anyone create instances of this class
     */
    private Client(){}

    /**
     * Listen for messages from server
     */
    public static final class MessageListener extends Thread {

        ObjectInputStream ois;

        public MessageListener(ObjectInputStream ois) {
            this.ois = ois;
        }

        public void run() {

            try {
                while (true) {
                    try {

                        ClientMessage receivedMessage = (ClientMessage) ois
                                .readObject();
                        receivedMessage.proccess();

                    } catch (Exception x) {
                        x.printStackTrace();
                        break;
                    }
                }

            } catch (Exception x) {

                x.printStackTrace();

            } finally {

                // ois.close();

            }
        }
    }

    public static final class PingerTask extends TimerTask {
        public void run() {

        	writePing();
            System.out.println("Pinged Server");

        }
    }

    public static void initTCPConnect() throws Exception {

        SocketAddress address = null;
        InetSocketAddress inet = null;

        java.net.Proxy proxy = null;
        boolean use_proxy = false;

        /*
         * Default Connection settings
         */
        String proxyHost = "localhost";
        int proxyPort = 9050;

        String host = "5vddatjhjhvybqwo.onion";
        int port = PokemonServer.PORT_NUM;

        /*
         * Tokenize location configuration from Login Menu
         */
        StringTokenizer st = new StringTokenizer(Login.location, "|:");

        host = st.nextToken();
        if (st.hasMoreTokens()) {
            port = Integer.parseInt(st.nextToken());
        }
        if (st.hasMoreTokens()) {
            use_proxy = true;
            proxyHost = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            proxyPort = Integer.parseInt(st.nextToken());
        }

        /*
         * Default to no proxy
         */
        proxy = Proxy.NO_PROXY;

        /*
         * Configure server INet address
         */
        if (use_proxy) {

        	/*
        	 * Setup proxy configuration
        	 */
            address = new InetSocketAddress(proxyHost, proxyPort);
            proxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS, address);

            inet = InetSocketAddress.createUnresolved(host, port);

        } else {

            inet = new InetSocketAddress(host, port);

        }

        socket_to_server = new Socket(proxy);

        socket_to_server.connect(inet);

        object_output_stream_to_server = new ObjectOutputStream(socket_to_server.getOutputStream());

        object_output_stream_to_server.writeObject(new SMLogin(Login.username, Login.password));
        object_output_stream_to_server.flush();

        ObjectInputStream ois = new ObjectInputStream(socket_to_server.getInputStream());

        new MessageListener(ois).start();

    }

    public static final class DatagramListener extends Thread {

        public DatagramSocket datagram_socket;
        private DatagramSocketStreamer datagram_socket_streamer;

        public DatagramListener(DatagramSocket datagram_socket, DatagramSocketStreamer datagram_socket_streamer) {

        	this.datagram_socket = datagram_socket;
            this.datagram_socket_streamer = datagram_socket_streamer;

        }

        public void run() {

        	DatagramPacket dp = new DatagramPacket(new byte[10000], 10000);

            try {

            	while(true){

                	datagram_socket.receive(dp);

                	ByteInputStream bis = new ByteInputStream(dp.getData());

                	/* Throw away read of ID
                	 * TODO Should we even send this int???
                	 */
                	bis.readInt();
                    int len = bis.readInt();

                    byte[] data = new byte[len];

                    for (int i = 0; i < len; i++)
                        data[i] = (byte) bis.read();

                    bis.close();

                    datagram_socket_streamer.addToByteArray(data);

            	}

            } catch (IOException ioe) {

            	System.err.println("Failed to recieve datagram packet: " + ioe.getMessage());
            	System.err.println("Attempting to reconnect...");
            	
            	// TODO reconnect

            }
        }
    }

    /*
    private static class ReconnectTask extends TimerTask{

    	int failed;

    	Timer self;

    	public ReconnectTask(Timer self){

    		this.self = self;
    		failed = 0;

    	}

        public void run() {

        	UI.disableInput();
        	UI.drawReconnect();

        	if(success){

        		//

        	} else if(failed >= 6) {

        		UI.enableInput();
        		// Show login

            } else {

            	self.schedule(this, 5000);

            }

           	failed++;

        }
    }*/

    /**
     * Initiates network connection to server is selected mode
     * @throws Exception
     */
    public static void startConnect() throws Exception {
    	switch (connectMode) {
        case PokemonServer.CM_TCP:
            initTCPConnect();
            break;
        case PokemonServer.CM_DATAGRAM:
            initDatagramConnect();
            break;
        }
    }

    public static void initDatagramConnect() throws Exception {

        DatagramSocket ds = new DatagramSocket();
        InetAddress loc = InetAddress.getByName(Login.location);
        ds.connect(loc, PokemonServer.PORT_NUM);

        DatagramSocketStreamer dss = new DatagramSocketStreamer(ds,
                new InetSocketAddress(loc, PokemonServer.PORT_NUM), -1);

        DatagramInputStream dis = (DatagramInputStream) dss.getInputStream();
        DatagramOutputStream dos = (DatagramOutputStream) dss.getOutputStream();

        (new DatagramListener(ds, dss)).start();

        dos.flush();

        ObjectInputStream ois = new ObjectInputStream(dis);

        int id = ois.readInt();
        dss.setID(id);

        (new MessageListener(ois)).start();


    }

    /**
     * All the outputs to the server class, Synchronized to insure no stream
     * corruption from possible sending of one message interrupting another
     */
    public static synchronized void writeItem(Item item) {

    	try {

    		object_output_stream_to_server.writeObject(new GetItemServerMessage(item));
    		object_output_stream_to_server.flush();

    	} catch (IOException ioe) {

    		System.err.println("Write Item Failed: " + ioe.getMessage());

    	}

    }

    /**
     * Requests players Pokemon and Items
     */
    public static synchronized void writeLoadMessage() {

    	try {

    		object_output_stream_to_server.writeObject(new SMLoad());
        	object_output_stream_to_server.flush();

    	} catch(IOException ioe) {

    		System.err.println("Write Item Failed: " + ioe.getMessage());

        }
    }

    /**
     * Send your pokemon to server to be updated
     * TODO this should be removed, all fights occur on the server
     * so it should always know correct Pokemon status
     */
    public static synchronized void writePokemon(Pokemon pokemon) {

    	try {

        	object_output_stream_to_server.writeObject(new GetPokemonServerMessage(pokemon));
        	object_output_stream_to_server.flush();

        } catch(IOException ioe) {

        	System.err.println("Write Pokemon Failed: " + ioe.getMessage());

        }
    }

    /**
     * Tell the server you are loging off
     */
    public static synchronized void writeLogoff() {

    	try {

    		object_output_stream_to_server.writeObject(new SMLogOff());
        	object_output_stream_to_server.flush();

    	} catch (IOException ioe) {

    		System.err.println("Write Logoff Failed: " + ioe.getMessage());

    	}
    }

    /**
     * Write a ping to the server so it knows we are still here and
     * dosen't disconnect us.
     * TODO why are we reseting the stream here?  Because of possible errors
     * that can be rectified on regular basis???  in that case it should be in
     * a Excpetion handler probably.  Keep Eye out for this type of exception.
     */
    public static synchronized void writePing() {

    	try {

    		object_output_stream_to_server.writeObject(new PingServerMessage());
        	object_output_stream_to_server.flush();
        	object_output_stream_to_server.reset();

    	} catch (IOException ioe) {

    		System.err.println("Write PING Failed: " + ioe.getMessage());

    	}
    }

    /**
     * Send a message from the Chat window to the server
     */
    public static synchronized void writeMessage(String msg) {

    	try {

    		object_output_stream_to_server.writeObject(new MessageServerMessage(msg));
    		object_output_stream_to_server.flush();

        } catch (IOException ioe) {

        	System.err.println("Write Message Failed: " + ioe.getMessage());

        }
    }

    /**
     * Generic server message write
     * TODO this may be completely unused but could also be use to replace
     * all the other messages and likely move error handling to a more
     * appropriate place
     */
    public static synchronized void writeServerMessage(ServerMessage sm) throws IOException {

        	object_output_stream_to_server.writeObject(sm);
        	object_output_stream_to_server.flush();

    }

    /**
     * Write the current player to the server
     * TODO once again server should always know current player status
     * this may be for position updates however in which case its IMPORTANT
     * and we need to make sure there is no funny buisness going on and check
     * that the player is making legal moves server side
     * TODO WHY RESET!!!
     */
    public static synchronized void writePlayer() {

    	try {

    		object_output_stream_to_server.writeObject(new PlayerServerMessage(Player.self));
        	object_output_stream_to_server.flush();
        	object_output_stream_to_server.reset();

    	} catch (IOException ioe) {

    		System.err.println("Write Player Failed: " + ioe.getMessage());

    	}
    }

    /**
     *  Tell server that you have engaged the actor in some activity
     *  TODO Juan has suggested that one might engage an important
     *  quest useful actor in constant conversation to screw other players
     *  over and we need to address that, also the possibility of actors
     *  blocking doors!
     *  TODO WHY RESET!!!
     */
    public static synchronized void writeActor(Actor actor, int activity) {

    	try {

    		Person person = (Person) actor;

    		/*
    		 * Inform server that the client has started
    		 * talking to a Actor
    		 */
    		if (activity == Person.A_TALKING_TO)
            	object_output_stream_to_server.writeObject(
            		new SendActTalkingToServerMessage(
            			person.id,
            			person.x,
                        person.y,
                        person.dir,
                        person.level,
                        person.onClick.getActive()
                    )
            	);

    		/*
    		 * TODO
    		 * Why would the client know about a ACTOR moving if the
    		 * server didn't TELL THEM!!!
    		 */
    		else
            	object_output_stream_to_server.writeObject(
            		new SendActMovedServerMessage(
            			person.id,
            			person.x,
            			person.y,
                        person.dir,
                        person.level
                    )
            	);

            object_output_stream_to_server.flush();
            object_output_stream_to_server.reset();

        } catch (IOException ioe) {

        	System.err.println("Write Actor Failed: " + ioe.getMessage());

        }
    }


}
