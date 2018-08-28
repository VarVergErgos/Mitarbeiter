package controller;

import java.net.URL;
import java.util.ResourceBundle;
import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


/**
 * Das ist unsere Klasse, die unser FirstWindow darstellt
 * Mit dem Menue koennen wir dann in die TableView Betreueransicht
 * wechseln.
 */
public class FirstWindowController implements Initializable {

    @FXML
    private StackPane stackPane;

    private Main main;

    /**
     *
     * @param main Main-Methode
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Wir wechseln in unsere View Betreuer
     */
     public void changeWindowBetreuer(){
         main.changeWindowBetreuer();
        }
/**
 * Mit dieser Methode schliessen wir die Anwendung
 */
    public void close() {
        System.exit(0);
    }

 /**
  * Das ist unser Init-Methode.
  */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Circle circle = new Circle(185);
        circle.setStroke(Color.GREEN);
        circle.setFill(Color.CADETBLUE);
        Text number = new Text("\n "
              +  "BETREUER"
                             );

        number.setFill(Color.BLACK);
        number.setFont(Font.font("COURIER NEW", FontWeight.BOLD, 25));
        stackPane.getChildren().addAll(circle, number);

    }

}
