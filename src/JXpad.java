import java.awt.event.*;
import java.io.*;
import java.awt.*;

public class JXpad extends Thread {

    KeyListener sendKeys;
    String dev;

    public JXpad(KeyListener keys, String device) {

        dev = device;
        sendKeys = keys;

        Thread t = this;
        t.start();

    }

    public void run() {

        BufferedInputStream bis = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(new File(dev)));
        } catch (Exception x) {
            x.printStackTrace();
        }
        System.out.println("Opened");
        XKeyboardSim xks = new XKeyboardSim();
        while (true) {
            try {
                int[] time = new int[4];
                for (int i = 0; i < 4; i++)
                    time[i] = bis.read();
                int val = bis.read() + bis.read() * 256;
                if (val > 32768)
                    val = val - 65536;
                int type = bis.read();
                int num = bis.read();
                XEvent xe = new XEvent(time, val, type, num);
                // System.out.println(xe);
                xks.sendXEvent(xe);
            } catch (Exception x) {
                x.printStackTrace();
            }
        }

    }

    public static void main(String args[]) {

        // new JXpad();

    }

    public class XKeyboardSim extends Component {

        char curr = 'a';

        int count = 0;

        boolean passlb[] = new boolean[14];

        public XKeyboardSim() {
            for (int i = 0; i < 14; i++)
                passlb[i] = false;
        }

        public void sendXEvent(XEvent x) {
            int val = x.getValue();
            int bu = x.getButton();
            if (bu == 6) {
                if (x.type == 1) {
                    if (val == 0)
                        sendKeys.keyReleased(new KeyEvent((Component) this, 0,
                                0, 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED));
                    else
                        sendKeys.keyPressed(new KeyEvent((Component) this, 0,
                                0, 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED));
                    return;
                }
                if (val > 0)
                    sendKeys.keyPressed(new KeyEvent((Component) this, 0, 0, 0,
                            KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED));
                else if (val < 0)
                    sendKeys.keyPressed(new KeyEvent((Component) this, 0, 0, 0,
                            KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED));
                else {
                    sendKeys.keyReleased(new KeyEvent((Component) this, 0, 0,
                            0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED));
                    sendKeys.keyReleased(new KeyEvent((Component) this, 0, 0,
                            0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED));
                }
                return;
            } else if (bu == 7) {
                if (val > 0)
                    sendKeys.keyPressed(new KeyEvent((Component) this, 0, 0, 0,
                            KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED));
                else if (val < 0)
                    sendKeys.keyPressed(new KeyEvent((Component) this, 0, 0, 0,
                            KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED));
                else {
                    sendKeys.keyReleased(new KeyEvent((Component) this, 0, 0,
                            0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED));
                    sendKeys.keyReleased(new KeyEvent((Component) this, 0, 0,
                            0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED));
                }
                return;

            }
            if (!passlb[bu]) {
                if (val > -30000) {
                    passlb[bu] = true;
                    axisDown(bu, true);
                }
            }
            if (passlb[bu]) {
                if (val < -30000) {
                    passlb[bu] = false;
                    axisDown(bu, false);
                }
            }
        }

        public void axisDown(int bu, boolean updown) {
            // System.out.print(bu);
            if (updown) {
                keyPress(bu, true);
                // System.out.println(" Down");
            }
            if (!updown) {
                keyPress(bu, false);
                // System.out.println(" Up");
            }
        }

        public void keyPress(int bu, boolean updown) {
            // System.out.print("a");
            char esc = 27;
            if (!updown) {
                if (bu == 8) {
                    sendKeys.keyReleased(new KeyEvent((Component) this, 0, 0,
                            0, KeyEvent.VK_Z, KeyEvent.CHAR_UNDEFINED));
                } else if (bu == 9) {
                    sendKeys.keyReleased(new KeyEvent((Component) this, 0, 0,
                            0, KeyEvent.VK_X, KeyEvent.CHAR_UNDEFINED));
                } else if (bu == 11) {
                    sendKeys.keyReleased(new KeyEvent((Component) this, 0, 0,
                            KeyEvent.SHIFT_DOWN_MASK, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED));
                }
            } else {
                switch (bu) {
                case 2:
                    // System.out.print(esc+"[1D");
                    break;
                case 5:
                    PokemonGame.pokeg.chattemp = "/f "
                            + PokemonGame.pokeg.players.get(count);
                    count++;
                    if (count > PokemonGame.pokeg.players.size())
                        count = 0;
                    // System.out.print(esc+"[1C");
                    break;
                case 8:
                    sendKeys.keyPressed(new KeyEvent((Component) this, 0, 0, 0,
                            KeyEvent.VK_Z, KeyEvent.CHAR_UNDEFINED));
                    // System.out.print(curr+""+curr);
                    // System.out.print(esc+"[1D");
                    break;
                case 9:
                    sendKeys.keyPressed(new KeyEvent((Component) this, 0, 0, 0,
                            KeyEvent.VK_X, KeyEvent.CHAR_UNDEFINED));
                    break;
                case 12:
                    // curr++;
                    // System.out.print(curr+"");
                    // System.out.print(esc+"[1D");
                    break;
                case 11:
                    sendKeys.keyPressed(new KeyEvent((Component) this, 0, 0,
                            InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED));
                    // curr--;
                    // System.out.print(curr+"");
                    // System.out.print(esc+"[1D");
                    break;
                }
            }
        }

    }

    public class XEvent {

        private int[] time;
        private int val;
        private int type;
        private int num;

        public XEvent(int[] time, int val, int type, int num) {
            this.time = time;
            this.val = val;
            this.type = type;
            this.num = num;
        }

        public int getValue() {
            return val;
        }

        public int getButton() {
            return num;
        }

        public int getType() {
            return type;
        }

        public String toString() {
            return "Button: " + num + "\nType: " + type + "\nValue: " + val;
        }

    }

}
