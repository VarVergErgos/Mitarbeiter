package controller;

import application.Main;
import betreuer.BetreuerDAO;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import betreuer.Betreuer;
import betreuer.BetreuerdateiWrapper;
import excel.ExcelTask;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalDateStringConverter;
import util.DateUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 * Das ist unsere Controller Klasse der TableView Betreuer
 * In der TableView sind die Spalten Vorname, RD, Eintritt Editierbar
 *
 */
public class BetreuerViewController implements Initializable {

	private File fileExcel;
	private List<String> copiedFilesAsString = new ArrayList<String>();

	@FXML	TextField textMessage;
	@FXML	Button buttonXMLDatei;
	@FXML	Button btnExcelDatei;
	@FXML	Button buttonGoBack;
	@FXML	Image image;
	@FXML	Image image2;
	@FXML	Image image3;
	@FXML	Button buttonPersonalSuche;
	@FXML	private TableView<Betreuer> tableView = new TableView<>();
	@FXML	private TableColumn<Betreuer, Integer> idColumn = new TableColumn<>("ID");
	@FXML	private TableColumn<Betreuer, Integer> persnrColumn = new TableColumn<>("PERSNR");
	@FXML	private TableColumn<Betreuer, String> vornameColumn = new TableColumn<>("VORNAME");
	@FXML	private TableColumn<Betreuer, String> nameColumn = new TableColumn<>("NACHNAME");
	@FXML	private TableColumn<Betreuer, String> strasseColumn = new TableColumn<>("STRASSE");
	@FXML	private TableColumn<Betreuer, String> wohnortColumn = new TableColumn<>("WOHNORT");
	@FXML	private TableColumn<Betreuer, String> funktionColumn = new TableColumn<>("FUNKTION");
	@FXML	private TableColumn<Betreuer, Integer> rdColumn = new TableColumn<>("RD");
	@FXML	private TableColumn<Betreuer, String> rdNameColumn = new TableColumn<>("RDNAME");
	@FXML	private TableColumn<Betreuer, String> geschlechtColumn = new TableColumn<>("GESCHLECHT");
	@FXML	private TableColumn<Betreuer, LocalDate> eintrittsdatumColumn = new TableColumn<>("EINTRITT");
	@FXML	private TableColumn<Betreuer, Double> gehaltColumn = new TableColumn<>("GEHALT");
	@FXML	private ProgressBar progressBar;
	@FXML	private ProgressIndicator progressIndicator;
	@FXML	private TextField txtNamePersonal;

	public static String suchFeldPersonal = "";

	private Main main;

	public void setMain(Main main) throws Exception {

		this.main = main;
	}

	/**
	 * Mit dieser Methode gehen wir zum Hauptfenster -FirstWindow-View zurueck
	 */
	@FXML
	public void goBacktoFirstWindow() {
		main.firstWindow();
	}

	/**
	 * Die ist unsere Initialize-Methode
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//TableView nur einzeln selektierbar
		tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		//Listener Selektierte Zeile in der TableView dann gibt er die toString aus
        //Mitarbeiter [persnr=1291, vorname=Eskil, nachname=Tremmel, geschlecht=M, eintritt=1999-03-01, gehalt=32042.29000000001]

		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		    if (newSelection != null) {

		     System.out.println(tableView.getSelectionModel().getSelectedItem());
		    }
		});


		// Progressbar und Indicator unsichtbar machen
		// Anhand des gestarteten Task machen wir diese sichtbar
		progressBar.setVisible(false);
		progressIndicator.setVisible(false);

		//Vorbereitung Methoden fuer die View
		createButton();
		createTextFieldNamePersonal();
		createTableColumn();
		formatColumn();


		tableView.setTableMenuButtonVisible(true);
		textMessage.setVisible(false);

		//Task aufrufen um die Daten in die TableView zu kopieren
		@SuppressWarnings("unchecked")
		Task<ObservableList<Betreuer>> task1 = new GetAllBetreuerDaten();
		tableView.itemsProperty().bind(task1.valueProperty());
		progressBar.progressProperty().bind(task1.progressProperty());
		progressBar.setVisible(true);

		task1.setOnSucceeded(e -> progressBar.setVisible(false));
		task1.setOnFailed(e -> progressBar.setVisible(false));

		new Thread(task1).start();

		//ExcelDatei erstellen
		createActionEvent();
	}

	/**
	 * Mit dieser Methode starten wir unseren Task vom ButtonPersonalSuche
	 * Im ScenBuilder OnAction rufen wir diese Methode auf
	 */
	@FXML
	public void buttonPersonalsucher() {
		@SuppressWarnings("unchecked")
		Task<ObservableList<Betreuer>> task2 = new GetAllNamenSuche();
		tableView.itemsProperty().bind(task2.valueProperty());
		progressBar.progressProperty().bind(task2.progressProperty());
		progressBar.setVisible(true);
		task2.setOnSucceeded(e -> progressBar.setVisible(false));
		task2.setOnFailed(e -> progressBar.setVisible(false));
		new Thread(task2).start();
	}

