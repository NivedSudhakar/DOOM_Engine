import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.*;

/**
 *
 * @author user
 */
public class DOOMEngine {


    public static JFrame frame;
    BufferedImage img;
    public static int WIDTH = 800;
    public static int HEIGHT = 600;

    public static Player p;

    Graphics g;

    public DOOMEngine() {
    }

    public static void main(String[] a){

        p = new Player(70, -110, 20, 0, 0);

        DOOMEngine t=new DOOMEngine();

        frame = new JFrame("WINDOW");
        frame.setVisible(true);
        frame.setResizable(false);

        t.start();
        frame.add(new JLabel(new ImageIcon(t.getImage())));






        frame.addKeyListener(new KeyAdapter() {
            // Key Pressed method
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_W){
                    p.movePlayer(1,0, 0,0);
                }
                if(e.getKeyCode() == KeyEvent.VK_S){
                    p.movePlayer(-1,0, 0,0);
                }
                if(e.getKeyCode() == KeyEvent.VK_A){
                    p.movePlayer(1, -1, 0,0);
                }
                if(e.getKeyCode() == KeyEvent.VK_D){
                    p.movePlayer(1, 1, 0,0);
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    p.movePlayer(0,0, 0,1);
                }
                if(e.getKeyCode() == KeyEvent.VK_LEFT){
                    p.movePlayer(0,0, 0,-1);
                }



            }
        });

        frame.pack();



        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        int delay = 1000/24; //milliseconds
        ActionListener taskPerformer = (ActionEvent evt) -> {

            t.clearBackground();
            t.draw();
            frame.repaint();
        };
        new Timer(delay, taskPerformer).start();



    }

    public Image getImage() {
        return img;
    }

    public void start(){

        img = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
        boolean running=true;
        while(running){
            BufferStrategy bs=frame.getBufferStrategy();

            if(bs==null){
                frame.createBufferStrategy(4);
                return;
            }
            for (int i = 0; i < WIDTH * HEIGHT; i++)
                pixels[i] = 0;

            g= bs.getDrawGraphics();
            g.drawImage(img, 0, 0, WIDTH, HEIGHT, null);
            g.dispose();
            bs.show();



        }
    }

    public Color pixel(int colorInt) {
        Color color = null;

        switch(colorInt) {
            case 0 -> {
                color = new Color(255, 255, 0);
            }
            case 1 -> {
                color = new Color(160, 160, 0);
            }
            case 2 -> {
                color = new Color(0, 255, 0);
            }
            case 3 -> {
                color = new Color(0, 160, 0);
            }
            case 4 -> {
                color = new Color(0, 255, 255);
            }
            case 5 -> {
                color = new Color(0, 160, 160);
            }
            case 6 -> {
                color = new Color(160, 100, 0);
            }
            case 7 -> {
                color = new Color(110, 50, 0);
            }
            case 8 -> {
                color = new Color(0, 60, 130);

            }



        }

        return color;

    }

    public void clearBackground() {
        for(int x = 0; x < WIDTH; x ++) {
            for(int y = 0; y < HEIGHT; y++) {
                img.setRGB(x, y, Color.black.getRGB());
            }
        }


    }

    public void draw() {
        int[] wx = new int[4];
        int[] wy = new int[4];
        int[] wz = new int[4];

        double cos = Math.cos(Math.toRadians(p.playerRotation));
        double sin = Math.sin(Math.toRadians(p.playerRotation));

        int x1 = 40-p.x;
        int y1 = 10-p.y;
        int x2 = 40-p.x;
        int y2 = 290 -p.y;

        wx[0] = (int) (x1*cos-y1*sin);
        wx[1] = (int) (x2*cos-y2*sin);
        wx[2] = wx[0];
        wx[3] = wx[1];



        wy[0]= (int) (y1*cos+x1*sin);
        wy[1] = (int) (y2*cos+x2*sin);
        wy[2] = wy[0];
        wy[3] = wy[1];


        wz[0] = 0-p.z + (p.playerPitch*wy[0])/32;
        wz[1] = 0-p.z + (p.playerPitch*wy[1])/32;
        wz[2] = wz[0] + 40;
        wz[3] = wz[1] + 40;

        if(wy[0] < 1 && wy[1] < 1) {
            return;
        }
        if(wy[0]<1) {
            int[] vals = clipPlayer(wx[0], wy[0], wz[0], wx[1], wy[1], wz[1]);
            wx[0] = vals[0];
            wy[0] = vals[1];
            wz[0] = vals[2];

            vals = clipPlayer(wx[2], wy[2], wz[2], wx[3], wy[3], wz[3]);
            wx[2] = vals[0];
            wy[2] = vals[1];
            wz[2] = vals[2];


        }
        if(wy[1]<1) {
            int[] vals = clipPlayer(wx[1], wy[1], wz[1], wx[0], wy[0], wz[0]);
            wx[1] = vals[0];
            wy[1] = vals[1];
            wz[1] = vals[2];

            vals = clipPlayer(wx[3], wy[3], wz[3], wx[2], wy[2], wz[2]);
            wx[3] = vals[0];
            wy[3] = vals[1];
            wz[3] = vals[2];


        }

        wx[0] = wy[0] == 0 ? 0 : wx[0]*200/wy[0] + WIDTH/2;
        wy[0] = wy[0] == 0 ? 0 : wz[0]*200/wy[0] + HEIGHT/2;

        wx[1] = wx[1]*200/wy[1]+WIDTH/2;
        wy[1] = wz[1]*200/wy[1]+HEIGHT/2;

        wx[2] = wx[2]*200/wy[2]+WIDTH/2;
        wy[2] = wz[2]*200/wy[2]+HEIGHT/2;

        wx[3] = wx[3]*200/wy[3]+WIDTH/2;
        wy[3] = wz[3]*200/wy[3]+HEIGHT/2;


        int[] xCoords = {wx[3], wx[1], wx[0], wx[2]};
        int[] yCoords = {wy[3], wy[1], wy[0], wy[2]};



        drawWall(xCoords, yCoords);





        //System.out.println(wx[0] + "," + wy[0]);


    }

    public void drawWall(int[] x, int[] y) {


        for(int i = 0; i < x.length; i ++) {
            if (x[i] < 1) {
                x[i] = 1;
            }
            if(x[i] > WIDTH-1) {
                x[i] = WIDTH-1;
            }
        }

        for(int i = 0; i < y.length; i ++) {
            if (y[i] < 1) {
                y[i] = 1;
            }
            if(y[i] > HEIGHT-1) {
                y[i] = HEIGHT-1;
            }
        }




        Graphics g = img.getGraphics();

        g.setColor(pixel(0));

        //g.drawPolygon(x, y, x.length);

        g.fillPolygon(x, y, x.length);


        g.dispose();


    }


    public int[] clipPlayer(int x1, int y1, int z1, int x2, int y2, int z2) {
        double s = y1/(y1-y2);

        x1 = (int) (x1 + s*(x2-x1));
        y1 = (int) (y1 + s*(y2-y1));
        if(y1 == 0) {
            y1 = 1;
        }
        z1 = (int) (z1 + s*(z2-z1));

        int[] vals = {x1, y1, z1};

        return vals;
    }





}

class Player {

    public int playerPitch = 0;
    public int playerRotation = 70;

    public int x,y,z = 0;

    public Player(int x, int y, int z, int playerPitch, int playerRotation) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.playerPitch = playerPitch;
        this.playerRotation = playerRotation;
    }

    public void movePlayer(int dir, int strafe, int pitch, int rotation) {
        playerRotation += 4*rotation;

        if(playerRotation < 0)
            playerRotation += 360;
        if(playerRotation > 359)
            playerRotation -= 360;

        double dx = Math.sin(Math.toRadians(playerRotation)) * 10 * dir;
        double dy = Math.cos(Math.toRadians(playerRotation)) * 10 * dir;

        if(strafe == 0) {
            x += dx;
            y+= dy;
        }


        x += dy*strafe;
        y-=dx*strafe;

        System.out.println(strafe);





    }
}