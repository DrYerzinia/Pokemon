package com.dryerzinia.pokemon.ui.menu;
import java.awt.*;
import java.util.*;

import com.dryerzinia.pokemon.PokemonGame;
import com.dryerzinia.pokemon.net.Client;
import com.dryerzinia.pokemon.obj.Item;
import com.dryerzinia.pokemon.obj.Player;
import com.dryerzinia.pokemon.obj.Pokeball;
import com.dryerzinia.pokemon.util.JSONObject;
import com.dryerzinia.pokemon.util.ResourceLoader;
import com.dryerzinia.pokemon.util.event.AbstractMenuListener;

public class ItemMenu extends ScrollMenu {

	public int numbers[];
	public boolean prices = false;

	public ArrayList<Item> items;

	public Player p;

	public ItemCountMenu itemCountMenu;
	public boolean itemCountMenuActive = false;
	public GMenu ItemAsk;
    public SelectionMenu yesnoMenu;
    public boolean yesnoMenuActive = false;

    public ItemMenu(ArrayList<Item> items, int x, int y, int w, int h, int wm) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        frameing = new Image[8];

        frameing[0] = ResourceLoader.getSprite("CornerMenuTL.png");
        frameing[1] = ResourceLoader.getSprite("CornerMenuTR.png");
        frameing[2] = ResourceLoader.getSprite("CornerMenuBL.png");
        frameing[3] = ResourceLoader.getSprite("CornerMenuBR.png");
        frameing[4] = ResourceLoader.getSprite("TopEdgeMenu.png");
        frameing[5] = ResourceLoader.getSprite("RightEdgeMenu.png");
        frameing[6] = ResourceLoader.getSprite("BottomEdgeMenu.png");
        frameing[7] = ResourceLoader.getSprite("LeftEdgeMenu.png");

        arrow = ResourceLoader.getSprite("ArrowRight.png");
        arrowd = ResourceLoader.getSprite("ArrowDown.png");

        this.items = items;

        widthM = wm;

        active = false;
        container = null;

        yesnoMenu = new SelectionMenu("Yes\nNo", null, 6, 4, 3, 3, 1);
        yesnoMenu.exitOnLast = false;
        yesnoMenu.addMenuListener(new AbstractMenuListener() {
            public void MenuPressed(MenuEvent e) {
                int i = e.getSelection();
                if (i == 0 && e.getButton() == MenuEvent.Z) {
                    if (p.money >= itemCountMenu.totalCost()) {
                        ItemAsk = new GMenu("Here you are!\nThank you!", 0, 6,
                                10, 3);
                        yesnoMenuActive = false;
                        itemCountMenuActive = false;
                        Iterator<Item> iti = p.items.iterator();
                        boolean found = false;
                        while (iti.hasNext()) {
                            Item it = iti.next();
                            if (addto(it)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            addto2();
                        }
                        p.money -= itemCountMenu.totalCost();
                    } else {
                        ItemAsk = new GMenu("You don't have\nenough money.", 0,
                                6, 10, 3);
                        yesnoMenuActive = false;
                        itemCountMenuActive = false;
                    }
                } else if (i == 1 || e.getButton() == MenuEvent.X) {
                    yesnoMenuActive = false;
                    itemCountMenuActive = false;
                    ItemAsk = null;
                    System.out.println("yar");
                }
                System.out.println("B:" + e.getButton());
            }
        });

    }

    public boolean addto(Item it) {
        if (it.name.toLowerCase().equals(
                items.get(selection).name.toLowerCase())) {
            it.number += itemCountMenu.count;
            Client.writeItem(it);
            return true;
        }
        return false;
    }

    public void addto2() {
        Item i = items.get(selection);
        Item i2;
        if (i instanceof Pokeball) {
            i2 = new Pokeball();
        } else {
            i2 = new Item();
        }
        i2.set(i);
        i2.number = itemCountMenu.count;
        Client.writeItem(i2);
        p.items.add(i2);
    }

    public void draw(Graphics g) {

        currentToken = new String[items.size() + 1];
        numbers = new int[items.size() + 1];

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getNumber() != 0) {
                currentToken[i] = items.get(i).toString();
                numbers[i] = items.get(i).getNumber();
            }
        }

        currentToken[items.size()] = "Cancel";
        numbers[items.size()] = -1;

        super.draw(g);
        try {
            g.setFont(new Font("monospaced", 0, 9));
            for (int i = menuPosition; i < h + menuPosition - 1; i++) {
                if (currentToken[i] != null && numbers[i] != -1) {
                    if (!prices)
                        g.drawString("x" + numbers[i], x * 16 + w * 16 - 28, y
                                * 16 + 16 * (i - menuPosition) + 20);
                    else
                        g.drawString("$" + items.get(i).price, x * 16 + w * 16
                                - 29, y * 16 + 16 * (i - menuPosition) + 24);
                }
            }
        } catch (Exception x) {
        }

        if (itemCountMenuActive)
            itemCountMenu.draw(g);
        if (ItemAsk != null)
            ItemAsk.draw(g);
        if (yesnoMenuActive)
            yesnoMenu.draw(g);

    }

    public void pressUp() {
        if (yesnoMenuActive) {
            yesnoMenu.pressUp();
        } else if (itemCountMenuActive) {
            itemCountMenu.pressUp();
        } else if (selection > 0) {
            selection--;
            if (selection - menuPosition < 0)
                menuPosition--;
        }
    }

    public void pressDown() {
        if (yesnoMenuActive) {
            yesnoMenu.pressDown();
        } else if (itemCountMenuActive) {
            itemCountMenu.pressDown();
        } else if (selection < currentToken.length - 1) {
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

        if (selection + 1 == currentToken.length) {
            return true;
        } else if (!itemCountMenuActive && prices) {
            if (ItemAsk == null) {
                itemCountMenu = new ItemCountMenu(selection,
                        items.get(selection).price);
                itemCountMenuActive = true;
            } else {
                ItemAsk = null;
            }
        } else if (ItemAsk == null && prices) {
            ItemAsk = new GMenu(items.get(selection).name
                    + "?\nThat will be\n$" + items.get(selection).price
                    + " OK?", 0, 6, 10, 3);
        } else if (ItemAsk != null && prices) {
            if (!yesnoMenuActive) {
                if (ItemAsk.push()) {
                    yesnoMenuActive = true;
                    ItemAsk.push();
                }
            } else {
                yesnoMenu.push();
            }
        }
        sendMenuEvent(MenuEvent.Z);

        return false;
    }

    public boolean pushB() {
        if (yesnoMenuActive) {
            yesnoMenu.pushB();
            return false;
        } else if (itemCountMenuActive) {
            itemCountMenuActive = false;
            return false;
        } else {
            sendMenuEvent(MenuEvent.X);
        }
        System.out.println("???");
        return true;
    }

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}
	@Override
	public void fromJSON(HashMap<String, Object> json) {
		// TODO Auto-generated method stub
	}

}