	public List<Betreuer> getBetreuerData() throws Exception {

		BetreuerDAO dao = new BetreuerDAO();
		return dao.getAllDaten();

	}

	/**
	 * Mit unserem Button XML-Datei-Erzeugen rufen wir im Scenebuilder unter
	 * OnAction diese Methode auf. Wir legen mit dem FileChooser den Dateinamen
	 * und den Speicherort fest.
	 *
	 * @see saveBetreuerDataToFile(File file)
	 *
	 */
	@FXML
	private void handleSaveBetreuerDatenAsXML() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(main.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			saveBetreuerDataToFile(file);
		}
	}

	public void saveBetreuerDataToFile(File file) {

		try {
			JAXBContext context = JAXBContext.newInstance(BetreuerdateiWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			BetreuerdateiWrapper wrapper = new BetreuerdateiWrapper();

			wrapper.setBetreuerdaten(getBetreuerData());
			// Marshalling and saving XML to the file.
			m.marshal(wrapper, file);

			// Bestaetige die Speicherung
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Speicherung von xml Datei");
			alert.setHeaderText("Datei wurde gespeichert");
			alert.setContentText("Speicherort: \n" + file.getPath());

			alert.showAndWait();

		} catch (Exception e) { // catches ANY exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + file.getPath());

			alert.showAndWait();
		}

	}

	/**
	 * Mit dieser Methode legen wir mittels FileChooser den Speicherort und den
	 * Dateinamen fest.
	 */
	@FXML
	public void erstelleExcel() {

		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLSX FILES (*.xlsx)", "*.xlsx");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(main.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".xlsx")) {
				file = new File(file.getPath() + ".xlxs");
			}

			// ExcelTask task = new ExcelTask(file);
			this.fileExcel = file;
		}

		// System.out.println(fileExcel.toString());

	}

	/**
	 *  Wir wollen die Spalte Vorname in der Tabelle aendern koennen
	    Im Secenebuilder onEdit Commit diese Methode eingeben
	    in fxml: onEditCommit="#changeBetreuerVornameCellEvent"
	 * @param edittedCell Editierbar
	 */

	@SuppressWarnings("rawtypes")
	@FXML
	public void changeBetreuerVornameCellEvent(CellEditEvent edittedCell) {
		Betreuer betreuerSelected = tableView.getSelectionModel().getSelectedItem();

		betreuerSelected.setVorname(((edittedCell.getNewValue().toString())));
		// ToDo jetzt noch in die Datenbank aktualisieren
	}

	/**
	 * Wir wollen die Spalte RD-Nummer (INT) in der Tabelle aendern koennen
	 Im Secenebuilder onEdit Commit diese Methode eingeben
	 in fxml: onEditCommit="#changeBetreuerRdCellEvent"

	 * @param edittedCell Editierbare Spalte (RD)
	 */

	@SuppressWarnings("rawtypes")
	@FXML
	public void changeBetreuerRdCellEvent(CellEditEvent edittedCell) {
		Betreuer betreuerSelected = tableView.getSelectionModel().getSelectedItem();

		betreuerSelected.setRd((Integer.parseInt(edittedCell.getNewValue().toString())));
		// Todo jetzt noch in die Datenbank aktualisieren
	}

	@SuppressWarnings("rawtypes")
	@FXML
	public void changeBetreuerEintrittsdatumCellEvent(CellEditEvent edittedCell) {
		Betreuer betreuerSelected = tableView.getSelectionModel().getSelectedItem();
		//Wir nutzen unser Util um den String in ein LocalDate zu parsen
		betreuerSelected.setEintrittsdatum(DateUtil.parse(edittedCell.getNewValue().toString()));
		// Todo jetzt noch in die Datenbank aktualisieren

	}

	public void printObjekt(){

	}

	/**
	 * Methode um die Eigenschaften der Button festzulegen.
	 * Wir wollen nicht alles in der Methode initialize haben
	 * Ist so ein wenig uebersichtlicher
	 */
	public void createButton(){

		image = new Image(getClass().getResourceAsStream("/icon/Log Out_16x16.png"));
		buttonGoBack.setGraphic(new ImageView(image));
		// buttonGoBack.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		buttonGoBack.setContentDisplay(ContentDisplay.LEFT);
		image2 = new Image(getClass().getResourceAsStream("/icon/Search_16x16.png"));
		Tooltip ttGoBack = new Tooltip("Geht zur Hauptanwendung");
		buttonGoBack.setTooltip(ttGoBack);

		buttonPersonalSuche.setGraphic(new ImageView(image2));
		buttonPersonalSuche.setContentDisplay(ContentDisplay.LEFT);
		Tooltip ttSuche = new Tooltip("Personalsuche");
		buttonPersonalSuche.setTooltip(ttSuche);

		image3 = new Image(getClass().getResourceAsStream("/icon/Internet_xml_Icon_16.png"));
		buttonXMLDatei.setGraphic(new ImageView(image3));
		buttonXMLDatei.setContentDisplay(ContentDisplay.LEFT);
		Tooltip ttXML = new Tooltip("Speichern als *xml Datei");
		buttonXMLDatei.setTooltip(ttXML);
	}

	/**
	 * Mit dieser Methode legen wir die Eigenschaften der TextFields fest.
	 *
	 */
	public void createTextFieldNamePersonal(){
		// Listener Text Personalsucher
				txtNamePersonal.textProperty().addListener((observable, oldValue, newValue) -> {
					suchFeldPersonal = newValue;
				});
				Tooltip ttSuchePers = new Tooltip("Nachnamen suchen");
				txtNamePersonal.setTooltip(ttSuchePers);
	}

	/**
	 * Mit dieser Methode legen wir die Eigenschaften unserer TableView und TableColumn fest
	 */
	public void createTableColumn(){
		idColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, Integer>("id"));
		idColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

		persnrColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, Integer>("persnr"));
		persnrColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

		vornameColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, String>("vorname"));
		vornameColumn.setStyle("-fx-alignment: CENTER;");
		vornameColumn.setEditable(true); // Editierebar Aendern des Wertes

		// Achtung textField bei Double und Integer ausserdem new
		// DoubleStringConverter()
		// Beispiel siehe rdColumn mit Integer
		vornameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		nameColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, String>("nachname"));
		nameColumn.setStyle("-fx-alignment: CENTER;");

		strasseColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, String>("strasse"));
		strasseColumn.setStyle("-fx-alignment: CENTER;");

		wohnortColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, String>("wohnort"));
		wohnortColumn.setStyle("-fx-alignment: CENTER;");
		funktionColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, String>("funktion"));
		funktionColumn.setStyle("-fx-alignment: CENTER;");

		rdColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, Integer>("rd"));
		rdColumn.setStyle("-fx-alignment: CENTER;");
		rdColumn.setEditable(true);

		rdNameColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, String>("rdname"));
		rdNameColumn.setStyle("-fx-alignment: CENTER;");
		rdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		geschlechtColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, String>("geschlecht"));
		geschlechtColumn.setStyle("-fx-alignment: CENTER;");
		geschlechtColumn.setEditable(true);

		// Eintrittsdatum Editierbar 12.03.2016 Deutsches Format
		eintrittsdatumColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, LocalDate>("eintrittsdatum"));
		eintrittsdatumColumn.setStyle("-fx-alignment: CENTER;");
		eintrittsdatumColumn.setEditable(true);

		LocalDateStringConverter converter = new LocalDateStringConverter();
		eintrittsdatumColumn.setCellFactory(
		TextFieldTableCell.<Betreuer, LocalDate>forTableColumn(converter));

		//Gehalt
		gehaltColumn.setCellValueFactory(new PropertyValueFactory<Betreuer, Double>("gehalt"));
		gehaltColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

	}

	/**
	 * Mit dier Methode Formatieren wir einige Spalten
	 * Zum Beispiel Gehalt, Datum
	 */
	public void formatColumn(){

		/* --> Formatierung in Deutsches Format
		eintrittsdatumColumn.setCellFactory(col -> {
			TableCell<Betreuer, LocalDate> cell = new TableCell<Betreuer, LocalDate>() {
				@Override
				public void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);
					// Cleanup the cell before populating it
					this.setText(null);
					this.setGraphic(null);

					if (!empty) {
						// Format the birth date in mm/dd/yyyy format
						String formattedDob = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(item);
						this.setText(formattedDob);
					}
				}
			};
			return cell;
		});
	*/

		//Gehalt Formatieren
		gehaltColumn.setCellFactory(col -> {
			TableCell<Betreuer, Double> cell = new TableCell<Betreuer, Double>() {
				@Override
				public void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					// Cleanup the cell before populating it
					this.setText(null);
					this.setGraphic(null);
					if (!empty) {
						DecimalFormat df = new DecimalFormat("###,##0.00");
						df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.GERMAN));
						String formattedDob = df.format(item);
						this.setText(formattedDob);
					}
				}
			};
			return cell;
		});

	}

	/**
	 * Mit dieser Methode erstellen wir fuer den buttonExcelDatei den Task
	 * damit wir eine Exceldatei erstellen koennen
	 */
	public void createActionEvent(){

		Tooltip ttExcel = new Tooltip("Speichern als *xls Datei Excel");
		btnExcelDatei.setTooltip(ttExcel);
		btnExcelDatei.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				btnExcelDatei.setDisable(true); // Button nicht mehr drückbar
				// cancelButon.setDisable(false);

				// Binding - Ziel und Quelle
				erstelleExcel();
				ExcelTask copyTask = new ExcelTask(fileExcel);

				progressIndicator.setVisible(true);
				progressIndicator.progressProperty().unbind();
				progressIndicator.progressProperty().bind(copyTask.progressProperty());

				textMessage.setVisible(true);
				textMessage.textProperty().unbind();
				textMessage.textProperty().bind(copyTask.messageProperty());

				// Aufgabe (Task)
				copyTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						List<String> copiedList = copyTask.getValue();
						// System.out.println("Liste korpierter Daten: " +
						// copiedList);

						for (String listen : copiedList) {
							copiedFilesAsString.add(listen.toString());
						}

						@SuppressWarnings("unused")
						ObservableList<String> list = FXCollections.observableArrayList(copiedFilesAsString);
						// listView.setItems(list);

					}
				});

				copyTask.setOnSucceeded(e -> setNodesUnsichtbar());

				// Kopiervorgang Start
				Thread copyTaskThread = new Thread(copyTask);
				copyTaskThread.start();

			}

		});

	}

	/*
	 * Wir wollen einige Nodes nicht mehr sichtbar machen Erst wieder sichtbar,
	 * wenn der Task gestartet wird
	 */
	private void setNodesUnsichtbar() {
		progressIndicator.setVisible(false);
		textMessage.setVisible(false);
		btnExcelDatei.setDisable(false);
	}
}

