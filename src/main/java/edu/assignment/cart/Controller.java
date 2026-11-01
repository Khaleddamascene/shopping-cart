package edu.assignment.cart;

import edu.assignment.cart.model.CartItem;
import edu.assignment.cart.model.CartCalculator;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

public class Controller {
    static final String[] CODES = {"en_US", "fi_FI", "sv_SE", "ja_JP", "ar_AR"};
    private static final String[] NAMES = {"English", "Suomi", "Svenska", "日本語", "العربية"};
    @FXML private VBox root, rows;
    @FXML private Label title, subtitle, languageLabel, countLabel, totalLabel, totalValue, error;
    @FXML private TextField count;
    @FXML private Button create, calculate;
    @FXML private ComboBox<String> language;
    private final List<ItemRow> items = new ArrayList<>();
    private Locale locale = Locale.US;
    private ResourceBundle bundle;
    private Stage stage;
    private boolean calculated;
    private record ItemRow(HBox box, Label name, Label priceLabel, TextField price,
                           Label quantityLabel, TextField quantity, Label total) {}

    @FXML public void initialize() {
        language.getItems().addAll(NAMES);
        language.getSelectionModel().select(0);
        bundle = ResourceBundle.getBundle("MessagesBundle", locale);
        language.valueProperty().addListener((o, old, value) -> changeLanguage(language.getSelectionModel().getSelectedIndex()));
        count.setText("3");
        localize(); createItems();
    }
    public void attach(Stage stage) { this.stage = stage; localize(); }
    private String text(String key) { return bundle.getString(key); }
    private String itemName(int i) { return new MessageFormat(text("item"), locale).format(new Object[]{i}); }
    private void changeLanguage(int index) {
        boolean wasCalculated = calculated;
        Locale next = Locale.forLanguageTag(CODES[index].replace('_', '-'));
        for (ItemRow row : items) {
            convert(row.price, next); convert(row.quantity, next);
        }
        convert(count, next);
        locale = next;
        bundle = ResourceBundle.getBundle("MessagesBundle", locale);
        error.setText(""); localize();
        if (wasCalculated) calculate();
    }
    private void convert(TextField field, Locale next) {
        try { field.setText(Numbers.decimal(Numbers.parse(field.getText(), locale), next)); }
        catch (IllegalArgumentException ignored) { /* Keep unfinished input for correction. */ }
    }
    private void localize() {
        root.setNodeOrientation(locale.getLanguage().equals("ar") ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
        title.setText("Khaled Marai / " + text("title"));
        if (stage != null) stage.setTitle(title.getText());
        subtitle.setText(text("subtitle")); languageLabel.setText(text("language"));
        countLabel.setText(text("count")); count.setPromptText(text("countPrompt"));
        create.setText(text("create")); calculate.setText(text("calculate")); totalLabel.setText(text("total"));
        for (int i = 0; i < items.size(); i++) {
            ItemRow row = items.get(i);
            row.name.setText(itemName(i + 1)); row.priceLabel.setText(text("price"));
            row.quantityLabel.setText(text("quantity")); row.price.setPromptText(text("pricePrompt"));
            row.quantity.setPromptText(text("quantityPrompt"));
            row.price.setAccessibleText(text("price") + " " + itemName(i + 1));
            row.quantity.setAccessibleText(text("quantity") + " " + itemName(i + 1));
            if (!calculated) row.total.setText(text("itemTotal") + " —");
        }
        if (!calculated) totalValue.setText("—");
    }
    @FXML public void createItems() {
        try {
            int number = Numbers.parse(count.getText(), locale).intValueExact();
            if (number < 1 || number > 100) throw new IllegalArgumentException();
            while (items.size() > number) rows.getChildren().remove(items.remove(items.size() - 1).box);
            while (items.size() < number) {
                Label name = new Label(); name.setMinWidth(90);
                Label priceLabel = new Label(), quantityLabel = new Label(), total = new Label();
                TextField price = new TextField(), quantity = new TextField();
                price.setPrefWidth(150); quantity.setPrefWidth(130);
                VBox p = new VBox(5, priceLabel, price), q = new VBox(5, quantityLabel, quantity);
                HBox box = new HBox(20, name, p, q, total); box.getStyleClass().add("item-row");
                box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                ItemRow row = new ItemRow(box, name, priceLabel, price, quantityLabel, quantity, total);
                items.add(row); rows.getChildren().add(box);
                price.textProperty().addListener((o,a,b) -> invalidate());
                quantity.textProperty().addListener((o,a,b) -> invalidate());
            }
            invalidate(); error.setText(""); localize();
        } catch (IllegalArgumentException | ArithmeticException ex) { error.setText(text("countError")); }
    }
    private void invalidate() {
        calculated = false; totalValue.setText("—");
        for (ItemRow row : items) row.total.setText(text("itemTotal") + " —");
    }
    @FXML public void calculate() {
        List<CartItem> values = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            ItemRow row = items.get(i);
            try {
                values.add(new CartItem(Numbers.parse(row.price.getText(), locale),
                        Numbers.parse(row.quantity.getText(), locale).intValueExact()));
            } catch (IllegalArgumentException | ArithmeticException ex) {
                invalidate(); error.setText(itemName(i + 1) + ": " + text("inputError")); return;
            }
        }
        for (int i = 0; i < items.size(); i++) items.get(i).total.setText(text("itemTotal") + " " + Numbers.money(values.get(i).total(), locale));
        totalValue.setText(Numbers.money(CartCalculator.total(values), locale));
        error.setText(""); calculated = true;
    }
    void demo(int index) {
        language.getSelectionModel().select(index);
        String[] prices = {"12.50", "3.99", "8.00"};
        int[] quantities = {2, 3, 1};
        for (int i = 0; i < 3; i++) {
            items.get(i).price.setText(Numbers.decimal(new BigDecimal(prices[i]), locale));
            items.get(i).quantity.setText(Numbers.decimal(BigDecimal.valueOf(quantities[i]), locale));
        }
        calculate(); verifyDemo();
    }
    void verifyDemo() {
        if (!totalValue.getText().equals(Numbers.money(new BigDecimal("44.97"), locale)) || !error.getText().isEmpty())
            throw new IllegalStateException("GUI total mismatch");
        if (locale.getLanguage().equals("ar") && root.getNodeOrientation() != NodeOrientation.RIGHT_TO_LEFT)
            throw new IllegalStateException("Arabic direction mismatch");
    }
}
