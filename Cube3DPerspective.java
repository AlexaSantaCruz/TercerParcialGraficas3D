import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Cube3DPerspective extends JPanel {

    private BufferedImage buffer;
    private static final int DISTANCE = 150; // Distancia del observador

    public Cube3DPerspective() {
        buffer = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Cube3DPerspective panel = new Cube3DPerspective();
        frame.add(panel);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        int[] Point = {50, 80, 40}; // Coordenadas iniciales del cubo
        panel.drawCube(Point, 10, Color.RED);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(buffer, 0, 0, null);
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
        buffer.setRGB(x, y, c.getRGB()); // Actualizando el color en las coordenadas dadas
        repaint(); // Actualizando la ventana
    }

    public void drawCube(int[] Point, int size, Color color) {
        size = size * 20;
        int[][] vertices = {
            {Point[0], Point[1], Point[2]},
            {Point[0] + size, Point[1], Point[2]},
            {Point[0] + size, Point[1] + size, Point[2]},
            {Point[0], Point[1] + size, Point[2]},
            {Point[0], Point[1], Point[2] + size},
            {Point[0] + size, Point[1], Point[2] + size},
            {Point[0] + size, Point[1] + size, Point[2] + size},
            {Point[0], Point[1] + size, Point[2] + size}
        };

        int[][] projectedVertices = new int[8][2];

        for (int i = 0; i < 8; i++) {
            projectedVertices[i] = project(vertices[i]);
        }

        // Draw edges of the cube
        drawEdges(projectedVertices, color);
    }

    private int[] project(int[] vertex) {
        int x = vertex[0];
        int y = vertex[1];
        int z = vertex[2];
        int px = (int) (x * DISTANCE / (z + DISTANCE));
        int py = (int) (y * DISTANCE / (z + DISTANCE));
        return new int[]{px + 400, py + 400}; // Ajustar la posición para centrar el cubo
    }

    private void drawEdges(int[][] vertices, Color color) {
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
            drawNonVerticalLineBresenham(x0, y0, x1, y1, color);
        }
    }
}
