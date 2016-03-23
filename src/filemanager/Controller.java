package filemanager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public TreeView<File> treeView;
    public TreeItem<File> root = new TreeItem();
    private File file;
    @FXML
    private TextField inputArea;


    public Controller() {};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.treeView.setCellFactory(new Controller.FileCellFactory());
    }


    @FXML
    private void load(ActionEvent event) {
        System.out.println("You clicked load Button!");
        DirectoryChooser DC = new DirectoryChooser();
        this.file = DC.showDialog((Window)null);

        if(this.file != null) {
            this.treeView.setRoot(this.root);
            this.readChild(this.root, this.file);
        } else {
            this.treeView.setRoot((TreeItem)null);
        }

    }

    private void readChild(TreeItem<File> root, File file) {
        root.setValue(file);
        root.getChildren().clear();
        File[] children = file.listFiles();
        int indexer = 0;

        for(int i =  0; i < children.length; i++) {
            File child = children[i];

            if(child.isDirectory()) {

                root.getChildren().add(new TreeItem(child));
                this.readChild((TreeItem)root.getChildren().get(indexer), child);

            } else {
                root.getChildren().add(new TreeItem(child));
            }

            indexer++;
        }

    }

    @FXML
    private void delete(ActionEvent event) {
        File delFile;
        System.out.println("You clicked delete Button!");
        TreeItem<File> selectedItem = treeView.getSelectionModel().getSelectedItem();

        try {
            if (selectedItem.getChildren().isEmpty()) {
                delFile = new File(selectedItem.getValue().getPath());
                delFile.delete();
                selectedItem.getParent().getChildren().remove(selectedItem);
            } else {
                removeInside(selectedItem);
            }
        }catch(NullPointerException e){
            AlertFunction("Warning!","You haven't chosen a file to delete.", "Choose file and try again.");
            String alertText = e.toString();


            System.out.println(e);
        }
    }

    private void removeInside(TreeItem<File> selectedItem) {


        try {

            if (!selectedItem.getChildren().isEmpty()) {
                int indexer = 0;
                File[] children = selectedItem.getValue().listFiles();
                for (File child : children)
                    removeInside(selectedItem.getChildren().get(indexer));
                indexer++;
            }

            if (selectedItem.getChildren().isEmpty()) {
                File delFile = new File(selectedItem.getValue().getPath());
                delFile.delete();
                selectedItem.getParent().getChildren().remove(selectedItem);
            }
        }catch(NullPointerException e){

            System.out.println(e);
        }
    }



    public void AlertFunction(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();

    }

    public void addFile(ActionEvent actionEvent) throws IOException {

        System.out.println("You Clicked Add File Button");
        TreeItem<File> selectedDirectory = treeView.getSelectionModel().getSelectedItem();
        try {
            File Dir = selectedDirectory.getValue();
            String fileName;


            fileName = inputArea.textProperty().get();
            if (!fileName.isEmpty()) {
                File nFile = new File(Dir, fileName);
                Files.createFile(Paths.get(Dir.toString(), fileName));
                TreeItem<File> newTreeFile = new TreeItem<File>(nFile);
                selectedDirectory.getChildren().add(newTreeFile);


            } else {
                AlertFunction("Warning!", "You haven't inserted a file name!","Insert a file name a try again.");
                System.out.println("You didn't input a name for a new file");
                return;

            }
        }catch(NullPointerException e){
            AlertFunction("Warning!", "You haven't chosen a directory!!","Choose a catalog and try again.");
            System.out.println(e);
        }
    }


    public void addDir(ActionEvent actionEvent) throws IOException {

        System.out.println("You clicked Add Directory Button");

        TreeItem<File> selectedDirectory = treeView.getSelectionModel().getSelectedItem();
        try {
            File Dir = selectedDirectory.getValue();
            String fileName;
            fileName = inputArea.textProperty().get();
            if (!fileName.isEmpty()) {
                File nFile = new File(Dir, fileName);
                Files.createDirectory(Paths.get(Dir.toString(), fileName));
                TreeItem<File> newTreeFile = new TreeItem<>(nFile);
                selectedDirectory.getChildren().add(newTreeFile);

            } else{
                    AlertFunction("Warning", "You haven't inserted a catalog file name!", "Enter a name and try again.");
                    System.out.println("You didn't input a name for a new directory");
                    return;
                }

            }catch(NullPointerException e){
            AlertFunction("Warning!", "You haven't chosen a directory!!","Choose a catalog and try again.");
            System.out.println(e);
            }
        }


    private class FileCell extends TreeCell<File> {
        private FileCell() {
        }

        protected void updateItem(File file, boolean empty) {
            super.updateItem(file, empty);
            if(file != null) {
                if(file.isDirectory()) {
                    this.setText("/" + file.getName());
                } else {
                    this.setText(file.getName() + " " + file.length() / 1024 + "KB");
                }
            } else {
                this.setText((String)null);
            }

        }
    }

    class FileCellFactory implements Callback<TreeView<File>, TreeCell<File>> {
        FileCellFactory() {
        }

        public Controller.FileCell call(TreeView<File> p) {
            return Controller.this.new FileCell();
        }
    }



}
