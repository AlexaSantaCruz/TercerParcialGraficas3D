import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/* paraboloide hiperbolico
 * es decir una papa frita (cerrados)
 */
public class Superficie3D extends JPanel implements KeyListener, Runnable {

    private BufferedImage buffer;
    private int WIDTH = 800;
    private int HEIGHT = 800;
    private double escala = 1.0;

    private double a = 50;
    private double b = 50;
    private double c = 15.0; // Aumentar el valor de c para hacer la curvatura más pronunciada

    private boolean animacionActiva = false;
    private Thread hiloAnimacion;

    private ArrayList<double[]> vertices = new ArrayList<>();
    private double[] puntoCubo = {400, 400, 0};
    private double[] puntoFuga = {400, 400, 1000};

    private double anguloX = 0;
    private double anguloY = 0;
    private double anguloZ = 0;

    public Superficie3D() {
        JFrame frame = new JFrame();
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);

        buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        for (double u = -3; u <= 3; u += 0.1) { 
            for (double v = -3; v <= 3; v += 0.1) { 
                double[] vertice = new double[3];
                vertice[0] = a * u;
                vertice[1] = b * v;
                vertice[2] = c * (u * u - v * v);
                vertices.add(vertice);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        double[][] verticesTrasladados = new double[vertices.size()][3];
        for (int i = 0; i < vertices.size(); i++) {
            double[] vertice = vertices.get(i);
            vertice = rotarX(vertice, anguloX);
            vertice = rotarY(vertice, anguloY);
            vertice = rotarZ(vertice, anguloZ);
            verticesTrasladados[i] = vertice;
        }

        for (int i = 0; i < vertices.size(); i++) {
            double[] v = verticesTrasladados[i];
            double[] trasladado = {
                (v[0] * escala) + puntoCubo[0],
                (v[1] * escala) + puntoCubo[1],
                (v[2] * escala) + puntoCubo[2]
            };
            verticesTrasladados[i] = trasladado;
        }

        for (int i = 0; i < vertices.size() - 1; i++) {
            double x0 = verticesTrasladados[i][0];
            double y0 = verticesTrasladados[i][1];
            double z0 = verticesTrasladados[i][2];

            double x1 = verticesTrasladados[i + 1][0];
            double y1 = verticesTrasladados[i + 1][1];
            double z1 = verticesTrasladados[i + 1][2];

            Point2D p1 = punto3D_a_2D(x0, y0, z0);
            Point2D p2 = punto3D_a_2D(x1, y1, z1);

            drawNonVerticalLineBresenham((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY(), Color.BLACK);
        }

        g.drawImage(buffer, 0, 0, null);
        resetBuffer();
    }

    private Point2D punto3D_a_2D(double x, double y, double z) {
        double u = -puntoFuga[2] / (z - puntoFuga[2]);

        double px = puntoFuga[0] + (x - puntoFuga[0]) * u;
        double py = puntoFuga[1] + (y - puntoFuga[1]) * u;

        return new Point2D.Double(px, py);
    }

    private double[] rotarX(double[] point, double angle) {
        double[] result = new double[3];
        result[0] = point[0];
        result[1] = point[1] * Math.cos(Math.toRadians(angle)) - point[2] * Math.sin(Math.toRadians(angle));
        result[2] = point[1] * Math.sin(Math.toRadians(angle)) + point[2] * Math.cos(Math.toRadians(angle));
        return result;
    }

    private double[] rotarY(double[] point, double angle) {
        double[] result = new double[3];
        result[0] = point[0] * Math.cos(Math.toRadians(angle)) + point[2] * Math.sin(Math.toRadians(angle));
        result[1] = point[1];
        result[2] = -point[0] * Math.sin(Math.toRadians(angle)) + point[2] * Math.cos(Math.toRadians(angle));
        return result;
    }

    private double[] rotarZ(double[] point, double angle) {
        double[] result = new double[3];
        result[0] = point[0] * Math.cos(Math.toRadians(angle)) - point[1] * Math.sin(Math.toRadians(angle));
        result[1] = point[0] * Math.sin(Math.toRadians(angle)) + point[1] * Math.cos(Math.toRadians(angle));
        result[2] = point[2];
        return result;
    }

    public void drawNonVerticalLineBresenham(int x0, int y0, int x1, int y1, Color color) {
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

    public void putPixel(int x, int y, Color c) {
        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            buffer.setRGB(x, y, c.getRGB());
        }
    }

    public void resetBuffer() {
        buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Superficie3D::new);
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();

        switch (key) {
            case KeyEvent.VK_W:
                anguloX += 2;
                break;
            case KeyEvent.VK_S:
                anguloX -= 2;
                break;
            case KeyEvent.VK_A:
                anguloY -= 2;
                break;
            case KeyEvent.VK_D:
                anguloY += 2;
                break;
            case KeyEvent.VK_E:
                anguloZ += 2;
                break;
            case KeyEvent.VK_Q:
                anguloZ -= 2;
                break;
            case KeyEvent.VK_SPACE:
                animacionActiva = !animacionActiva;
                if (animacionActiva) {
                    hiloAnimacion = new Thread(this);
                    hiloAnimacion.start();
                } else {
                    hiloAnimacion.interrupt();
                }
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void run() {
        while (animacionActiva) {
            anguloX += 1;
            anguloY += 1;
            anguloZ += 1;

            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
