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

public class Cilindro3D extends JPanel implements KeyListener, Runnable {

    GraficosCilindro3D buffer = new GraficosCilindro3D(800, 800);

    private double anguloMaximo = 2 * Math.PI;
    private int numPuntos = 50;
    private double anguloIncremento = anguloMaximo / numPuntos;
    private double escala = 50;

    private boolean animacionActiva = false;

    private ArrayList<double[]> vertices = new ArrayList<>();
    private double[] puntoCubo = {400, 400, 0};
    private double[] puntoFuga = {400, 400, 1000};

    private double anguloX = 0;
    private double anguloY = 0;
    private double anguloZ = 0;

    public Cilindro3D() {
        JFrame frame = new JFrame();
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);

        for (double alpha = 0; alpha < anguloMaximo; alpha += anguloIncremento) {
            for (double beta = 0; beta < anguloMaximo; beta += anguloIncremento) {
                double[] vertice = new double[3];
                vertice[0] = (2 + Math.cos(alpha)) * Math.cos(beta);
                vertice[1] = (2 + Math.cos(alpha)) * Math.sin(beta);
                vertice[2] = alpha;
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

        for (int i = 0; i < numPuntos - 1; i++) {
            for (int j = 0; j < numPuntos; j++) {
                int index0 = i * numPuntos + j;
                int index1 = (i + 1) * numPuntos + j;
                int index2 = i * numPuntos + (j + 1) % numPuntos; 
                int index3 = (i + 1) * numPuntos + (j + 1) % numPuntos;

                double[] v0 = verticesTrasladados[index0];
                double[] v1 = verticesTrasladados[index1];
                double[] v2 = verticesTrasladados[index2];
                double[] v3 = verticesTrasladados[index3];

                Point2D p0 = punto3D_a_2D(v0[0], v0[1], v0[2]);
                Point2D p1 = punto3D_a_2D(v1[0], v1[1], v1[2]);
                Point2D p2 = punto3D_a_2D(v2[0], v2[1], v2[2]);
                Point2D p3 = punto3D_a_2D(v3[0], v3[1], v3[2]);

                buffer.drawLine((int) p0.getX(), (int) p0.getY(), (int) p1.getX(), (int) p1.getY(), Color.BLACK);
                buffer.drawLine((int) p0.getX(), (int) p0.getY(), (int) p2.getX(), (int) p2.getY(), Color.BLACK);
                buffer.drawLine((int) p1.getX(), (int) p1.getY(), (int) p3.getX(), (int) p3.getY(), Color.BLACK);
                buffer.drawLine((int) p2.getX(), (int) p2.getY(), (int) p3.getX(), (int) p3.getY(), Color.BLACK);
            }
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
        SwingUtilities.invokeLater(Cilindro3D::new);
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
            anguloX += 1;
            anguloY += 1;
//            anguloZ += 1;

            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                Logger.getLogger(Superficie3D.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

class GraficosCilindro3D {

    private BufferedImage buffer;
    private int WIDTH;
    private int HEIGHT;

    public GraficosCilindro3D(int WIDTH, int HEIGHT) {
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