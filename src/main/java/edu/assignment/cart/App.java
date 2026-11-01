package edu.assignment.cart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ResourceBundle bundle =
                ResourceBundle.getBundle("MessagesBundle", Locale.US);

        FXMLLoader loader = new FXMLLoader(
                requireResource("/edu.assignment.cart/cart.fxml"),
                bundle
        );

        Scene scene = new Scene(loader.load(), 920, 750);

        scene.getStylesheets().add(
                requireResource("/edu.assignment.cart/style.css")
                        .toExternalForm()
        );

        Controller controller = loader.getController();
        controller.attach(stage);

        stage.setScene(scene);
        stage.setMinWidth(820);
        stage.setMinHeight(580);
        stage.show();

        if (getParameters().getRaw().contains("--screenshots")) {
            Platform.runLater(() -> {
                try {
                    Path directory = Path.of("screenshots");
                    Files.createDirectories(directory);

                    for (int i = 0; i < Controller.CODES.length; i++) {
                        controller.demo(i);

                        scene.getRoot().applyCss();
                        scene.getRoot().layout();

                        Path screenshot = directory.resolve(
                                "cart-" + Controller.CODES[i] + ".png"
                        );

                        ImageIO.write(
                                SwingFXUtils.fromFXImage(
                                        scene.snapshot(null), null
                                ),
                                "png",
                                screenshot.toFile()
                        );
                    }

                    controller.verifyDemo();

                    System.out.println(
                            "GUI_SMOKE_OK: five locales, FXML, rows, "
                                    + "totals, RTL and screenshots"
                    );

                    Platform.exit();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    System.exit(1);
                }
            });
        }
    }

    private static URL requireResource(String path) {
        URL resource = App.class.getResource(path);

        if (resource == null) {
            throw new IllegalStateException(
                    "Missing resource: " + path
                            + ". Expected file: src/main/resources"
                            + path
                            + ". Run mvn clean compile before launching."
            );
        }

        return resource;
    }
}