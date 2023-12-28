package agh.ics.oop.presenter;
import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationStatistics;
import agh.ics.oop.model.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class SimulationPresenter implements MapChangeListener {
    @FXML
    private TextField maxMutationsField;
    @FXML
    private TextField minMutationsField;
    @FXML
    private TextField reproduceEnergyLostField;
    @FXML
    private TextField reproduceEnergyField;
    @FXML
    private TextField moveEnergyCost;
    @FXML
    private TextField energyEatField;
    @FXML
    private TextField plantPerDayField;
    @FXML
    private TextField startEnergyField;
    @FXML
    private TextField animalNumber;
    @FXML
    private TextField grassQuantityField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField widthField;
    @FXML
    private Spinner genNumber;
    @FXML
    private Label description;

    public void setMapGrid(GridPane mapGrid) {
        Platform.runLater(() -> {
            this.mapGrid = mapGrid;
        });
    }

    @FXML
    private GridPane mapGrid;
    private GrassField worldMap;
    private int cellSize = 280;
    private int startEnergy;
    private int moveCost;
    private int startAnimalNumber;
    private int energyForGrass;
    private int plantPerDay;
    private int reproduceEnergy;
    private int reproduceEnergyLost;
    private SimulationEngine simulationEngine;
    private int maxMutations;
    private int minMutations;

    Image blackCat = new Image("file:oolab/src/main/resources/koty/kot1.png");
    Image grayCat = new Image("file:oolab/src/main/resources/koty/kot2.png");
    Image redCat = new Image("file:oolab/src/main/resources/koty/kot3.png");
    Image orangekCat = new Image("file:oolab/src/main/resources/koty/kot4.png");
    Image yellowCat = new Image("file:oolab/src/main/resources/koty/kot5.png");
    Image goodPlant = new Image("file:oolab/src/main/resources/koty/grass1.png");
    Image badPlant = new Image("file:oolab/src/main/resources/koty/grass2.png");

    @FXML
    private GridPane statisticsGrid;

    public void setAnimalCountLabel(Label animalCountLabel) {
        this.animalCountLabel = animalCountLabel;
    }

    @FXML
    private Label animalCountLabel;

    public void setDaysAliveLabel(Label daysAliveLabel) {
        this.daysAliveLabel = daysAliveLabel;
    }

    @FXML
    private Label daysAliveLabel;
    private SimulationStatistics statistics; // Referencja do obiektu SimulationStatistics



    public void setWorldMap(GrassField worldMap) {
        this.worldMap = worldMap;
    }
    private void clearGrid() {
        mapGrid.setGridLinesVisible(false);
        mapGrid.getChildren().clear();
        mapGrid.setGridLinesVisible(true);
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    private void drawMap(){
        clearGrid();

        Boundary bounds = worldMap.getCurrentBounds();
        int numRows = bounds.rightUp().getY() - bounds.leftDown().getY() ;
        int numCols = bounds.rightUp().getX() - bounds.leftDown().getX() ;

        for (int i = 0; i <= numCols; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }
        for (int i = 0; i <= numRows; i++) {
            mapGrid.getRowConstraints().add(new RowConstraints(cellSize));
        }

        //Label leftUp = new Label("y/x");
        //mapGrid.add(leftUp, 0, 0);
        //GridPane.setHalignment(leftUp, HPos.CENTER);

        //for (int x = 1; x <= numCols; x++) {
        //    Label columnDescription = new Label(String.valueOf(x - 1 + bounds.leftDown().getX()));
        //    mapGrid.add(columnDescription, x, 0);
        //    GridPane.setHalignment(columnDescription, HPos.CENTER);
        //}

        //for (int y = numRows; y > 0; y--) {
        //    Label rowsDescription = new Label(String.valueOf(y - 1 + bounds.leftDown().getY()));
        //    mapGrid.add(rowsDescription, 0, numRows - y + 1);
        //    GridPane.setHalignment(rowsDescription, HPos.CENTER);
        //}

        for (int y = numRows; y >= 0; y--) {
            for (int x = 0; x <= numCols; x++) {
                Rectangle komorka = new Rectangle(cellSize, cellSize);
                komorka.setStroke(Color.BLACK);
                Vector2d aktualnaPozycja = new Vector2d(x + bounds.leftDown().getX(), y + bounds.leftDown().getY());

                FieldType fieldType = worldMap.getFieldType(aktualnaPozycja);
                if(fieldType == FieldType.PREFERRED){
                    komorka.setFill(Color.GREEN);
                    mapGrid.add(komorka, x, numRows - y);
                } else {
                    komorka.setFill(Color.rgb(215, 255, 190));
                    mapGrid.add(komorka, x, numRows - y);
                }

                // Ustawianie kolorów na podstawie typu obiektu na danej pozycji
                Object obiekt = worldMap.objectAt(aktualnaPozycja);
                if (obiekt instanceof Grass) {
                    ImageView imageView = new ImageView(goodPlant);
                    imageView.setFitWidth(cellSize);
                    imageView.setFitHeight(cellSize);
                    mapGrid.add(imageView, x, numRows - y);
                } else if (obiekt instanceof Animal) {
                    int energy = ((Animal) obiekt).getCurrentEnergy();
                    //System.out.println((double)energy/startEnergy*100);
                    if((double)energy/startEnergy*100 > 80){
                        ImageView imageView = new ImageView(blackCat);
                        imageView.setFitWidth(cellSize);
                        imageView.setFitHeight(cellSize);
                        mapGrid.add(imageView, x, numRows - y);
                    }else if((double)energy/startEnergy*100 <= 80 && (double)energy/startEnergy*100 > 60){
                        ImageView imageView = new ImageView(grayCat);
                        imageView.setFitWidth(cellSize);
                        imageView.setFitHeight(cellSize);
                        mapGrid.add(imageView, x, numRows - y);
                    }else if((double)energy/startEnergy*100 <= 60 && (double)energy/startEnergy*100 > 40){
                        ImageView imageView = new ImageView(redCat);
                        imageView.setFitWidth(cellSize);
                        imageView.setFitHeight(cellSize);
                        mapGrid.add(imageView, x, numRows - y);
                    }else if((double)energy/startEnergy*100 <= 40 && (double)energy/startEnergy*100 > 20){
                        ImageView imageView = new ImageView(orangekCat);
                        imageView.setFitWidth(cellSize);
                        imageView.setFitHeight(cellSize);
                        mapGrid.add(imageView, x, numRows - y);
                    }else if((double)energy/startEnergy*100 <= 20){
                        ImageView imageView = new ImageView(yellowCat);
                        imageView.setFitWidth(cellSize);
                        imageView.setFitHeight(cellSize);
                        mapGrid.add(imageView, x, numRows - y);
                    }
                }
                GridPane.setHalignment(komorka, HPos.CENTER);
            }
        }

    }

    @Override
    public void mapChanged(GrassField worldMap, String message) {
        Platform.runLater(() -> {
            drawMap();
            description.setText(message);
            updateStatistics();
        });
    }

    public void onSimulationStartClicked(ActionEvent actionEvent) {
        try {
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            int grassQuantity = Integer.parseInt(grassQuantityField.getText());
            startEnergy = Integer.parseInt(startEnergyField.getText());
            moveCost = Integer.parseInt(moveEnergyCost.getText());
            startAnimalNumber = Integer.parseInt(animalNumber.getText());
            plantPerDay = Integer.parseInt(plantPerDayField.getText());
            energyForGrass = Integer.parseInt(energyEatField.getText());
            reproduceEnergy = Integer.parseInt(reproduceEnergyField.getText());
            reproduceEnergyLost = Integer.parseInt(reproduceEnergyLostField.getText());
            minMutations = Integer.parseInt(minMutationsField.getText());
            maxMutations = Integer.parseInt(maxMutationsField.getText());
            cellSize=cellSize/width+30;

            if (width <= 0 || height <= 0 || grassQuantity < 0 || startEnergy < 0) {

            } else {
                GrassField map = new GrassField(grassQuantity, width, height);
                statistics = new SimulationStatistics(map);
                map.subscribe(this);
                setWorldMap(map);

                List<Vector2d> positions = generateStartPositions();
                Simulation simulation = new Simulation(positions, worldMap, (Integer) genNumber.getValue(), startEnergy, moveCost, plantPerDay, energyForGrass, reproduceEnergy, reproduceEnergyLost, minMutations, maxMutations);

                SimulationEngine simulationEngine = new SimulationEngine(Arrays.asList(simulation), 4);
                this.simulationEngine = simulationEngine;
                simulationEngine.runAsyncInThreadPool();
            }
        } catch (NumberFormatException e) {
            // tu tez
        }
    }

    public void onPauseSimulation() {
        if (simulationEngine != null) {
            simulationEngine.stopAllSimulations();
        }
    }

    public void onResumeSimulation() {
        if (simulationEngine != null) {
            simulationEngine.resumeAllSimulations();
        }
    }

    public void initialize() {
        // Ustawienie wartości domyślnej dla Spinnera genNumber
        genNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 10)); // zakres od 0 do oo, wartość domyślna 5
        animalNumber.setText("3");
        startEnergyField.setText("15");
        widthField.setText("5");
        heightField.setText("5");
        grassQuantityField.setText("10");
        energyEatField.setText("5");
        moveEnergyCost.setText("1");
        plantPerDayField.setText("2");
        reproduceEnergyField.setText("5");
        reproduceEnergyLostField.setText("4");
        minMutationsField.setText("4");
        maxMutationsField.setText("7");
    }

    private List<Vector2d> generateStartPositions(){
        List<Vector2d> positions = new ArrayList<>();
        Random random = new Random();

        Boundary bounds = worldMap.getCurrentBounds();
        int width = bounds.rightUp().getX() - bounds.leftDown().getX() + 1;
        int height = bounds.rightUp().getY() - bounds.leftDown().getY() + 1;

        for (int i = 0; i < startAnimalNumber; i++) {
            int x = random.nextInt(width) + bounds.leftDown().getX();
            int y = random.nextInt(height) + bounds.leftDown().getY();
            positions.add(new Vector2d(x, y));
        }


        return positions;

    }

    public void updateStatistics() {

        int animalCount = statistics.getNumberOfInsistingAnimals();
        double daysAlive = statistics.getAvgDaysAlive();

        animalCountLabel.setText("Animal Count: " + animalCount);
        daysAliveLabel.setText("Average life length: " + daysAlive);
    }
    public SimulationStatistics getSimulationStatistics() {
        return statistics;
    }


}
