package com.dryerzinia.pokemon.util;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import com.dryerzinia.pokemon.net.Client;

public class WindowEventHandler implements WindowListener {

	@Override
    public void windowClosed(WindowEvent e){}

	@Override
    public void windowClosing(WindowEvent e){

		Client.writeLogoff();
        System.exit(0);

	}

	@Override
    public void windowActivated(WindowEvent e){}

	@Override
    public void windowDeactivated(WindowEvent e){}

	@Override
    public void windowIconified(WindowEvent e){}

	@Override
    public void windowDeiconified(WindowEvent e){}

	@Override
    public void windowOpened(WindowEvent e){}

}
