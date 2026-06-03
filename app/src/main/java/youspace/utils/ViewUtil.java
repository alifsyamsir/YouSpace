package youspace.utils;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class ViewUtil {

    private ViewUtil() {
    }

    public static ScrollPane createScrollable(Node content) {

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setPannable(true);

        scrollPane.setStyle("""
            -fx-background:#F8FAFC;
            -fx-background-color:#F8FAFC;
            -fx-padding:0;
            -fx-border-width:0;
        """);

        return scrollPane;
    }
}