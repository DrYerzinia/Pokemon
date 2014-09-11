package com.dryerzinia.pokemon.obj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.dryerzinia.pokemon.input.InputController;
import com.dryerzinia.pokemon.input.KeyboardInputController;
import com.dryerzinia.pokemon.map.Level;
import com.dryerzinia.pokemon.util.string.StringStore.Locale;

public class ClientState {

	private static final Logger LOG =
			Logger.getLogger(ClientState.class.getName());

	public static Player player;
	public static ConcurrentHashMap<Integer, Player> players;

	public static InputController inputDevice;

	public static Locale locale = Locale.EN;

	private static boolean loaded;

	/*
	 * Global login information
	 */
    public static String username = "";
    public static String password = "";
    public static String location = "";

	public static void init(){

		loaded = false;

		players = new ConcurrentHashMap<Integer, Player>();

		inputDevice = new KeyboardInputController();

		loadSettings();

	}

	public static boolean isLoaded(){
		return loaded;
	}

	public static void setLoaded(){
		loaded = true;
	}

	public static Level getPlayerLevel(){

		return GameState.getMap().getLevel(player.getPose().getLevel());

	}

	public static void saveSettings(){

		ClientState.writeSettingsData(ClientState.getSettingsFile());

	}

	public static void loadSettings(){

		// Check if the .pokemonData folder exists and if it dosen't make it
		File f = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".pokemonData");
		if(!f.exists())
			f.mkdir();

		// Load settings file
		f = getSettingsFile();

		// If settings file doesn't exists try and make it
		if(!f.exists()) {

			try {

				f.createNewFile();
				writeSettingsData(f);

			} catch(IOException ioe){

				System.err.println("Coulden't create settings file: " + ioe.getMessage());

			}

		}

		// Or read in the settings
		else {

			readSettingsData(f);

		}

	}

	public static void writeSettingsData(File f) {

		try {

			Properties properties = new Properties();
			properties.setProperty("username", username);
			properties.setProperty("password", password);
			properties.setProperty("location", location);
			properties.setProperty("locale", locale.toString());
			OutputStream out = new FileOutputStream(f);
			properties.store(out, "User related settings");

		} catch (FileNotFoundException e) {
			LOG.warning("File " + f.toString() + " was not found!");
		} catch (IOException e) {
			LOG.warning("Failed to write user properties file!");
		}

	}

	public static void readSettingsData(File f) {

		Properties properties = new Properties();

		try (InputStream in = new FileInputStream(f)) {

			properties.load(in);
			username = properties.getProperty("username");
			password = properties.getProperty("password");
			location = properties.getProperty("location");

			String locStr = properties.getProperty("locale");

			if(locStr == null)
				locStr = "en";

			ClientState.locale = Locale.fromString(locStr);

		} catch (IOException e) {

			LOG.warning("Failed to read user properties file: " 
					+ e.getMessage());
		}

	}

	public static File getSettingsFile() {

		String fs = System.getProperty("file.separator");

		return new File(System.getProperty("user.home") + fs + ".pokemonData"
				+ fs + "user.properties");

	}

}
