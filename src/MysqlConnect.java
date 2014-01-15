import java.io.*;
import java.net.*;
import java.util.*;

public class MysqlConnect {

    public static final int Q_PHP = 0;
    public static final int Q_SQL = 1;
    public static final int Q_FILE = 2;

    public static boolean localized = false;

    private static int mode = Q_PHP;

    public MysqlConnect() {
    }

    public static void main(String args[]) {

        // String username = JOptionPane.showInputDialog(null, "Username:",
        // "Username", JOptionPane.QUESTION_MESSAGE);
        // String password = JOptionPane.showInputDialog(null, "Password:",
        // "Password", JOptionPane.QUESTION_MESSAGE);
        // login(username, password);
        // saveCharacterStatus(new Player(1, 2, 1, 0, 9, "DrYerzinia"));
        // getCharacterPokemon(2);

        // getCharacterItems(6);

    }

    public static void saveCharacterStatus(Player p) {

        // Save Location

        // if(p.x == 5 && p.y == 5 && p.level == 9) return;

        String query = "UPDATE PokemonUsers SET x = " + p.x + ", y = " + p.y
                + ", level = " + p.level + ", Dir = " + p.dir + ", LPCX = "
                + p.lpcx + ", LPCY = " + p.lpcy + ", LPCLEVEL = " + p.lpclevel
                + ", Money = " + p.money + " WHERE UserName = '" + p.name
                + "';";
        sendQuery(query);
        System.out.println("Saving: " + query);

        // Save Item

        Iterator<Item> iti = p.items.iterator();
        while (iti.hasNext()) {
            Item it = iti.next();
            if (it.number > 0) {
                if (!it.added) {
                    query = "UPDATE PokemonItems SET number = " + it.number
                            + " WHERE ownerid = " + p.id + " AND name = '"
                            + it.name + "'";
                    System.out.println("ITM:" + query);
                    sendQuery(query);
                } else {
                    query = "INSERT INTO PokemonItems VALUES (" + p.id + ", '"
                            + it.name + "', '" + it.description + "', '"
                            + it.use + "', " + it.number + ");";
                    System.out.println("ITM:" + query);
                    sendQuery(query);
                }
            } else if (it.number == 0) {
                query = "DELETE FROM PokemonItems WHERE ownerid = " + p.id
                        + " AND name = '" + it.name + "'";
                sendQuery(query);
            }
        }

        // Save Pokemon

        // DEBUG
        System.out.println(p.getName() + "'s pokemon: ");
        Iterator<Pokemon> itpoke = p.poke.box.iterator();
        while (itpoke.hasNext()) {
            Pokemon po = itpoke.next();
            System.out.println(po.getName());
        }
        // END

        Iterator<Pokemon> itp = p.poke.box.iterator();
        while (itp.hasNext()) {
            Pokemon po = itp.next();
            if (!po.added) {
                query = "UPDATE Pokemon SET location = " + po.location
                        + ", nickName = '" + po.nickName + "', level = "
                        + po.level + ", currenthp = " + po.currentHP
                        + ", attack = " + po.attackSE + ", defense = "
                        + po.defenseSE + ", speed = " + po.speedSE
                        + ", special = " + po.specialSE + ", Exp = " + po.EXP
                        + ", status = '" + po.status + "', Species = '"
                        + po.getSpecies() + "' WHERE ownerid = " + p.id
                        + " AND id = " + po.idNo + ";";
                System.out.println("POK:" + query);
                for (int i = 0; i < 4; i++) {
                    if (po.moves[i] == null)
                        break;
                    String query2 = "UPDATE PokemonMoves SET currentpp = "
                            + po.moves[i].currentpp + ", pp = '"
                            + po.moves[i].pp + "' WHERE pokemonid = " + po.idNo
                            + " AND name = '" + po.moves[i].name + "'";
                    System.out.println("MOV:" + query2);
                    sendQuery(query2);
                }
                sendQuery(query);
            } else {
                query = "INSERT INTO Pokemon (location, nickName, level, currenthp, totalhp, attack, defense, speed, special, Exp, status, ownerid, idNo, Species) VALUES ("
                        + po.location
                        + ", '"
                        + po.nickName
                        + "', "
                        + po.level
                        + ", "
                        + po.currentHP
                        + ", 0, "
                        + po.attackSE
                        + ", "
                        + po.defenseSE
                        + ", "
                        + po.speedSE
                        + ", "
                        + po.specialSE
                        + ", "
                        + po.EXP
                        + ", '"
                        + po.status
                        + "', "
                        + p.id
                        + ", '"
                        + po.idNo
                        + "', '"
                        + po.Species
                        + "');";
                System.out.println("POK:" + query);
                sendQuery(query);
                query = "SELECT id FROM Pokemon WHERE ownerid = " + p.id
                        + " AND idNo = " + po.idNo + ";";
                ArrayList<String> q = sendQuery(query);
                po.idNo = Integer.parseInt(q.get(0));
                query = "UPDATE Pokemon SET idNo = " + po.idNo
                        + " WHERE ownerid = " + p.id + " AND id = " + po.idNo
                        + ";";
                sendQuery(query);
                for (int i = 0; i < 4; i++) {
                    if (po.moves[i] == null)
                        break;
                    String query2 = "INSERT INTO PokemonMoves VALUES ("
                            + po.idNo + ", '" + po.moves[i].name + "', '"
                            + po.moves[i].description + "', '"
                            + po.moves[i].effect + "', '" + po.moves[i].type
                            + "', " + po.moves[i].currentpp + ", "
                            + po.moves[i].pp + ", " + po.moves[i].dmg + ", "
                            + po.moves[i].accuracy + ");";
                    System.out.println("MOV:" + query2);
                    sendQuery(query2);
                }
            }
        }

    }

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

