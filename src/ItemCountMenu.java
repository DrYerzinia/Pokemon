import java.awt.*;
import java.util.HashMap;

public class ItemCountMenu extends GMenu {

	private static final long serialVersionUID = -6111554869637680375L;

	transient int index;
    transient int price;
    transient int count;

    public ItemCountMenu(int index, int price) {
        this.index = index;
        this.price = price;
        this.count = 1;
        message = "";
        init();
        x = 3;
        y = 5;
        w = 7;
        h = 2;
    }

    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(Color.BLACK);
        g.drawString("x" + count, x * 16 + 10, y * 16 + 20);
        g.drawString("$" + (price * count), x * 16 + 50, y * 16 + 20);
    }

    public int totalCost() {
        return price * count;
    }

    public void pressUp() {
        count++;
        if (count == 100)
            count = 1;
    }

    public void pressDown() {
        count--;
        if (count == 0)
            count = 99;
    }

	@Override
	public String toJSON() {

		String json = super.toJSON();
		json = json.replaceFirst("GMenu", "ItemCountMenu");

        return json;

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {
		// TODO Auto-generated method stub
	}

}
