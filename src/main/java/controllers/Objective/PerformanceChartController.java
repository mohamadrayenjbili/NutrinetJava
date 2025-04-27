package controllers.Objective;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javafx.util.StringConverter;
import models.Objective;
import services.IPerformanceService;
import services.PerformanceService;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.text.DecimalFormat;

public class PerformanceChartController {

    @FXML private LineChart<Number, Number> lineChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Label chartTitle;
    @FXML private VBox chartContainer;
    @FXML private HBox infoContainer;

    private final IPerformanceService performanceService = new PerformanceService();
    private Objective objective;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd MMM - HH:mm");

    // Pour le formatage des valeurs selon l'unité
    private DecimalFormat valueFormatter = new DecimalFormat("#,##0.##");
    // Format pour les pourcentages
    private DecimalFormat percentFormatter = new DecimalFormat("+#0.0%;-#0.0%");

    public void initData(Objective objective) {
        this.objective = objective;
        setupChart();
        loadChartData();
        applyCustomStyles();
    }

    private void setupChart() {
        // Réinitialiser le graphique
        lineChart.getData().clear();

        // Configuration des axes
        configureDateAxis();
        configureValueAxis();

        // Titre et légende
        String formattedTarget = valueFormatter.format(objective.getTargetValue());
        chartTitle.setText("Performance - " + objective.getName() + " (Objectif: " + formattedTarget + " " + objective.getUnit() + ")");

        // Animation et options visuelles
        lineChart.setAnimated(true);
        lineChart.setCreateSymbols(true);
        lineChart.setLegendVisible(true);

        // Créer le conteneur d'informations s'il n'existe pas
        if (infoContainer == null) {
            infoContainer = new HBox();
            infoContainer.setSpacing(15);
            infoContainer.setAlignment(Pos.CENTER_RIGHT);
            infoContainer.setPadding(new Insets(10, 0, 0, 0));
            chartContainer.getChildren().add(chartContainer.getChildren().size() - 1, infoContainer);
        } else {
            infoContainer.getChildren().clear();
        }
    }

    private void configureDateAxis() {
        // Déterminer si nous utilisons des jours, heures ou minutes en fonction de l'objectif
        long duration = ChronoUnit.DAYS.between(objective.getStartDate(), objective.getEndDate());

        String timeLabel;
        if (duration <= 1) {
            timeLabel = "Heures";
        } else if (duration <= 7) {
            timeLabel = "Jours et heures";
        } else {
            timeLabel = "Jours";
        }

        xAxis.setLabel(timeLabel + " depuis le début");

        // Formateur d'étiquettes personnalisé pour l'axe X
        xAxis.setTickLabelFormatter(createDateAxisFormatter());

        // Ne pas définir les limites fixes pour l'axe X - nous les définirons après avoir chargé les données
        xAxis.setAutoRanging(true);
    }

    private StringConverter<Number> createDateAxisFormatter() {
        return new StringConverter<Number>() {
            @Override
            public String toString(Number value) {
                if (value == null) return "";

                double hours = value.doubleValue();

                // Déterminer si nous affichons les jours ou les heures
                if (hours < 24) {
                    // Format pour les heures
                    LocalDateTime dateTime = objective.getStartDate().atStartOfDay().plusHours((long)hours);
                    return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                } else {
                    // Format pour les jours
                    LocalDate date = objective.getStartDate().plusDays((long)(hours / 24));
                    return date.format(DateTimeFormatter.ofPattern("dd MMM"));
                }
            }

            @Override
            public Number fromString(String string) {
                return null; // Non nécessaire pour l'affichage
            }
        };
    }

    private void configureValueAxis() {
        String unitLabel = objective.getUnit().isEmpty() ? "Valeur" : "Valeur (" + objective.getUnit() + ")";
        yAxis.setLabel(unitLabel);

        // Formateur personnalisé pour l'axe Y
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number value) {
                return valueFormatter.format(value.doubleValue());
            }

