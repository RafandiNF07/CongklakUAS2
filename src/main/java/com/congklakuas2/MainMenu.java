package com.congklakuas2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class MainMenu extends Application {
    
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showMainMenu();
    }
    
    private void showMainMenu() {
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web("#8B4513")),
            new Stop(0.5, Color.web("#A0522D")),
            new Stop(1, Color.web("#DEB887"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("CONGKLAK");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 72));
        titleLabel.setTextFill(Color.web("#FFD700"));
        titleLabel.setEffect(new DropShadow(20, Color.BLACK));
        
        Label subtitleLabel = new Label("Permainan Tradisional Indonesia");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        subtitleLabel.setTextFill(Color.web("#F5DEB3"));
        
        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        StackPane logoPane = new StackPane();
        logoPane.setMaxSize(200, 200);
        
        Rectangle logoBg = new Rectangle(200, 200);
        logoBg.setArcWidth(30);
        logoBg.setArcHeight(30);
        logoBg.setFill(Color.web("#A0522D", 0.7));
        logoBg.setStroke(Color.web("#FFD700"));
        logoBg.setStrokeWidth(5);
        logoBg.setEffect(new DropShadow(15, Color.BLACK));
        
        Label logoText = new Label("🎮");
        logoText.setFont(Font.font(100));
        
        logoPane.getChildren().addAll(logoBg, logoText);

        ScaleTransition scaleLogo = new ScaleTransition(Duration.seconds(2), logoPane);
        scaleLogo.setFromX(0.8);
        scaleLogo.setFromY(0.8);
        scaleLogo.setToX(1.0);
        scaleLogo.setToY(1.0);
        scaleLogo.setAutoReverse(true);
        scaleLogo.setCycleCount(ScaleTransition.INDEFINITE);
        scaleLogo.play();

        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(300);
        
        Button startButton = createMenuButton("🎮 MULAI PERMAINAN", "#8B4513", "#D2691E");
        Button rulesButton = createMenuButton("📖 ATURAN PERMAINAN", "#2E8B57", "#3CB371");
        Button exitButton = createMenuButton("🚪 KELUAR", "#B22222", "#DC143C");

        FadeTransition fade1 = new FadeTransition(Duration.seconds(1), startButton);
        fade1.setFromValue(0);
        fade1.setToValue(1);
        
        FadeTransition fade2 = new FadeTransition(Duration.seconds(1), rulesButton);
        fade2.setFromValue(0);
        fade2.setToValue(1);
        fade2.setDelay(Duration.seconds(0.3));
        
        FadeTransition fade3 = new FadeTransition(Duration.seconds(1), exitButton);
        fade3.setFromValue(0);
        fade3.setToValue(1);
        fade3.setDelay(Duration.seconds(0.6));
        
        ParallelTransition buttonAnimation = new ParallelTransition(fade1, fade2, fade3);
        
        buttonBox.getChildren().addAll(startButton, rulesButton, exitButton);

        Label footerLabel = new Label("© 2024 Game Congklak - Tugas UAS Semester 3");
        footerLabel.setFont(Font.font("Arial", 14));
        footerLabel.setTextFill(Color.web("#F5DEB3", 0.8));

        startButton.setOnAction(e -> {
            animateTransition(() -> {
                CongklakGUI game = new CongklakGUI();
                try {
                    game.start(new Stage());
                    primaryStage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });
        rulesButton.setOnAction(e -> {
            showRulesDialog();
        });       
        exitButton.setOnAction(e -> {
            animateTransition(() -> System.exit(0));
        });        
        root.getChildren().addAll(headerBox, logoPane, buttonBox, footerLabel);

        Scene scene = new Scene(root, 900, 700);
        
        primaryStage.setTitle("Congklak - Menu Utama");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        primaryStage.show();

        buttonAnimation.play();
    }
    
    private Button createMenuButton(String text, String baseColor, String hoverColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        button.setTextFill(Color.WHITE);
        button.setPrefWidth(350);
        button.setPrefHeight(60);
        button.setStyle(
            "-fx-background-color: " + baseColor + ";" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 10, 0, 0, 3);"
        );
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #FFD700;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 15, 0, 0, 5);"
            );
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });       
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + baseColor + ";" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #FFD700;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 10, 0, 0, 3);"
            );
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });        
        return button;
    }
    
    private void animateTransition(Runnable nextAction) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), primaryStage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> nextAction.run());
        fadeOut.play();
    }
    
    private void showRulesDialog() {
        Stage rulesStage = new Stage();
        rulesStage.initOwner(primaryStage);
        
        VBox rulesBox = new VBox(20);
        rulesBox.setAlignment(Pos.CENTER);
        rulesBox.setPadding(new Insets(30));
        rulesBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #F5DEB3, #DEB887);" +
            "-fx-border-color: #8B4513;" +
            "-fx-border-width: 5;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;"
        );
        
        Label title = new Label("📜 ATURAN PERMAINAN CONGKLAK");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#8B4513"));
        
        VBox rulesContent = new VBox(15);
        rulesContent.setAlignment(Pos.TOP_LEFT);
        rulesContent.setPadding(new Insets(20));
        
        String[] rules = {
            "1. Setiap pemain memiliki 7 lubang kecil dan 1 lubang besar (store)",
            "2. Setiap lubang kecil diisi dengan 7 biji congklak",
            "3. Pemain memilih satu lubang miliknya dan mengambil semua bijinya",
            "4. Biji disebar satu per satu ke lubang berikutnya searah jarum jam",
            "5. Jika biji terakhir jatuh di store milik sendiri, dapat giliran lagi",
            "6. Jika biji terakhir jatuh di lubang kosong milik sendiri dan lubang",
            "   seberang lawan berisi biji, biji tersebut diambil",
            "7. Permainan berakhir ketika salah satu pemain kehabisan biji",
            "8. Pemain dengan biji terbanyak di store-nya adalah pemenang"
        };       
        for (String rule : rules) {
            Label ruleLabel = new Label(rule);
            ruleLabel.setFont(Font.font("Arial", 16));
            ruleLabel.setTextFill(Color.web("#5D4037"));
            ruleLabel.setWrapText(true);
            rulesContent.getChildren().add(ruleLabel);
        }       
        Button closeButton = new Button("Tutup");
        closeButton.setStyle(
            "-fx-background-color: #8B4513;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> rulesStage.close());
        
        rulesBox.getChildren().addAll(title, rulesContent, closeButton);
        
        Scene rulesScene = new Scene(rulesBox, 600, 500);
        rulesStage.setScene(rulesScene);
        rulesStage.setTitle("Aturan Permainan");
        rulesStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}