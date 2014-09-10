package com.dryerzinia.pokemon.ui.menu;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;


public final class MenuStack {	
	private static Deque<Menu> menus = new ArrayDeque<Menu>();
	
	private MenuStack() {
		// no instantiation
	}
	
	public static void push(Menu menu) {
		menus.push(menu);
	}
	
	public static void pop() {
		menus.pop();
	}
	
	public static void handleInput() {
		Menu menu = menus.peek();
		if (menu != null)
			menu.handleInput();
	}
	
	public static void update(long deltaTime) {
		Menu menu = menus.peek();
		if (menu != null)
			menu.update(deltaTime);
	}
	
	// render the menus in reverse z-order
	public static void render(Graphics g) {
		Iterator<Menu> iter = menus.descendingIterator();
		while (iter.hasNext()) {
			Menu menu = iter.next();
			menu.render(g);
		}
	}
	
	public static boolean isEmpty() {
		return menus.isEmpty();
	}

	public static void print() {
		for (Menu m : menus)
			System.out.println(m);
		System.out.println();
		
	}
}
