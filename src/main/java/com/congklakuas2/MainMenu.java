package com.congklakuas2;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenu extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showMainMenu();
    }

    private void showMainMenu() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("root");

        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("CONGKLAK");
        titleLabel.getStyleClass().add("header-title");
        titleLabel.setStyle("-fx-font-size: 72px;");
        Label subtitleLabel = new Label("Permainan Tradisional Indonesia");
        subtitleLabel.getStyleClass().add("subtitle-label");
        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        StackPane logoPane = new StackPane();
        Rectangle logoBg = new Rectangle(200, 200);
        logoBg.getStyleClass().add("pit-view");
        logoBg.setStyle("-fx-arc-width: 30; -fx-arc-height: 30; -fx-fill: #A0522D; -fx-stroke: #FFD700; -fx-stroke-width: 5;");
        Label logoText = new Label("🎮");
        logoText.setFont(Font.font(100));
        logoPane.getChildren().addAll(logoBg, logoText);

        ScaleTransition scaleLogo = new ScaleTransition(Duration.seconds(2), logoPane);
        scaleLogo.setFromX(0.8); scaleLogo.setFromY(0.8);
        scaleLogo.setToX(1.0); scaleLogo.setToY(1.0);
        scaleLogo.setAutoReverse(true);
        scaleLogo.setCycleCount(ScaleTransition.INDEFINITE);
        scaleLogo.play();

        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(300);

        Button startButton = createMenuButton("🎮 MULAI PERMAINAN", "btn-home");
        Button rulesButton = createMenuButton("📖 ATURAN PERMAINAN", "btn-restart");
        Button exitButton = createMenuButton("🚪 KELUAR", "btn-rules");

        animateButtons(startButton, rulesButton, exitButton);
        buttonBox.getChildren().addAll(startButton, rulesButton, exitButton);

        Label footerLabel = new Label("© Game Congklak - Tugas UAS Semester 3");
        footerLabel.setStyle("-fx-text-fill: #F5DEB3; -fx-font-size: 14px;");

        // --- FIX ACTION HANDLERS ---
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

        rulesButton.setOnAction(e -> showRulesDialog());

        exitButton.setOnAction(e -> animateTransition(() -> System.exit(0)));

        root.getChildren().addAll(headerBox, logoPane, buttonBox, footerLabel);

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setTitle("Congklak - Menu Utama");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showRulesDialog() {
        Stage rulesStage = new Stage();
        rulesStage.initOwner(primaryStage);
        rulesStage.initModality(Modality.WINDOW_MODAL);
        rulesStage.setResizable(false);

        // 1. Layout Utama (Wadah Gradient Background)
        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #F5DEB3, #DEB887);" +
                        "-fx-border-color: #8B4513; -fx-border-width: 5;"
        );


        Label title = new Label("📜 ATURAN PERMAINAN");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#8B4513"));
        title.setEffect(new DropShadow(5, Color.web("#000000", 0.2)));

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.setStyle("-fx-background-color: transparent;");

        String[] rules = {
                "1. Pemain memilih satu lubang di sisi miliknya yang berisi biji.",
                "2. Biji akan dibagikan satu per satu ke lubang berikutnya searah jarum jam.",
                "3. Jika biji terakhir jatuh di LUMBUNG (Store) sendiri, pemain mendapatkan giliran tambahan (Free Turn).",
                "4. Jika biji terakhir jatuh di LUBANG KOSONG di sisi sendiri dan seberangnya ada biji lawan, lakukan PENEMBAKAN (Capture).",
                "5. Jika biji terakhir jatuh di LUBANG ISI (selain lumbung), ambil semua biji di lubang tersebut dan lanjutkan pembagian (Estafet / Chain Move).",
                "6. Jika biji terakhir jatuh di lubang kosong lawan, giliran berakhir.",
                "7. Permainan berakhir jika salah satu sisi pemain kosong melompong.",
                "8. Pemenang adalah pemain dengan jumlah biji terbanyak di lumbungnya."
        };

        for (String r : rules) {
            Label l = new Label(r);
            l.setFont(Font.font("Arial", 14));
            l.setTextFill(Color.web("#5D4037"));
            l.setWrapText(true); // Agar teks turun ke bawah
            l.setMaxWidth(400);  // Batas lebar teks agar rapi
            contentBox.getChildren().add(l);
        }

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300); // Tinggi area scroll
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Button closeBtn = new Button("TUTUP");
        closeBtn.setStyle(
                "-fx-background-color: #8B4513; -fx-text-fill: white;" +
                        "-fx-font-weight: bold; -fx-padding: 10 30;" +
                        "-fx-background-radius: 10; -fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 2, 2);"
        );

        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 10;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 10;"));

        closeBtn.setOnAction(e -> rulesStage.close());

        mainLayout.getChildren().addAll(title, scrollPane, closeBtn);

        Scene scene = new Scene(mainLayout, 500, 500); // Ukuran window diperbesar sedikit
        rulesStage.setScene(scene);
        rulesStage.show();
    }

    private Button createMenuButton(String text, String cssClass) {
        Button button = new Button(text);
        button.setPrefWidth(350); button.setPrefHeight(60);
        button.getStyleClass().addAll("game-button", cssClass);
        button.setStyle("-fx-font-size: 20px;");
        return button;
    }

    private void animateButtons(Button... buttons) {
        ParallelTransition pt = new ParallelTransition();
        double delay = 0;
        for (Button b : buttons) {
            FadeTransition ft = new FadeTransition(Duration.seconds(1), b);
            ft.setFromValue(0); ft.setToValue(1); ft.setDelay(Duration.seconds(delay));
            pt.getChildren().add(ft); delay += 0.3;
        }
        pt.play();
    }

    private void animateTransition(Runnable nextAction) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), primaryStage.getScene().getRoot());
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> nextAction.run());
        fadeOut.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}