package application;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.jfree.layout.CenterLayout;


public class app extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Group root = new Group();
        Scene scene = new Scene(root, 60,60, Color.LIGHTSKYBLUE);  
         
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Pharmacy Management System");
        Text text = new Text("WHOOOOA!!!");
        text.setFont(Font.font("verdana",50));
        text.setX(50);
        text.setY(50);
        Line line = new Line();
        line.setStartX(200);
        line.setStartY(200);
        line.setEndX(500);
        line.setEndY(200);
        root.getChildren().add(line);


  
        root.getChildren().add(text);

    }
}