import java.awt.*;
import java.util.*;

public class AttackSelectionMenu extends SelectionMenu {

    private Pokemon p;

    public AttackSelectionMenu(Pokemon p, int x, int y, int w, int h) {

        this.p = p;

        message = "";

        for (int i = 0; i < 4; i++) {
            if (p.moves[i] != null) {
                message += "\n" + p.moves[i].name;
            } else {
                message += "\n--";
            }
        }

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        frameing = new Image[8];

        frameing[0] = PokemonGame.images.getSprite("CornerMenuTL.png");
        frameing[1] = PokemonGame.images.getSprite("CornerMenuTR.png");
        frameing[2] = PokemonGame.images.getSprite("CornerMenuBL.png");
        frameing[3] = PokemonGame.images.getSprite("CornerMenuBR.png");
        frameing[4] = PokemonGame.images.getSprite("TopEdgeMenu.png");
        frameing[5] = PokemonGame.images.getSprite("RightEdgeMenu.png");
        frameing[6] = PokemonGame.images.getSprite("BottomEdgeMenu.png");
        frameing[7] = PokemonGame.images.getSprite("LeftEdgeMenu.png");

        arrow = PokemonGame.images.getSprite("ArrowRight.png");

        initst();

        messageMenu = null;

        widthM = 1;

        active = false;
        container = null;

    }

    public void setPokemon(Pokemon p) {

        this.p = p;

        message = "";

        for (int i = 0; i < 4; i++) {
            if (p.moves[i] != null) {
                message += "\n" + p.moves[i].name;
            } else {
                message += "\n--";
            }
        }

        initst();

    }

    public void initst() {
        st = new StringTokenizer(message, "\n");
        currentToken = new String[h * 2];
        for (int i = 0; i < h * 2; i++)
            if (st.hasMoreTokens())
                currentToken[i] = st.nextToken();
        drawArrow = st.hasMoreTokens();
    }

    public void draw(Graphics g) {

        g.drawImage(frameing[0], x * 16, y * 16, null);
        g.drawImage(frameing[1], x * 16 + (w - 1) * 16, y * 16, null);
        g.drawImage(frameing[2], x * 16, y * 16 + (h - 1) * 16, null);
        g.drawImage(frameing[3], x * 16 + (w - 1) * 16, y * 16 + (h - 1) * 16,
                null);
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
        g.setFont(new Font("monospaced", 0, 10));
        for (int i = 0; i < h * 2; i++) {
            if (currentToken[i] != null)
                g.drawString(currentToken[i], x * 16 + 12, y * 16 + i * 8 + 14);
        }
        g.drawImage(arrow, x * 16 + 6, y * 16 + 7 + selection * 8, null);
        if (messageMenu != null)
            messageMenu.draw(g);
        if (submenu != null)
            submenu.draw(g);

        g.drawImage(frameing[0], 0, 4 * 16, null);
        g.drawImage(frameing[1], 4 * 16, 4 * 16, null);
        g.drawImage(frameing[2], 0, 4 * 16 + 1 * 16, null);
        g.drawImage(frameing[3], 4 * 16, 4 * 16 + 1 * 16, null);
        for (int i = 1; 4 > i; i++) {
            g.drawImage(frameing[4], i * 16, 4 * 16, null);
            g.drawImage(frameing[6], i * 16, 4 * 16 + 2 * 16 - 16, null);
        }
        for (int i = 1; 1 > i; i++) {
            g.drawImage(frameing[7], 0, 4 * 16 + i * 16, null);
            g.drawImage(frameing[5], 4 * 16, 4 * 16 + i * 16, null);
        }
        g.setFont(new Font("monospaced", 0, 9));
        g.drawString("Type/", 6, 78);
        if (p.moves[selection] != null) {
            g.drawString(p.moves[selection].type, 12, 84);
            g.setFont(new Font("monospaced", Font.BOLD, 9));
            g.drawString(p.moves[selection].currentpp + "/"
                    + p.moves[selection].pp, 32, 90);
        }
    }

    public boolean push() {

        if (submenu != null) {

            if (submenu.push())
                submenu = null;
            return false;

        } else if (overlays != null && overlays[selection] != null) {

            toset.o = overlays[selection];

        } else if (submenus != null && submenus[selection] != null) {

            submenu = submenus[selection];

        } else if (selection == h - 2 && exitOnLast) {

            return true;

        } else
            sendMenuEvent(MenuEvent.Z);

        return false;

    }

    public boolean update() {
        return true;
    }

    // public boolean pushB(){
    // return super.pushB();
    // }

    public void pressUp() {
        if (submenu != null)
            submenu.pressLeft();
        else if (selection > widthM - 1)
            selection -= widthM;
    }

    public void pressDown() {
        if (submenu != null)
            submenu.pressRight();
        else if (selection < (h - 2) * 2 + 1)
            selection += widthM;
    }

    public void pressLeft() {
        if (submenu != null)
            submenu.pressUp();
        else if (selection > 0)
            selection--;
    }

    public void pressRight() {
        if (submenu != null)
            submenu.pressDown();
        else if (selection < (h - 2) * 2 + 1)
            selection++;
    }

	@Override
	public String toJSON() {

		String json = super.toJSON();
		json = json.replaceFirst("SelectionMenu", "AttackSelectionMenu");
		json = json.substring(0,json.length()-1);

	    json += ",'p':" + JSONObject.objectToJSON(p);

        json += "}";

        return json;

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {
		// TODO Auto-generated method stub
	}

}
