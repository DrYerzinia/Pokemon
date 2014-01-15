package com.dryerzinia.pokemon.obj;
import java.awt.Image;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Item implements Serializable {

    static final long serialVersionUID = -6980864752241593937L;

    public static final int THUNDER_STONE = -2;
    public static final int MOON_STONE = -3;
    public static final int FIRE_STONE = -4;

    public transient Image sprite;

    public String name;
    public String description;
    public String use;

    public int number;
    public transient int price;

    public transient boolean added = false;

    public Item() {
    }

    public Item(String name, String description, String use, int number) {

        this.name = name;
        this.description = description;
        this.use = use;

        this.number = number;

    }

    public Item(String name, String description, String use, int number,
            int price) {

        this.name = name;
        this.description = description;
        this.use = use;

        this.number = number;
        this.price = price;

    }

    public void set(Item i) {
        name = i.name;
        description = i.description;
        use = i.use;
        number = i.number;
    }

    public String toString() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {

        ois.defaultReadObject();

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

}
