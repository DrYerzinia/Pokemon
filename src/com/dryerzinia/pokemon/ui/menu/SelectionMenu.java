package com.dryerzinia.pokemon.ui.menu;
import java.awt.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.util.JSONArray;
import com.dryerzinia.pokemon.ui.Overlay;
import com.dryerzinia.pokemon.ui.OverlayO;

public class SelectionMenu extends GMenu {

    static final long serialVersionUID = 5709845116293032515L;

    protected int widthM;

    public transient int selection = 0;

    public transient boolean exitOnLast = true;

    public transient GMenu messageMenu = null;
    public transient PokemonGame pg = null;

    public transient Overlay overlays[];
    public transient OverlayO toset;

    public GMenu submenus[];

    public transient GMenu submenu = null;

    public transient ArrayList<MenuListener> menuListeners;

    public SelectionMenu() {
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

        arrow = PokemonGame.images.getSprite("ArrowRight.png");

        initst();
    }

    public SelectionMenu(String message, int x, int y, int w, int h, int wm) {
        this.message = message;

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        widthM = wm;

        init();

        active = false;
        container = null;

    }

    public SelectionMenu(String message, GMenu m, int x, int y, int w, int h,
            int wm) {
        this.message = message;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        messageMenu = m;

        widthM = wm;

        init();

        active = false;
        container = null;

    }

    public void initst() {
        st = new StringTokenizer(message, "\n");
        currentToken = new String[h * widthM];
        for (int i = 0; i < h * widthM * 2; i++)
            if (st.hasMoreTokens())
                currentToken[i] = st.nextToken();
        drawArrow = st.hasMoreTokens();
    }

    public void addMenuListener(MenuListener ml) {
        if (menuListeners == null) {
            menuListeners = new ArrayList<MenuListener>();
        }

        menuListeners.add(ml);

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
        g.setFont(new Font("monospaced", 0, 12));
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < widthM; j++) {
                if (currentToken[i * widthM + j] != null)
                    g.drawString(currentToken[i * widthM + j], x * 16
                            + (int) (((w * 16) / widthM) * j) + 12, y * 16 + 16
                            * i + 20);
            }
        }
        g.drawImage(arrow, x * 16
                + (int) ((selection % widthM) * (w * 16) / widthM) + 6, y * 16
                + 11 + (selection / widthM) * 16, null);
        if (messageMenu != null)
            messageMenu.draw(g);
        if (submenu != null)
            submenu.draw(g);
    }

    public boolean push() {

        System.out.println("A " + submenus);

        if (submenu != null) {

            if (submenu.push())
                submenu = null;
            return false;

        } else if (overlays != null && overlays[selection] != null) {

            toset.o = overlays[selection];
            toset.o.active = true;

        } else if (submenus != null && submenus[selection] != null) {

            submenu = submenus[selection];

        } else if (selection == h - 2 && exitOnLast) {

            return true;

        }

        sendMenuEvent(MenuEvent.Z);

        return false;
    }

    public int getHeight() {
        return h - 2;
    }

    protected void sendMenuEvent(int button) {

        if (menuListeners != null) {
            MenuEvent e = new MenuEvent(this, button);
            for (int i = 0; i < menuListeners.size(); i++) {
                menuListeners.get(i).MenuPressed(e);
            }
        }
    }

    public boolean pushB() {
        System.out.println("B");
        if (submenu != null) {
            if (submenu.pushB()) {
                submenu = null;
            }
            return false;
        } else {
            sendMenuEvent(MenuEvent.X);
        }
        return true;
    }

    public void pressUp() {
        System.out.println("u");
        if (submenu != null)
            submenu.pressLeft();
        else if (selection > widthM - 1)
            selection -= widthM;
    }

    public void pressDown() {
        System.out.println("d");
        if (submenu != null)
            submenu.pressRight();
        else if (selection < (h - 2) * widthM)
            selection += widthM;
    }

    public void pressLeft() {
        System.out.println("l");
        if (submenu != null)
            submenu.pressUp();
        else if (selection > 0)
            selection--;
    }

    public void pressRight() {
        System.out.println("r");
        if (submenu != null)
            submenu.pressDown();
        else if (selection < (h - 2) * widthM + 1)
            selection++;
    }

	@Override
	public String toJSON() {

		String json = super.toJSON();
		json = json.substring(0,json.length()-1);

	    json += ",'widthM':" + widthM;

	    json += ",'submenus':" + JSONArray.objectArrayToJSON(submenus);

        json += "}";

        return json;

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {
		// TODO Auto-generated method stub
	}

}
