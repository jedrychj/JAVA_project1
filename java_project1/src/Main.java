import javax.swing.*;
import java.awt.*;
import java.util.Random;


void main() {
    Screen window = new Screen(500, 500);
    Random rand = new Random(); // tworzymy losowa liczbe

    //Square example = new Square(10, 10, window); // deklaracja watku kwadratu
    for (int i=0; i<5; i++){ //deklaracja kilku wadratow, dla kazdego tworzymy watek
        int startY = (50+(i*80)); // ustawienie kwadratow co 60 pikseli
        int speedX = rand.nextInt(4)+1; // nadanie losowej predkosci
        Square sq = new Square(10, startY, speedX, window);
        sq.start();


    }
    //example.start(); // start watku

    // window.add(new JPanel()); // przy moich eksperymentach mi wyszlo ze musi byc jakies tlo, idk czemu

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
        this.setLayout(null); // @Janek to trzeba wpisać i masz wyswietlanie
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

    public Square(int startX, int startY, int speedX, Screen okno){
        this.sizeX = 60; //robimy prostokat
        this.sizeY = 10;

        this.cordX = startX; // mozna jakos losowo koordynaty wstepne
        this.cordY = startY; // tylko tak zeby sie nie nakladaly nawzajem, juz to implementujemy wyzej

        this.velX = speedX; // wersor predkosci, jakos znormalizowany, np ze dlugosc = 1
        this.velY = 0; // chyba ze chcemy rozne predkosci, to w sumie nie powinno byc trudne
        // wstepny kierunek predkosci tez mozna losowac chyba

        this.s = okno;

        this.graphic = new JPanel();
        this.graphic.setBounds(cordX,cordY,sizeX,sizeY);
        this.graphic.setBackground(Color.GREEN);
        this.s.add(this.graphic);
    }

    @Override
    public void run() {
        //this.s.add(this.graphic,1);

        while(true) { // przyklad z ruchomym kwadratem
            // tu kod ktory robi update kwadratu
            this.cordX += this.velX; //zwiekszamy pozycje
            this.graphic.setBounds(cordX,cordY,sizeX,sizeY);
            int rightBorder = this.s.getContentPane().getWidth(); //zapewnia, ze prostokat nie najezdza na prawa sciane

            if (this.cordX >= rightBorder - this.sizeX) {
                this.velX = -this.velX; //zmiana kierunku wektora predkosci
                this.cordX = rightBorder - this.sizeX; // zeby nam sie nie zakleszczyl przy scianie, bez tego tez dziala
            } // chyba ze nagle zmniejszycie szerokosc okna

            if (this.cordX <=0){ //odbijanie od lewej sciany
                this.velX = -this.velX;
                this.cordX = 0; // bez tego tez dziala
            }

            try {
                sleep(30);
            } catch (InterruptedException ie) {
                break;
            }
        }
    }
}