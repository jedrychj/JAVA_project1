// ====================================================================================================
// ====================================================================================================
// Egzekutory
// ====================================================================================================
// ====================================================================================================

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

void main() {
    Screen window = new Screen(500, 500);
    Random rand = new Random();

    List<Square> platformy = new ArrayList<>();
    List<Ball> pilki = new ArrayList<>();

    // Tworzenie egzekutora na 4 watki
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

    for (int i = 0; i < 5; i++) {
        int startY = (50 + (i * 80));
        int speedX = rand.nextInt(4) + 1;
        Square sq = new Square(10, startY, speedX, window);
        platformy.add(sq);

        // egzekutor dostaje zadania
        executor.scheduleAtFixedRate(sq, 0, 20, TimeUnit.MILLISECONDS);
    }

    for (int i = 0; i < 8; i++) {
        int startX = rand.nextInt(450);
        int speedX = rand.nextInt(6) - 3;
        if (speedX == 0) { speedX = 2; }
        int speedY = rand.nextInt(5) + 2;

        Ball ball = new Ball(startX, 100, speedX, speedY, window, platformy, pilki);
        pilki.add(ball);

        // egzekutor dostaje zadanie
        executor.scheduleAtFixedRate(ball, 0, 20, TimeUnit.MILLISECONDS);
    }
}

class Screen extends JFrame {
    int sizeX;
    int sizeY;

    public Screen(int x, int y) {
        this.sizeX = x;
        this.sizeY = y;
        this.setSize(x, y);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Animacja z Egzekutorami");
        this.setLayout(null);
        this.setVisible(true);
    }
}

// tu zmieniłem extends Thread na implements Runnable
class Square implements Runnable {
    volatile int cordX;
    volatile int cordY;
    int sizeX;
    int sizeY;
    int velX;
    int velY;
    Screen s;
    JPanel graphic;

    public Square(int startX, int startY, int speedX, Screen okno) {
        this.sizeX = 60;
        this.sizeY = 10;
        this.cordX = startX;
        this.cordY = startY;
        this.velX = speedX;
        this.velY = 0;
        this.s = okno;
        this.graphic = new JPanel();
        this.graphic.setBounds(cordX, cordY, sizeX, sizeY);
        this.graphic.setBackground(Color.RED);
        this.s.add(this.graphic);
    }

    @Override
    public void run() {

        // wyrzuciłem pętlę

        this.cordX += this.velX;
        this.graphic.setBounds(cordX, cordY, sizeX, sizeY);
        int rightBorder = this.s.getContentPane().getWidth();

        if (this.cordX >= rightBorder - this.sizeX) {
            this.velX = -Math.abs(this.velX);
            this.cordX = rightBorder - this.sizeX;
        }

        if (this.cordX <= 0) {
            this.velX = Math.abs(this.velX);
            this.cordX = 0;
        }
    }
}

// tu zmieniłem extends Thread na implements Runnable
class Ball implements Runnable {
    volatile int cordX;
    volatile int cordY;
    int size = 20;
    volatile int velX;
    volatile int velY;
    Screen s;
    JPanel graphic;
    List<Square> platformy;
    List<Ball> pilki;

    public Ball(int startX, int startY, int speedX, int speedY, Screen okno, List<Square> platformy, List<Ball> pilki) {
        this.cordX = startX;
        this.cordY = startY;
        this.velX = speedX;
        this.velY = speedY;
        this.s = okno;
        this.platformy = platformy;
        this.pilki = pilki;

        this.graphic = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.GREEN);
                g.fillOval(0, 0, getWidth(), getHeight());
            }
        };

        this.graphic.setOpaque(false);
        this.graphic.setBounds(cordX, cordY, size, size);
        this.s.add(this.graphic);
    }

    @Override
    public void run() {
        // wyrzucona pętla

        this.cordX += this.velX;
        this.cordY += this.velY;
        this.graphic.setBounds(cordX, cordY, size, size);

        int rightBorder = this.s.getContentPane().getWidth();
        int bottomBorder = this.s.getContentPane().getHeight();

        if (this.cordX <= 0) {
            this.velX = Math.abs(this.velX);
            this.cordX = 0;
        } else if (this.cordX >= rightBorder - this.size) {
            this.velX = -Math.abs(this.velX);
            this.cordX = rightBorder - this.size;
        }

        if (this.cordY <= 0) {
            this.velY = Math.abs(this.velY);
            this.cordY = 0;
        } else if (this.cordY >= bottomBorder - this.size) {
            this.velY = -Math.abs(this.velY);
            this.cordY = bottomBorder - this.size;
        }

        for (Ball other : pilki) {
            if (other == this) continue;

            int dx = this.cordX - other.cordX;
            int dy = this.cordY - other.cordY;
            int distanceSquared = (dx * dx) + (dy * dy);

            if (distanceSquared < this.size * this.size) {
                int dvx = this.velX - other.velX;
                int dvy = this.velY - other.velY;

                if ((dx * dvx + dy * dvy) < 0) {
                    int tempX = this.velX;
                    int tempY = this.velY;
                    this.velX = other.velX;
                    this.velY = other.velY;
                    other.velX = tempX;
                    other.velY = tempY;
                }
            }
        }

        Rectangle ballRect = new Rectangle(this.cordX, this.cordY, this.size, this.size);
        for (Square sq : platformy) {
            Rectangle sqRect = new Rectangle(sq.cordX, sq.cordY, sq.sizeX, sq.sizeY);

            if (ballRect.intersects(sqRect)) {
                int ballCenterX = this.cordX + size / 2;
                int ballCenterY = this.cordY + size / 2;
                int sqCenterX = sq.cordX + sq.sizeX / 2;
                int sqCenterY = sq.cordY + sq.sizeY / 2;

                int overlapX = (size / 2 + sq.sizeX / 2) - Math.abs(ballCenterX - sqCenterX);
                int overlapY = (size / 2 + sq.sizeY / 2) - Math.abs(ballCenterY - sqCenterY);

                if (overlapX < overlapY) {
                    if (ballCenterX < sqCenterX) {
                        this.velX = -Math.abs(this.velX);
                        this.cordX = sq.cordX - size;
                    } else {
                        this.velX = Math.abs(this.velX);
                        this.cordX = sq.cordX + sq.sizeX;
                    }
                } else {
                    if (ballCenterY < sqCenterY) {
                        this.velY = -Math.abs(this.velY);
                        this.cordY = sq.cordY - size;
                    } else {
                        this.velY = Math.abs(this.velY);
                        this.cordY = sq.cordY + sq.sizeY;
                    }
                }
                break;
            }
        }
    }
}