/**
 * Diese Klasse wird benoetigt um alle Mitarbeiter in die TableView anzuzeigen.
 * Erbt von Task, damit die Anwendung stabil bleibt
 */
@SuppressWarnings("rawtypes")
class GetAllBetreuerDaten extends Task {

	@Override
	public ObservableList<Betreuer> call() throws Exception {

		BetreuerDAO dao = new BetreuerDAO();

		return FXCollections.observableArrayList(dao.getAllDaten());

	}
}

/**
 * Diese Klasse wird benoetigt um den gesuchten Namen anzuzeigen Erbt von Task
 */
@SuppressWarnings("rawtypes")
class GetAllNamenSuche extends Task {

	@Override
	public ObservableList<Betreuer> call() throws Exception {

		BetreuerDAO dao = new BetreuerDAO();
		String name = BetreuerViewController.suchFeldPersonal;

		return FXCollections.observableArrayList(dao.getPersonalName(name));

	}
}

/**
 *
 * @deprecated
 *
 */
@SuppressWarnings("rawtypes")
class getAllNamesSearch extends Task {

	@Override
	protected ObservableList<Betreuer> call() throws Exception {
		BetreuerDAO dao = new BetreuerDAO();
		String name = BetreuerViewController.suchFeldPersonal;
		return FXCollections.observableArrayList(dao.getPersonalName(name));
	}

}
