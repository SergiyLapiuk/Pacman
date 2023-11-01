import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.*;


public class App extends JPanel implements KeyListener {
    private int pacManX, pacManY; // Координати Пакмана
    private int[] ghostX, ghostY; // Координати привидів
    int foodX, foodY;

    private int score; // Рахунок

    private Image[] ghostImages;
    private Image FoodImage;

    private int[] findValidGhostPosition() {
        int x, y;

        do {
            x = (int) (Math.random() * MAZE_WIDTH);
            y = (int) (Math.random() * MAZE_HEIGHT);
        } while (isObstacle(x, y)); // Повторюємо, поки не знайдемо недоступну позицію

        return new int[]{x, y};
    }


    private final int CELL_SIZE = 44; // Розмір однієї клітинки
    private final static  int MAZE_WIDTH = 20; // Ширина лабіринту
    private final static int MAZE_HEIGHT = 20; // Висота лабіринту

    private int[][] maze= generateMaze(MAZE_WIDTH,MAZE_HEIGHT);

    private int[][] generateMaze(int width, int height) {
        int[][] maze = new int[height][width];

        // Заповнюємо лабіринт стінами
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = 1;
            }
        }

        // Рекурсивна функція для генерації лабіринту
        generateMazeRecursive(maze, 1, 1, width - 2, height - 2);

        return maze;
    }

    private void generateMazeRecursive(int[][] maze, int x, int y, int width, int height) {
        if (width < 2 || height < 2) {
            return;
        }

        // Вибираємо випадковий початковий напрямок
        int[] directions = {1, 2, 3, 4};
        shuffleArray(directions);

        for (int direction : directions) {
            int dx = 0;
            int dy = 0;

            if (direction == 1) { // Вгору
                dy = -2;
            } else if (direction == 2) { // Вниз
                dy = 2;
            } else if (direction == 3) { // Вліво
                dx = -2;
            } else if (direction == 4) { // Вправо
                dx = 2;
            }

            int newX = x + dx;
            int newY = y + dy;

            if (newX > 0 && newX < maze[0].length && newY > 0 && newY < maze.length && maze[newY][newX] == 1) {
                maze[y + dy / 2][x + dx / 2] = 0;
                maze[newY][newX] = 0;
                generateMazeRecursive(maze, newX, newY, width - Math.abs(dx), height - Math.abs(dy));
            }
        }
    }

    private void shuffleArray(int[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
    }




    public App() {

        pacManX = 1;
        pacManY = 1;
        ghostX = new int[3];
        ghostY = new int[3];
        for (int i = 0; i < ghostX.length; i++) {
            int[] pos = findValidGhostPosition();
            ghostX[i]=pos[0];
            ghostY[i] = pos[1];
        }

        score = 0;
        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(CELL_SIZE * MAZE_WIDTH, CELL_SIZE * MAZE_HEIGHT));

        // Генерація випадкової позиції для їжі
        //int foodX, foodY;
        do {
            foodX = (int) (Math.random() * MAZE_WIDTH);
            foodY = (int) (Math.random() * MAZE_HEIGHT);
        } while (maze[foodY][foodX] != 0);

        maze[foodY][foodX] = 2;


        // Завантаження зображень привидів
        ghostImages = new Image[3];
        ghostImages[0] = new ImageIcon("src\\ghost1.png").getImage();
        ghostImages[1] = new ImageIcon("src\\ghost2.png").getImage();
        ghostImages[2] = new ImageIcon("src\\ghost3.png").getImage();

        FoodImage = new ImageIcon("src\\apple.png").getImage();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Малювання лабіринту
        for (int i = 0; i < MAZE_HEIGHT; i++) {
            for (int j = 0; j < MAZE_WIDTH; j++) {
                if (maze[i][j] == 1) {
                    g.setColor(Color.PINK);
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
                else {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        // // Малювання Pacman

        g.setColor(Color.YELLOW);
        g.fillArc(pacManX * CELL_SIZE, pacManY * CELL_SIZE, CELL_SIZE, CELL_SIZE, 45, 270);

        // Малювання привидів
        for (int i = 0; i < ghostX.length; i++) {

            g.drawImage(ghostImages[i], ghostX[i] * CELL_SIZE, ghostY[i] * CELL_SIZE, CELL_SIZE, CELL_SIZE, null);
        }

        //Малювання їжі
        g.setColor(Color.GREEN);
        for (int i = 0; i < MAZE_HEIGHT; i++) {
            for (int j = 0; j < MAZE_WIDTH; j++) {
                if (maze[i][j] == 2) {
                    //g.fillOval(j * CELL_SIZE + CELL_SIZE / 2 - 5, i * CELL_SIZE + CELL_SIZE / 2 - 5, 10, 10);
                    g.drawImage(FoodImage,j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE, null);

                }
            }
        }
        // Малювання рахунку
        g.setColor(Color.BLACK);
        Font largerFont = new Font("SansSerif", Font.PLAIN, 24);

        // Встановлюємо більший шрифт
        g.setFont(largerFont);
        g.drawString("SCORE: " + score, 10, 30);
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            movePacman(0, -1);
        } else if (key == KeyEvent.VK_DOWN) {
            movePacman(0, 1);
        } else if (key == KeyEvent.VK_LEFT) {
            movePacman(-1, 0);
        } else if (key == KeyEvent.VK_RIGHT) {
            movePacman(1, 0);
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public void movePacman(int dx, int dy) {
        int newX = pacManX + dx;
        int newY = pacManY + dy;
        if (newX >= 0 && newX < MAZE_WIDTH && newY >= 0 && newY < MAZE_HEIGHT && maze[newY][newX] != 1) {
            pacManX = newX;
            pacManY = newY;

            if (maze[newY][newX] == 2) {
                score += 10;
                maze[newY][newX] = 0;

                int foodX, foodY;
                do {
                    foodX = (int) (Math.random() * MAZE_WIDTH);
                    foodY = (int) (Math.random() * MAZE_HEIGHT);
                } while (maze[foodY][foodX] != 0);

                maze[foodY][foodX] = 2;
            }
        }
        repaint();
    }




    class Node {
        int x, y;
        int g; // g-значення (вартість шляху від початкового вузла до поточного вузла)
        int h; // h-значення (приблизна вартість шляху від поточного вузла до цільового вузла)
        Node parent; // Вершина-батько, що вела до цієї вершини

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public int[] aStar(int startX, int startY, int targetX, int targetY) {
        // Створення структури даних для відкритого та закритого списків вершин
        ArrayList<Node> openList = new ArrayList<>();
        ArrayList<Node> closedList = new ArrayList<>();

        // Створення початкової вершини і додавання її до відкритого списку
        Node startNode = new Node(startX, startY);
        startNode.g = 0;
        startNode.h = calculateHeuristic(startX, startY, targetX, targetY);
        startNode.parent = null;
        openList.add(startNode);

        while (!openList.isEmpty()) {
            // Знаходження вершини з найменшим f-значенням в openList
            Node currentNode = findLowestF(openList);
            openList.remove(currentNode);
            closedList.add(currentNode);

            // Перевірка, чи ми досягли цільової вершини
            if (currentNode.x == targetX && currentNode.y == targetY) {
                // Повертаємо згенерований список рухів (dx, dy), перебираючи ланцюжок вершин від цільової до початкової
                return buildPath(currentNode, startX, startY);
            }

            // Перебираємо сусідні вершини
            for (int[] neighbor : getNeighbors(currentNode)) {
                int neighborX = neighbor[0];
                int neighborY = neighbor[1];

                // Перевірка, чи сусідній вузол є стіною або вже в закритому списку
                if (isObstacle(neighborX, neighborY) || isInClosedList(closedList, neighborX, neighborY)) {
                    continue;
                }

                // Розрахунок g-значення для сусідньої вершини
                int tentativeG = currentNode.g + 1;

                // Перевіряємо, чи сусідній вузол ще не відкритий або має менше g-значення
                Node neighborNode = findNodeInList(openList, neighborX, neighborY);
                if (neighborNode == null || tentativeG < neighborNode.g) {
                    if (neighborNode == null) {
                        neighborNode = new Node(neighborX, neighborY);
                        openList.add(neighborNode);
                    }

                    // Оновлюємо сусідню вершину з новими значеннями
                    neighborNode.parent = currentNode;
                    neighborNode.g = tentativeG;
                    neighborNode.h = calculateHeuristic(neighborX, neighborY, targetX, targetY);
                }
            }
        }

        // Якщо openList пустий і не було знайдено шляху, повертаємо порожній список
        return new int[]{0, 0}; // Порожній список рухів (dx, dy)
    }

    private int calculateHeuristic(int x1, int y1, int x2, int y2) {
        // Розрахунок оцінки h-значення (наприклад, відстань Манхеттен)
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private Node findLowestF(ArrayList<Node> openList) {
        // Шукаємо вузол з найменшим f-значенням
        Node lowestFNode = openList.get(0);
        int lowestF = lowestFNode.g + lowestFNode.h;

        for (Node node : openList) {
            int nodeF = node.g + node.h;
            if (nodeF < lowestF) {
                lowestFNode = node;
                lowestF = nodeF;
            }
        }
        return lowestFNode;
    }

    private ArrayList<int[]> getNeighbors(Node node) {
        // Отримуємо сусідні клітинки
        ArrayList<int[]> neighbors = new ArrayList<>();
        int x = node.x;
        int y = node.y;

        int[][] possibleMoves = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] move : possibleMoves) {
            int newX = x + move[0];
            int newY = y + move[1];

            neighbors.add(new int[]{newX, newY});
        }

        return neighbors;
    }

    private boolean isObstacle(int x, int y) {
        // перевірка, чи координати знаходяться в діапазоні допустимих значень
        if (x >= 0 && x < MAZE_WIDTH && y >= 0 && y < MAZE_HEIGHT) {
            // перевірка, чи відповідна клітинка в лабіринті є стіною (значення 1 в масиві лабіринту)
            return maze[y][x] == 1;
        } else {
            // Якщо x та y знаходяться поза допустимими межами, то ця функція вважає їх завжди перешкодою (стіною)
            return true;
        }
    }

    private boolean isInClosedList(ArrayList<Node> closedList, int x, int y) {
        // Перевірка, чи вершина вже в закритому списку
        for (Node node : closedList) {
            if (node.x == x && node.y == y) {
                return true;
            }
        }
        return false;
    }

    private Node findNodeInList(ArrayList<Node> nodeList, int x, int y) {
        // Знахлдження вершини за координатами у списку вершин
        for (Node node : nodeList) {
            if (node.x == x && node.y == y) {
                return node;
            }
        }
        return null;
    }

    private int[] buildPath(Node targetNode, int startX, int startY) {
        // Побудова і повернення список рухів (dx, dy), перебираючи ланцюжок вершин від цільової до початкової
        ArrayList<int[]> path = new ArrayList<>();
        Node currentNode = targetNode;

        while (currentNode.parent != null) {
            int dx = currentNode.x - currentNode.parent.x;
            int dy = currentNode.y - currentNode.parent.y;
            path.add(new int[]{dx, dy});
            currentNode = currentNode.parent;
        }

        Collections.reverse(path);
        return path.get(0); //
    }

    public int[] greedy(int startX, int startY, int targetX, int targetY) {
        int dx = 0;
        int dy = 0;

        // Розрахунок різниці по координатах між привидом і цільовою позицією
        int diffX = targetX - startX;
        int diffY = targetY - startY;

        // Пошук горизонтального або вертикального руху до цілі
        if (Math.abs(diffX) > Math.abs(diffY)) {
            dx = (int) Math.signum(diffX);
            if (isObstacle(startX + dx, startY)) {
                dx = 0;
                dy = (int) Math.signum(diffY);
            }
        } else {
            dy = (int) Math.signum(diffY);
            if (isObstacle(startX, startY + dy)) {
                dy = 0;
                dx = (int) Math.signum(diffX);
            }
        }

        // Якщо обидва напрямки блоковані, спробуємо вибрати інший напрямок
        if (isObstacle(startX + dx, startY + dy)) {
            dx = dy = 0;
            int[] directions = {-1, 1};
            for (int dir : directions) {
                if (!isObstacle(startX + dir, startY)) {
                    dx = dir;
                    break;
                }
                if (!isObstacle(startX, startY + dir)) {
                    dy = dir;
                    break;
                }
            }
        }

        return new int[]{dx, dy};
    }

    public void moveGhosts() {
        for (int i = 0; i < ghostX.length; i++) {
            int targetX = pacManX;
            int targetY = pacManY;
            int[] nextMove = {0, 0};

            if (i == 0) {  // Перший привід слідкує за Pac-Man за допомогою A* алгоритму
                nextMove = aStar(ghostX[i], ghostY[i], targetX, targetY);
            } else if (i == 2) {  // Третій привід (індекс 2) патрулює навколо їжі
                int distanceToFood = Math.abs(ghostX[i] - foodX) + Math.abs(ghostY[i] - foodY);
                if (distanceToFood > 1) {
                    targetX = foodX;
                    targetY = foodY;
                    nextMove = aStar(ghostX[i], ghostY[i], targetX, targetY);
                } else {
                    // Якщо привід вже поруч з їжею, зупиняємо його рух
                    nextMove = new int[]{0, 0};
                }
            } else if (i == 1) {  // Інші привиди використовують жадібний алгоритм для слідкування за Pac-Man
                nextMove = greedy(ghostX[i], ghostY[i], targetX, targetY);
            }

            int dx = nextMove[0];
            int dy = nextMove[1];

            ghostX[i] += dx;
            ghostY[i] += dy;
        }

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public boolean checkCollision() {
        for (int i = 0; i < ghostX.length; i++) {
            if (pacManX == ghostX[i] && pacManY == ghostY[i]) {
                return true;
            }
        }
        return false;
    }

    public void restartGame() {
        pacManX = 1;
        pacManY = 1;
        for (int i = 0; i < ghostX.length; i++) {
            ghostX[i] = MAZE_WIDTH / 2;
            ghostY[i] = MAZE_HEIGHT / 2;
        }
        score = 0;
    }

    public void playGame() {

        while (true) {
            moveGhosts();
            if (checkCollision()) {
                JOptionPane.showMessageDialog(this, "Гру закінчено!!!! Твій рахунок: " + score);
                restartGame();
            }
            repaint();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Пакман");
        App game = new App();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        game.playGame();
    }
}