            @Override
            public Number fromString(String string) {
                return null; // Non nécessaire pour l'affichage
            }
        });

        // Nous définirons les limites après avoir chargé les données
        yAxis.setAutoRanging(true);
    }

    private void loadChartData() {
        try {
            // Obtenir les données de performance
            Map<LocalDateTime, Double> performanceData = getPerformanceDataWithTime();

            if (performanceData.isEmpty()) {
                showNoDataMessage();
                return;
            }

            // Créer les séries de données
            XYChart.Series<Number, Number> performanceSeries = new XYChart.Series<>();
            performanceSeries.setName("Performances");

            XYChart.Series<Number, Number> targetSeries = new XYChart.Series<>();
            targetSeries.setName("Objectif");

            // Variables pour auto-scaling
            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = Double.MIN_VALUE;

            // Variables pour calculer la performance moyenne et l'écart avec l'objectif
            double totalValue = 0;
            int dataPointCount = 0;
            double lastValue = 0;

            // Ajouter les points de données de performance
            for (Map.Entry<LocalDateTime, Double> entry : performanceData.entrySet()) {
                LocalDateTime dateTime = entry.getKey();
                Double value = entry.getValue();

                // Mettre à jour les statistiques
                totalValue += value;
                dataPointCount++;
                lastValue = value;

                // Calculer les heures depuis le début
                double hoursSinceStart = calculateHoursSinceStart(dateTime);

                // Ajouter le point au graphique
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(hoursSinceStart, value);
                performanceSeries.getData().add(dataPoint);

                // Mettre à jour les min/max pour l'auto-scaling
                minX = Math.min(minX, hoursSinceStart);
                maxX = Math.max(maxX, hoursSinceStart);
                minY = Math.min(minY, value);
                maxY = Math.max(maxY, value);
            }

            // Ajouter la ligne d'objectif
            double targetValue = objective.getTargetValue();

            // Étendre la ligne d'objectif un peu au-delà des données
            double startX = Math.max(0, minX - (maxX - minX) * 0.05);
            double endX = maxX + (maxX - minX) * 0.05;

            targetSeries.getData().add(new XYChart.Data<>(startX, targetValue));
            targetSeries.getData().add(new XYChart.Data<>(endX, targetValue));

            // Mettre à jour minY/maxY avec la valeur cible
            minY = Math.min(minY, targetValue);
            maxY = Math.max(maxY, targetValue);

            // Configurer les axes basés sur les données
            configureAxisRanges(minX, maxX, minY, maxY);

            // Ajouter les séries au graphique
            lineChart.getData().addAll(performanceSeries, targetSeries);

            // Ajouter des tooltips aux points de données
            addTooltipsToDataPoints(performanceSeries, performanceData);

            // Ajouter une zone colorée entre la performance et l'objectif
            addPerformanceAreas(performanceSeries, targetValue);

            // Ajouter l'information sur l'écart entre la valeur actuelle et l'objectif
            if (dataPointCount > 0) {
                double avgValue = totalValue / dataPointCount;
                addGapInfo(lastValue, targetValue, avgValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage(e.getMessage());
        }
    }

    private Map<LocalDateTime, Double> getPerformanceDataWithTime() {
        // Simuler des données avec heures et minutes
        // Dans un vrai scénario, vous obtiendriez ces données de votre service
        try {
            // Obtenir les données de base
            Map<LocalDate, Double> basicData = performanceService.getPerformanceTrend(objective.getId());

            // Convertir en LocalDateTime avec des heures différentes
            Map<LocalDateTime, Double> timeData = new TreeMap<>();

            for (Map.Entry<LocalDate, Double> entry : basicData.entrySet()) {
                LocalDate date = entry.getKey();
                Double value = entry.getValue();

                // Si c'est un objectif à court terme, créer plusieurs entrées par jour
                long duration = ChronoUnit.DAYS.between(objective.getStartDate(), objective.getEndDate());

                if (duration <= 7) {
                    // Pour les objectifs courts, générer plusieurs entrées par jour
                    for (int hour = 8; hour <= 20; hour += 4) {
                        // Variation légère des valeurs
                        double variation = Math.random() * 0.05 * value;
                        double adjustedValue = value + variation - 0.025 * value;

                        timeData.put(date.atTime(hour, 0), adjustedValue);
                    }
                } else {
                    // Pour les objectifs plus longs, une entrée par jour
                    timeData.put(date.atTime(12, 0), value);
                }
            }

            return timeData;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private double calculateHoursSinceStart(LocalDateTime dateTime) {
        LocalDateTime startDateTime = objective.getStartDate().atStartOfDay();
        return ChronoUnit.HOURS.between(startDateTime, dateTime);
    }

    private void configureAxisRanges(double minX, double maxX, double minY, double maxY) {
        // Calculer les marges
        double xRange = maxX - minX;
        double yRange = maxY - minY;

        // Marges de 5% pour X
        double xPadding = xRange * 0.05;
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(Math.max(0, minX - xPadding));
        xAxis.setUpperBound(maxX + xPadding);

        // Déterminer l'intervalle des ticks basé sur la durée
        if (xRange <= 24) { // Moins d'un jour
            xAxis.setTickUnit(2); // Toutes les 2 heures
        } else if (xRange <= 72) { // Moins de 3 jours
            xAxis.setTickUnit(6); // Toutes les 6 heures
        } else if (xRange <= 168) { // Moins d'une semaine
            xAxis.setTickUnit(24); // Tous les jours
        } else {
            xAxis.setTickUnit(Math.max(24, xRange / 10)); // Maximum 10 ticks sur l'axe
        }

        // Marges de 10% pour Y avec minimum à zéro si approprié
        double yPadding = yRange * 0.1;
        double yMin = minY < yPadding * 2 ? 0 : minY - yPadding;

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(maxY + yPadding);

        // Intervalle des ticks pour Y
        yAxis.setTickUnit(calculateNiceTickInterval(yMin, maxY + yPadding));
    }

    private double calculateNiceTickInterval(double min, double max) {
        double range = max - min;

        // Calculer un intervalle "joli" (1, 2, 5, 10, 20, etc.)
        double roughInterval = range / 10.0; // Essayer d'avoir ~10 ticks

        // Trouver l'ordre de grandeur
        int exponent = (int) Math.floor(Math.log10(roughInterval));
        double factor = Math.pow(10, exponent);

        // Normaliser à [1, 10)
        double normalized = roughInterval / factor;

        // Choisir un "joli" intervalle
        double niceInterval;
        if (normalized < 1.5) {
            niceInterval = 1;
        } else if (normalized < 3) {
            niceInterval = 2;
        } else if (normalized < 7) {
            niceInterval = 5;
        } else {
            niceInterval = 10;
        }

        return niceInterval * factor;
    }

    private void addTooltipsToDataPoints(XYChart.Series<Number, Number> series, Map<LocalDateTime, Double> dataMap) {
        // Créer une map inverse pour trouver les dates à partir des valeurs en heures
        Map<Double, LocalDateTime> hoursToDateTime = new HashMap<>();

        for (LocalDateTime dateTime : dataMap.keySet()) {
            double hours = calculateHoursSinceStart(dateTime);
            hoursToDateTime.put(hours, dateTime);
        }

        // Ajouter des tooltips et interactions aux points
        for (XYChart.Data<Number, Number> data : series.getData()) {
            double hours = data.getXValue().doubleValue();
            LocalDateTime dateTime = hoursToDateTime.get(hours);

            // Identifier si ce point est au-dessus de l'objectif
            boolean isAboveTarget = data.getYValue().doubleValue() >= objective.getTargetValue();

            // Appliquer des styles spéciaux aux points au-dessus de l'objectif
            if (isAboveTarget && data.getNode() != null) {
                data.getNode().getStyleClass().add("above-target");
            }

            if (dateTime != null) {
                String formattedDateTime;

                // Format différent selon la durée de l'objectif
                if (ChronoUnit.DAYS.between(objective.getStartDate(), objective.getEndDate()) <= 7) {
                    formattedDateTime = dateTime.format(timeFormatter);
                } else {
                    formattedDateTime = dateTime.toLocalDate().format(dateFormatter);
                }

                String formattedValue = valueFormatter.format(data.getYValue().doubleValue());

                // Calculer l'écart par rapport à l'objectif
                double gap = data.getYValue().doubleValue() - objective.getTargetValue();
                String gapText = valueFormatter.format(Math.abs(gap)) + " " + objective.getUnit();

                // Calculer le pourcentage d'écart
                double gapPercentage = gap / objective.getTargetValue();
                String gapPercentText = percentFormatter.format(gapPercentage);

                Tooltip tooltip = new Tooltip(
                        "Date: " + formattedDateTime + "\n" +
                                "Valeur: " + formattedValue + " " + objective.getUnit() + "\n" +
                                "Écart: " + (gap >= 0 ? "+" : "-") + gapText + " (" + gapPercentText + ")\n" +
                                (isAboveTarget ? "✓ Objectif atteint" : "✗ Objectif non atteint")
                );
                tooltip.setStyle("-fx-font-size: 12px; -fx-font-weight: normal;");

                Tooltip.install(data.getNode(), tooltip);

                // Animation au survol
                data.getNode().setOnMouseEntered(event -> {
                    data.getNode().setScaleX(1.8);
                    data.getNode().setScaleY(1.8);
                    data.getNode().setStyle("-fx-cursor: hand;");
                });

                data.getNode().setOnMouseExited(event -> {
                    data.getNode().setScaleX(1);
                    data.getNode().setScaleY(1);
                });
            }
        }
    }

    private void addPerformanceAreas(XYChart.Series<Number, Number> series, double targetValue) {
        // Cette méthode ajoute une zone colorée entre la ligne de performance et l'objectif

        // Nous avons besoin d'accéder au graphique après qu'il ait été dessiné
        lineChart.applyCss();
        lineChart.layout();

        // Obtenir la zone de traçage du graphique
        Node plotArea = lineChart.lookup(".chart-plot-background");
        if (plotArea == null) return;

        // On ne peut pas ajouter directement des zones au graphique en JavaFX
        // Nous allons donc utiliser une approche à base de texte en console
        System.out.println("Pour ajouter des zones colorées, il faudrait créer une classe personnalisée héritant de LineChart");
        System.out.println("Cette fonctionnalité nécessiterait une extension personnalisée du LineChart standard de JavaFX");

        // Au lieu de cela, nous pouvons modifier la couleur des points au-dessus/en-dessous de l'objectif
        for (XYChart.Data<Number, Number> data : series.getData()) {
            if (data.getNode() != null) {
                if (data.getYValue().doubleValue() >= targetValue) {
                    // Point au-dessus de l'objectif
                    data.getNode().setStyle("-fx-background-color: #27ae60, white; -fx-background-insets: 0, 2;");
                } else {
                    // Point en-dessous de l'objectif
                    data.getNode().setStyle("-fx-background-color: #e74c3c, white; -fx-background-insets: 0, 2;");
                }
            }
        }
    }

    private void addGapInfo(double currentValue, double targetValue, double avgValue) {
        // Créer un conteneur pour les informations d'écart
        VBox gapInfoBox = new VBox();
        gapInfoBox.getStyleClass().add("gap-info");
        gapInfoBox.setSpacing(5);

        // Calculer l'écart entre la valeur actuelle et l'objectif
        double currentGap = currentValue - targetValue;
        double currentGapPercentage = currentGap / targetValue;
        boolean isPositive = currentGap >= 0;

        // Écart pour la valeur actuelle
        Label currentGapLabel = new Label("Écart actuel: " + (isPositive ? "+" : "") +
                valueFormatter.format(currentGap) + " " + objective.getUnit() +
                " (" + percentFormatter.format(currentGapPercentage) + ")");
        currentGapLabel.getStyleClass().addAll("gap-info-label", isPositive ? "gap-positive" : "gap-negative");

        // Écart pour la valeur moyenne
        double avgGap = avgValue - targetValue;
        double avgGapPercentage = avgGap / targetValue;
        boolean isAvgPositive = avgGap >= 0;

        Label avgGapLabel = new Label("Écart moyen: " + (isAvgPositive ? "+" : "") +
                valueFormatter.format(avgGap) + " " + objective.getUnit() +
                " (" + percentFormatter.format(avgGapPercentage) + ")");
        avgGapLabel.getStyleClass().addAll("gap-info-label", isAvgPositive ? "gap-positive" : "gap-negative");

        // Dernière valeur enregistrée
        Label lastValueLabel = new Label("Dernière valeur: " + valueFormatter.format(currentValue) + " " + objective.getUnit());
        lastValueLabel.getStyleClass().add("gap-info-label");

        // Valeur cible
        Label targetValueLabel = new Label("Valeur cible: " + valueFormatter.format(targetValue) + " " + objective.getUnit());
        targetValueLabel.getStyleClass().add("gap-info-label");

        // Ajouter les labels au conteneur
        gapInfoBox.getChildren().addAll(lastValueLabel, targetValueLabel, currentGapLabel, avgGapLabel);

        // Ajouter le conteneur d'info à notre layout principal
        infoContainer.getChildren().add(gapInfoBox);
    }

    private void showNoDataMessage() {
        lineChart.setTitle("Aucune donnée disponible");
        // Créer un message explicatif
        Label noDataLabel = new Label("Aucune donnée de performance n'est disponible pour cet objectif.");
        noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Centrer le message dans le graphique
        VBox messageBox = new VBox(noDataLabel);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPrefSize(lineChart.getWidth(), lineChart.getHeight());

        chartContainer.getChildren().add(chartContainer.getChildren().indexOf(lineChart) + 1, messageBox);
    }

    private void showErrorMessage(String message) {
        lineChart.setTitle("Erreur de chargement des données");
        // Créer un message d'erreur
        Label errorLabel = new Label("Erreur lors du chargement des données: " + message);
        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");

        // Centrer le message dans le graphique
        VBox messageBox = new VBox(errorLabel);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPrefSize(lineChart.getWidth(), lineChart.getHeight());

        chartContainer.getChildren().add(chartContainer.getChildren().indexOf(lineChart) + 1, messageBox);
    }

    private void applyCustomStyles() {
        // Application des styles de base
        chartContainer.getStyleClass().add("chart-container");
        lineChart.getStyleClass().add("performance-chart");
        chartTitle.getStyleClass().add("chart-main-title");

        // Style de l'arrière-plan
        chartContainer.setStyle("-fx-background-color: #ffffff; -fx-padding: 20;");

        // Appliquer un effet d'ombre subtil
        lineChart.setEffect(new javafx.scene.effect.DropShadow(8, 2, 2, javafx.scene.paint.Color.rgb(0, 0, 0, 0.15)));
    }

    @FXML
    public void handleExportPNG() {
        try {
            // Capture d'écran du graphique
            WritableImage image = chartContainer.snapshot(new SnapshotParameters(), null);

            // Dialogue de sauvegarde
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le graphique");
            fileChooser.setInitialFileName(objective.getName() + "_performance.png");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Image", "*.png"));

            File file = fileChooser.showSaveDialog(lineChart.getScene().getWindow());
            if (file != null) {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) chartContainer.getScene().getWindow()).close();
    }
}