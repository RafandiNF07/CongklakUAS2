package com.congklakuas2;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PitView extends StackPane {
    private Label numberLabel = new Label();
    private StackPane seedPane = new StackPane();
    private Pane animationLayer;

    public PitView(Pane animationLayer) {
        this.animationLayer = animationLayer;

        seedPane.getStyleClass().add("pit-view");

        setMinSize(80, 80);
        seedPane.setMinSize(70, 70); seedPane.setMaxSize(70, 70);
        seedPane.setAlignment(Pos.CENTER);

        numberLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox container = new VBox(5, numberLabel, seedPane);
        container.setAlignment(Pos.CENTER);
        getChildren().add(container);
    }

    public void setSeeds(int count) {
        numberLabel.setText(String.valueOf(count));
        seedPane.getChildren().clear();

        double radius = 22;
        for (int i = 0; i < count; i++) {
            addSingleVisualSeed(radius);
        }
    }

    public void addVisualSeed() {
        int current = Integer.parseInt(numberLabel.getText());
        numberLabel.setText(String.valueOf(current + 1));
        addSingleVisualSeed(22);
    }

    public void clearVisualSeeds() {
        seedPane.getChildren().clear();
        numberLabel.setText("0");
    }

    public int getVisualSeedCount() {
        return Integer.parseInt(numberLabel.getText());
    }

    private void addSingleVisualSeed(double radius) {
        Circle c = new Circle(4);
        c.getStyleClass().add("seed"); // Pastikan CSS .seed ada
        if (c.getStyleClass().isEmpty()) c.setFill(Color.IVORY); // Fallback

        // Random Position dalam lingkaran
        double angle = Math.random() * Math.PI * 2;
        double dist = Math.random() * radius * 0.8;
        c.setTranslateX(Math.cos(angle) * dist);
        c.setTranslateY(Math.sin(angle) * dist);

        seedPane.getChildren().add(c);
    }

    public void setAsStore() {
        seedPane.getStyleClass().add("store-pit");
    }
    public void setActive(boolean active) {
        if (active) {
            if (!seedPane.getStyleClass().contains("pit-active")) {
                seedPane.getStyleClass().add("pit-active");
            }
        } else {
            seedPane.getStyleClass().remove("pit-active");
        }
    }
}