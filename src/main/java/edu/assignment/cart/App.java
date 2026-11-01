package edu.assignment.cart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {
    public static void main(String[] args) { launch(args); }
    @Override public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("cart.fxml"),
                ResourceBundle.getBundle("MessagesBundle", Locale.US));
        Scene scene = new Scene(loader.load(), 920, 750);
        scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
        Controller controller = loader.getController();
        controller.attach(stage);
        stage.setScene(scene);
        stage.setMinWidth(820); stage.setMinHeight(580); stage.show();
        if (getParameters().getRaw().contains("--screenshots")) {
            Platform.runLater(() -> {
                try {
                    Path directory = Path.of("screenshots"); Files.createDirectories(directory);
                    for (int i = 0; i < 5; i++) {
                        controller.demo(i);
                        scene.getRoot().applyCss(); scene.getRoot().layout();
                        ImageIO.write(SwingFXUtils.fromFXImage(scene.snapshot(null), null), "png",
                                directory.resolve("cart-" + Controller.CODES[i] + ".png").toFile());
                    }
                    controller.verifyDemo();
                    System.out.println("GUI_SMOKE_OK: five locales, FXML, rows, totals, RTL and screenshots");
                    Platform.exit();
                } catch (Exception ex) { ex.printStackTrace(); System.exit(1); }
            });
        }
    }
}
