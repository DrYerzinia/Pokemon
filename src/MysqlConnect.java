import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class MysqlConnect {

    public static boolean localized = false;
    
    // hard-coded for now; change this
    private static String username;
    private static String password;
    private static String server;
    private static int port;
    
    /**
     * Gets a connection to the database.
     * @return the connection to the database
     * @throws SQLException
     */
    private static Connection getConnection() throws SQLException {
    	Connection conn = null;
    	Properties credentials = new Properties();
    	credentials.put("user", username);
    	credentials.put("password", password);    	
    	
    	conn = DriverManager.getConnection(
    			"jdbc:mysql://" + server + ":" + port + "/",
    			credentials);
    	System.out.println("Connected to the database.");
    	return conn;	
    }
    
    /**
     * Save player data to the database
     * @param player the Player whose data to save
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
     * @param connection connection to the database
     * @param player the character's player
     */
    private static void saveCharacterData(Connection connection, Player player) 
    	throws SQLException {
    	String sql = "UPDATE PokemonUsers SET x = ?, y = ?, "
    			+ "level = ?, Dir = ?, LPCX = ?, LPCY = ?, LPCLEVEL = ?, "
    			+ "Money = ? WHERE UserName = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, player.x);
			stmt.setInt(2, player.y);
			stmt.setInt(3, player.level); // current level player is on
			stmt.setInt(4, player.dir); // player direction
			stmt.setInt(5, player.lpcx); // player's last x position
			stmt.setInt(6, player.lpcy); // player's last y position
			stmt.setInt(7, player.lpclevel); // last level player was on
			stmt.setInt(8, player.money); 
			stmt.setString(9, player.name);
			stmt.executeUpdate();
		}
	}
    
    /**
     * Save the items of the given player
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
            	if (item.added) //item marked for adding to db
            		addItem(connection, player, item);
            	else
            		updateItem(connection, player, item);
            }
        }
    }
	
	/**
	 * Removes an item from the database for the given player.
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
	 * @param connection
	 * @param player
	 * @param item
	 */
	private static void addItem(Connection connection, Player player,
			Item item) throws SQLException {
		String sql = "INSERT INTO PokemonItems "
				+ "VALUES (?, ?, ?, ?, ?)";
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
	 * @param connection
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void addPokemonMoves(Connection connection, Pokemon pokemon) 
			throws SQLException{
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
	 * @param connection
	 * @param pokemon
	 * @throws SQLException
	 */
	private static void updatePokemonMoves(Connection connection, Pokemon pokemon)
		throws SQLException {
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
	
	/*********************************************************************
	 * TODO: EDIT BELOW SO IT USES JDBC
	 */


    public static class PokemonContainer {

        ArrayList<Pokemon> box;
        Pokemon belt[];

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
                    System.out.println(i + ": " + belt[i].name + " HP: " + belt[i].getCurrentHP() + "/" + belt[i].getTotalHP());
        }
        
    }

    public static ArrayList<Item> getCharacterItems(int userid) {

        ArrayList<Item> item = new ArrayList<Item>();

        String query = "SELECT * FROM PokemonItems WHERE ownerid = " + userid
                + ";";
        ArrayList<String> q = sendQuery(query);

        if (q.size() > 2) {
            for (int i = 0; i < q.size(); i = i + 5) {
                if (q.get(i + 1).toLowerCase().equals("pokeball")) {
                    Pokeball it = new Pokeball();
                    it.name = q.get(i + 1);
                    it.description = q.get(i + 2);
                    it.use = q.get(i + 3);
                    it.number = Integer.parseInt(q.get(i + 4));
                    item.add(it);
                } else {
                    Item it = new Item();
                    it.name = q.get(i + 1);
                    it.description = q.get(i + 2);
                    it.use = q.get(i + 3);
                    it.number = Integer.parseInt(q.get(i + 4));
                    item.add(it);
                }
            }
        }

        return item;

    }

    public static PokemonContainer getCharacterPokemon(int userid) {

        ArrayList<Pokemon> poke = new ArrayList<Pokemon>();
        Pokemon poke2[] = new Pokemon[6];

        String query = "SELECT * FROM Pokemon WHERE ownerid = " + userid + ";";
        ArrayList<String> q = sendQuery(query);

        if (q.size() > 2) {

            for (int i = 0; i < q.size(); i = i + 15) {
                // for(int j = 0; j < 15; j++){
                Pokemon p = new Pokemon();
                int id = Integer.parseInt(q.get(i + 0));
                // Integer.parseInt(q.get(i*15+1));
                int location = Integer.parseInt(q.get(i + 2));
                p.location = location;
                p.nickName = q.get(i + 3);
                p.level = Integer.parseInt(q.get(i + 4));
                // p.totalHP = Integer.parseInt(q.get(i+5));
                p.currentHP = Integer.parseInt(q.get(i + 6));
                p.attackSE = Integer.parseInt(q.get(i + 7));
                p.defenseSE = Integer.parseInt(q.get(i + 8));
                p.speedSE = Integer.parseInt(q.get(i + 9));
                p.specialSE = Integer.parseInt(q.get(i + 10));
                p.idNo = Integer.parseInt(q.get(i + 11));
                p.idNo = id;
                p.EXP = Integer.parseInt(q.get(i + 12));
                p.status = q.get(i + 13);
                p.Species = q.get(i + 14);
                p.name = p.Species;
                p.ot = p.nickName;
                System.out.println("K:" + p.Species);
                /*
                 * if(p.Species.equals("Pikachu")){ p.type = "Eletric"; p.type2
                 * = "None"; }
                 */

                String query2 = "SELECT * FROM PokemonMoves WHERE pokemonid = "
                        + id + ";";
                ArrayList<String> q2 = sendQuery(query2);

                Move[] moves = new Move[4];
                for (int k = 0; k < q2.size(); k = k + 9) {
                    // for(int l = 0; l < 9; l++){
                    Move m = new Move();
                    m.name = q2.get(k + 1);
                    m.description = q2.get(k + 2);
                    m.effect = q2.get(k + 3);
                    m.type = q2.get(k + 4);
                    m.currentpp = Integer.parseInt(q2.get(k + 5));
                    m.pp = Integer.parseInt(q2.get(k + 6));
                    m.dmg = Integer.parseInt(q2.get(k + 7));
                    m.accuracy = Integer.parseInt(q2.get(k + 8));
                    if (k / 9 < 4) {
                        moves[k / 9] = m;
                    } else {
                        System.err
                                .println("Error: This Pokemon Has More Than 4 Moves?!?!?!");
                    }
                    // }
                    // try{for(int l = 0; l < 18; l++)
                    // System.out.println(q2.get(k+l)+",");}catch(Exception x){}
                }
                p.moves = moves;
                if (location < 6)
                    poke2[location] = p;
                // else
                poke.add(p);
                // for(int j = 0; j < 15; j++)
                // System.out.println(q.get(i+j)+",");
            }
        }

        return new PokemonContainer(poke, poke2);

    }

    public static Player login(String username, String password) {

        Player p = null;

        try {

            String query = "SELECT * FROM PokemonUsers WHERE UserName = '"
                    + username + "' AND Password = '" + password + "';";
            ArrayList<String> q = sendQuery(query);
            System.out.println("Loading: " + query);

            System.out.println(Integer.parseInt(q.get(0)) + " "
                    + Integer.parseInt(q.get(4)) + " "
                    + Integer.parseInt(q.get(5)) + " "
                    + Integer.parseInt(q.get(7)) + " "
                    + Integer.parseInt(q.get(6)) + " " + q.get(1) + " "
                    + q.get(8));

            p = new Player(Integer.parseInt(q.get(0)), Integer.parseInt(q
                    .get(4)), Integer.parseInt(q.get(5)), Integer.parseInt(q
                    .get(7)), Integer.parseInt(q.get(6)), q.get(1), q.get(8));

            p.lpcx = Integer.parseInt(q.get(9));
            p.lpcy = Integer.parseInt(q.get(10));
            p.lpclevel = Integer.parseInt(q.get(11));
            p.money = Integer.parseInt(q.get(12));

        } catch (Exception x) {
            x.printStackTrace();
        }

        return p;

    }
    
    static ArrayList<String> sendQuery(String sql) {
    	return null;
    }
}
