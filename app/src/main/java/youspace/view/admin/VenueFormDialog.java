package youspace.view.admin;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import youspace.enums.VenueCategory;
import youspace.enums.VenueStatus;

import youspace.models.Venue;

import youspace.service.VenueService;

public class VenueFormDialog extends Stage {

    private final Venue existingVenue;

    private final VenueService venueService;

    private ComboBox<VenueCategory> cbCategory;

    private ComboBox<VenueStatus> cbStatus;

    private TextField tfName;

    private TextField tfPrice;

    private TextField tfCapacity;

    private TextField tfImage;

    private TextArea taDescription;

    private ImageView previewImage;

    public VenueFormDialog(
            Stage owner,
            Venue existingVenue
    ) {

        this.existingVenue = existingVenue;

        this.venueService =
                new VenueService();

        initOwner(owner);

        initModality(
                Modality.APPLICATION_MODAL
        );

        setTitle(
                existingVenue == null
                        ? "Tambah Venue"
                        : "Edit Venue"
        );

        BorderPane root =
                new BorderPane();

        root.setStyle("""
            -fx-background-color:#F8FAFC;
        """);

        VBox container =
                new VBox(25);

        container.setPadding(
                new Insets(35)
        );

        // ================= HEADER =================

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Label title =
                new Label(
                        existingVenue == null
                                ? "Tambah Venue"
                                : "Edit Venue"
                );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        32
                )
        );

        title.setStyle("""
            -fx-text-fill:#183B63;
        """);

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button btnClose =
                new Button("✕");

        btnClose.setStyle("""
            -fx-background-color:transparent;
            -fx-font-size:24;
            -fx-cursor:hand;
        """);

        btnClose.setOnAction(e -> close());

        header.getChildren().addAll(
                title,
                spacer,
                btnClose
        );

        // ================= FORM =================

        HBox formSection =
                new HBox(25);

        // ================= LEFT =================

        VBox leftSection =
                new VBox(18);

        previewImage =
                new ImageView();

        previewImage.setFitWidth(320);

        previewImage.setFitHeight(190);

        previewImage.setPreserveRatio(false);

        previewImage.setStyle("""
            -fx-background-radius:14;
        """);

        tfImage =
                new TextField();

        tfImage.setEditable(false);

        tfImage.setPromptText(
                "Pilih gambar venue..."
        );

        Button btnUpload =
                new Button("Unggah Gambar");

        btnUpload.setStyle("""
            -fx-background-color:#183B63;
            -fx-text-fill:white;
            -fx-font-weight:bold;
            -fx-background-radius:10;
            -fx-cursor:hand;
        """);

        btnUpload.setOnAction(e -> {

            FileChooser chooser =
                    new FileChooser();

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Image Files",
                            "*.png",
                            "*.jpg",
                            "*.jpeg"
                    )
            );

            File file =
                    chooser.showOpenDialog(this);

            if (file != null) {

                tfImage.setText(
                        file.getAbsolutePath()
                );

                previewImage.setImage(
                        new Image(
                                file.toURI().toString()
                        )
                );
            }
        });

        taDescription =
                new TextArea();

        taDescription.setPromptText(
                "Deskripsi venue..."
        );

        taDescription.setPrefHeight(180);

        taDescription.setWrapText(true);

        leftSection.getChildren().addAll(
                previewImage,
                tfImage,
                btnUpload,
                createLabel("Deskripsi"),
                taDescription
        );

        // ================= RIGHT =================

        VBox rightSection =
                new VBox(16);

        GridPane grid =
                new GridPane();

        grid.setHgap(18);

        grid.setVgap(15);

        cbCategory =
                new ComboBox<>();

        cbCategory.getItems().addAll(
                VenueCategory.values()
        );

        cbStatus =
                new ComboBox<>();

        cbStatus.getItems().addAll(
                VenueStatus.values()
        );

        tfName =
                new TextField();

        tfPrice =
                new TextField();

        tfCapacity =
                new TextField();

        grid.add(createLabel("Tipe"), 0, 0);
        grid.add(cbCategory, 0, 1);

        grid.add(createLabel("Kondisi"), 1, 0);
        grid.add(cbStatus, 1, 1);

        grid.add(createLabel("Nama Venue"), 0, 2, 2, 1);
        grid.add(tfName, 0, 3, 2, 1);

        grid.add(createLabel("Harga / Hari"), 0, 4);
        grid.add(tfPrice, 0, 5);

        grid.add(createLabel("Kapasitas"), 1, 4);
        grid.add(tfCapacity, 1, 5);

        rightSection.getChildren().add(grid);
        formSection.getChildren().addAll(
                leftSection,
                rightSection
        );

        // ================= BUTTON =================

        HBox footer =
                new HBox();

        footer.setAlignment(
                Pos.CENTER_RIGHT
        );

        Button btnSave =
                new Button(
                        existingVenue == null
                                ? "Simpan Venue"
                                : "Simpan Perubahan"
                );

        btnSave.setPrefWidth(220);

        btnSave.setPrefHeight(48);

        btnSave.setStyle("""
            -fx-background-color:#183B63;
            -fx-text-fill:white;
            -fx-font-weight:bold;
            -fx-font-size:15;
            -fx-background-radius:14;
            -fx-cursor:hand;
        """);

        btnSave.setOnAction(e -> handleSave());

        footer.getChildren().add(btnSave);

        // ================= ADD =================

        container.getChildren().addAll(
                header,
                formSection,
                footer
        );

        root.setCenter(container);

        loadExistingData();

        setScene(
                new Scene(root, 1000, 700)
        );
    }

    private void loadExistingData() {

        if (existingVenue == null) {

            cbStatus.setValue(
                    VenueStatus.AVAILABLE
            );

            return;
        }

        cbCategory.setValue(
                existingVenue.getCategory()
        );

        cbStatus.setValue(
                existingVenue.getStatus()
        );

        tfName.setText(
                existingVenue.getName()
        );

        tfPrice.setText(
                String.valueOf(
                        existingVenue.getPricePerDay()
                )
        );

        tfCapacity.setText(
                String.valueOf(
                        existingVenue.getCapacity()
                )
        );

        tfImage.setText(
                existingVenue.getImagePath()
        );

        taDescription.setText(
                existingVenue.getDescription()
        );

        if (
                existingVenue.getImagePath()
                        != null
        ) {

            previewImage.setImage(
                    new Image(
                            new File(
                                    existingVenue.getImagePath()
                            ).toURI().toString()
                    )
            );
        }
    }

    private void handleSave() {

        try {

            String name =
                    tfName.getText();

            String desc =
                    taDescription.getText();

            VenueCategory category =
                    cbCategory.getValue();

            VenueStatus status =
                    cbStatus.getValue();

            int capacity =
                    Integer.parseInt(
                            tfCapacity.getText()
                    );

            double price =
                    Double.parseDouble(
                            tfPrice.getText()
                    );

            String image =
                    tfImage.getText();

            boolean success;

            if (existingVenue == null) {

                success =
                        venueService.addVenue(
                                name,
                                desc,
                                category,
                                capacity,
                                price,
                                image
                        );

            } else {

                existingVenue.setName(name);
                existingVenue.setDescription(desc);
                existingVenue.setCategory(category);
                existingVenue.setCapacity(capacity);
                existingVenue.setPricePerDay(price);
                existingVenue.setImagePath(image);
                existingVenue.setStatus(status);

                success =
                        venueService.updateVenue(
                                existingVenue
                        );
            }

            if (success) {

                close();

            } else {

                throw new Exception(
                        "Gagal menyimpan data"
                );
            }

        } catch (Exception ex) {

            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setHeaderText(null);

            alert.setContentText(
                    ex.getMessage()
            );

            alert.showAndWait();
        }
    }

    private Label createLabel(String text) {

        Label label =
                new Label(text);

        label.setFont(
                Font.font(
                        "System",
                        FontWeight.SEMI_BOLD,
                        14
                )
        );

        return label;
    }
}