import java.util.*;
import java.awt.*;
import java.io.*; // Serializable

public class Player implements Serializable {

    static final long serialVersionUID = 3191532376844138757L;

    public int id; // Player MYSQL_ID
    public int x, y; // X, Y location in map level
    public int dir; // facing direction
    public int level; // current level player is in
    public int lpcx, lpcy; // player level position x and y ??? WTF IS THIS ???
                           // why not just above XY
    public int lpclevel; // player current level ??? ^^^ ??? level transition?
    public int money; // players amount of money
    public String name; // Player ID/Handle/UserName

    public String imgName; // Character Sprite Base Name

    public transient Image sprite[]; // Character sprite image references for
                                     // draw

    public transient Image img; // Character large back image for game

    public transient MysqlConnect.PokemonContainer poke; // Container for
                                                         // players pokemon
    public transient ArrayList<Item> items; // Container for players items

    public Player() {

        poke = new MysqlConnect.PokemonContainer();
        items = new ArrayList<Item>();

    }

    public Player(int id, int x, int y, int dir, int level, String name) {

        this.id = id;

        this.x = x;
        this.y = y;

        this.dir = dir;

        this.level = level;

        this.name = name;

        poke = new MysqlConnect.PokemonContainer();
        items = new ArrayList<Item>();

    }

    public Player(int id, int x, int y, int dir, int level, String name,
            String imgName) {

        this.id = id;

        this.x = x;
        this.y = y;

        this.dir = dir;

        this.level = level;

        this.name = name;
        this.imgName = imgName;

        poke = new MysqlConnect.PokemonContainer();
        items = new ArrayList<Item>();

    }

    public int getID() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public Pokemon getFirstOut() {
        return poke.getFirstOut();
    }

    public MysqlConnect.PokemonContainer getPokemonContainer() {
        return poke;
    }

    public void set(Player p) {

        this.id = p.id;

        this.x = p.x;
        this.y = p.y;

        this.dir = p.dir;

        this.level = p.level;

        this.lpcx = p.lpcx;
        this.lpcy = p.lpcy;

        this.lpclevel = p.lpclevel;

        this.name = p.name;
        this.imgName = p.imgName;

        this.money = p.money;

    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public void setPokemon(MysqlConnect.PokemonContainer poke) {
        this.poke = poke;
    }

    public void draw(Graphics g) {

        setImage(0, 0);
        g.drawImage(img, 4 * 16, 4 * 16, null);

    }

    public void draw(int x, int y, Graphics g) {
        setImage(0, 0);
        g.drawImage(img, (this.x - x + 4) * 16, (this.y - y + 4) * 16, null);
    }

    public void draw(int x, int y, int xo, int yo, Graphics g) {
        setImage(0, 0);
        g.drawImage(img, (this.x - x + 4) * 16 + xo,
                (this.y - y + 4) * 16 + yo, null);
    }

    protected void setImage(int x, int y) {
        img = sprite[dir];
    }

    public String toString() {
        return name;
    }

    public void loadImages() {
        sprite = new Image[4];

        sprite[0] = PokemonGame.images.getSprite(imgName + "U.png");
        sprite[1] = PokemonGame.images.getSprite(imgName + "D.png");
        sprite[2] = PokemonGame.images.getSprite(imgName + "L.png");
        sprite[3] = PokemonGame.images.getSprite(imgName + "R.png");
    }

    public boolean equals(Object o) {
        Player p = (Player) o;
        if (p.name.equals(name))
            return true;
        return false;
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();

        poke = new MysqlConnect.PokemonContainer();
        items = new ArrayList<Item>();

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

}
