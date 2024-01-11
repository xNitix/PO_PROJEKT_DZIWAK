package agh.ics.oop;
import agh.ics.oop.model.*;
import agh.ics.oop.presenter.SimulationPresenter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulationApp extends Application {

    private BorderPane viewRoot;

    public void start(Stage primaryStage) {
        configureStage(primaryStage, viewRoot);
        primaryStage.show();
    }

    private List<SimulationPresenter> simulationControllers = new ArrayList<>();

    private static Button getButton(SimulationPresenter controller, TabPane tabPane) {
        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(event -> {
            Tab simulationTab = new Tab("Simulation");

            HBox simulationContent = new HBox();
            simulationContent.setStyle("-fx-background-image: url('file:oolab/src/main/resources/koty/background.png'); -fx-padding: 30;");

            VBox leftContent = new VBox();
            leftContent.setStyle("-fx-background-color: white;-fx-border-color: black; -fx-border-width: 3px;-fx-padding: 3;");

            GridPane mapGrid = new GridPane();
            mapGrid.setStyle("-fx-background-color: white;-fx-border-color: black; -fx-border-width: 3px;");
            controller.setMapGrid(mapGrid);

            Label animalCountLabel = new Label();
            Label plantCountLabel = new Label();
            Label freeFieldCountLabel = new Label();
            Label mostFamounsGenoTypeLabel = new Label();
            Label liveAnimalsAvgEnergyLabel = new Label();
            Label deadAniamlsDaysAlivedLabel = new Label();
            Label liveAnimalsChildAvgLabel = new Label();

            LineChart<Number, Number> lineChart = new LineChart<>(new NumberAxis(), new NumberAxis());

            controller.setLineChart(lineChart);
            controller.setAnimalCountLabel(animalCountLabel);
            controller.setDeadAniamlsDaysAlivedLabel(deadAniamlsDaysAlivedLabel);
            controller.setPlantCountLabel(plantCountLabel);
            controller.setFreeFieldCountLabel(freeFieldCountLabel);
            controller.setMostFamounsGenoTypeLabel(mostFamounsGenoTypeLabel);
            controller.setLiveAnimalsAvgEnergyLabel(liveAnimalsAvgEnergyLabel);
            controller.setLiveAnimalsChildAvgLabel(liveAnimalsChildAvgLabel);

            Button pauseButton = new Button("Pause");
            pauseButton.setOnAction(e -> controller.onPauseSimulation());

            Button resetButton = new Button("Reset");
            resetButton.setOnAction(e -> controller.onResumeSimulation());

            Button dominantButton = new Button("Dominant Genotype Animals");
            dominantButton.setVisible(false);
            controller.setDominantButton(dominantButton);
            dominantButton.setOnAction(e -> controller.onFollowSimulation());

            Button trackAnimalButton = new Button("Track Animal");
            trackAnimalButton.setOnAction(e -> controller.ontrackAnimalButton());

            HBox buttonContainer = new HBox(pauseButton, resetButton, dominantButton, trackAnimalButton);
            buttonContainer.setSpacing(10); // Odstęp między przyciskami

            VBox legendContainer = new VBox();
            controller.setLegendContainer(legendContainer);
            controller.createLegend();

            Label statisticsTitle = new Label("Statistics: ");
            statisticsTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
            leftContent.getChildren().addAll(statisticsTitle, animalCountLabel, plantCountLabel, freeFieldCountLabel, mostFamounsGenoTypeLabel, liveAnimalsAvgEnergyLabel, deadAniamlsDaysAlivedLabel, liveAnimalsChildAvgLabel, legendContainer, lineChart, buttonContainer);

            VBox mapAndTrack = new VBox();
            mapAndTrack.getChildren().addAll(mapGrid);
            controller.setMapAndTrack(mapAndTrack);

            simulationContent.getChildren().addAll(leftContent, mapAndTrack);

            leftContent.setAlignment(Pos.TOP_LEFT);
            leftContent.setSpacing(5);

            simulationTab.setContent(simulationContent);

            tabPane.getTabs().add(simulationTab);
            tabPane.getSelectionModel().select(simulationTab);

            controller.onSimulationStartClicked(event);
        });
        return startButton;
    }



    private void configureStage(Stage primaryStage, BorderPane viewRoot) {
        if (viewRoot == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulation.fxml"));
                viewRoot = loader.load();
                SimulationPresenter controller = loader.getController();

                TabPane tabPane = new TabPane();
                Tab parametersTab = new Tab("Parameters");
                VBox parametersContent = new VBox(10);

                parametersContent.getChildren().add(viewRoot);

                Button startButton = getButton(controller, tabPane);
                startButton.setMaxWidth(200);

                HBox buttonContainer = new HBox();
                buttonContainer.getChildren().add(startButton);
                buttonContainer.setAlignment(Pos.CENTER);

                parametersContent.getChildren().addAll(new Label(), buttonContainer);
                parametersTab.setContent(parametersContent);
                tabPane.getTabs().add(parametersTab);
                controller.initialize();
                Scene scene = new Scene(new BorderPane(tabPane), 200, 720);
                primaryStage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        primaryStage.setTitle("Simulation App");
        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }


}
