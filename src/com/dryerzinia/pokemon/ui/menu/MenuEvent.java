package com.dryerzinia.pokemon.ui.menu;
public class MenuEvent {

    public static final int Z = 0;
    public static final int X = 1;

    private int bp;
    private int button;

    private SelectionMenu sm;

    public MenuEvent(SelectionMenu sm, int button) {

        this.sm = sm;
        this.bp = sm.selection;
        this.button = button;
    }

    public int getSelection() {
        return bp;
    }

    public boolean isLast() {
        if (bp == sm.getHeight())
            return true;
        return false;
    }

    public SelectionMenu getSelectMenu() {
        return sm;
    }

    public int getButton() {
        return button;
    }

    public String getValue() {
        return sm.currentToken[bp];
    }

    public String getValue(int i) {
        return sm.currentToken[i];
    }

}
