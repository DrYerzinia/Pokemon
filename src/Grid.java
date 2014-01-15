import java.io.*;
import java.awt.*;
import java.util.*;

public class Grid implements Serializable, JSON {

    static final long serialVersionUID = 8900706544643827271L;

    public transient Level l;

    public transient ArrayList<Tile> g[][];

    public Grid() {
    }

    public Grid(int x, int y) {
        g = new ArrayList[x][y];
    }

    public void add(int x, int y, Tile t) {
        if (g.length < x || g[0].length < y)
            throw new ArrayIndexOutOfBoundsException("Not in grid.");
        if (g[x][y] == null) {
            g[x][y] = new ArrayList<Tile>();
            g[x][y].add(t);
        } else {
            g[x][y].add(t);
        }
    }

    public Object get(int x, int y, int i) {
        if (g[x][y] == null)
            return null;
        if (g[x][y].size() >= i)
            return null;
        return g[x][y].get(i);
    }

    public boolean move(int nx, int ny, int x, int y, Tile t) {
        for (int i = 0; i < g[x][y].size(); i++) {
            if (g[x][y].get(i) == t) {
                g[nx][ny].add(g[x][y].remove(i));
                return true;
            }
        }
        return false;
    }

    public RandomFight getRF(int x, int y) {
        x += 4;
        y += 4;
        if (g.length <= x || g[0].length <= y || x < 0 || y < 0)
            return null;
        for (int i = 0; i < g[x][y].size(); i++)
            if (g[x][y].get(i).rf != null)
                return g[x][y].get(i).rf;
        return null;

    }

    public Object remove(int x, int y, int i) {
        if (g[x][y] == null)
            return null;
        if (g[x][y].size() >= i)
            return null;
        return g[x][y].remove(i);
    }

    public void draw(int x, int y, Graphics gg) {
        for (int j = 0; j < 10; j++) {
            for (int k = 0; k < 9; k++) {
                if (g.length <= j + x || g[0].length <= k + y || j + x < 0
                        || k + y < 0) {
                    gg.setColor(Color.BLACK);
                    gg.fillRect(j * 16, k * 16, j * 16 + 16, k * 16 + 16);
                } else if (g[j + x][k + y] == null) {
                    gg.setColor(Color.BLACK);
                    gg.fillRect(j * 16, k * 16, j * 16 + 16, k * 16 + 16);
                } else {
                    for (int i = 0; i < g[j + x][k + y].size(); i++) {
                        g[j + x][k + y].get(i).draw(j, k, gg);
                    }
                }
            }
        }
    }

    public void draw(int x, int y, Graphics gg, boolean black) {
        for (int j = 0; j < 10; j++) {
            for (int k = 0; k < 9; k++) {
                if (g.length <= j + x || g[0].length <= k + y || j + x < 0
                        || k + y < 0) {
                    if (black) {
                        gg.setColor(Color.BLACK);
                        gg.fillRect(j * 16, k * 16, j * 16 + 16, k * 16 + 16);
                    }
                } else if (g[j + x][k + y] == null) {
                    if (black) {
                        gg.setColor(Color.BLACK);
                        gg.fillRect(j * 16, k * 16, j * 16 + 16, k * 16 + 16);
                    }
                } else {
                    for (int i = 0; i < g[j + x][k + y].size(); i++) {
                        g[j + x][k + y].get(i).draw(j, k, gg);
                    }
                }
            }
        }
    }

    public void act(int x, int y) {
        // for(int j = 0; j < 10; j++){
        // for(int k = 0; k < 9; k++){
        // if(g.length <= j+x || g[0].length <= k+y || j+x < 0 || k+y < 0){

        // } else if(g[j+x][k+y] == null){
        // } else {
        // for(int i = 0; i < g[j+x][k+y].size(); i++){
        // g[j+x][k+y].get(i).act(j, k);
        // }
        // }
        // }
        // }
    }

