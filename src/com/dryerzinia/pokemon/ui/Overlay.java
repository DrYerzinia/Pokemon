package com.dryerzinia.pokemon.ui;
import java.awt.*;
import java.awt.event.*;

public abstract class Overlay implements KeyListener {

	public transient boolean active = false;

    public abstract void draw(Graphics g);

    public abstract void set(Overlay o);

}
