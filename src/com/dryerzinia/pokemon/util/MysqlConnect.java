package com.dryerzinia.pokemon.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import com.dryerzinia.pokemon.map.Direction;
import com.dryerzinia.pokemon.map.Pose;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Move;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokeball;
import com.dryerzinia.pokemon.obj.Pokemon;
import com.mysql.jdbc.CommunicationsException;

public class MysqlConnect {

	public static boolean localized = false;

	public static String username;
	public static String password;
	public static String dbname;
	public static String server;
	public static int port;

	/**
	 * Gets a connection to the database.
	 * 
	 * @return the connection to the database
	 * @throws SQLException
	 */
	private static Connection getConnection() throws SQLException {

		Connection conn = null;
		Properties credentials = new Properties();
		credentials.put("user", username);
		credentials.put("password", password);

		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance(); 

		} catch(IllegalAccessException e){

			System.err.println("Can't access com.mysql.jdbc.Driver: " + e.getMessage());
			throw new SQLException("Not allowed to access MySQL Driver!");

		} catch (InstantiationException e) {

			System.err.println("Can't instantiate com.mysql.jdbc.Driver: " + e.getMessage());
			throw new SQLException("Could not create MySQL Driver Instance!");

		} catch (ClassNotFoundException e) {

			System.err.println("Class com.mysql.jdbc.Driver not found: " + e.getMessage());
			throw new SQLException("MySQL Driver not found!");

		}

		try {
			conn = DriverManager.getConnection(
				  "jdbc:mysql://"
				+ server
				+ ":"
				+ port
				+ "/"
				+ dbname, credentials);
		} catch(CommunicationsException ce){

			System.err.println("Could not connect to DB!");
			throw new SQLException("Could not connect to DB!");

			/*
			 * TODO this crashes the CLIENT!
			 * need to do better handling of this error
			 * Probably should be THROWN all the way to the
			 * login message where it is handled with
			 * appropriate response informing client of 
			 * DB ERROR!
			 */
			
		}

