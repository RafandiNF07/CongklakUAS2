package com.congklakuas2;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CongklakGUI extends Application {

    private CongklakGame game = new CongklakGame();
    private PitView[] pitViews = new PitView[16];
    private Label turnLabel = new Label("PLAYER 1'S TURN");
    private Pane animationLayer = new Pane();
    private Button backToMenuButton = new Button("🏠 MENU UTAMA");
    private Label player1ScoreLabel = new Label("0");
    private Label player2ScoreLabel = new Label("0");

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        StackPane rootWrapper = new StackPane();

        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #8B4513, #A0522D, #D2691E);" +
            "-fx-background-image: url('data:image/svg+xml;utf8,<svg width=\"100\" height=\"100\" xmlns=\"http://www.w3.org/2000/svg\"><rect width=\"100\" height=\"100\" fill=\"%238B4513\"/><path d=\"M0,50 Q25,30 50,50 T100,50\" stroke=\"%23A0522D\" stroke-width=\"2\" fill=\"none\" opacity=\"0.3\"/></svg>');"
        );

        animationLayer.setPickOnBounds(false);
        animationLayer.setMouseTransparent(true);
        animationLayer.prefWidthProperty().bind(rootWrapper.widthProperty());
        animationLayer.prefHeightProperty().bind(rootWrapper.heightProperty());
        animationLayer.setStyle("-fx-background-color: transparent;");

        rootWrapper.getChildren().addAll(root, animationLayer);

        HBox headerBox = createHeaderBox();
        VBox player1Panel = createPlayerPanel(1, "#FF6B6B");
        VBox player2Panel = createPlayerPanel(2, "#4ECDC4");

        GridPane boardPane = createGameBoard();
        
        VBox centerPanel = new VBox(20);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(20));

        HBox turnPanel = createTurnPanel();
        
        centerPanel.getChildren().addAll(turnPanel, boardPane);
        root.setTop(headerBox);
        root.setLeft(player1Panel);
        root.setRight(player2Panel);
        root.setCenter(centerPanel);
        HBox bottomBox = createControlButtons();
        root.setBottom(bottomBox);
        
        updateBoard();
        
        Scene scene = new Scene(rootWrapper, 1200, 700);
        
        stage.setTitle("🎮 CONGKLAK - Permainan Tradisional Indonesia");
        stage.setScene(scene);
        stage.show();
        animateInitialStart();
    }
    
    private HBox createHeaderBox() {
        HBox headerBox = new HBox();
        headerBox.setPadding(new Insets(15));
        headerBox.setStyle(
            "-fx-background-color: linear-gradient(to right, #5D4037, #8B4513);" +
            "-fx-border-color: #D4A76A;" +
            "-fx-border-width: 0 0 3 0;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 3);"
        );

        Label title = new Label("🎮 CONGKLAK");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#FFD700"));
        title.setEffect(new DropShadow(15, Color.BLACK));
        
        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
    
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        backToMenuButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-background-color: linear-gradient(to bottom, #D2691E, #8B4513);" +
            "-fx-padding: 8px 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 20px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);"
        );
        
        backToMenuButton.setOnMouseEntered(e -> {
            backToMenuButton.setStyle(backToMenuButton.getStyle() +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        backToMenuButton.setOnMouseExited(e -> {
            backToMenuButton.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: linear-gradient(to bottom, #D2691E, #8B4513);" +
                "-fx-padding: 8px 20px;" +
                "-fx-background-radius: 20px;" +
                "-fx-border-color: #FFD700;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 20px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);"
            );
        });
        
        backToMenuButton.setOnAction(e -> {
            Stage currentStage = (Stage) backToMenuButton.getScene().getWindow();
            currentStage.close();
            
            MainMenu menu = new MainMenu();
            try {
                menu.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        headerBox.getChildren().addAll(leftSpacer, title, rightSpacer, backToMenuButton);
        return headerBox;
    }
    
    private VBox createPlayerPanel(int playerNumber, String color) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(200);
        panel.setAlignment(Pos.CENTER);
        
        String gradient = (playerNumber == 1) ? 
            "linear-gradient(to bottom, #FF6B6B, #EE5A24)" : 
            "linear-gradient(to bottom, #4ECDC4, #1A535C)";
        
        panel.setStyle(
            "-fx-background-color: " + gradient + ";" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 5, 5);"
        );
        Label avatar = new Label(playerNumber == 1 ? "👤" : "👤");
        avatar.setFont(Font.font(48));
        
        Label name = new Label("PLAYER " + playerNumber);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        name.setTextFill(Color.WHITE);
        name.setEffect(new DropShadow(5, Color.BLACK));
        
        Label scoreTitle = new Label("SCORE");
        scoreTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        scoreTitle.setTextFill(Color.web("#FFD700"));
        
        Label scoreLabel;
        if (playerNumber == 1) {
            player1ScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
            player1ScoreLabel.setTextFill(Color.WHITE);
            player1ScoreLabel.setEffect(new InnerShadow(10, Color.BLACK));
            scoreLabel = player1ScoreLabel;
        } else {
            player2ScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
            player2ScoreLabel.setTextFill(Color.WHITE);
            player2ScoreLabel.setEffect(new InnerShadow(10, Color.BLACK));
            scoreLabel = player2ScoreLabel;
        }

        VBox storeBox = new VBox(5);
        storeBox.setAlignment(Pos.CENTER);
        storeBox.setPadding(new Insets(15));
        storeBox.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );
        
        Label storeLabel = new Label("STORE");
        storeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        storeLabel.setTextFill(Color.web("#FFD700"));
        
        Label storeCount = new Label("0");
        storeCount.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        storeCount.setTextFill(Color.WHITE);
        
        storeBox.getChildren().addAll(storeLabel, storeCount);
        panel.getChildren().addAll(avatar, name, scoreTitle, scoreLabel, storeBox);
        return panel;
    }
    
    private GridPane createGameBoard() {
        GridPane boardPane = new GridPane();
        boardPane.setHgap(15);
        boardPane.setVgap(15);
        boardPane.setAlignment(Pos.CENTER);
        boardPane.setPadding(new Insets(20));

        boardPane.setStyle(
            "-fx-background-color: linear-gradient(45deg, #8B4513, #A0522D, #D2691E);" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #5D4037;" +
            "-fx-border-width: 10;" +
            "-fx-border-radius: 20;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 10, 10);"
        );
        for (int i = 0; i < 16; i++) {
            pitViews[i] = new PitView(animationLayer);
            pitViews[i].setMinSize(80, 80);
            pitViews[i].setMaxSize(80, 80);

            if (i == 7 || i == 15) {
                pitViews[i].setMinSize(100, 100);
                pitViews[i].setMaxSize(100, 100);
            }
            int pos = i;
            pitViews[i].setOnMouseClicked(e -> handlePitClick(pos));
            pitViews[i].setOnMouseEntered(e -> {
                if (!pitViews[pos].isDisabled()) {
                    pitViews[pos].setEffect(new DropShadow(15, Color.GOLD));
                    pitViews[pos].setScaleX(1.1);
                    pitViews[pos].setScaleY(1.1);
                }
            });
            pitViews[i].setOnMouseExited(e -> {
                pitViews[pos].setEffect(null);
                pitViews[pos].setScaleX(1.0);
                pitViews[pos].setScaleY(1.0);
            });
        }
        for (int col = 1, pit = 14; pit >= 8; col++, pit--) {
            boardPane.add(pitViews[pit], col, 0);
        }
        boardPane.add(pitViews[15], 0, 1);
        GridPane.setRowSpan(pitViews[15], 3);

        boardPane.add(pitViews[7], 8, 1);
        GridPane.setRowSpan(pitViews[7], 3);

        for (int col = 1, pit = 0; pit <= 6; col++, pit++) {
            boardPane.add(pitViews[pit], col, 4);
        }
        return boardPane;
    }
    
    private HBox createTurnPanel() {
        HBox turnPanel = new HBox(10);
        turnPanel.setAlignment(Pos.CENTER);
        turnPanel.setPadding(new Insets(10, 30, 10, 30));
        turnPanel.setStyle(
            "-fx-background-color: linear-gradient(to right, #FFD700, #FFA500);" +
            "-fx-background-radius: 25;" +
            "-fx-border-color: #8B4513;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 3, 3);"
        );
        
        Label turnIcon = new Label("🔄");
        turnIcon.setFont(Font.font(24));
        
        turnLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        turnLabel.setTextFill(Color.web("#8B4513"));
        
        turnPanel.getChildren().addAll(turnIcon, turnLabel);
        return turnPanel;
    }
    
    private HBox createControlButtons() {
        HBox controlBox = new HBox(20);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(15));
        controlBox.setStyle(
            "-fx-background-color: rgba(0,0,0,0.2);" +
            "-fx-border-color: #D4A76A;" +
            "-fx-border-width: 2 0 0 0;"
        );      
        Button restartButton = createControlButton("🔄 RESTART", "#2E8B57");
        Button rulesButton = createControlButton("📖 ATURAN", "#4169E1");
        
        restartButton.setOnAction(e -> {
            game = new CongklakGame();
            updateBoard();
            animateTurnLabelChange("PLAYER 1'S TURN");
        });       
        rulesButton.setOnAction(e -> showRulesDialog());          
        controlBox.getChildren().addAll(restartButton, rulesButton);
        return controlBox;
    }
    
    private Button createControlButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setPrefSize(150, 40);
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );      
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: derive(" + color + ", 20%);" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: #FFD700;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 20;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 3, 3);"
            );
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: #FFD700;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 20;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
            );
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });       
        return button;
    }
    
    private void animateInitialStart() {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.5), turnLabel);
        scale.setFromX(0.5);
        scale.setFromY(0.5);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), turnLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ParallelTransition parallel = new ParallelTransition(scale, fade);
        parallel.play();
    }
    
    private void handlePitClick(int index) {
        System.out.println("Clicked pit: " + index);
        MoveResult result = game.makeMove(index);
        
        if (!result.valid) {
            shakePit(index);
            return;
        }
        if (result.path == null || result.path.isEmpty()) {
            updateBoard();
            return;
        }
        animateMove(result);
    }
    
    private void shakePit(int pitIndex) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(100), pitViews[pitIndex]);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }
    
    private void animateMove(MoveResult result) {
        animateSeedStep(result, 0);
    }
    
    private void afterMove(MoveResult result) {
        updateBoard();
        if (game.isGameOver()) {
            game.getBoard().collectRemainingSeeds();
            updateBoard();    
            Platform.runLater(() -> {
                int winner = game.getBoard().getWinner();
                showGameOverPopup(winner);
                disableAllPits(true);
            });
            return;
        }        
        String playerText = "PLAYER " + (game.getCurrentPlayer() + 1) + "'S TURN";
        animateTurnLabelChange(playerText);
        disableAllPits(false);
        updateBoard();
    }
    
    private void animateSeedStep(MoveResult result, int step) {
        if (result.path == null || result.path.isEmpty()) {
            updateBoard();
            turnLabel.setText("PLAYER " + (game.getCurrentPlayer() + 1) + "'S TURN");
            return;
        }
        if (step >= result.path.size()) {
            updateBoard();
            afterMove(result);
            return;
        }
        disableAllPits(true);

        int fromPit = (step == 0) ? result.startPit : result.path.get(step - 1);
        int toPit   = result.path.get(step);

        PitView source = pitViews[fromPit];
        PitView target = pitViews[toPit];
        source.removeOneVisualSeed();
        source.animateSeedTo(target, () -> {
            target.addVisualSeed();
            animateSeedStep(result, step + 1);
        });
    }
    
    private void disableAllPits(boolean disable) {
        for (PitView p : pitViews) {
            if (p != null) {
                p.setDisable(disable);
                p.setOpacity(disable ? 0.7 : 1.0);
            }
        }
    }
    
    private void animateTurnLabelChange(String newText) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), turnLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), turnLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        fadeOut.setOnFinished(e -> {
            turnLabel.setText(newText);
            if (newText.contains("PLAYER 1")) {
                turnLabel.setTextFill(Color.web("#FF6B6B"));
            } else {
                turnLabel.setTextFill(Color.web("#4ECDC4"));
            }    
            fadeIn.play();

            ScaleTransition pulse = new ScaleTransition(Duration.millis(300), turnLabel);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.1);
            pulse.setToY(1.1);
            pulse.setCycleCount(2);
            pulse.setAutoReverse(true);
            pulse.play();
        });
        fadeOut.play();
    }
    
    private void updateBoard() {
        int[] panel = game.getBoard().getBoard();      
        for (int i = 0; i < 16; i++) {
            pitViews[i].setSeeds(panel[i]);
            pitViews[i].setActive(false);
        }
        player1ScoreLabel.setText(String.valueOf(panel[7]));
        player2ScoreLabel.setText(String.valueOf(panel[15]));
        
        int current = game.getCurrentPlayer();
        
        if (current == 0) {
            for (int i = 0; i <= 6; i++) {
                if (panel[i] > 0) pitViews[i].setActive(true);
            }
        } else {
            for (int i = 8; i <= 14; i++) {
                if (panel[i] > 0) pitViews[i].setActive(true);
            }
        }
    }
    
    private void showRulesDialog() {
        Stage rulesStage = new Stage();
        rulesStage.initModality(Modality.APPLICATION_MODAL);
        
        VBox rulesBox = new VBox(20);
        rulesBox.setAlignment(Pos.CENTER);
        rulesBox.setPadding(new Insets(30));
        rulesBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #F5DEB3, #DEB887);" +
            "-fx-border-color: #8B4513;" +
            "-fx-border-width: 5;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 0, 0);"
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
        Button closeButton = new Button("TUTUP");
        closeButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #8B4513, #A0522D);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 16px;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);"
        );
        closeButton.setOnAction(e -> rulesStage.close());
        closeButton.setOnMouseEntered(e -> {
            closeButton.setScaleX(1.05);
            closeButton.setScaleY(1.05);
        });
        closeButton.setOnMouseExited(e -> {
            closeButton.setScaleX(1.0);
            closeButton.setScaleY(1.0);
        });
        rulesBox.getChildren().addAll(title, rulesContent, closeButton);
        
        Scene rulesScene = new Scene(rulesBox, 600, 500);
        rulesStage.setScene(rulesScene);
        rulesStage.setTitle("Aturan Permainan");
        rulesStage.show();
    }
    
    private void showGameOverPopup(int winner) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("🎉 PERMAINAN SELESAI!");
        
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #8B4513, #A0522D, #D2691E);" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 8px;" +
            "-fx-border-radius: 25px;" +
            "-fx-background-radius: 25px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 40, 0, 0, 0);"
        );
        
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER);
        
        Label crownLabel = new Label("👑");
        crownLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label("PERMAINAN SELESAI!");
        titleLabel.setStyle(
            "-fx-font-size: 36px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #FFD700;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 15, 0, 4, 4);"
        );
        headerBox.getChildren().addAll(crownLabel, titleLabel);
        
        Label winnerLabel = new Label();
        winnerLabel.setStyle(
            "-fx-font-size: 42px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 25px 50px;" +
            "-fx-background-radius: 20px;" +
            "-fx-border-radius: 20px;" +
            "-fx-border-width: 5px;"
        );
        if (winner == 1) {
            winnerLabel.setText("🎉 PLAYER 1 MENANG! 🎉");
            winnerLabel.setStyle(winnerLabel.getStyle() + 
                "-fx-text-fill: #FF6B6B;" +
                "-fx-background-color: rgba(255, 215, 0, 0.3);" +
                "-fx-border-color: #FF6B6B;"
            );
        } else if (winner == 2) {
            winnerLabel.setText("🎉 PLAYER 2 MENANG! 🎉");
            winnerLabel.setStyle(winnerLabel.getStyle() + 
                "-fx-text-fill: #4ECDC4;" +
                "-fx-background-color: rgba(65, 105, 225, 0.3);" +
                "-fx-border-color: #4ECDC4;"
            );
        } else {
            winnerLabel.setText("🤝 SERI! 🤝");
            winnerLabel.setStyle(winnerLabel.getStyle() + 
                "-fx-text-fill: #32CD32;" +
                "-fx-background-color: rgba(50, 205, 50, 0.3);" +
                "-fx-border-color: #32CD32;"
            );
        }
        int score1 = game.getBoard().getBoard()[7];
        int score2 = game.getBoard().getBoard()[15];
        
        HBox scoreBox = new HBox(40);
        scoreBox.setAlignment(Pos.CENTER);
        
        VBox player1Box = createPlayerScoreBox("PLAYER 1", score1, "#FF6B6B");
        VBox vsBox = new VBox();
        vsBox.setAlignment(Pos.CENTER);
        Label vsLabel = new Label("VS");
        vsLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
        vsBox.getChildren().add(vsLabel);
        
        VBox player2Box = createPlayerScoreBox("PLAYER 2", score2, "#4ECDC4");
        
        scoreBox.getChildren().addAll(player1Box, vsBox, player2Box);
        
        Label messageLabel = new Label("Terima kasih telah bermain Congklak!");
        messageLabel.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-style: italic;" +
            "-fx-text-fill: #F5DEB3;"
        );
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button replayButton = new Button("🔄 MAIN LAGI");
        replayButton.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-background-color: linear-gradient(to bottom, #2E8B57, #228B22);" +
            "-fx-padding: 15px 40px;" +
            "-fx-background-radius: 15px;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 3px;" +
            "-fx-border-radius: 15px;" +
            "-fx-cursor: hand;"
        );  
        Button menuButton = new Button("🏠 MENU UTAMA");
        menuButton.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-background-color: linear-gradient(to bottom, #D2691E, #8B4513);" +
            "-fx-padding: 15px 40px;" +
            "-fx-background-radius: 15px;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 3px;" +
            "-fx-border-radius: 15px;" +
            "-fx-cursor: hand;"
        );  
        replayButton.setOnAction(e -> {
            popupStage.close();
            game = new CongklakGame();
            updateBoard();
            animateTurnLabelChange("PLAYER 1'S TURN");
            disableAllPits(false);
        });
        menuButton.setOnAction(e -> {
            popupStage.close();
            Stage currentStage = (Stage) menuButton.getScene().getWindow();
            currentStage.close();
            
            MainMenu menu = new MainMenu();
            try {
                menu.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        buttonBox.getChildren().addAll(replayButton, menuButton);
        
        root.getChildren().addAll(headerBox, winnerLabel, scoreBox, messageLabel, buttonBox);
        
        Scene scene = new Scene(root);
        popupStage.setScene(scene);
        
        root.setOpacity(0);
        root.setScaleX(0.8);
        root.setScaleY(0.8);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.5), root);
        scaleIn.setFromX(0.8); scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0); scaleIn.setToY(1.0);
        
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ParallelTransition parallel = new ParallelTransition(scaleIn, fadeIn);
        
        popupStage.show();
        parallel.play();
    }
    
    private VBox createPlayerScoreBox(String playerName, int score, String color) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20, 30, 20, 30));
        box.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-background-radius: 15px;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 3px;" +
            "-fx-border-radius: 15px;"
        );
        Label nameLabel = new Label(playerName);
        nameLabel.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + color + ";"
        );
        Label scoreLabel = new Label(String.valueOf(score));
        scoreLabel.setStyle(
            "-fx-font-size: 48px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 2, 2);"
        );
        box.getChildren().addAll(nameLabel, scoreLabel);
        return box;
    }
    
    public static void main(String[] args) {
        Application.launch(MainMenu.class, args);
    }
}