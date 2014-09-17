package com.dryerzinia.pokemon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Move;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokeball;
import com.dryerzinia.pokemon.obj.Pokemon;

/**
 * Interface for connecting to and performing operations
 * on the database.
 * @author jc
 *
 */
public class Database {
	private static final Logger LOG =
			Logger.getLogger(Database.class.getName());
	
	private enum DBMS {
		MYSQL("mysql"), 
		SQLITE("sqlite");
		
		private DBMS(String name) {
			this.name = name;
		}
		
		private static DBMS dbmsFromString(String s) {
			for (DBMS dbms : DBMS.values()) {
				if (dbms.name.equalsIgnoreCase(s)) 
					return dbms;
			}
			return null;
		}
		
		private String name;
	}
	private static DBMS dbms;
	private static String dbname;
	
	private static String username;
	private static String password;
	private static String host;
	private static int port;
	
	private static final String dbPropComments =
			"This is the database properties file.\n"
			+ "You need to provide the dbms (mysql or sqlite) and "
			+ "the dbname.\nIf you are using mysql, you also need to "
			+ "provide a host, port, username, and password.\n";
	
	private static void writeDefaultPropertiesFile(File file) {
		Properties dbProperties = new Properties();
		try (OutputStream out = new FileOutputStream(file)) {
			dbProperties.setProperty("dbms", "sqlite");
			dbProperties.setProperty("dbname", "");
			dbProperties.store(out, dbPropComments);
			LOG.info("Writing default property file.");
			System.out.println("Wrote default properties file; "
					+ "you will need to make changes to connect to the database.");
		} catch (Exception e) {
			String errMsg = "Error writing defaults to properties file!";
			LOG.severe(errMsg);
			throw new ExceptionInInitializerError(errMsg);
		}				
	}
	
	
	/**
	 * Load the database properties and initialize the correct class
	 * depending on which dbms is used (MySQL or SQLite).
	 */
	static {

    	String fileSeparator = System.getProperty("file.separator");
    	File propFile = new File(
    			System.getProperty("user.home")
    			+ fileSeparator
    			+ ".pokemonData"
    			+ fileSeparator
    			+ "db.properties");

		Properties dbProperties = new Properties();
		try (InputStream in = new FileInputStream(propFile))
		{
			dbProperties.load(in);		
			LOG.info("Database properties file loaded.");
			dbms = DBMS.dbmsFromString(dbProperties.getProperty("dbms"));
			System.out.println(dbms);
			if (dbms == null) {
				writeDefaultPropertiesFile(propFile);
				System.exit(1);
			}
						
			LOG.info("Using dbms: " + dbms);
			dbname = dbProperties.getProperty("dbname");
			if ("".equals(dbname)) {
				System.err.println(
						"You must provide a dbname in the database properties file.");
				System.exit(1);
			}
			
			switch(dbms) {
			
			case MYSQL:
				Class.forName("com.mysql.jdbc.Driver");
				username = dbProperties.getProperty("username");
				password = dbProperties.getProperty("password");
				host = dbProperties.getProperty("host");
				port = Integer.parseInt(dbProperties.getProperty("port"));
				LOG.info(String.format(
						"Username[%s] Password[%s] Host[%s] Port[%d]",
						username, password, host, port));
				break;
				
			case SQLITE:
				Class.forName("org.sqlite.JDBC");
				break;
			}
			
		} catch (FileNotFoundException e) {
			String errMsg = "Properties file not found!";
			LOG.warning(errMsg);
			writeDefaultPropertiesFile(propFile);
			System.exit(1);
			
		} catch (NumberFormatException e) {
			String errMsg = "Invalid port number in database properties file.";
			LOG.severe(errMsg);
			throw new ExceptionInInitializerError(errMsg);
			
		} catch (IOException e) {
			String errMsg = "Failure loading database properties.";
			LOG.severe(errMsg);
			throw new ExceptionInInitializerError(errMsg);
			
		} catch (ClassNotFoundException e) {
			String errMsg = "No suitable database driver found for " + dbms;
			LOG.severe(errMsg);
			throw new ExceptionInInitializerError(errMsg);
		}
	}

