import java.awt.*;
import java.util.*;

public class PokemonBoxMenu extends ScrollMenu {

    static final long serialVersionUID = 865704629980284330L;

    public static final int WIDTHDRAW = 0;
    public static final int DEPOSIT = 1;
    public static final int RELEASE = 2;

    int mode;

    transient int[] numbers;

    public PokemonBoxMenu() {
    }

    public PokemonBoxMenu(int m) {
        this.x = 2;
        this.y = 1;
        this.w = 8;
        this.h = 5;
        mode = m;

        frameing = new Image[8];

        frameing[0] = PokemonGame.pokeg.images.getSprite("CornerMenuTL.png");
        frameing[1] = PokemonGame.pokeg.images.getSprite("CornerMenuTR.png");
        frameing[2] = PokemonGame.pokeg.images.getSprite("CornerMenuBL.png");
        frameing[3] = PokemonGame.pokeg.images.getSprite("CornerMenuBR.png");
        frameing[4] = PokemonGame.pokeg.images.getSprite("TopEdgeMenu.png");
        frameing[5] = PokemonGame.pokeg.images.getSprite("RightEdgeMenu.png");
        frameing[6] = PokemonGame.pokeg.images.getSprite("BottomEdgeMenu.png");
        frameing[7] = PokemonGame.pokeg.images.getSprite("LeftEdgeMenu.png");

        arrow = PokemonGame.pokeg.images.getSprite("ArrowRight.png");
        arrowd = PokemonGame.pokeg.images.getSprite("ArrowDown.png");

        widthM = 1;

        active = false;
        container = null;

    }

    public void draw(Graphics g) {

        Pokemon poke[] = PokemonGame.pokeg.Char.poke.belt;
        ArrayList<Pokemon> pokemon = PokemonGame.pokeg.Char.poke.box;

        int j = 0;
        for (int i = 0; i < pokemon.size(); i++) {
            for (int k = 0; k < 6; k++) {
                if (poke[k] == pokemon.get(i)) {
                    j++;
                    break;
                }
            }
        }

        if (mode == WIDTHDRAW || mode == RELEASE) {
            currentToken = new String[pokemon.size() - j + 1];
            numbers = new int[pokemon.size() - j + 1];
            int k = 0;
            for (int i = 0; i < pokemon.size(); i++) {
                if (pokemon.get(i).getLocation() == 6) {
                    currentToken[k] = pokemon.get(i).getName();
                    numbers[k] = pokemon.get(i).getLevel();
                    k++;
                }
            }
        } else if (mode == DEPOSIT) {
            currentToken = new String[j + 1];
            numbers = new int[j + 1];
            for (int i = 0; i < j; i++) {
                currentToken[i] = poke[i].getName();
                numbers[i] = poke[i].getLevel();
            }
        }

        currentToken[currentToken.length - 1] = "Cancel";
        numbers[numbers.length - 1] = -1;

        super.draw(g);
        try {
            g.setFont(new Font("monospaced", 0, 9));
            for (int i = menuPosition; i < h + menuPosition - 1; i++) {
                if (currentToken[i] != null && numbers[i] != -1) {
                    g.drawString(":L" + numbers[i], x * 16 + w * 16 - 28, y
                            * 16 + 16 * (i - menuPosition) + 20);
                }
            }
        } catch (Exception x) {
        }

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

    public boolean push() {

        if (selection == currentToken.length - 1) {
            return true;
        }

        Pokemon poke[] = PokemonGame.pokeg.Char.poke.belt;
        ArrayList<Pokemon> pokemon = PokemonGame.pokeg.Char.poke.box;

        Pokemon p = null;

        int k = 0;
        int j = 0;

        if (mode == WIDTHDRAW || mode == RELEASE) {
            for (int i = 0; i < pokemon.size(); i++) {
                boolean found = false;
                for (int l = 0; l < 6; l++) {
                    if (poke[l] == pokemon.get(i)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if (selection == j) {
                        p = pokemon.get(i);
                    }
                    j++;
                }
            }
        } else if (mode == DEPOSIT) {
            p = poke[selection];
        }

        switch (mode) {
        case WIDTHDRAW:
            for (int i = 0; i < 6; i++) {
                if (poke[i] == null) {
                    p.location = i;
                    poke[i] = p;
                    break;
                }
            }
            PokemonGame.pokeg.Char.poke.belt = poke;
            break;
        case DEPOSIT:
            p.location = 6;
            int l = 0;
            for (int i = 0; i < 6; i++) {
                poke[l] = poke[i];
                if (p != poke[i]) {
                    l++;
                }
            }
            for (; l < 6; l++) {
                poke[l] = null;
            }
            PokemonGame.pokeg.Char.poke.belt = poke;
            break;
        case RELEASE:
            break;
        }

        // TODO: Needs to be Serialized funtion in Pokemon Game

        try {
            PokemonGame.pokeg.oos2.writeInt(PokemonServer.ID_GET_POKEMON);
            PokemonGame.pokeg.oos2.writeObject(p);
        } catch (Exception x) {
            x.printStackTrace();
        }

        // Fight.sendNowPokemon();
        // TODO: FIX THIS!

        sendMenuEvent(MenuEvent.Z);

        return false;
    }

    public boolean pushB() {
        sendMenuEvent(MenuEvent.X);
        return true;
    }

}
