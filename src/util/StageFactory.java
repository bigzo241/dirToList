package util;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageFactory {

    public static Stage getStage(char StyleStage, double width, double height) {
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, width, height);

        if (StyleStage == 'd') {
            Stage newStage = new Stage();
            newStage.setScene(scene);

            return newStage;
        } else if (StyleStage == 't') {
            root.setStyle("-fx-background-color : transparent");
//            scene.setFill(null);
            Stage newStage = new Stage(StageStyle.TRANSPARENT);
            newStage.setScene(scene);

            return newStage;
        } else {
            return null;
        }
    }
}
