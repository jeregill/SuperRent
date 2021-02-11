package ca.ubc.cs304.controller;

import ca.ubc.cs304.model.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

public class queryViewer {

    public void showResults(List<Column<?>> columns, List data){
        Stage primaryStage = new Stage();
        StackPane root = new StackPane();
        TableView<Vehicle> table = createTable(columns, data);
        root.getChildren().add(table);
        primaryStage.setTitle("Query Results");
        primaryStage.setScene(new Scene(root, 750, 500));
        primaryStage.show();
    }


    public  <E> TableView<E> createTable(List<Column<?>> columns, List<E> data) {
        TableView<E> table = new TableView<>();
        ObservableList<E> myList = FXCollections.observableArrayList(data);
        for (Column<?> column : columns) {
            table.getColumns().add(createColumn(column));
        }

        table.setItems(myList);
        return table;
    }

    private <E, C> TableColumn<E, C> createColumn(Column<?> column, C type) {
        TableColumn<E, C> tableColumn = new TableColumn<E, C>(column.getTitle());
        tableColumn.setCellValueFactory(new PropertyValueFactory<E, C>(column.getFieldName()));
        return tableColumn;
    }

    private <E> TableColumn<E, ?> createColumn(Column<?> column) {
        switch (column.getType().getCanonicalName()) {
            case "java.lang.Integer":
                return createColumn(column, Integer.class);
            case "java.lang.Double":
                return createColumn(column, Double.class);
            case "java.lang.String":
            default:
                return createColumn(column, String.class);
        }
    }
}
