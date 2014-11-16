package com.paranoidfrog.ui;

import javax.swing.KeyStroke;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
	private AudioEngine asioHost;

	public static void main(String[] args) {
		launch(args);
	}

	public Main() {
		asioHost = new AudioEngine();
	}

	@Override
	public void start(Stage primaryStage) {
		

		Button download = new Button();
		download.getStyleClass().add("button");

		Button rockOn = new Button();
		rockOn.getStyleClass().add("button");
		rockOn.getStyleClass().add("button-rock-on");
		rockOn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				asioHost.initDrivers();
			}
		});

		Button rockOff = new Button();
		rockOff.getStyleClass().add("button");
		rockOff.getStyleClass().add("button-rock-off");
		rockOff.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				asioHost.stopDrivers();
			}
		});

		Button flag = new Button();
		flag.getStyleClass().add("button");
		flag.getStyleClass().add("button-overdrive");
		flag.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				asioHost.openControlPanel();
			}
		});

		Button rockOn2 = new Button();
		rockOn2.getStyleClass().add("button");
		rockOn2.getStyleClass().add("button-rock-on");
		rockOn2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				asioHost.initDrivers();
			}
		});

		Button rockOff2 = new Button();
		rockOff2.getStyleClass().add("button");
		rockOff2.getStyleClass().add("button-rock-off");
		rockOff2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				asioHost.stopDrivers();
			}
		});

		Button flag2 = new Button();
		flag2.getStyleClass().add("button");
		flag2.getStyleClass().add("button-overdrive");
		flag2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				asioHost.openControlPanel();
			}
		});
		HBox cabecote = new HBox(2);
		cabecote.setAlignment(Pos.CENTER);
		cabecote.getChildren().addAll(rockOn, flag, rockOff);
		cabecote.getStyleClass().add("hbox");
		cabecote.getStyleClass().add("head");
		cabecote.setPrefSize(100.0, 134.0);
		cabecote.setMaxWidth(600.0);
		cabecote.setEffect(new DropShadow(25.0, Color.BLACK));
		
		 Label keyPressed = new Label();

		HBox cabecote2 = new HBox(2);
		cabecote2.setAlignment(Pos.CENTER);
		cabecote2.getChildren().addAll(rockOn2, flag2, rockOff2, keyPressed);
		cabecote2.getStyleClass().add("hbox");
		cabecote2.getStyleClass().add("head");
		cabecote2.setPrefSize(100.0, 134.0);
		cabecote2.setMaxWidth(600.0);
		cabecote2.setEffect(new DropShadow(25.0, Color.BLACK));

		HBox amp = new HBox();
		amp.setPrefSize(100.0, 300.0);
		amp.setMaxWidth(700.0);
		amp.getStyleClass().add("hbox");
		amp.getStyleClass().add("body");
		amp.setEffect(new DropShadow(10.0, Color.BLACK));
		
		

		VBox vBox = new VBox(2);
		vBox.setAlignment(Pos.BOTTOM_CENTER);
		vBox.getChildren().addAll(cabecote, cabecote2, amp);
		vBox.setMargin(amp, new Insets(1.0, 0, 0, 0));
		vBox.getStyleClass().add("paranoid-background");
		Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();

		Scene scene = new Scene(vBox, screenSize.getWidth() - 20, screenSize.getHeight() - 20);
		
		 scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
	            public void handle(KeyEvent ke) {
	               asioHost.footSwitch();
	            }
	        });
		 
		 scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
	            public void handle(KeyEvent ke) {
	               asioHost.footSwitch();
	            }
	        });

		Image icon = new Image(Main.class.getResourceAsStream("ic_launcher.png"));
		primaryStage.getIcons().add(icon);
		primaryStage.setTitle("Paranoid Frog");
		primaryStage.setScene(scene);
		primaryStage.getScene().getStylesheets()
				.setAll(Main.class.getResource("styles.css").toString());
		primaryStage.setMaximized(true);
		primaryStage.show();
	}
}