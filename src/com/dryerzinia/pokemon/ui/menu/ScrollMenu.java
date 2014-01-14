package com.dryerzinia.pokemon.ui.menu;
import java.awt.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;

public class ScrollMenu extends SelectionMenu {

    static final long serialVersionUID = -2139226991737527071L;

    transient Image arrowd;

    transient int menuPosition = 0;

    public ScrollMenu() {
    }

    public ScrollMenu(String message, int x, int y, int w, int h, int wm) {
        this.message = message;
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
        arrowd = PokemonGame.images.getSprite("ArrowDown.png");

        widthM = wm;

        initst();

        active = false;
        container = null;

    }

    public void initst() {
        try {
            ArrayList<String> tokens = new ArrayList<String>();
            st = new StringTokenizer(message, "\n");
            while (st.hasMoreTokens())
                tokens.add(st.nextToken());
            currentToken = new String[tokens.size()];
            for (int i = 0; i < tokens.size(); i++)
                currentToken[i] = tokens.get(i);
        } catch (Exception x) {
            System.out.println("initst fail");
        }
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
        try {
            for (int i = menuPosition; i < h + menuPosition - 1; i++)
                if (currentToken[i] != null)
                    g.drawString(currentToken[i], x * 16 + 12, y * 16 + 16
                            * (i - menuPosition) + 20);
        } catch (Exception x) {
        }
        g.drawImage(arrow, x * 16 + 6, y * 16 + 11 + (selection - menuPosition)
                * 16, null);
        drawCycle++;
        if (drawCycle % 6 >= 3)
            g.drawImage(arrowd, x * 16 + (w - 1) * 16 + 2, y * 16 + (h - 1)
                    * 16 + 2, null);
    }

    public boolean push() {

        if (pg != null && overlays[selection] != null) {
            pg.overlay.o = overlays[selection];
        } else if (selection + 1 == currentToken.length)
            return true;
        else
            sendMenuEvent(MenuEvent.Z);
        return false;
    }

    public boolean pushB() {
        return true;
    }

    public void pressUp() {
        if (selection > 0) {
            selection--;
            if (selection - menuPosition < 0)
                menuPosition--;
        }
    }

    public void pressDown() {
        if (selection < currentToken.length - 1) {
            selection++;
            if (selection - menuPosition > h - 2)
                menuPosition++;
        }
    }

    public void pressLeft() {
        pressUp();
    }

    public void pressRight() {
        pressDown();
    }

    public int getHeight() {
        return currentToken.length;
    }

}
