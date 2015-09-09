package io.github.mzmine.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author mjrb5
 */
public class FXTabs extends Application {

    @Override
    public void start(final Stage primaryStage) {

        DraggableTab tab1 = new DraggableTab("Tab 1");
        tab1.setClosable(true);
        tab1.setDetachable(true);
        tab1.setContent(new Rectangle(500, 500, Color.BLACK));

        DraggableTab tab2 = new DraggableTab("Tab 2");
        tab2.setClosable(true);
        tab2.setContent(new Rectangle(500, 500, Color.RED));

        DraggableTab tab3 = new DraggableTab("Tab 3");
        tab3.setClosable(true);
        tab3.setContent(new Rectangle(500, 500, Color.BLUE));

        DraggableTab tab4 = new DraggableTab("Tab 4");
        tab4.setClosable(true);
        tab4.setContent(new Rectangle(500, 500, Color.ORANGE));

        TabPane tabs = new TabPane();
        tabs.getTabs().add(tab1);
        tabs.getTabs().add(tab2);
        tabs.getTabs().add(tab3);
        tabs.getTabs().add(tab4);

        StackPane root = new StackPane();
        root.getChildren().add(tabs);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
