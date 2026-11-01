Localized JavaFX Shopping Cart - Khaled Marai
Completed source project for the GUI localization assignment. The application title and the visible heading use the supplied name 11.

Run
Install JDK 21 and Maven 3.9 or later, then open a terminal in this folder:

mvn clean verify
mvn javafx:run
On Windows, run.cmd also launches the app. The first build downloads dependencies. JavaFX is supplied by Maven; it does not need a separate SDK installation.

Select English, Finnish, Swedish, Japanese, or Arabic.
Set the number of items (1–100), then choose Enter items.
Enter each price and quantity, then choose Calculate total.
Switch languages at any time. Valid entered values are reformatted and calculated totals are retained. Arabic uses right-to-left layout.
All prices use EUR; changing the language changes formatting, not currency or exchange rates. Prices must be nonnegative with at most two decimal places. Quantities must be nonnegative integers within Java's integer range. Use the selected locale's decimal separator and no grouping separators. Finnish and Swedish use a comma; English and Japanese use a period; Arabic supports the Arabic decimal separator. No taxes or discounts apply. Editing any value clears stale totals until recalculation. Changing the item count preserves retained rows; reducing it removes trailing rows.

Implementation
cart.fxml: GUI structure, initial resource lookups, and controller bindings.
Controller.java: dynamic labels, item rows, input validation, language switching and right-to-left layout.
MessagesBundle_*.properties: UTF-8 translations, including all validation messages.
CartItem and CartCalculator: GUI-independent calculation using BigDecimal.
Numbers: locale-aware parsing and EUR formatting.
CartTest: JUnit 5 calculation, validation, parsing and bundle tests.
pom.xml: JavaFX, JUnit 5, and JaCoCo; verify requires 100% line and branch coverage of the model package.
The assignment says “four” files but lists five languages, so all five are included. It lists en_UK alongside an en_US filename; this project consistently uses en_US. It retains the requested ar_AR resource filename (AR is not an Arabic-speaking country code); EUR is set explicitly and Arabic layout is selected by language. An English base bundle provides fallback text.