    public static ArrayList<String> sendQuery(String query) {
        switch (mode) {
        case Q_SQL:
            return sendQueryMySQL(query);
        case Q_FILE:
            return sendQueryFile(query);
        case Q_PHP:
            return sendQueryPHP(query);
        }
        return null;
    }

    public static ArrayList<String> sendQueryPHP(String query) {
        ArrayList<String> data = new ArrayList<String>();
        URL u;
        InputStream is = null;
        BufferedReader dis;
        String s = null;
        String ur = null;
        if (localized)
            ur = "http://localhost/query.php?query=" + fixQuery(query);
        else
            ur = "http://dryerzinia.comxa.com/query.php?query="
                    + fixQuery(query);
        System.out.println();
        try {

            u = new URL(ur);
            dis = new BufferedReader(new InputStreamReader(u.openStream()));
            s = dis.readLine();
            String[] result = s.split(",");
            for (int i = 0; i < result.length; i++)
                data.add(result[i]);
            while ((s = dis.readLine()) != null)
                ;
        } catch (Exception x) {
            System.out.println(query);
            System.out.println(s);
            x.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }

        return data;
    }

    public static ArrayList<String> sendQueryMySQL(String query) {
        return null;
    }

    public static ArrayList<String> sendQueryFile(String query) {
        return null;
    }

    private static String fixQuery(String s) {
        String r = "";
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
            case ' ':
                r += "%20";
                break;
            case '\'':
                r += "%27";
                break;
            case '=':
                r += "%3D";
                break;
            default:
                r += s.charAt(i);
                break;
            }
        }
        return r;
    }
    /*
     * public static class UserPositionData {
     * 
     * int x, y, level;
     * 
     * public UserPositionData(int x, int y, int level){
     * 
     * this.x = x; this.y = y; this.level = level;
     * 
     * }
     * 
     * public String toString(){ return "X: "+x+"\nY: "+y+"\nLevel: "+level; }
     * 
     * }
     */
}
