
package application;

import controller.BetreuerViewController;
import controller.FirstWindowController;
import java.util.Locale;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Das ist unsere Start-Anwendung, die unser
 * Menue steuert
 */
public class Main extends Application {

    private Stage primaryStage;
    private String message = "";
    private final String user = System.getProperty("user.name");

    /**
     * Wir eroeffnen hier die Hauptanwendung FirstWindowView.fxml
     */
    public void firstWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource
            ("/view/FirstWindowView.fxml"));
            AnchorPane pane = loader.load();
            //  Scene scene = new Scene(new ScrollPane(pane), 1900, 1100);
            Scene scene = new Scene((pane), 1900, 1100);
            scene.getStylesheets().add(getClass().getResource
            ("/css/dark_theme.css").toExternalForm());
           FirstWindowController firstWindowController = loader.getController();
            firstWindowController.setMain(this);
            primaryStage.setScene(scene);
            primaryStage.setX(10);
            primaryStage.setY(10);
            primaryStage.getIcons().add(new Image("/icon/auge.png"));
            primaryStage.setTitle("Testversion 001");
            primaryStage.show();

        } catch (Exception ex) {
            System.out.println("Fehler first window now" + ex.getStackTrace()
                    + ex.getLocalizedMessage());
        }

    }


    /**
     * Wir wechseln hier in unsere TableView Betreuer
     */
      public void changeWindowBetreuer() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource
            ("/view/BetreuerView.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene((pane), 1900, 980);
            //    Scene scene = new Scene(new ScrollPane(pane), 1620, 980);
            scene.getStylesheets().add(getClass().getResource
        ("/css/dark_theme.css").toExternalForm());
            BetreuerViewController betreuerViewWindowController =
                    loader.getController();
            betreuerViewWindowController.setMain(this);
            primaryStage.setScene(scene);
            primaryStage.setOnHiding(new EventHandler<WindowEvent>() {

         @Override
         public void handle(WindowEvent event) {
             Platform.runLater(new Runnable() {

                 @Override
                 public void run() {

                 firstWindow();
                 }
             });
         }
     });
            primaryStage.setTitle("Tabellen-Ansicht Mitarbeiter" );
            primaryStage.show();

        } catch (Exception ex) {

            System.out.println("Fehler beim Oeffnen Mitarbeiter"
                    + ex.getMessage());
        }

    }


     @Override
    public void start(Stage primaryStage) {
    	 	// wir begruessen unseren Anwender
            message = "Herzlich willkommen " + user;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Lizenz fuer " + user);
            alert.setHeaderText(message);
            alert.setContentText("Lizensierter Teilnehmer(in)!");
            alert.showAndWait();
            this.primaryStage = primaryStage;

            firstWindow();

    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.GERMANY);

        launch(args);
    }

    @Override
    public void init() throws Exception {
        // TODO Auto-generated method stub
        super.init();
    }

    @Override
    public void stop() throws Exception {
        // TODO Auto-generated method stub
        super.stop();
    }
}
