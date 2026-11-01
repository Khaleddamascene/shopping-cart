# Localized JavaFX Shopping Cart — Khaled Marai

A JavaFX shopping cart application developed for the GUI localization assignment. Users can enter item prices and quantities, calculate totals, and switch between five languages.

The application heading and window title display **Khaled Marai**, or **خالد مرعي** when Arabic is selected.

## Requirements

- JDK 21
- Maven 3.9 or later

Maven downloads the JavaFX dependencies automatically. A separate JavaFX SDK installation is not required.

## Run the application

Open a terminal in the project directory:

```sh
mvn clean verify
mvn javafx:run
```

On Windows, you can also use `run.cmd` if it is included in your checkout. The first build requires an internet connection to download dependencies.

## How to use

1. Select English, Finnish, Swedish, Japanese, or Arabic.
2. Enter the number of items, from 1 to 100, and select **Enter items**.
3. Enter a price and quantity for each item.
4. Select **Calculate total** to display each item's subtotal and the overall cost.
5. Switch languages to update the labels, prompts, buttons, messages, and number formatting.

Arabic uses a right-to-left layout. Valid entered numbers are reformatted when the language changes, and calculated totals are recalculated in the selected format.

## Calculation and input rules

- All prices and totals use EUR. Changing languages does not convert currencies.
- Prices must be nonnegative and have at most two decimal places.
- Quantities must be nonnegative whole numbers within Java's `int` range.
- Use the selected language's decimal separator without thousands separators.
- English and Japanese use a period; Finnish and Swedish use a comma. Arabic supports Arabic digits and the decimal separator `٫`, for example `١٢٫٥٠`.
- No taxes or discounts are applied.
- Editing a price or quantity clears the displayed totals until recalculation.
- Changing the item count preserves retained rows; reducing the count removes trailing rows.

The calculation uses `BigDecimal` to avoid binary floating-point rounding errors.

Example:

```text
12.50 × 2 = 25.00 EUR
 3.99 × 3 = 11.97 EUR
 8.00 × 1 =  8.00 EUR
Total     = 44.97 EUR
```

## Implementation

| File or class | Purpose |
| --- | --- |
| `App.java` | Loads the FXML interface, applies styling, and starts the application. |
| `Launcher.java` | Provides the application entry point for classpath-based launches. |
| `cart.fxml` | Defines the GUI structure and controller bindings. |
| `style.css` | Defines the application's appearance. |
| `Controller.java` | Manages item rows, validation, language switching, titles, and displayed totals. |
| `MessagesBundle_*.properties` | Provides UTF-8 translations, including validation messages. |
| `CartItem` | Validates an item's price and quantity and calculates its subtotal. |
| `CartCalculator` | Sums all item subtotals. |
| `Numbers` | Parses localized numbers and formats decimal and EUR values. |
| `CartTest` | Tests calculations, invalid input, localized parsing, and translation bundles. |
| `pom.xml` | Configures JavaFX, JUnit 5, JaCoCo, and Maven build plugins. |

## Localization

| Language | Resource file |
| --- | --- |
| English | `MessagesBundle_en_US.properties` |
| Finnish | `MessagesBundle_fi_FI.properties` |
| Swedish | `MessagesBundle_sv_SE.properties` |
| Japanese | `MessagesBundle_ja_JP.properties` |
| Arabic | `MessagesBundle_ar_AR.properties` |

`MessagesBundle.properties` supplies English fallback text.

The assignment mentions four resource files but lists five languages; this project includes all five. It also lists `en_UK` alongside an `en_US` filename; this implementation consistently uses `en_US`.

The Arabic bundle retains the assignment's `ar_AR` filename. Since `AR` denotes Argentina, the number-formatting code explicitly selects Arabic numerals for Arabic locales rather than relying on the region's default numbering system. Currency remains EUR in every language.

## Resource placement

The current `App.java` uses these absolute classpath paths:

```java
App.class.getResource("/edu.assignment.cart/cart.fxml")
App.class.getResource("/edu.assignment.cart/style.css")
```

These paths correspond to a **single directory named `edu.assignment.cart`**:

```text
src/main/resources/
├── MessagesBundle.properties
├── MessagesBundle_en_US.properties
├── MessagesBundle_fi_FI.properties
├── MessagesBundle_sv_SE.properties
├── MessagesBundle_ja_JP.properties
├── MessagesBundle_ar_AR.properties
└── edu.assignment.cart/
    ├── cart.fxml
    └── style.css
```

If you change this to nested directories `edu/assignment/cart`, update both resource paths in `App.java` accordingly. Rebuild with `mvn clean compile` after moving resources.

## Unit tests and coverage

Run the tests and generate the JaCoCo report:

```sh
mvn clean verify
```

Open the report at:

```text
target/site/jacoco/index.html
```

The current POM enforces **at least 90% line coverage** for `edu.assignment.cart.model`. This is the configured threshold, not a claim about the latest measured result.

The core calculation classes are `CartItem` and `CartCalculator`. Their coverage appears under `edu.assignment.cart.model`. GUI classes such as `App` and `Controller` appear under `edu.assignment.cart`; calculation unit tests do not provide full GUI coverage.

## Docker and Jenkins

The repository includes Docker and Jenkins configuration for building, testing, and publishing the application. Configure your Docker Hub repository and Jenkins credentials before publishing.

JavaFX requires platform-specific dependencies. Build the application inside Linux when preparing a Linux Docker image; do not package Windows JavaFX dependencies into that image. A multi-stage Dockerfile can perform this Linux build automatically.

The application is a desktop GUI, not a web server. Container smoke tests use Xvfb to run the interface on a virtual display and do not open a desktop window on the host.

## References

- [Official OpenJFX setup documentation](https://openjfx.io/openjfx-docs/)
- [Official JaCoCo coverage-check documentation](https://www.jacoco.org/jacoco/trunk/doc/check-mojo.html)
- [Jenkins Pipeline documentation](https://www.jenkins.io/doc/book/pipeline/)
