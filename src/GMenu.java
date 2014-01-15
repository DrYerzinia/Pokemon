import java.io.*;
import java.awt.*;
import java.util.*;

public class GMenu implements Serializable, DeepCopy, JSON {

    static final long serialVersionUID = -6634161359468271631L;

    public String message;
    public int x, y, w, h;
    public GMenu nextmenu = null;

    protected transient StringTokenizer st;
    protected transient boolean drawArrow;
    protected transient int drawCycle;

    public transient Image frameing[];
    public transient Image arrow;
    public transient String currentToken[];
    public transient GMenu extramenu = null;
    public transient boolean active;
    public transient Person container;

    public GMenu() {
    }

    public boolean equals(GMenu g) {
        return g.getMessage().equals(message);
    }

    public void init() {
        frameing = new Image[8];

        frameing[0] = PokemonGame.images.getSprite("CornerMenuTL.png");
        frameing[1] = PokemonGame.images.getSprite("CornerMenuTR.png");
        frameing[2] = PokemonGame.images.getSprite("CornerMenuBL.png");
        frameing[3] = PokemonGame.images.getSprite("CornerMenuBR.png");
        frameing[4] = PokemonGame.images.getSprite("TopEdgeMenu.png");
        frameing[5] = PokemonGame.images.getSprite("RightEdgeMenu.png");
        frameing[6] = PokemonGame.images.getSprite("BottomEdgeMenu.png");
        frameing[7] = PokemonGame.images.getSprite("LeftEdgeMenu.png");

        arrow = PokemonGame.pokeg.images.getSprite("ArrowDown.png");

        initst();

    }

    public GMenu(String message, int x, int y, int w, int h) {
        this.message = message;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        init();

        active = false;
        container = null;

    }

    public GMenu(String message, GMenu nm, int x, int y, int w, int h) {
        this.message = message;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        nextmenu = nm;

        init();

        active = false;
        container = null;

    }

    public void initst() {
        st = new StringTokenizer(message, "\n");
        currentToken = new String[h * 16 / 24];
        for (int i = 0; i < h * 16 / 24; i++)
            if (st.hasMoreTokens())
                currentToken[i] = st.nextToken();
        drawArrow = st.hasMoreTokens();
    }

    public void draw(Graphics g) {

        try {

            g.setFont(new Font("monospaced", 0, 12));

            g.drawImage(frameing[0], x * 16, y * 16, null);
            g.drawImage(frameing[1], x * 16 + (w - 1) * 16, y * 16, null);
            g.drawImage(frameing[2], x * 16, y * 16 + (h - 1) * 16, null);
            g.drawImage(frameing[3], x * 16 + (w - 1) * 16, y * 16 + (h - 1)
                    * 16, null);
            for (int i = 1; w - 1 > i; i++) {
                g.drawImage(frameing[4], x * 16 + i * 16, y * 16, null);
                g.drawImage(frameing[6], x * 16 + i * 16, y * 16 + h * 16 - 16,
                        null);
            }
            for (int i = 1; h - 1 > i; i++) {
                g.drawImage(frameing[7], x * 16, y * 16 + i * 16, null);
                g.drawImage(frameing[5], x * 16 + w * 16 - 16, y * 16 + i * 16,
                        null);
            }
            g.setColor(Color.WHITE);
            g.fillRect(x * 16 + 16, y * 16 + 16, (w - 2) * 16, (h - 2) * 16);
            g.setColor(Color.BLACK);
            try {
                for (int i = 0; i < h * 16 / 24; i++)
                    if (currentToken[i] != null)
                        g.drawString(currentToken[i], x * 16 + 8, y * 16 + 16
                                * i + 20);
            } catch (Exception x) {
            }
            drawCycle++;
            if (drawCycle % 6 >= 3 && (drawArrow || nextmenu != null))
                g.drawImage(arrow, x * 16 + (w - 1) * 16 + 2, y * 16 + (h - 1)
                        * 16 + 2, null);
            if (extramenu != null)
                extramenu.draw(g);
        } catch (NullPointerException npe) {
            if (frameing == null) {
                System.out.println("Frameing not loaded!");
                init();
            }
        }
    }

