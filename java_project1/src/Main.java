import javax.swing.*;
import java.awt.*;

void main() {
    Screen window = new Screen(500, 500);

    Square example = new Square(10, 10, window); // deklaracja watku kwadratu
    example.start(); // start watku

    window.add(new JPanel()); // przy moich eksperymentach mi wyszlo ze musi byc jakies tlo, idk czemu
}

class Screen extends JFrame {
    int sizeX;
    int sizeY;

    JLayeredPane layer;

    JPanel background;

    public Screen(int x, int y) {
        this.sizeX = x;
        this.sizeY = y;

        this.setSize(x,y);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Okno");
        this.setVisible(true);
    }
}

class Square extends Thread {
    int cordX;
    int cordY;

    int sizeX;
    int sizeY;

    int velX;
    int velY;

    Screen s;
    JPanel graphic;

    public Square(int x, int y, Screen okno){
        this.sizeX = x;
        this.sizeY = y;

        this.cordX = 10; // mozna jakos losowo koordynaty wstepne
        this.cordY = 10; // tylko tak zeby sie nie nakladaly nawzajem

        this.velX = 1; // wersor predkosci, jakos znormalizowany, np ze dlugosc = 1
        this.velY = 0; // chyba ze chcemy rozne predkosci, to w sumie nie powinno byc trudne
        // wstepny kierunek predkosci tez mozna losowac chyba

        this.s = okno;

        this.graphic = new JPanel();
        this.graphic.setBounds(cordX,cordY,sizeX,sizeY);
        this.graphic.setBackground(Color.GREEN);
    }

    @Override
    public void run() {
        this.s.add(this.graphic,1);

        while(true) { // przyklad z ruchomym kwadratem
            // tu kod ktory robi update kwadratu
            this.graphic.setBounds(cordX,cordY,sizeX,sizeY);

            cordX++;
            try {
                sleep(100);
            } catch (InterruptedException ie) {}
        }
    }
}