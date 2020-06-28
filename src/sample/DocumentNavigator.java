package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.stream.Collectors;

public class DocumentNavigator extends BorderPane {

    @FXML
    private Button previousPage;

    @FXML
    private Button nextPage;

    @FXML
    private Button zoomInPage;

    @FXML
    private Button zoomOutPage;

    @FXML
    private ScrollPane imageHolder;

    private ListProperty<Page> pages = new SimpleListProperty<>();
    private ObservableList<StackPane> images = FXCollections.observableArrayList();
    private ObjectProperty<Page> currentPage = new SimpleObjectProperty<>();
    private IntegerProperty index = new SimpleIntegerProperty();
    private ObjectBinding<StackPane> currentImage;
    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1);
    private DoubleProperty originalHeightProperty = new SimpleDoubleProperty(300);
    private DoubleProperty originalWidthProperty = new SimpleDoubleProperty(200);

    public DocumentNavigator() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("document_navigator.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
            setupBindings();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void setupBindings() {

        pages.addListener(this::initializeImages);

        IntegerBinding size = Bindings.size(images);
        currentPage.bind(Bindings.valueAt(pages, index));
        IntegerBinding currentImageIndex = index.add(1);
        currentImage = Bindings.valueAt(images, index);
        imageHolder.contentProperty().bind(currentImage);
        imageHolder.setMaxHeight(600);
        imageHolder.setMaxWidth(400);

        previousPage.disableProperty().bind(index.lessThanOrEqualTo(0));
        nextPage.disableProperty().bind(currentImageIndex.greaterThanOrEqualTo(size));
        currentImage.addListener((observableValue, oldStackPane, newStackPane) -> {
            // Reset zoom factor so that next image should not retain the zoom from previous image
            zoomFactor.set(1);
            if (oldStackPane != null) {
                // Remove unnecessary binding for performance and to prevent memory leak?
                ((ImageView) oldStackPane.lookup(".image-view")).fitHeightProperty().unbind();
            }
            DoubleProperty fitHeightProperty = ((ImageView) newStackPane.lookup(".image-view")).fitHeightProperty();
            DoubleProperty fitWidthProperty = ((ImageView) newStackPane.lookup(".image-view")).fitWidthProperty();
            fitHeightProperty.bind(originalHeightProperty.multiply(zoomFactor));
            fitWidthProperty.bind(originalWidthProperty.multiply(zoomFactor));
        });

        previousPage.setOnAction(this::previousPage);
        nextPage.setOnAction(this::nextPage);
        zoomInPage.setOnAction(this::zoomInPage);
        zoomOutPage.setOnAction(this::zoomOutPage);
        zoomInPage.disableProperty().bind(zoomFactor.greaterThanOrEqualTo(2));
        zoomOutPage.disableProperty().bind(zoomFactor.lessThanOrEqualTo(1));
    }

    private void previousPage(ActionEvent actionEvent) {
        index.setValue(index.get() - 1);
    }

    private void nextPage(ActionEvent actionEvent) {
        index.setValue(index.get() + 1);
    }

    private void zoomInPage(ActionEvent actionEvent) {
        zoomFactor.set(zoomFactor.get() + .2);
    }

    private void zoomOutPage(ActionEvent actionEvent) {
        zoomFactor.set(zoomFactor.get() - .2);
    }

    private void initializeImages(ListChangeListener.Change<? extends Page> change) {
        images.clear();
        images.addAll(pages.stream()
                .map(path -> new Image(path.getFilePath(), true))
                .map(this::createPage)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    private StackPane createPage(Image image) {
        StackPane stackPane = new StackPane();
        ProgressBar progressBar = new ProgressBar(0);
        ImageView imageView = new ImageView(image);
        imageView.getStyleClass().add("image-view");
        stackPane.getChildren().add(imageView);
        progressBar.progressProperty().bind(image.progressProperty());
        progressBar.visibleProperty().bind(image.progressProperty().lessThan(1));
        stackPane.getChildren().add(progressBar);
        stackPane.setMaxHeight(600);
        stackPane.setMaxWidth(400);
        return stackPane;
    }

    public ObservableList<Page> getPages() {
        return pages.get();
    }

    public ListProperty<Page> pagesProperty() {
        return pages;
    }

    public void setImages(ObservableList<Page> images) {
        this.pages.set(images);
    }

    public ObjectProperty<Page> currentPageProperty() {
        return currentPage;
    }
}
