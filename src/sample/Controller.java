package sample;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public GridPane root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        DocumentNavigator documentNavigator = new DocumentNavigator();
        documentNavigator.setImages(FXCollections.observableArrayList(
                new Page(1L, "https://picsum.photos/seed/picsum1/400/600", "federal"),
                new Page(2L, "https://picsum.photos/seed/picsum2/400/600", "w2"),
                new Page(3L, "https://picsum.photos/seed/picsum3/400/600", "canadian"),
                new Page(4L, "https://picsum.photos/seed/picsum4/400/600", "business"),
                new Page(5L, "https://picsum.photos/seed/picsum5/400/600", "aadhar"),
                new Page(6L, "https://picsum.photos/seed/picsum6/400/600", "pan")

        ));

        documentNavigator.currentPageProperty().addListener((observableValue, oldPage, newPage) -> {
            System.out.println(observableValue.getValue().getSlug());
        });

        root.getChildren().add(documentNavigator);

    }
}