    public void draw(int x, int y, int xo, int yo, Graphics gg) {
        for (int j = -1; j < 11; j++) {
            for (int k = -1; k < 10; k++) {
                if (g.length <= j + x || g[0].length <= k + y || j + x < 0
                        || k + y < 0) {
                    gg.setColor(Color.BLACK);
                    gg.fillRect(j * 16 + xo, k * 16 + yo, j * 16 + 16 + xo, k
                            * 16 + 16 + yo);
                } else if (g[j + x][k + y] == null) {
                    gg.setColor(Color.BLACK);
                    gg.fillRect(j * 16 + xo, k * 16 + yo, j * 16 + 16 + xo, k
                            * 16 + 16 + yo);
                } else {
                    for (int i = 0; i < g[j + x][k + y].size(); i++) {
                        g[j + x][k + y].get(i).draw(j, k, xo, yo, gg);
                    }
                }
            }
        }
    }

    public void draw(int x, int y, int xo, int yo, Graphics gg, boolean black) {
        for (int j = -1; j < 11; j++) {
            for (int k = -1; k < 10; k++) {
                if (g.length <= j + x || g[0].length <= k + y || j + x < 0
                        || k + y < 0) {
                    if (black) {
                        gg.setColor(Color.BLACK);
                        gg.fillRect(j * 16 + xo, k * 16 + yo, j * 16 + 16 + xo,
                                k * 16 + 16 + yo);
                    }
                } else if (g[j + x][k + y] == null) {
                    gg.setColor(Color.BLACK);
                    gg.fillRect(j * 16 + xo, k * 16 + yo, j * 16 + 16 + xo, k
                            * 16 + 16 + yo);
                } else {
                    for (int i = 0; i < g[j + x][k + y].size(); i++) {
                        g[j + x][k + y].get(i).draw(j, k, xo, yo, gg);
                    }
                }
            }
        }
    }

    public boolean canStepOnB(int x, int y) {
        if (g.length <= x || g[0].length <= y || x < 0 || y < 0)
            return false;
        for (int i = 0; i < g[x][y].size(); i++)
            if (!g[x][y].get(i).canBeSteppedOn)
                return false;
        return true;
    }

    public boolean canStepOn(int x, int y) {
        x += 4;
        y += 4;
        return canStepOnB(x, y);
    }

    public GMenu hasMenu(int x, int y) {
        x += 4;
        y += 4;
        if (g.length <= x || g[0].length <= y || x < 0 || y < 0)
            return null;
        for (int i = 0; i < g[x][y].size(); i++)
            if (g[x][y].get(i).onClick != null)
                return g[x][y].get(i).getMenu(x, y);
        return null;
    }

