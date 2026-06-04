package youspace.view.user;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import youspace.models.Venue;
import youspace.service.FavoriteService;
import youspace.utils.SessionManager;
import youspace.utils.ViewUtil;


import java.util.List;

public class FavoriteUserView {

    private final Stage stage;

    private final FavoriteService favoriteService;

    public FavoriteUserView(Stage stage) {

        this.stage = stage;

        this.favoriteService =
                new FavoriteService();
    }

    public Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:#F8F9FA;"
        );

        SidebarUser sidebar =
                new SidebarUser(stage, "Favorit");

        root.setLeft(sidebar);

        VBox mainContent =
                new VBox(20);

        mainContent.setPadding(
                new Insets(30)
        );

        Label title =
                new Label("Favorit Venue");

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        34
                )
        );

        title.setStyle(
                "-fx-text-fill:#16355B;"
        );

        mainContent.getChildren().add(title);

        GridPane grid =
                new GridPane();

        grid.setHgap(25);

        grid.setVgap(25);

        int userId =
                SessionManager
                        .getCurrentUser()
                        .getId();

        List<Venue> venues =
                favoriteService
                        .getFavoriteVenuesByUser(
                                userId
                        );

        int column = 0;
        int row = 0;

        VenueUserView helper =
                new VenueUserView(stage);

        for (Venue venue : venues) {

            VBox card =
                    helper.createVenueCard(
                            venue
                    );

            grid.add(card, column, row);

            column++;

            if (column == 2) {

                column = 0;

                row++;
            }
        }

        ScrollPane scrollPane =
                new ScrollPane(grid);

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle("""
            -fx-background-color:transparent;
        """);

        mainContent.getChildren().add(scrollPane);

        root.setCenter(
                ViewUtil.createScrollable(mainContent)
        );

        return new Scene(root, 1280, 760);
    }
}