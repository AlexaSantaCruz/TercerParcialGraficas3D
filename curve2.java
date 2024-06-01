import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class curve2 extends JPanel implements ActionListener, KeyListener {
    private BufferedImage buffer;
    private Timer timer;
    private double angleX = 0;
    private double angleY = 0;
    private double angleZ = 0;
    private boolean autoRotate = true;
    private double scale = 10; // Factor de escala para aumentar el tamaño de la curva

    public curve2() {
        buffer = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        timer = new Timer(30, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
    }

    private void drawNonVerticalLineBresenham(int x0, int y0, int x1, int y1, Color color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        int e2;

        while (true) {
            putPixel(x0, y0, color);

            if (x0 == x1 && y0 == y1) {
                break;
            }

            e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    private void putPixel(int x, int y, Color c) {
        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            buffer.setRGB(x, y, c.getRGB());
        }
    }

    private void clearBuffer() {
        buffer =new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics();
        g2d.dispose();
    }

    private double[] rotatePoint(double x, double y, double z) {
        double cosX = Math.cos(angleX), sinX = Math.sin(angleX);
        double cosY = Math.cos(angleY), sinY = Math.sin(angleY);
        double cosZ = Math.cos(angleZ), sinZ = Math.sin(angleZ);

        // Rotate around X axis
        double y1 = y * cosX - z * sinX;
        double z1 = y * sinX + z * cosX;
        y = y1;
        z = z1;

        // Rotate around Y axis
        double x1 = x * cosY + z * sinY;
        z1 = -x * sinY + z * cosY;
        x = x1;
        z = z1;

        // Rotate around Z axis
        x1 = x * cosZ - y * sinZ;
        y1 = x * sinZ + y * cosZ;
        x = x1;
        y = y1;

        return new double[]{x, y, z};
    }

    private int[] projectPoint(double x, double y, double z) {
        double scalePerspective = 1000 / (z + 1000); // Simple perspective projection
        int px = (int) (x * scale * scalePerspective + buffer.getWidth() / 2);
        int py = (int) (y * scale * scalePerspective + buffer.getHeight() / 2);
        return new int[]{px, py};
    }

    private void drawCurve() {
        double tMin = 0, tMax = 2 * Math.PI;
        double dt = 0.01;
        double prevX = 0, prevY = 0, prevZ = 0;
        boolean first = true;

        for (double t = tMin; t <= tMax; t += dt) {
            double x = Math.cos(t);
            double y = Math.sin(t);
            double z = t;

            double[] rotated = rotatePoint(x * scale, y * scale, z * scale);
            int[] projected = projectPoint(rotated[0], rotated[1], rotated[2]);

            if (!first) {
                drawNonVerticalLineBresenham((int) prevX, (int) prevY, projected[0], projected[1], Color.black);
            }

            prevX = projected[0];
            prevY = projected[1];
            prevZ = rotated[2];
            first = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        clearBuffer();
        drawCurve();
        g.drawImage(buffer, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (autoRotate) {
            angleX += 0.01;
            angleY += 0.01;
            angleZ += 0.01;
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_A) {
            autoRotate = !autoRotate;
        } else if (keyCode == KeyEvent.VK_LEFT) {
            angleY -= 0.1;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            angleY += 0.1;
        } else if (keyCode == KeyEvent.VK_UP) {
            angleX -= 0.1;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            angleX += 0.1;
        } else if (keyCode == KeyEvent.VK_Q) {
            angleZ -= 0.1;
        } else if (keyCode == KeyEvent.VK_E) {
            angleZ += 0.1;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Curve");
        curve2 panel = new curve2();
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
