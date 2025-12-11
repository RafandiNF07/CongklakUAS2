package com.congklakuas2;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.application.Platform;

public class PitView extends StackPane {
    private Label numberLabel = new Label();
    private StackPane seedPane = new StackPane();
    private Pane animationLayer;
    
    public PitView() {
        this(new Pane());
    }

    public PitView(Pane animationLayer) {
        this.animationLayer = animationLayer;
        setMinSize(80, 80);

        seedPane.setMinSize(70, 70);
        seedPane.setMaxSize(70, 70);
        seedPane.setStyle(
            "-fx-background-radius: 50%;"
            + "-fx-background-color: #c49b5f;"
            + "-fx-border-radius: 50%;"
            + "-fx-border-width: 3px;"
            + "-fx-border-color: #8b5a2b;"
        );
        seedPane.setAlignment(Pos.CENTER);

        numberLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(5);
        container.getChildren().addAll(numberLabel, seedPane);

        getChildren().add(container);
    }
    
    public void setSeeds(int count) {
        numberLabel.setText(String.valueOf(count));
        seedPane.getChildren().clear();

        double radius = 22;

        for (int i = 0; i < count; i++) {
            Circle c = new Circle(4);
            c.setFill(Color.DARKRED);

            double angle = Math.random() * Math.PI * 2;

            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;

            c.setTranslateX(x);
            c.setTranslateY(y);

            seedPane.getChildren().add(c);
        }
    }

    public void setActive(boolean active) {
        if (active) {
            seedPane.setStyle(
                "-fx-background-radius: 50%;"
                + "-fx-background-color: #dcb67a;"
                + "-fx-border-radius: 50%;"
                + "-fx-border-width: 3px;"
                + "-fx-border-color: yellow;"
                + "-fx-effect: dropshadow(gaussian, yellow, 20, 0.5, 0, 0);"
            );
        } else {
            seedPane.setStyle(
                "-fx-background-radius: 50%;"
                + "-fx-background-color: #c49b5f;"
                + "-fx-border-radius: 50%;"
                + "-fx-border-width: 3px;"
                + "-fx-border-color: #8b5a2b;"
            );
        }
    }

    public void animateSeedTo(PitView targetPit, Runnable onFinished) {
        System.out.println("animateSeedTo called from pit[" + this + "] to pit[" + targetPit + "], animationLayer=" + animationLayer);

        if (animationLayer == null) {
            System.out.println("animationLayer is null!");
            if (onFinished != null) onFinished.run();
            return;
        }
        Platform.runLater(() -> {
            try {
                double sourceSceneX = this.localToScene(this.getBoundsInLocal()).getCenterX();
                double sourceSceneY = this.localToScene(this.getBoundsInLocal()).getCenterY();
                double targetSceneX = targetPit.localToScene(targetPit.getBoundsInLocal()).getCenterX();
                double targetSceneY = targetPit.localToScene(targetPit.getBoundsInLocal()).getCenterY();

                System.out.println("scene coords src(" + sourceSceneX + "," + sourceSceneY + ") tgt(" + targetSceneX + "," + targetSceneY + ")");

                javafx.geometry.Point2D startLocal = animationLayer.sceneToLocal(sourceSceneX, sourceSceneY);
                javafx.geometry.Point2D endLocal   = animationLayer.sceneToLocal(targetSceneX, targetSceneY);

                System.out.println("layer local coords start(" + startLocal.getX() + "," + startLocal.getY() + ") end(" + endLocal.getX() + "," + endLocal.getY() + ")");

                Circle moving = new Circle(6, Color.RED);
                moving.setManaged(false);

                moving.setLayoutX(startLocal.getX() - moving.getRadius());
                moving.setLayoutY(startLocal.getY() - moving.getRadius());

                moving.setTranslateX(0);
                moving.setTranslateY(0);

                animationLayer.getChildren().add(moving);
                System.out.println("dummy moving added to animationLayer childrenCount=" + animationLayer.getChildren().size());

                double dx = endLocal.getX() - startLocal.getX();
                double dy = endLocal.getY() - startLocal.getY();

                if (Double.isNaN(dx) || Double.isNaN(dy)) {
                    System.out.println("Invalid delta for animation, dx/dy NaN -> aborting");
                    animationLayer.getChildren().remove(moving);
                    if (onFinished != null) onFinished.run();
                    return;
                }
                TranslateTransition tt = new TranslateTransition(Duration.millis(350), moving);
                tt.setFromX(0);
                tt.setFromY(0);
                tt.setToX(dx);
                tt.setToY(dy);
                tt.setInterpolator(Interpolator.EASE_BOTH);

                tt.setOnFinished(evt -> {
                    System.out.println("TranslateTransition finished; removing moving and calling callback");
                    animationLayer.getChildren().remove(moving);
                    if (onFinished != null) onFinished.run();
                });
                tt.play();
            } catch (Exception ex) {
                ex.printStackTrace();
                if (onFinished != null) onFinished.run();
            }
        });
    }

    public int getVisualSeedCount() {
        return seedPane.getChildren().size();
    }

    public void addVisualSeed() {
        Circle c = new Circle(4, Color.DARKRED);

        double radius = 18;
        double angle = Math.random() * Math.PI * 2;
        c.setTranslateX(Math.cos(angle) * (Math.random() * radius * 0.6));
        c.setTranslateY(Math.sin(angle) * (Math.random() * radius * 0.6));
        seedPane.getChildren().add(c);

        numberLabel.setText(String.valueOf(getVisualSeedCount()));
    }

    public void removeOneVisualSeed() {
        if (!seedPane.getChildren().isEmpty()) {
            seedPane.getChildren().remove(seedPane.getChildren().size() - 1);
        }
        numberLabel.setText(String.valueOf(getVisualSeedCount()));
    }

    public void addSingleSeed(Node seed) {
        seed.setTranslateX(0);
        seed.setTranslateY(0);
        seedPane.getChildren().add(seed);
    }
}
