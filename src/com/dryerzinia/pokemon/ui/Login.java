package com.dryerzinia.pokemon.ui;
import java.awt.*;
import java.awt.event.*;

public class Login extends Overlay {

    public String username = "";
    public String password = "";
    public String location = "75.70.0.170";

    int selection = 0;

    public void draw(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 160, 144);
        g.setColor(Color.WHITE);
        g.setFont(new Font("monospaced", 0, 16));
        g.drawString("LOGIN", 50, 20);
        g.setFont(new Font("monospaced", 0, 12));
        if (selection == 0)
            g.setColor(Color.BLUE);
        g.drawString("Username: " + username, 10, 40);
        g.setColor(Color.WHITE);
        if (selection == 1)
            g.setColor(Color.BLUE);
        String s = "";
        for (int i = 0; i < password.length(); i++)
            s += "*";
        g.drawString("Password: " + s, 10, 60);
        g.setColor(Color.WHITE);
        if (selection == 2)
            g.setColor(Color.BLUE);

        g.drawString("Location:", 10, 80);

        if (location.length() < 9)
            g.drawString(location, 80, 80);

        else {

            int start = 10;

            g.drawString(location.substring(0, 9), 80, 80);

            int i = 1;
            while (start < location.length()) {
                int end = start + 20;
                if (end > location.length())
                    end = location.length();
                g.drawString(location.substring(start, end), 10, 80 + 20 * i);
                i++;
                start += 20;
            }

        }

        g.setColor(Color.WHITE);
        if (selection == 3)
            g.setColor(Color.BLUE);
        g.drawString("GO", 75, 140);

    }

    public void set(Overlay o) {
    }

    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();
        if (c == KeyEvent.VK_UP) {
            selection--;
            if (selection == -1)
                selection = 3;
        } else if (c == KeyEvent.VK_LEFT) {
        } else if (c == KeyEvent.VK_RIGHT) {
        } else if (c == KeyEvent.VK_DOWN) {
            selection++;
            if (selection == 4)
                selection = 0;
        } else if (c == KeyEvent.VK_ENTER) {
            selection++;
            if (selection == 4) {
                active = false;
                selection = 0;
            }
        } else if (c == KeyEvent.VK_BACK_SPACE) {
            try {
                switch (selection) {
                case 0:
                    username = username.substring(0, username.length() - 1);
                    break;
                case 1:
                    password = password.substring(0, password.length() - 1);
                    break;
                case 2:
                    location = location.substring(0, location.length() - 1);
                    break;
                }
            } catch (Exception x) {
            }
        } else {
            // char d = e.getKeyChar();
            if (65535 != (int) e.getKeyChar() && 10 != (int) e.getKeyChar()
                    && !e.isActionKey()) {
                char d = e.getKeyChar();
                switch (selection) {
                case 0:
                    username += d;
                    break;
                case 1:
                    password += d;
                    break;
                case 2:
                    location += d;
                    break;
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

}
