package com.dryerzinia.pokemon.util.event;
import com.dryerzinia.pokemon.ui.menu.MenuEvent;
import com.dryerzinia.pokemon.ui.menu.MenuListener;

public abstract class AbstractMenuListener implements MenuListener {

    public abstract void MenuPressed(MenuEvent e);

}
