import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Curve3D extends JPanel implements KeyListener, Runnable {

    GraficosCurvaExplicita3D buffer = new GraficosCurvaExplicita3D(800, 800);

    private double anguloMaximo = Math.PI;
    private int numPuntos = 100;
    private double anguloIncremento = anguloMaximo / numPuntos;
    private double escala = 100;

    private boolean animacionActiva = false;
    private Thread hiloAnimacion;

    private ArrayList<double[]> vertices = new ArrayList<>();
    private double[] puntoCubo = {400, 400, 0};
    private double[] puntoFuga = {400, 400, 1000};

    private double anguloX = 0;
    private double anguloY = 0;
    private double anguloZ = 0;

    public Curve3D() {
        JFrame frame = new JFrame();
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);

        for (double angulo = -Math.PI; angulo <= anguloMaximo; angulo += anguloIncremento) {
            double[] vertice = new double[3];
            vertice[0] = Math.cos(3 * angulo);
            vertice[1] = 2 * Math.pow(Math.cos(angulo), 2);
            vertice[2] = Math.sin(2 * angulo);
            vertices.add(vertice);
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

            buffer.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY(), Color.BLACK);
        }

        g.drawImage(buffer.getBuffer(), 0, 0, null);
        buffer.resetBuffer();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Curve3D::new);
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
                    new Thread(this).start();
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
            anguloY += 1;
            anguloZ += 1;

            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                Logger.getLogger(Curve3D.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class GraficosCurvaExplicita3D {

    private BufferedImage buffer;
    private int WIDTH;
    private int HEIGHT;

    public GraficosCurvaExplicita3D(int WIDTH, int HEIGHT) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;

        buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }

    public void drawLine(int x0, int y0, int x1, int y1, Color color) {
        int dx = x1 - x0;
        int dy = y1 - y0;

        int pasos = Math.max(Math.abs(dx), Math.abs(dy));

        float xIncremento = (float) dx / pasos;
        float yIncremento = (float) dy / pasos;

        float x = x0;
        float y = y0;

        for (int i = 0; i <= pasos; i++) {
            putPixel(Math.round(x), Math.round(y), color);
            x += xIncremento;
            y += yIncremento;
        }
    }

    private void putPixel(int x, int y, Color color) {
        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            buffer.setRGB(x, y, color.getRGB());
        }
    }

    public void resetBuffer() {
        buffer = new BufferedImage(HEIGHT, WIDTH, BufferedImage.TYPE_INT_ARGB);
    }

    public BufferedImage getBuffer() {
        return buffer;
    }
}
