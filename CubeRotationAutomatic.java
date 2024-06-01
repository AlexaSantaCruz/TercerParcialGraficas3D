import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;


public class CubeRotationAutomatic extends JPanel {

    private BufferedImage buffer;
    private double[][] vertices;
    private double angleX = 0, angleY = 0, angleZ = 0; // Angles of rotation around each axis
    private final int size = 75; // Size of the cube
    private Timer timer;

    public CubeRotationAutomatic() {
        buffer = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
        initializeVertices();
        
        // Crear un temporizador para rotar automáticamente el cubo
        timer = new Timer(50, e -> {
            angleX += Math.toRadians(1);
            angleY += Math.toRadians(1);
            angleZ += Math.toRadians(1);
            drawCube();
            repaint();
        });
        timer.start();
    }

    private void initializeVertices() {
        vertices = new double[][] {
            {-size, -size, -size}, {size, -size, -size}, {size, size, -size}, {-size, size, -size},
            {-size, -size, size}, {size, -size, size}, {size, size, size}, {-size, size, size}
        };
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        CubeRotationAutomatic panel = new CubeRotationAutomatic();
        frame.add(panel);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel.drawCube();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(buffer, 0, 0, null);
    }

    private void drawCube() {
        buffer = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
        double[][] rotatedVertices = new double[8][3];

        for (int i = 0; i < vertices.length; i++) {
            double[] rotatedX = rotateX(vertices[i], angleX);
            double[] rotatedXY = rotateY(rotatedX, angleY);
            double[] rotatedXYZ = rotateZ(rotatedXY, angleZ);
            rotatedVertices[i] = rotatedXYZ;
        }

        int[][] projectedVertices = new int[8][2];
        for (int i = 0; i < rotatedVertices.length; i++) {
            projectedVertices[i] = project(rotatedVertices[i]);
        }

        drawEdges(projectedVertices);
    }

    private double[] rotateX(double[] vertex, double angle) {
        double[] rotated = new double[3];
        rotated[0] = vertex[0];
        rotated[1] = vertex[1] * Math.cos(angle) - vertex[2] * Math.sin(angle);
        rotated[2] = vertex[1] * Math.sin(angle) + vertex[2] * Math.cos(angle);
        return rotated;
    }

    private double[] rotateY(double[] vertex, double angle) {
        double[] rotated = new double[3];
        rotated[0] = vertex[0] * Math.cos(angle) + vertex[2] * Math.sin(angle);
        rotated[1] = vertex[1];
        rotated[2] = -vertex[0] * Math.sin(angle) + vertex[2] * Math.cos(angle);
        return rotated;
    }

    private double[] rotateZ(double[] vertex, double angle) {
        double[] rotated = new double[3];
        rotated[0] = vertex[0] * Math.cos(angle) - vertex[1] * Math.sin(angle);
        rotated[1] = vertex[0] * Math.sin(angle) + vertex[1] * Math.cos(angle);
        rotated[2] = vertex[2];
        return rotated;
    }

    private int[] project(double[] vertex) {
        int x = (int) (vertex[0] * 2) + 400; // Translate to center
        int y = (int) (vertex[1] * 2) + 400; // Translate to center
        return new int[]{x, y};
    }

    private void drawEdges(int[][] vertices) {
        int[][] edges = {
            {0, 1}, {1, 2}, {2, 3}, {3, 0},
            {4, 5}, {5, 6}, {6, 7}, {7, 4},
            {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        for (int[] edge : edges) {
            int x0 = vertices[edge[0]][0];
            int y0 = vertices[edge[0]][1];
            int x1 = vertices[edge[1]][0];
            int y1 = vertices[edge[1]][1];
            drawLineBresenham(x0, y0, x1, y1, Color.RED);
        }
    }

    private void drawLineBresenham(int x0, int y0, int x1, int y1, Color color) {
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
}
