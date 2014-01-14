package com.dryerzinia.pokemon.obj;
import java.util.*;

public class Pokeball extends Item {

    static final long serialVersionUID = 9051369486586172718L;

    public int chance;

    public Pokeball() {
    }

    public Pokeball(String name, String description, String use, int number,
            int chance) {
        super(name, description, use, number);
        this.chance = chance;
    }

    public Pokeball(String name, String description, String use, int number,
            int chance, int price) {
        super(name, description, use, number, price);
        this.chance = chance;
    }

    public boolean captured(Pokemon p) {

        Random rand = new Random(); // TODO: NO SEED will be same always fix
        int r = rand.nextInt(255);
        double C = p.getRareness();
        double A = p.currentHP, B = p.getTotalHP();
        double D = 0; // TODO: what is this for its always 0 right now
        if (A > 255)
            A = (A / 2) / 2;
        if (B > 255)
            B = (B / 2) / 2;
        int E = (int) ((((((B * 4.0) - (A * 2.0)) * C) / B) + D + 1.0) / 7);
        if (E > 255)
            E = 255;
        System.out.println("Cap:" + E + "A" + A + "b" + B + "c" + C + "d" + D);
        if (r < E)
            return true;
        return false;
    }

    public void set(Item i) {
        Pokeball p = (Pokeball) i;
        super.set(i);
        this.chance = p.chance;
    }
}
