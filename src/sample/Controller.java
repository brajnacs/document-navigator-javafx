package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    @FXML
    private Button previousPage;

    @FXML
    private Button nextPage;

    @FXML
    private ScrollPane imageHolder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<String> imagePaths = FXCollections.observableArrayList(
                "https://picsum.photos/seed/picsum1/400/600",
                "https://picsum.photos/seed/picsum2/400/600",
                "https://picsum.photos/seed/picsum3/400/600",
                "https://picsum.photos/seed/picsum4/400/600",
                "https://picsum.photos/seed/picsum5/400/600",
                "https://picsum.photos/seed/picsum6/400/600"
        );

        ObservableList<StackPane> pages = imagePaths.stream()
                .map(path -> new Image(path, true))
                .map(this::createPage)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        IntegerBinding size = Bindings.size(pages);
        IntegerProperty index = new SimpleIntegerProperty();
        IntegerBinding currentImageIndex = index.add(1);
        imageHolder.contentProperty().bind(Bindings.valueAt(pages, index));

        previousPage.disableProperty().bind(currentImageIndex.lessThanOrEqualTo(0));
        nextPage.disableProperty().bind(currentImageIndex.greaterThanOrEqualTo(size));

        previousPage.setOnAction(event -> {
            index.setValue(index.get() - 1);
        });
        nextPage.setOnAction(event -> {
            index.setValue(index.get() + 1);
        });
    }

    private StackPane createPage(Image image) {
        StackPane stackPane = new StackPane();
        ProgressBar progressBar = new ProgressBar(0);
        ImageView imageView = new ImageView(image);
        stackPane.getChildren().add(imageView);
        progressBar.progressProperty().bind(image.progressProperty());
        progressBar.visibleProperty().bind(image.progressProperty().lessThan(1));
        stackPane.getChildren().add(progressBar);
        stackPane.setMinHeight(300);
        stackPane.setMinWidth(200);
        return stackPane;
    }
}
