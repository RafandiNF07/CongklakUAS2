package com.congklakuas2;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class CongklakGUI extends Application {

    private CongklakGame game = new CongklakGame();
    private PitView[] pitViews = new PitView[16];
    private Label turnLabel = new Label("PLAYER 1'S TURN");

    private Stage primaryStage;

    private Pane animationLayer = new Pane();
    private Button backToMenuButton = new Button("🏠 MENU UTAMA");
    private Button skipButton = new Button("⏩ SKIP");
    private Label player1ScoreLabel = new Label("0");
    private Label player2ScoreLabel = new Label("0");
    private StackPane rootWrapper;

    private boolean isAnimating = false;
    private Animation currentAnimation = null;
    private StackPane flyingHand;
    private Label flyingCountLabel;

    private MoveResult currentMoveResult;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        BorderPane root = new BorderPane();
        rootWrapper = new StackPane();
        rootWrapper.getStyleClass().add("root");

        animationLayer.setPickOnBounds(false);
        animationLayer.setMouseTransparent(true);
        animationLayer.prefWidthProperty().bind(rootWrapper.widthProperty());
        animationLayer.prefHeightProperty().bind(rootWrapper.heightProperty());

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
        root.setBottom(createControlButtons());

        createFlyingHand();

        updateBoardVisuals();

        Scene scene = new Scene(rootWrapper, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("🎮 CONGKLAK PRO - Full Version");
        stage.setScene(scene);
        stage.show();

        animateInitialStart();
    }

    private void createFlyingHand() {
        flyingHand = new StackPane();
        flyingHand.setPrefSize(60, 60);
        flyingHand.setStyle(
                "-fx-background-color: rgba(255, 223, 0, 0.9);" +
                        "-fx-background-radius: 50%;" +
                        "-fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 50%;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 5);"
        );

        flyingCountLabel = new Label("0");
        flyingCountLabel.setStyle("-fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-font-size: 20px;");

        flyingHand.getChildren().add(flyingCountLabel);
        flyingHand.setVisible(false);
        animationLayer.getChildren().add(flyingHand);
    }

    private void handlePitClick(int index) {
        if (isAnimating) return;

        if (!game.isPitPlayable(index)) {
            shakePit(index);
            return;
        }

        isAnimating = true;
        skipButton.setDisable(false);

        currentMoveResult = game.makeMove(index);

        runComplexAnimation(currentMoveResult, index, currentMoveResult.startSeeds);
    }

    private void runComplexAnimation(MoveResult result, int startPitIndex, int initialSeeds) {
        Point2D startPos = getPitScenePos(pitViews[startPitIndex]);

        flyingHand.setTranslateX(startPos.getX() - 30);
        flyingHand.setTranslateY(startPos.getY() - 30);
        flyingHand.setVisible(true);
        flyingCountLabel.setText(String.valueOf(initialSeeds));

        pitViews[startPitIndex].clearVisualSeeds();

        ScaleTransition grabAnim = new ScaleTransition(Duration.millis(300), flyingHand);
        grabAnim.setFromX(0.5); grabAnim.setFromY(0.5);
        grabAnim.setToX(1.2);   grabAnim.setToY(1.2);
        grabAnim.setInterpolator(Interpolator.EASE_OUT);

        grabAnim.setOnFinished(e -> {
            ScaleTransition normalize = new ScaleTransition(Duration.millis(200), flyingHand);
            normalize.setToX(1.0); normalize.setToY(1.0);
            normalize.play();
            executeStep(result.path, 0, initialSeeds);
        });

        currentAnimation = grabAnim;
        grabAnim.play();
    }

    private void executeStep(List<Integer> path, int stepIndex, int currentSeedsInHand) {
        if (!isAnimating) return;

        if (currentSeedsInHand <= 0 || stepIndex >= path.size()) {
            if (currentMoveResult.hitOpponent) {
                animateCapture(currentMoveResult);
            } else {
                endTurnAnimation();
            }
            return;
        }

        int targetPitIdx = path.get(stepIndex);
        PitView targetPit = pitViews[targetPitIdx];
        Point2D targetPos = getPitScenePos(targetPit);

        TranslateTransition moveT = new TranslateTransition(Duration.millis(300), flyingHand);
        moveT.setToX(targetPos.getX() - 30);
        moveT.setToY(targetPos.getY() - 30);
        moveT.setInterpolator(Interpolator.LINEAR);

        ScaleTransition dropPulse = new ScaleTransition(Duration.millis(100), flyingHand);
        dropPulse.setFromX(1.0); dropPulse.setFromY(1.0);
        dropPulse.setToX(0.9); dropPulse.setToY(0.9);
        dropPulse.setAutoReverse(true);
        dropPulse.setCycleCount(2);

        SequentialTransition stepAnim = new SequentialTransition(moveT, dropPulse);
        currentAnimation = stepAnim;

        stepAnim.setOnFinished(e -> {
            int nextHandCount = currentSeedsInHand - 1;
            flyingCountLabel.setText(String.valueOf(nextHandCount));
            targetPit.addVisualSeed();

            if (nextHandCount == 0 && stepIndex < path.size() - 1) {
                PauseTransition pause = new PauseTransition(Duration.millis(300));
                pause.setOnFinished(ev -> {
                    int capturedSeeds = targetPit.getVisualSeedCount();
                    targetPit.clearVisualSeeds();
                    flyingCountLabel.setText(String.valueOf(capturedSeeds));

                    ScaleTransition grabChain = new ScaleTransition(Duration.millis(250), flyingHand);
                    grabChain.setFromX(1.0); grabChain.setToX(1.2);
                    grabChain.setAutoReverse(true); grabChain.setCycleCount(2);
                    grabChain.play();

                    grabChain.setOnFinished(ev2 -> executeStep(path, stepIndex + 1, capturedSeeds));
                });
                currentAnimation = pause;
                pause.play();
            } else {
                executeStep(path, stepIndex + 1, nextHandCount);
            }
        });

        stepAnim.play();
    }

    private void animateCapture(MoveResult result) {
        int lastPitIdx = result.stopAtIndex;
        int oppPitIdx = game.getBoard().oppositePos(lastPitIdx);
        int storeIdx = game.getBoard().ownStoreIdx(game.getCurrentPlayer());

        PitView lastPit = pitViews[lastPitIdx];
        PitView oppPit = pitViews[oppPitIdx];
        PitView store = pitViews[storeIdx];

        Circle c1 = new Circle(10, Color.GOLD);
        Circle c2 = new Circle(10, Color.GOLD);

        Point2D p1 = getPitScenePos(lastPit);
        Point2D p2 = getPitScenePos(oppPit);
        Point2D pStore = getPitScenePos(store);

        animationLayer.getChildren().addAll(c1, c2);
        c1.setTranslateX(p1.getX()); c1.setTranslateY(p1.getY());
        c2.setTranslateX(p2.getX()); c2.setTranslateY(p2.getY());

        lastPit.clearVisualSeeds();
        oppPit.clearVisualSeeds();
        flyingHand.setVisible(false);

        TranslateTransition t1 = new TranslateTransition(Duration.millis(800), c1);
        t1.setToX(pStore.getX()); t1.setToY(pStore.getY());
        TranslateTransition t2 = new TranslateTransition(Duration.millis(800), c2);
        t2.setToX(pStore.getX()); t2.setToY(pStore.getY());

        ParallelTransition captureAnim = new ParallelTransition(t1, t2);
        currentAnimation = captureAnim;

        captureAnim.setOnFinished(e -> {
            animationLayer.getChildren().removeAll(c1, c2);
            endTurnAnimation();
        });
        captureAnim.play();
    }

    private void handleSkip() {
        if (isAnimating) {
            forceStopAnimation();
            updateBoardVisuals();
            processTurnResult(currentMoveResult);
        }
    }

    private void endTurnAnimation() {
        forceStopAnimation();
        updateBoardVisuals();
        processTurnResult(currentMoveResult);
    }

    private void processTurnResult(MoveResult result) {
        if (game.isGameOver()) {
            game.collectRemainingSeeds();
            updateBoardVisuals();
            showGameOverDialog();
            return;
        }

        if (result.freeTurn) {
            int currentPlayer = game.getCurrentPlayer();
            animateTurnLabelChange("PLAYER " + (currentPlayer + 1) + " MAIN LAGI! (Free Turn)");
        } else {
            game.switchTurn();
            animateTurnLabelChange("PLAYER " + (game.getCurrentPlayer() + 1) + "'S TURN");
        }

        updateBoardVisuals();
    }

    private void forceStopAnimation() {
        if (currentAnimation != null) currentAnimation.stop();
        isAnimating = false;
        flyingHand.setVisible(false);
        skipButton.setDisable(true);
        animationLayer.getChildren().removeIf(node -> node instanceof Circle);
    }

    private void handleRestart() {
        forceStopAnimation();
        game = new CongklakGame();
        updateBoardVisuals();
        turnLabel.setText("PLAYER 1'S TURN");
        animateInitialStart();
    }

    private void updateBoardVisuals() {
        int[] board = game.getBoard().getBoard();
        for (int i = 0; i < 16; i++) {
            pitViews[i].setSeeds(board[i]);
            pitViews[i].setDisable(isAnimating);
            pitViews[i].setActive(!isAnimating && game.isPitPlayable(i));
        }
        player1ScoreLabel.setText(String.valueOf(board[7]));
        player2ScoreLabel.setText(String.valueOf(board[15]));
    }

    private void shakePit(int index) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), pitViews[index]);
        tt.setByX(5); tt.setCycleCount(4); tt.setAutoReverse(true); tt.play();
    }

    private void animateTurnLabelChange(String text) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), turnLabel);
        ft.setFromValue(1); ft.setToValue(0);
        ft.setOnFinished(e -> {
            turnLabel.setText(text);
            if (text.contains("PLAYER 1")) turnLabel.setTextFill(Color.web("#FF6B6B"));
            else turnLabel.setTextFill(Color.web("#4ECDC4"));
            FadeTransition ft2 = new FadeTransition(Duration.millis(300), turnLabel);
            ft2.setFromValue(0); ft2.setToValue(1);
            ft2.play();
        });
        ft.play();
    }

    private void animateInitialStart() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), turnLabel);
        st.setFromX(0); st.setFromY(0); st.setToX(1); st.setToY(1);
        st.play();
    }

    // --- METHOD INI HANYA SATU SEKARANG ---
    private Point2D getPitScenePos(PitView pit) {
        return pit.localToScene(pit.getWidth()/2, pit.getHeight()/2);
    }

    // --- 5. UI COMPONENTS ---

    private HBox createHeaderBox() {
        HBox headerBox = new HBox();
        headerBox.setPadding(new Insets(15));
        headerBox.getStyleClass().add("header-box");
        Label title = new Label("🎮 CONGKLAK");
        title.getStyleClass().add("header-title");
        Region leftSpacer = new Region(); HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        Region rightSpacer = new Region(); HBox.setHgrow(rightSpacer, Priority.ALWAYS);
        backToMenuButton.getStyleClass().addAll("game-button", "btn-home");
        backToMenuButton.setOnAction(e -> {
            forceStopAnimation();
            Stage currentStage = (Stage) backToMenuButton.getScene().getWindow();
            currentStage.close();
            try { new MainMenu().start(new Stage()); } catch (Exception ex) { ex.printStackTrace(); }
        });
        headerBox.getChildren().addAll(leftSpacer, title, rightSpacer, backToMenuButton);
        return headerBox;
    }

    private VBox createPlayerPanel(int playerNumber, String colorHex) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(200);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color: linear-gradient(to bottom, " + colorHex + ", derive(" + colorHex + ", -30%));");
        panel.getStyleClass().add("player-panel");
        Label avatar = new Label("👤");
        avatar.setFont(Font.font(48));
        Label name = new Label("PLAYER " + playerNumber);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        name.setTextFill(Color.WHITE);
        Label scoreTitle = new Label("SCORE");
        scoreTitle.getStyleClass().add("score-title");
        Label scoreLabel = (playerNumber == 1) ? player1ScoreLabel : player2ScoreLabel;
        scoreLabel.getStyleClass().add("score-label");
        VBox storeBox = new VBox(5);
        storeBox.setAlignment(Pos.CENTER);
        storeBox.setPadding(new Insets(15));
        storeBox.getStyleClass().add("store-box");
        Label storeLabel = new Label("STORE");
        storeLabel.getStyleClass().add("store-label");
        storeBox.getChildren().addAll(storeLabel, scoreLabel);
        panel.getChildren().addAll(avatar, name, scoreTitle, storeBox);
        return panel;
    }

    private GridPane createGameBoard() {
        GridPane boardPane = new GridPane();
        boardPane.getStyleClass().add("board-pane");
        boardPane.setHgap(10); boardPane.setVgap(10);
        boardPane.setAlignment(Pos.CENTER);
        boardPane.setPadding(new Insets(40, 50, 40, 50));
        for (int i = 0; i < 16; i++) {
            pitViews[i] = new PitView(animationLayer);
            if (i == 7 || i == 15) {
                pitViews[i].setMinSize(100, 100); pitViews[i].setMaxSize(100, 100);
                pitViews[i].setAsStore();
            } else {
                pitViews[i].setMinSize(80, 80); pitViews[i].setMaxSize(80, 80);
            }
            int pos = i;
            pitViews[i].setOnMouseClicked(e -> handlePitClick(pos));
        }
        for (int col = 1, pit = 14; pit >= 8; col++, pit--) boardPane.add(pitViews[pit], col, 0);
        boardPane.add(pitViews[15], 0, 1); GridPane.setRowSpan(pitViews[15], 3);
        boardPane.add(pitViews[7], 8, 1); GridPane.setRowSpan(pitViews[7], 3);
        for (int col = 1, pit = 0; pit <= 6; col++, pit++) boardPane.add(pitViews[pit], col, 4);
        return boardPane;
    }

    private HBox createTurnPanel() {
        HBox turnPanel = new HBox(10);
        turnPanel.setAlignment(Pos.CENTER);
        turnPanel.setPadding(new Insets(10, 30, 10, 30));
        turnPanel.getStyleClass().add("turn-panel");
        Label turnIcon = new Label("🔄");
        turnIcon.setFont(Font.font(24));
        turnLabel.getStyleClass().add("turn-label");
        turnPanel.getChildren().addAll(turnIcon, turnLabel);
        return turnPanel;
    }

    private HBox createControlButtons() {
        HBox controlBox = new HBox(20);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(15));
        controlBox.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-border-color: #D4A76A; -fx-border-width: 2 0 0 0;");
        Button restartButton = new Button("🔄 RESTART");
        restartButton.getStyleClass().addAll("game-button", "btn-restart");
        Button rulesButton = new Button("📖 ATURAN");
        rulesButton.getStyleClass().addAll("game-button", "btn-rules");
        skipButton.getStyleClass().addAll("game-button");
        skipButton.setStyle("-fx-background-color: #FF8C00;");
        skipButton.setDisable(true);
        restartButton.setOnAction(e -> handleRestart());
        skipButton.setOnAction(e -> handleSkip());
        rulesButton.setOnAction(e -> showRulesDialog());
        controlBox.getChildren().addAll(restartButton, skipButton, rulesButton);
        return controlBox;
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
        // Style background ditaruh di sini agar statis
        mainLayout.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #F5DEB3, #DEB887);" +
                        "-fx-border-color: #8B4513; -fx-border-width: 5;"
        );

        // 2. Judul
        Label title = new Label("📜 ATURAN PERMAINAN");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#8B4513"));
        title.setEffect(new DropShadow(5, Color.web("#000000", 0.2)));

        // 3. Konten Aturan (Wadah Teks)
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

        // 4. ScrollPane (Agar bisa digulir)
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300); // Tinggi area scroll
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // 5. Tombol Tutup
        Button closeBtn = new Button("TUTUP");
        closeBtn.setStyle(
                "-fx-background-color: #8B4513; -fx-text-fill: white;" +
                        "-fx-font-weight: bold; -fx-padding: 10 30;" +
                        "-fx-background-radius: 10; -fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 2, 2);"
        );

        // Efek Hover Tombol
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 10;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 10;"));

        closeBtn.setOnAction(e -> rulesStage.close());

        mainLayout.getChildren().addAll(title, scrollPane, closeBtn);

        Scene scene = new Scene(mainLayout, 500, 500); // Ukuran window diperbesar sedikit
        rulesStage.setScene(scene);
        rulesStage.show();
    }

    private void showGameOverDialog() {
        int winner = game.getBoard().getWinner();
        String msg = (winner == 0) ? "SERI!" : "PLAYER " + winner + " MENANG!";
        Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2E8B57; -fx-border-color: gold; -fx-border-width: 5;");
        Label lbl = new Label(msg);
        lbl.setStyle("-fx-font-size: 32px; -fx-text-fill: white; -fx-font-weight: bold;");
        Button close = new Button("OK");
        close.setOnAction(e -> dialog.close());
        root.getChildren().addAll(lbl, close);
        dialog.setScene(new Scene(root, 400, 200));
        dialog.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}