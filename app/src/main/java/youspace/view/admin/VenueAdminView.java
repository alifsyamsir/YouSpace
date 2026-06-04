package youspace.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import youspace.enums.VenueCategory;
import youspace.models.Venue;
import youspace.service.VenueService;

public class VenueAdminView {

    private final Stage stage;
    private final VenueService venueService;
    private TableView<Venue> tableView;
    private String currentCategory = "Semua";

    public VenueAdminView(Stage stage) {
        this.stage = stage;
        this.venueService = new VenueService(); // Menggunakan service asli kamu
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F8F9FA;");

        // Sidebar reusable
        SidebarAdmin sidebar = new SidebarAdmin(stage, "Venue");
        root.setLeft(sidebar);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(35, 40, 35, 40));

        // Header
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label txtHeader = new Label("Kelola Venue");
        txtHeader.setFont(Font.font("System", FontWeight.BOLD, 26));
        txtHeader.setStyle("-fx-text-fill: #1A202C;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnTambah = new Button("+ Tambah Venue");
        btnTambah.setStyle("-fx-background-color: #1A365D; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand; -fx-font-weight: bold;");
        btnTambah.setOnAction(e -> {
            VenueFormDialog dialog = new VenueFormDialog(stage, null);
            dialog.showAndWait();
            refreshTable();
        });

        headerRow.getChildren().addAll(txtHeader, spacer, btnTambah);
        mainContent.getChildren().add(headerRow);

        // Filter Bar (Pill Buttons)
        HBox filterBar = new HBox(10);
        String[] kategoriArr = {"Semua", "Aula", "Ballroom", "Meeting Room", "Studio", "Wedding Room"};
        for (String kat : kategoriArr) {
            Button btnFilter = new Button(kat);
            setFilterButtonStyle(btnFilter, kat.equals(currentCategory));
            btnFilter.setOnAction(e -> {
                currentCategory = kat;
                filterBar.getChildren().forEach(node -> {
                    Button b = (Button) node;
                    setFilterButtonStyle(b, b.getText().equals(currentCategory));
                });
                refreshTable();
            });
            filterBar.getChildren().add(btnFilter);
        }
        mainContent.getChildren().add(filterBar);

        // Table View dengan Model Venue Asli
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle("-fx-background-radius: 10; -fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");

        TableColumn<Venue, Integer> colId = new TableColumn<>("No.");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Venue, String> colNama = new TableColumn<>("Nama Ruangan");
        colNama.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Venue, VenueCategory> colTipe = new TableColumn<>("Tipe Ruangan");
        colTipe.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Venue, Object> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Tombol Aksi Edit
        TableColumn<Venue, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("↗");
            {
                btnEdit.setStyle("-fx-background-color: #EDF2F7; -fx-text-fill: #2B6CB0; -fx-background-radius: 50; -fx-font-weight: bold; -fx-cursor: hand;");
                btnEdit.setOnAction(e -> {
                    Venue selectedVenue = getTableView().getItems().get(getIndex());
                    VenueFormDialog dialog = new VenueFormDialog(stage, selectedVenue);
                    dialog.showAndWait();
                    refreshTable();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btnEdit);
            }
        });

        tableView.getColumns().addAll(colId, colNama, colTipe, colStatus, colAksi);
        mainContent.getChildren().add(tableView);

        refreshTable();
        root.setCenter(mainContent);
        return new Scene(root, 1280, 760);
    }

    private void refreshTable() {
        tableView.getItems().clear();
        if (currentCategory.equals("Semua")) {
            tableView.getItems().addAll(venueService.getAllVenues());
        } else {
            // Convert filter string to enum, handling special case for Wedding Room
            String enumName = currentCategory.toUpperCase().replace(" ", "_");
            VenueCategory catEnum;
            if (enumName.equals("WEDDING_ROOM")) {
                catEnum = VenueCategory.WEDDING_HALL; // map to actual enum constant
            } else {
                catEnum = VenueCategory.valueOf(enumName);
            }
            tableView.getItems().addAll(venueService.getVenuesByCategory(catEnum));
        }
    }

    private void setFilterButtonStyle(Button btn, boolean isActive) {
        if (isActive) {
            btn.setStyle("-fx-background-color: #1A365D; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 6 15;");
        } else {
            btn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #4A5568; -fx-background-radius: 15; -fx-padding: 6 15; -fx-cursor: hand;");
        }
    }
}