	/**
	 * Get a database connection.
	 * 
	 * @return a connection to the database.
	 * @throws SQLException
	 */
	private static Connection getConnection() throws SQLException {
		
		Connection connection = null;
		try {
			switch (dbms) {
			
			case MYSQL:
				connection = DriverManager.getConnection(
						"jdbc:mysql://" + host + ":" + port + "/" + dbname,
						username, password);
				break;
			
			case SQLITE:
				connection = DriverManager.getConnection(
						"jdbc:sqlite:" + dbname + ".db");
				break;
			}
			
		} catch (SQLException e) {
			String errMsg = "Failure connecting to the database!";
			LOG.warning(errMsg);
			throw new SQLException(errMsg);
		}

		return connection;
	}
	
	
	/**
	 * Save player data to the database.
	 * @param player
	 */
	public static void savePlayerData(Player player) {
		
		Connection connection = null;
		try {
			connection = getConnection();
			
			// manual committing allows for rolling back transactions
			// if something goes wrong
			connection.setAutoCommit(false);
			
			saveCharacterData(connection, player);
			saveItemData(connection, player);
			savePokemonData(connection, player);
			
			connection.commit();
			
		} catch (SQLException e) {
			LOG.warning("Error ocurred while saving player data.");
			try {
				LOG.warning("Rolling back changes.");
				connection.rollback();
			} catch (SQLException e2) {
				LOG.warning("Error ocurred while rolling back changes.");
			} finally {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e3) {
					LOG.warning("Error occurred setting auto commit back on");
				}
			}
		}
	}
	
	
	
	/**
	 * Save character data to the database.
	 * @param connection
	 * @param player
	 * @throws SQLException
	 */
	private static void saveCharacterData(Connection connection, 
			Player player) throws SQLException {
		
		String sql = "UPDATE PokemonUsers SET "
				+ "x = ?, "
				+ "y = ?, "
				+ "level = ?, "
				+ "Dir = ?, "
				+ "LPCX = ?, "
				+ "LPCY = ?, "
				+ "LPCLEVEL = ?, "
				+ "Money = ? "
				+ "WHERE UserName = ?";
		
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, (int) player.getPose().getX());
			statement.setInt(2, (int) player.getPose().getY());
			statement.setInt(3, player.getPose().getLevel());
			statement.setInt(4, player.getPose().facing().getValue());
			statement.setInt(5, (int) player.lastPokemonCenter.getX());
			statement.setInt(6, (int) player.lastPokemonCenter.getY());
			statement.setInt(7, player.lastPokemonCenter.getLevel());
			statement.setInt(8, player.money);
			statement.setString(9, player.name);
			statement.executeUpdate();
		}
	}

	
	/**
	 * Save item data for a player to the database.
	 * @param connection
	 * @param player
	 * @throws SQLException
	 */
	private static void saveItemData(Connection connection, 
			Player player) throws SQLException {
		
		Iterator<Item> itemIter = player.items.iterator();
		
		while (itemIter.hasNext()) {
			Item item = itemIter.next();
			if (item.number == 0) {
				removeItem(connection, player, item);
			} else {
				if (item.number > 0) {					
					if (item.added) {
						// item is marked for adding to the database
						addItem(connection, player, item);
					} else {
						updateItem(connection, player, item);
					}
				}
			}
		}
	}

	
	/**
	 * Removes an item associated with a player from the database.
	 * @param connection
	 * @param player
	 * @param item
	 * @throws SQLException
	 */
	private static void removeItem(Connection connection,
			Player player, Item item) throws SQLException {
		
		String sql = "DELETE FROM PokemonItems "
				+ "WHERE ownerid = ? AND name = ?";
		
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, player.id);
			statement.setString(2, item.name);
			statement.executeUpdate();
		}
	}

	
	/**
	 * Adds an item associated with a player to the database.
	 * @param connection
	 * @param player
	 * @param item
	 * @throws SQLException
	 */
	private static void addItem(Connection connection, 
			Player player, Item item) throws SQLException {
		
		String sql = "INSERT INTO PokemonItems VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, player.id);
			statement.setString(2, item.name);
			statement.setString(3, item.description);
			statement.setString(4, item.use);
			statement.setInt(5, item.number);
			statement.executeUpdate();
		}
	}

	
	/**
	 * Updates an item associated with a player in the database.
	 * @param connection
	 * @param player
	 * @param item
	 * @throws SQLException
	 */
	private static void updateItem(Connection connection, 
			Player player, Item item) throws SQLException {
		
		String sql = "UPDATE PokemonItems SET number = ? "
				+ "WHERE ownerid = ? AND name = ?";
		
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, item.number);
			statement.setInt(2, player.id);
			statement.setString(3, item.name);
			statement.executeUpdate();
		}
	}

	
	/**
	 * Save pokemon data associated with a player to the database.
	 * @param connection
	 * @param player
	 * @throws SQLException
	 */
	private static void savePokemonData(Connection connection, 
			Player player) throws SQLException {
		
		Iterator<Pokemon> pokemonIter = player.poke.box.iterator();
		
		while (pokemonIter.hasNext()) {
			Pokemon pokemon = pokemonIter.next();
			if (pokemon.added) {
				// pokemon is marked for adding to the database
				addPokemon(connection, player, pokemon);
			} else {
				updatePokemon(connection, player, pokemon);
			}
		}
	}

	
	/**
	 * Adds a pokemon associated with a player to the database.
	 * @param connection
	 * @param player
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void addPokemon(Connection connection, 
			Player player, Pokemon pokemon) throws SQLException {
		
		String sql = "INSERT INTO Pokemon "
				+ "(location, "
				+ "nickName, "
				+ "level, "
				+ "curenthp, "
				+ "totalhp, "
				+ "attack, "
				+ "defense, "
				+ "speed, "
				+ "special, "
				+ "Exp, "
				+ "status, "
				+ "ownerid, "
				+ "idNo, "
				+ "Species) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, pokemon.location);
			statement.setString(2, pokemon.nickName);
			statement.setInt(3, pokemon.level);
			statement.setInt(4, pokemon.currentHP);
			statement.setInt(5, pokemon.hpSE);
			statement.setInt(6, pokemon.attackSE);
			statement.setInt(7, pokemon.defenseSE);
			statement.setInt(8, pokemon.speedSE);
			statement.setInt(9, pokemon.specialSE);
			statement.setInt(10, pokemon.EXP);
			statement.setString(11, pokemon.status);
			statement.setInt(12, player.id);
			statement.setInt(13, pokemon.idNo);
			statement.setString(14, pokemon.Species);
			statement.executeUpdate();
		}
		addPokemonMoves(connection, pokemon);
	}

	
	/**
	 * Add moves associated with a pokemon to the database.
	 * @param connection
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void addPokemonMoves(Connection connection, 
			Pokemon pokemon) throws SQLException {
		
		String sql = "INSERT INTO PokemonMoves VALUES (?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			for (int i = 0; i < 4; i++) {
				if (pokemon.moves[i] == null)
					break;

				statement.setInt(1, pokemon.idNo);
				statement.setString(2, pokemon.moves[i].name);
				statement.setString(3, pokemon.moves[i].description);
				statement.setString(4, pokemon.moves[i].effect);
				statement.setString(5, pokemon.moves[i].type);
				statement.setInt(6, pokemon.moves[i].currentpp);
				statement.setInt(7, pokemon.moves[i].pp);
				statement.setInt(8, pokemon.moves[i].dmg);
				statement.setInt(9, pokemon.moves[i].accuracy);
				statement.addBatch();
			}
			
			statement.executeBatch();
		}
	}

	
	/**
	 * Updates a pokemon associated with a player in the database.
	 * @param connection
	 * @param player
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void updatePokemon(Connection connection, 
			Player player, Pokemon pokemon) throws SQLException {
		
		String sql = "UPDATE Pokemon SET "
				+ "location = ?, "
				+ "nickName = ?, "
				+ "level = ?, "
				+ "currentHp = ?, "
				+ "attack = ?, "
				+ "defense = ?, "
				+ "speed = ?, "
				+ "special = ?, "
				+ "Exp = ?, "
				+ "status = ?, "
				+ "Species = ? "
				+ "WHERE ownerid = ? AND id = ?";
		
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, pokemon.location);
			statement.setString(2, pokemon.nickName);
			statement.setInt(3, pokemon.level);
			statement.setInt(4, pokemon.currentHP);
			statement.setInt(5, pokemon.attackSE);
			statement.setInt(6, pokemon.defenseSE);
			statement.setInt(7, pokemon.speedSE);
			statement.setInt(8, pokemon.specialSE);
			statement.setInt(9, pokemon.EXP);
			statement.setString(10, pokemon.status);
			statement.setString(11, pokemon.Species);
			statement.setInt(12, player.id);
			statement.setInt(13, pokemon.idNo);
			statement.executeUpdate();
		}
		
		updatePokemonMoves(connection, pokemon);
	}

	
	/**
	 * Updates moves associated with a pokemon in the database.
	 * @param connection
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void updatePokemonMoves(Connection connection,
			Pokemon pokemon) throws SQLException {
		
		String sql = "UPDATE PokemonMoves SET "
				+ "currentpp = ?, "
				+ "pp = ? "
				+ "WHERE pokemonid = ? AND name = ?";
		
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			for (int i = 0; i < 4; i++) {
				if (pokemon.moves[i] == null)
					break;

				statement.setInt(1, pokemon.moves[i].currentpp);
				statement.setInt(2, pokemon.moves[i].pp);
				statement.setInt(3, pokemon.idNo);
				statement.setString(4, pokemon.moves[i].name);
				statement.addBatch();
			}
			
			statement.executeBatch();
		}
	}
	

	/**
	 * Get a list of items associated with a player from the database.
	 * @param userid the player's id
	 * @return
	 */
	public static ArrayList<Item> getCharacterItems(int userid) {
		
		String sql = "SELECT * FROM PokemonItems WHERE ownerid = ?";
		
		ArrayList<Item> items = new ArrayList<Item>();
		
		try (Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, userid);
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				String itemName = results.getString("name");
				Item item = null;

				if (itemName.equals("pokeball")) {
					item = new Pokeball();
				} else {
					item = new Item();
				}

				item.name = itemName;
				item.description = results.getString("description");
				item.use = results.getString("usee");
				item.number = Integer.parseInt(results.getString("number"));
				items.add(item);
			}
		} catch (SQLException e) {
			LOG.warning(String.format(
					"Error occurred while getting items for player id %d: %s",
					userid, e.getMessage()));
		}

		return items;
	}
	

	
	/**
	 * Get the pokemon associated with a player from the database.
	 * @param userid the player's id
	 * @return
	 */
	public static PokemonContainer getCharacterPokemon(int userid) {
		
		String sql = "SELECT * FROM Pokemon WHERE ownerid = ?";
		ArrayList<Pokemon> allPokemon = new ArrayList<Pokemon>();
		Pokemon[] beltPokemon = new Pokemon[6];

		try (Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, userid);
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				Pokemon pokemon = new Pokemon();
				pokemon.location = results.getInt("location");
				pokemon.nickName = results.getString("nickName");
				pokemon.level = results.getInt("level");
				pokemon.currentHP = results.getInt("currenthp");
				pokemon.attackSE = results.getInt("attack");
				pokemon.defenseSE = results.getInt("defense");
				pokemon.speedSE = results.getInt("speed");
				pokemon.specialSE = results.getInt("special");
				pokemon.idNo = results.getInt("id");
				pokemon.EXP = results.getInt("EXP");
				pokemon.status = results.getString("status");
				pokemon.Species = results.getString("species");	
				
				setMoves(connection, pokemon);
	            pokemon.getBase();

				if (pokemon.location < 6) {
					beltPokemon[pokemon.location] = pokemon;
				}

				allPokemon.add(pokemon);

			}

		} catch (SQLException e) {
			LOG.warning(String.format(
					"Error occurred while getting pokemon for player id %d: %s",
					userid, e.getMessage()));
		}

		return new PokemonContainer(allPokemon, beltPokemon);
	}

	
	/**
	 * Sets the moves for a pokemon.
	 * @param connection
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void setMoves(Connection connection, 
			Pokemon pokemon) throws SQLException {
		
		String sql = "SELECT * FROM PokemonMoves WHERE pokemonid = ?";
		Move[] moves = new Move[4];
		
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, pokemon.idNo);
			ResultSet results = statement.executeQuery();
			int nMoves = 0;
			
			while (results.next()) {
				if (nMoves > 4) {
					LOG.warning(String.format(
							"Error adding move: "
							+ "Pokemon %s already has 4 moves!",
							pokemon));
				} else {
					Move move = new Move();
					move.name = results.getString("name");
					move.description = results.getString("description");
					move.effect = results.getString("effect");
					move.type = results.getString("effect");
					move.currentpp = results.getInt("currentpp");
					move.pp = results.getInt("pp");
					move.dmg = results.getInt("dmg");
					move.accuracy = results.getInt("accuracy");
					LOG.info(String.format("Added move %s to Pokemon %s.",
							move, pokemon));
					moves[nMoves] = move;
				
					nMoves++;
				}
			}

			pokemon.moves = moves;
		}
	}
	
	
	/**
	 * Log in and retrieve initial player information.
	 * @param username
	 * @param password
	 * @return
	 */
	public static Player logIn(String username, String password) {
		
		String sql = "SELECT * FROM PokemonUsers "
				+ "WHERE UserName = ? AND Password = ?";
		Player player = null;
		
		try (Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				player = new Player();
				player.id = results.getInt("id");
				player.setPosition(new Pose(
						results.getInt("x"), results.getInt("y"), 
						results.getInt("level"), 
						Direction.get(results.getInt("dir"))));
				player.name = results.getString("UserName");
				player.imgName = results.getString("picture");
				player.lastPokemonCenter = new Pose(
						results.getInt("lpcx"), results.getInt("lpcy"), 
						results.getInt("lpclevel"), Direction.NONE);
				player.money = results.getInt("money");
			}
			
			if (player == null) {
				LOG.warning(String.format(
						"Failure retrieving player %s. "
						+ "Check the username and password.", username));
			} else {
				LOG.info("Retrieved information for player " + username);				
			}
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return player;
	}

	

	/**
	 * Helper class for the concept of box and belt pokemon.
	 * Unchanged from the original.
	 * Probably should be in a separate file.
	 * Note that the box stores ALL pokemon (including those on the belt).
	 *
	 */
	public static class PokemonContainer {
		public ArrayList<Pokemon> box;
		public Pokemon belt[];

		public PokemonContainer() {
			box = new ArrayList<Pokemon>();
			belt = new Pokemon[6];

		}

		public PokemonContainer(ArrayList<Pokemon> box, Pokemon belt[]) {
			this.box = box;
			this.belt = belt;
		}

		public Pokemon getFirstOut() {
			return belt[0];
		}

		public int getFirstHealthy() {
			for (int i = 0; i < 6; i++)
				if (belt[i] != null && belt[i].currentHP > 0)
					return i;
			return -1;
		}

		public void printHP() {
			for (int i = 0; i < 6; i++)
				if (belt[i] != null)
					System.out.println(i + ": " + belt[i].Species + " HP: "
							+ belt[i].getCurrentHP() + "/"
							+ belt[i].getTotalHP());
		}

	}
}