    public int[] changeLevel(int x, int y) {
        x += 4;
        y += 4;
        int r[] = new int[5];
        r[0] = -1;

        System.out.println("X:" + x + ",Y:" + y + ",L:" + l.id);
        if (x < 0 && l.borderL != null && l.borderL[0] != null) {
            r[0] = l.borders[0];
            r[1] = l.borderL[0].g.getWidth() - 5;
            r[2] = PokemonGame.pokeg.Char.y + l.borderoffset[0];
            r[3] = 2;
            r[4] = -1;
            if (!l.borderL[0].g.canStepOn(r[1], r[2]))
                return new int[5];
            System.out.println("Should Change lvl:" + l.borders[1]);
            return r;
        } else if (x >= l.g.getWidth() && l.borderL != null
                && l.borderL[3] != null) {
            r[0] = l.borders[3];
            r[1] = -5;
            r[2] = PokemonGame.pokeg.Char.y + l.borderoffset[3];
            r[3] = 3;
            r[4] = -1;
            if (!l.borderL[3].g.canStepOn(r[1] + 1, r[2]))
                return new int[5];
            System.out.println("Should Change lvl:" + l.borders[1]);
            return r;
        }
        // top border level change, need to set variables based on transition...
        else if (y < 0 && l.borderL != null && l.borderL[1] != null) {
            r[0] = l.borders[1];
            r[1] = PokemonGame.pokeg.Char.x + l.borderoffset[1];
            r[2] = l.borderL[1].g.getHeight() - 5;
            r[3] = 0;
            r[4] = -1;
            if (!l.borderL[1].g.canStepOn(r[1], r[2]))
                return new int[5];
            System.out.println("Should Change lvl:" + l.borders[1]);
            return r;
        } else if (y >= l.g.getHeight() && l.borderL != null
                && l.borderL[7] != null) {
            r[0] = l.borders[7];
            r[1] = PokemonGame.pokeg.Char.x + l.borderoffset[7];
            r[2] = -5;
            r[3] = 1;
            r[4] = -1;
            if (!l.borderL[7].g.canStepOn(r[1], r[2] + 1))
                return new int[5];
            System.out.println("Should Change lvl:" + l.borders[1]);
            return r;
        }
        if (g.length <= x || g[0].length <= y || x < 0 || y < 0)
            return r;
        for (int i = 0; i < g[x][y].size(); i++)
            if (g[x][y].get(i).changeToLevel != -1) {
                r[0] = g[x][y].get(i).changeToLevel;
                r[1] = g[x][y].get(i).xnew;
                r[2] = g[x][y].get(i).ynew;
                r[3] = g[x][y].get(i).leaveDirection;
                r[4] = g[x][y].get(i).exitDir;
                System.out.println("Should Change");
                return r;
            }
        return r;
    }

    public int getHeight() {
        return g[0].length;
    }

    public int getWidth() {
        return g.length;
    }

    // !!!IMPORTANT
    // GRID loads reference from master tile list so we want to load the tile
    // ids manualy instead of the tiles

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        ois.defaultReadObject();

        int x = ois.readInt();
        int y = ois.readInt();
        g = new ArrayList[x][y];
        for (x = 0; x < g.length; x++) {
            for (y = 0; y < g[0].length; y++) {
                int l = ois.readInt();
                g[x][y] = new ArrayList<Tile>();
                for (int j = 0; j < l; j++) {
                    g[x][y].add(new Tile(ois.readInt()));
                }
            }
        }

        // TODO: Validate loaded object
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();

        oos.writeInt(g.length);
        oos.writeInt(g[0].length);
        for (int x = 0; x < g.length; x++) {
            for (int y = 0; y < g[0].length; y++) {
                oos.writeInt(g[x][y].size());
                for (int j = 0; j < g[x][y].size(); j++) {
                    oos.writeInt(g[x][y].get(j).id);
                }
            }
        }

    }

    public void initLevelReference(Level l) {
        this.l = l;
    }

	@Override
	public String toJSON() {

		String json = "{'class':'Grid'";
		
		json += ",'g':[";
        for (int x = 0; x < g.length; x++) {
    		json += "[";
        	for (int y = 0; y < g[0].length; y++) {
        		json += "[";
                for (int j = 0; j < g[x][y].size(); j++) {
                    json += g[x][y].get(j).id;
            		if(j != g[x][y].size()-1) json+= ",";
                }
        		json += "]";
        		if(y != g[0].length-1) json+= ",";
            }
        	json += "]";
    		if(x != g.length-1) json+= ",";
        }
        json += "]";	

		json += "}";

		return json;

	}

	@Override
	public void fromJSON(HashMap<String, Object> json) {

		Object[] g_map_0 = (Object[]) json.get("g");
		
		g = new ArrayList[g_map_0.length][((Object[])g_map_0[0]).length];

		for(int x = 0; x < g_map_0.length; x++){
			
			Object[] g_map_1 = (Object[]) g_map_0[x];
			for(int y = 0; y < g_map_1.length; y++){

				Object[] g_map_2 = (Object[]) g_map_1[y];
				g[x][y] = new ArrayList<Tile>();

				for(int z = 0; z < g_map_2.length; z++){
					g[x][y].add(
						new Tile(
							((Float)g_map_2[z]).intValue()
						)
					);
				}

			}
		}
	}

}