    public void set(GMenu g) {
        this.message = g.message;
        this.x = g.x;
        this.y = g.y;
        this.w = g.w;
        this.h = g.h;

        this.nextmenu = g.nextmenu;
        this.extramenu = g.extramenu;

        frameing = new Image[8];

        frameing[0] = g.frameing[0];
        frameing[1] = g.frameing[1];
        frameing[2] = g.frameing[2];
        frameing[3] = g.frameing[3];
        frameing[4] = g.frameing[4];
        frameing[5] = g.frameing[5];
        frameing[6] = g.frameing[6];
        frameing[7] = g.frameing[7];

        arrow = g.arrow;

        initst();

        active = false;
        container = null;

    }

    public String getMessage() {
        return message;
    }

    public boolean push() {
        for (int i = 0; i < h * 16 / 24 - 1; i++)
            currentToken[i] = currentToken[i + 1];
        if (st.hasMoreTokens())
            currentToken[h * 16 / 24 - 1] = st.nextToken();
        else {
            initst();
            if (container != null)
                container.deactivate();
            active = false;
            return true;
        }

        int k = 0;

        if (currentToken[h * 16 / 24 - 1].equals("  ")) {
            for (int i = 0; i < h * 16 / 24 - 1; i++)
                currentToken[i] = currentToken[i + 1];
            if (st.hasMoreTokens())
                currentToken[h * 16 / 24 - 1] = st.nextToken();
            for (int i = 0; i < h * 16 / 24 - 1; i++)
                currentToken[i] = currentToken[i + 1];
            if (st.hasMoreTokens())
                currentToken[h * 16 / 24 - 1] = st.nextToken();
            return false;
        } else if ((k = currentToken[h * 16 / 24 - 1].indexOf("$#")) != -1) {
            int j = currentToken[h * 16 / 24 - 1].indexOf("$#", k + 1);
            int l = currentToken[h * 16 / 24 - 1].indexOf("$#", j + 1);
            int eventNumber = Integer.parseInt(currentToken[h * 16 / 24 - 1]
                    .substring(k + 2, j));
            String itemGained = currentToken[h * 16 / 24 - 1].substring(j + 2,
                    l);
            currentToken[h * 16 / 24 - 1] = currentToken[h * 16 / 24 - 1]
                    .substring(0, k)
                    + currentToken[h * 16 / 24 - 1].substring(l + 2);
            ArrayList<Item> its = PokemonGame.pokeg.Char.items;
            Iterator<Item> itit = its.iterator();
            boolean found = false;
            while (itit.hasNext()) {
                Item it = itit.next();
                if (it.toString().equals(itemGained)) {
                    it.number++;
                    found = true;
                }
            }
            if (!found) {
                its.add(new Item(itemGained, "", "", 1));
            }
        }

        drawArrow = st.hasMoreTokens();
        return false;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean b) {
        active = b;
    }

    public boolean pushB() {
        return push();
    }

    public void pressUp() {
    }

    public void pressDown() {
    }

    public void pressLeft() {
    }

    public void pressRight() {
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    public Object deepCopy() {
        GMenu g = null;
        if (nextmenu != null)
            g = (GMenu) nextmenu.deepCopy();
        return new GMenu(new String(message), g, x, y, w, h);
    }

	@Override
	public String toJSON() {

    	String json = "{'class':'GMenu'";

    	json += ",'message':" + JSONObject.stringToJSON(message);

		json += ",'x':" + x;
		json += ",'y':" + y;
		json += ",'w':" + w;
		json += ",'h':" + h;

		json += ",'nextmenu':" + JSONObject.objectToJSON(nextmenu);

        json += "}";

        return json;

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {
		// TODO Auto-generated method stub
		
	}

}
