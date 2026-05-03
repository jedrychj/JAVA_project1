import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;


void main() {
    Screen window = new Screen(500, 500);
    Random rand = new Random(); // tworzymy losowa liczbe

    List<Square> platformy = new ArrayList<>();
    List<Ball> pilki = new ArrayList<>();

    //Square example = new Square(10, 10, window); // deklaracja watku kwadratu
    for (int i=0; i<5; i++){ //deklaracja kilku prostokatow, dla kazdego tworzymy watek
        int startY = (50+(i*80)); // ustawienie prostoktow co 60 pikseli
        int speedX = rand.nextInt(4)+1; // nadanie losowej predkosci
        Square sq = new Square(10, startY, speedX, window);
        platformy.add(sq);
        sq.start();
    }

    for (int i=0; i<8; i++){ //deklaracja kilku kulek, dla kazdej tworzymy watek
        int startX = rand.nextInt(450); // losowa pozycja poczatkowa
        int speedX = rand.nextInt(6)-3;
        if (speedX == 0) {speedX = 2;}

        int speedY = rand.nextInt(5)+2; // nadanie losowej predkosci
        Ball ball = new Ball(startX, 100, speedX, speedY, window, platformy, pilki);
        pilki.add(ball);
        ball.start();
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
    volatile int cordX;
    volatile int cordY;

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
        this.graphic.setBackground(Color.RED);
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
                this.velX = -Math.abs(this.velX); //zmiana kierunku wektora predkosci
                this.cordX = rightBorder - this.sizeX; // zeby nam sie nie zakleszczyl przy scianie, bez tego tez dziala
            } // chyba ze nagle zmniejszycie szerokosc okna

            if (this.cordX <=0){ //odbijanie od lewej sciany
                this.velX = Math.abs(this.velX);
                this.cordX = 0; // bez tego tez dziala
            }

            try {
                sleep(20);
            } catch (InterruptedException ie) {
                break;
            }
        }
    }
}

class Ball extends Thread {
    volatile int cordX;
    volatile int cordY;
    int size = 20; // Średnica kulki
    volatile int velX; // Prędkość w bok
    volatile int velY; // Prędkość w dół

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

        // Tworzymy panel, rysujemy żeby był kółkiem
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
        while (true) {
            this.cordX += this.velX;
            this.cordY += this.velY;
            this.graphic.setBounds(cordX, cordY, size, size);

            int rightBorder = this.s.getContentPane().getWidth();
            int bottomBorder = this.s.getContentPane().getHeight();

            if (this.cordX <= 0) { // odbijanie się od lewej ściany
                this.velX = Math.abs(this.velX); //Math.abs gwarantuje ze kulka sie nie przykleji, ale bez tego tez raczej dziala
                this.cordX = 0;
            } else if (this.cordX >= rightBorder - this.size) { //odbijanie sie od prawej sciany
                this.velX = -Math.abs(this.velX);
                this.cordX = rightBorder - this.size;
            }


            if (this.cordY <= 0) { // odbijanie sie od gornej sciany
                this.velY = Math.abs(this.velY);
                this.cordY = 0;
            } else if (this.cordY >= bottomBorder - this.size) { // odbijanie sie od dolnej sciany
                this.velY = -Math.abs(this.velY);
                this.cordY = bottomBorder - this.size;
            }

            for (Ball other : pilki) {
                if (other == this) continue; // Patrzymy tylko na inne kulki

                int dx = this.cordX - other.cordX;
                int dy = this.cordY - other.cordY;

                // Twierdzenie Pitagorasa
                int distanceSquared = (dx * dx) + (dy * dy);

                if (distanceSquared < this.size * this.size) {

                    // Obliczamy różnicę ich prędkości
                    int dvx = this.velX - other.velX;
                    int dvy = this.velY - other.velY;

                    // sprawdzamy czy kulki się do siebie zbliżają, wynik mniejszy od zera oznacza, że lecą na siebie
                    if ((dx * dvx + dy * dvy) < 0) {

                        // wymiana prędkości, zderzenie sprężyste
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
                // Tworzymy wirtualny kwadrat reprezentujący aktualnie sprawdzaną platformę
                Rectangle sqRect = new Rectangle(sq.cordX, sq.cordY, sq.sizeX, sq.sizeY);

                // Sprawdzamy, czy się przecinają
                if (ballRect.intersects(sqRect)) {
                    int ballCenterX = this.cordX + size / 2;
                    int ballCenterY = this.cordY + size / 2;
                    int sqCenterX = sq.cordX + sq.sizeX / 2;
                    int sqCenterY = sq.cordY + sq.sizeY / 2;

                    // Obliczamy wielkość najechania kulki na prostokąt na obu osiach
                    int overlapX = (size / 2 + sq.sizeX / 2) - Math.abs(ballCenterX - sqCenterX);
                    int overlapY = (size / 2 + sq.sizeY / 2) - Math.abs(ballCenterY - sqCenterY);

                    if (overlapX < overlapY) {
                        if (ballCenterX < sqCenterX) {
                            this.velX = -Math.abs(this.velX); // uderzenie z lewej
                            this.cordX = sq.cordX - size;
                        } else {
                            this.velX = Math.abs(this.velX); // uderzenie z prawej
                            this.cordX = sq.cordX + sq.sizeX;
                        }
                    } else {
                        if (ballCenterY < sqCenterY) {
                            this.velY = -Math.abs(this.velY); // uderzenie z góry
                            this.cordY = sq.cordY - size;
                        } else {
                            this.velY = Math.abs(this.velY); // uderzenie z dołu
                            this.cordY = sq.cordY + sq.sizeY;
                        }
                    }
                    break;
                }
            }

            try { sleep(20); } catch (InterruptedException ie) { break; }
        }
    }
}