		System.out.println("Connected to the database.");
		return conn;
	}

	/**
	 * Save player data to the database
	 * 
	 * @param player
	 *            the Player whose data to save
	 */
	public static void savePlayerData(Player player) {
		Connection connection = null;
		try {
			connection = getConnection();
			connection.setAutoCommit(false); // transactions possible
			saveCharacterData(connection, player);
			saveItemData(connection, player);
			savePokemonData(connection, player);
			connection.commit(); // commit all changes
		} catch (SQLException e1) {
			e1.printStackTrace();
			if (connection != null) {
				try {
					System.err.println("Transaction is being rolled back.");
					connection.rollback();
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
			}
		} finally {
			try {
				// revert to default mode
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Save character data to the database
	 * 
	 * @param connection
	 *            connection to the database
	 * @param player
	 *            the character's player
	 */
	private static void saveCharacterData(Connection connection, Player player)
			throws SQLException {
		String sql = "UPDATE PokemonUsers SET x = ?, y = ?, "
				+ "level = ?, Dir = ?, LPCX = ?, LPCY = ?, LPCLEVEL = ?, "
				+ "Money = ? WHERE UserName = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, (int) player.getPose().getX());
			stmt.setInt(2, (int) player.getPose().getY());
			stmt.setInt(3, player.getPose().getLevel()); // current level player is on
			stmt.setInt(4, player.getPose().facing().getValue()); // player direction
			stmt.setInt(5, (int) player.lastPokemonCenter.getX());
			stmt.setInt(6, (int) player.lastPokemonCenter.getY());
			stmt.setInt(7, player.lastPokemonCenter.getLevel());
			stmt.setInt(8, player.money);
			stmt.setString(9, player.name);
			stmt.executeUpdate();
		}
	}

	/**
	 * Save the items of the given player
	 * 
	 * @param connection
	 * @param player
	 */
	private static void saveItemData(Connection connection, Player player)
			throws SQLException {
		Iterator<Item> itemIter = player.items.iterator();
		while (itemIter.hasNext()) {
			Item item = itemIter.next();
			if (item.number == 0) {
				removeItem(connection, player, item);
				continue;
			}
			if (item.number > 0) {
				if (item.added) // item marked for adding to DB
					addItem(connection, player, item);
				else
					updateItem(connection, player, item);
			}
		}
	}

	/**
	 * Removes an item from the database for the given player.
	 * 
	 * @param connection
	 * @param player
	 * @param item
	 */
	private static void removeItem(Connection connection, Player player,
			Item item) throws SQLException {
		String sql = "DELETE FROM PokemonItems "
				+ "WHERE ownerid = ? AND name = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, player.id);
			stmt.setString(2, item.name);
			stmt.executeUpdate();
		}
	}

	/**
	 * Adds an item to the database for the given player.
	 * 
	 * @param connection
	 * @param player
	 * @param item
	 */
	private static void addItem(Connection connection, Player player, Item item)
			throws SQLException {
		String sql = "INSERT INTO PokemonItems " + "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, player.id);
			stmt.setString(2, item.name);
			stmt.setString(3, item.description);
			stmt.setString(4, item.use);
			stmt.setInt(5, item.number);
			stmt.executeUpdate();
		}
	}

	/**
	 * Updates an item already in the database for the given player.
	 * 
	 * @param connection
	 * @param player
	 * @param item
	 */
	private static void updateItem(Connection connection, Player player,
			Item item) throws SQLException {
		String sql = "UPDATE PokemonItems SET number = ? "
				+ "WHERE ownerid = ? AND name = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, item.number);
			stmt.setInt(2, player.id);
			stmt.setString(3, item.name);
			stmt.executeUpdate();
		}
	}

	/**
	 * Save pokemon data for the given player.
	 * 
	 * @param connection
	 * @param player
	 */
	private static void savePokemonData(Connection connection, Player player)
			throws SQLException {
		Iterator<Pokemon> pokemonIter = player.poke.box.iterator();
		while (pokemonIter.hasNext()) {
			Pokemon pokemon = pokemonIter.next();
			if (pokemon.added) // recently caught pokemon
				addPokemon(connection, player, pokemon);
			else
				updatePokemon(connection, player, pokemon);
		}
	}

	/**
	 * Adds a pokemon to the given player in the db
	 * 
	 * @param connection
	 * @param player
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void addPokemon(Connection connection, Player player,
			Pokemon pokemon) throws SQLException {
		String sql = "INSERT INTO Pokemon (location, nickName, level, curenthp, "
				+ "totalhp, attack, defense, speed, special, Exp, status, "
				+ "ownerid, idNo, Species) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, pokemon.location);
			stmt.setString(2, pokemon.nickName);
			stmt.setInt(3, pokemon.level);
			stmt.setInt(4, pokemon.currentHP);
			stmt.setInt(5, pokemon.hpSE);
			stmt.setInt(6, pokemon.attackSE);
			stmt.setInt(7, pokemon.defenseSE);
			stmt.setInt(8, pokemon.speedSE);
			stmt.setInt(9, pokemon.specialSE);
			stmt.setInt(10, pokemon.EXP);
			stmt.setString(11, pokemon.status);
			stmt.setInt(12, player.id);
			stmt.setInt(13, pokemon.idNo);
			stmt.setString(14, pokemon.Species);
			stmt.executeUpdate();
		}
		addPokemonMoves(connection, pokemon);
	}

	/**
	 * Inserts moves for a pokemon into the db
	 * 
	 * @param connection
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void addPokemonMoves(Connection connection, Pokemon pokemon)
			throws SQLException {
		String sql = "INSERT INTO PokemonMoves VALUES (?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			for (int i = 0; i < 4; i++) {
				if (pokemon.moves[i] == null)
					break;

				stmt.setInt(1, pokemon.idNo);
				stmt.setString(2, pokemon.moves[i].name);
				stmt.setString(3, pokemon.moves[i].description);
				stmt.setString(4, pokemon.moves[i].effect);
				stmt.setString(5, pokemon.moves[i].type);
				stmt.setInt(6, pokemon.moves[i].currentpp);
				stmt.setInt(7, pokemon.moves[i].pp);
				stmt.setInt(8, pokemon.moves[i].dmg);
				stmt.setInt(9, pokemon.moves[i].accuracy);
				stmt.addBatch();
			}
			stmt.executeBatch();
		}
	}

	/**
	 * Updates an existing pokemon in the db for a given player.
	 * 
	 * @param connection
	 * @param player
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void updatePokemon(Connection connection, Player player,
			Pokemon pokemon) throws SQLException {
		String sql = "UPDATE Pokemon SET location = ?, nickName = ?, "
				+ "level = ?, currentHp = ?, attack = ?, defense = ?, "
				+ "speed = ?, special = ?, Exp = ?, status = ?, "
				+ "Species = ? WHERE ownerid = ? AND id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, pokemon.location);
			stmt.setString(2, pokemon.nickName);
			stmt.setInt(3, pokemon.level);
			stmt.setInt(4, pokemon.currentHP);
			stmt.setInt(5, pokemon.attackSE);
			stmt.setInt(6, pokemon.defenseSE);
			stmt.setInt(7, pokemon.speedSE);
			stmt.setInt(8, pokemon.specialSE);
			stmt.setInt(9, pokemon.EXP);
			stmt.setString(10, pokemon.status);
			stmt.setString(11, pokemon.Species);
			stmt.setInt(12, player.id);
			stmt.setInt(13, pokemon.idNo);
			stmt.executeUpdate();
		}
		updatePokemonMoves(connection, pokemon);
	}

	/**
	 * Updates the moves for an existing pokemon in the db
	 * 
	 * @param connection
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void updatePokemonMoves(Connection connection,
			Pokemon pokemon) throws SQLException {
		String sql = "UPDATE PokemonMoves SET currentpp = ?, pp = ? "
				+ "WHERE pokemonid = ? AND name = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			for (int i = 0; i < 4; i++) {
				if (pokemon.moves[i] == null)
					break;

				stmt.setInt(1, pokemon.moves[i].currentpp);
				stmt.setInt(2, pokemon.moves[i].pp);
				stmt.setInt(3, pokemon.idNo);
				stmt.setString(4, pokemon.moves[i].name);
				stmt.addBatch();
			}
			stmt.executeBatch();
		}
	}

	// TODO: store numbers in the DB as numbers and stop parsing strings..

	/**
	 * Get a list of items that the given character has.
	 * 
	 * @param userid
	 *            the id of the character
	 * @return
	 */
	public static ArrayList<Item> getCharacterItems(int userid) {
		String sql = "SELECT * FROM PokemonItems WHERE ownerid = ?";
		ArrayList<Item> items = new ArrayList<Item>();
		try (Connection connection = getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setInt(1, userid);
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				String itemName = results.getString("name");
				Item item = null;

				if (itemName.equals("pokeball"))
					item = new Pokeball();
				else
					item = new Item();

				item.name = itemName;
				item.description = results.getString("description");
				item.use = results.getString("usee"); // what is this?
				item.number = Integer.parseInt(results.getString("number"));
				items.add(item);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return items;
	}

	/**
	 * Get the pokemon of the given character.
	 * 
	 * @param userid
	 *            the id of the character
	 * @return a PokemonContainer containing box and belt pokemon.
	 */
	public static PokemonContainer getCharacterPokemon(int userid) {
		String sql = "SELECT * FROM Pokemon WHERE ownerid = ?";
		ArrayList<Pokemon> boxPokemon = new ArrayList<Pokemon>();
		Pokemon[] beltPokemon = new Pokemon[6];

		try (Connection connection = getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setInt(1, userid);
			ResultSet results = stmt.executeQuery();
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
				pokemon.status = results.getString("status"); // stored in db as
																// int -
																// problem?
				pokemon.Species = results.getString("species");				
				fetchAndSetMoves(connection, pokemon);
	            pokemon.getBase();

				if (pokemon.location < 6)
					beltPokemon[pokemon.location] = pokemon;

				boxPokemon.add(pokemon);

			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return new PokemonContainer(boxPokemon, beltPokemon);
	}

	/**
	 * Sets the moves for the given pokemon.
	 * 
	 * @param connection
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void fetchAndSetMoves(Connection connection, Pokemon pokemon)
			throws SQLException {
		String sql = "SELECT * FROM PokemonMoves WHERE pokemonid = ?";
		Move[] moves = new Move[4];
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, pokemon.idNo);
			ResultSet results = stmt.executeQuery();

			int nMoves = 0; // counting the number of moves (more than 4 is
							// invalid)
			while (results.next()) {
				if (nMoves > 4) {
					System.err.println("The pokemon has more than 4 moves!");
					// custom exception? InvalidNumberOfMovesException
				}
				Move move = new Move();
				move.name = results.getString("name");
				move.description = results.getString("description");
				move.effect = results.getString("effect");
				move.type = results.getString("effect");
				move.currentpp = results.getInt("currentpp");
				move.pp = results.getInt("pp");
				move.dmg = results.getInt("dmg");
				move.accuracy = results.getInt("accuracy");
				System.out.println(move.name);
				moves[nMoves] = move;
				
				nMoves++;
			}

			pokemon.moves = moves;
		}
	}

	/**
	 * Log in and retrieve player information
	 * 
	 * @param username
	 * @param password
	 * @return the Player whose credentials match username and password, or
	 *         null.
	 */
	public static Player login(String username, String password) {
		String sql = "SELECT * FROM PokemonUsers WHERE UserName = ? AND Password = ?";
		Player player = null;
		try (Connection connection = getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql)) {

			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet results = stmt.executeQuery();

			int nUsers = 0;
			while (results.next()) {
				if (nUsers > 1) {
					System.err
							.println("Multiple users for the same information!");
					// MultipleUsersException?
				}
				player = new Player();
				player.id = results.getInt("id");
				player.setPosition(new Pose(results.getInt("x"), results.getInt("y"), results.getInt("level"), Direction.get(results.getInt("dir"))));
				player.name = results.getString("UserName");
				player.imgName = results.getString("picture");
				player.lastPokemonCenter = new Pose(results.getInt("lpcx"), results.getInt("lpcy"), results.getInt("lpclevel"), Direction.NONE);
				player.money = results.getInt("money");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return player;
	}

	/**
	 * Helper class for the concept of box and belt pokemon This is unchanged
	 * from the original.
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