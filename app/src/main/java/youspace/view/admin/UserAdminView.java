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
import youspace.enums.UserStatus;
import youspace.models.AppUser;
import youspace.service.UserService;

import java.util.Optional;

public class UserAdminView {

    private final Stage stage;
    private final UserService userService;
    private TableView<AppUser> tableView;
    private TextField tfSearch;

    public UserAdminView(Stage stage) {
        this.stage = stage;
        this.userService = new UserService(); // Menggunakan UserService asli milikmu
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F8F9FA;");

        // Memanggil komponen sidebar admin yang reusable
        SidebarAdmin sidebar = new SidebarAdmin(stage, "Pengguna");
        root.setLeft(sidebar);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(35, 40, 35, 40));

        // Header Atas & Kolom Pencarian
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label txtHeader = new Label("Kelola Pengguna");
        txtHeader.setFont(Font.font("System", FontWeight.BOLD, 26));
        txtHeader.setStyle("-fx-text-fill: #1A202C;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        tfSearch = new TextField();
        tfSearch.setPromptText("🔍 Cari Nama...");
        tfSearch.setPrefWidth(220);
        tfSearch.setStyle("-fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10; -fx-padding: 8 12;");
        
        // Memanggil metode searchUsers(keyword) asli milikmu secara real-time
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshTable(newValue);
        });

        headerRow.getChildren().addAll(txtHeader, spacer, tfSearch);
        mainContent.getChildren().add(headerRow);

        // Konfigurasi Table View untuk AppUser
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle("-fx-background-radius: 10; -fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");

        TableColumn<AppUser, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<AppUser, String> colNama = new TableColumn<>("Nama");
        colNama.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<AppUser, String> colPhone = new TableColumn<>("Nomor Telpon");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<AppUser, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Kolom Custom Visual Badge Status (ACTIVE / SUSPENDED)
        TableColumn<AppUser, UserStatus> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(param -> new TableCell<>() {
            private final Label labelBadge = new Label();
            @Override
            protected void updateItem(UserStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    if (item == UserStatus.ACTIVE) {
                        labelBadge.setText("Aktif");
                        labelBadge.setStyle("-fx-background-color: #C6F6D5; -fx-text-fill: #22543D; -fx-padding: 4 12; -fx-background-radius: 10; -fx-font-weight: bold;");
                    } else {
                        labelBadge.setText("Ditangguhkan");
                        labelBadge.setStyle("-fx-background-color: #FED7D7; -fx-text-fill: #742A2A; -fx-padding: 4 12; -fx-background-radius: 10; -fx-font-weight: bold;");
                    }
                    setGraphic(labelBadge);
                }
            }
        });

        // Kolom Tombol Aksi Kontrol (Aktivasi / Tangguhkan & Hapus)
        TableColumn<AppUser, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnToggleStatus = new Button();
            private final Button btnDelete = new Button("🗑");
            private final HBox container = new HBox(8, btnToggleStatus, btnDelete);
            {
                container.setAlignment(Pos.CENTER);
                btnDelete.setStyle("-fx-background-color: #FED7D7; -fx-text-fill: #C53030; -fx-background-radius: 5; -fx-cursor: hand;");

                // Logika Tombol Tangguhkan / Aktifkan Kembali
                btnToggleStatus.setOnAction(e -> {
                    AppUser selectedUser = getTableView().getItems().get(getIndex());
                    boolean isCurrentlyActive = selectedUser.getStatus() == UserStatus.ACTIVE;
                    String konfirmasiMsg = isCurrentlyActive 
                            ? "Apakah anda yakin ingin menangguhkan akun " + selectedUser.getName() + "?"
                            : "Apakah anda yakin ingin mengaktifkan kembali akun " + selectedUser.getName() + "?";
                    
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText(null);
                    alert.setContentText(konfirmasiMsg);
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        if (isCurrentlyActive) {
                            userService.suspendUser(selectedUser.getId()); // Panggil suspendUser asli
                        } else {
                            userService.activateUser(selectedUser.getId()); // Panggil activateUser asli
                        }
                        refreshTable(tfSearch.getText());
                    }
                });

                // Logika Tombol Hapus Permanen
                btnDelete.setOnAction(e -> {
                    AppUser selectedUser = getTableView().getItems().get(getIndex());
                    
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Apakah anda yakin ingin menghapus user " + selectedUser.getName() + " secara permanen?");
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        userService.deleteUser(selectedUser.getId()); // Panggil deleteUser asli
                        refreshTable(tfSearch.getText());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AppUser currentUser = getTableView().getItems().get(getIndex());
                    // Mengubah icon / teks tombol status secara adaptif sesuai kondisi user saat ini
                    if (currentUser.getStatus() == UserStatus.ACTIVE) {
                        btnToggleStatus.setText("⚠️ Suspend");
                        btnToggleStatus.setStyle("-fx-background-color: #FEFCBF; -fx-text-fill: #744210; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 11;");
                    } else {
                        btnToggleStatus.setText("✅ Aktifkan");
                        btnToggleStatus.setStyle("-fx-background-color: #EBF8FF; -fx-text-fill: #2B6CB0; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 11;");
                    }
                    setGraphic(container);
                }
            }
        });
        colAksi.setPrefWidth(150);

        tableView.getColumns().addAll(colId, colNama, colPhone, colEmail, colStatus, colAksi);
        mainContent.getChildren().add(tableView);

        // Load data pertama kali saat halaman dibuka
        refreshTable("");
        root.setCenter(mainContent);
        return new Scene(root, 1280, 760);
    }

    // Fungsi pembantu untuk memuat ulang data ke tabel secara dinamis
    private void refreshTable(String keyword) {
        tableView.getItems().clear();
        // Menggunakan metode searchUsers() asli milikmu
        tableView.getItems().addAll(userService.searchUsers(keyword));
    }
}