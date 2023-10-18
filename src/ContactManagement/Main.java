package ContactManagement;

import java.io.*;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application{

  public TableView<Contact> listTableView;
  ImageView imageView = new ImageView();
  class Header extends HBox {

    Header() {
      this.setPrefSize(500, 60);
      this.setStyle("-fx-background-color: #F0F8FF;");

      Text titleText = new Text("Contact Manager"); 
      titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
      this.getChildren().add(titleText);
      this.setAlignment(Pos.CENTER); 

    }
  }

  class Footer extends HBox {

    private Button addButton;
    private Button deleteButton;
    private Button sortButton;
    private Button saveButton;

    Footer() {
      this.setPrefSize(500, 60);
      this.setStyle("-fx-background-color: #F0F8FF;");
      this.setSpacing(15);

      String defaultButtonStyle =
        "-fx-font-style: italic; -fx-background-color: #FFFFFF;  -fx-font-weight: bold; -fx-font: 11 arial;";

      addButton = new Button("Add Contacts"); 
      addButton.setStyle(defaultButtonStyle); 
      deleteButton = new Button("Delete Contacts");
      deleteButton.setStyle(defaultButtonStyle);
      sortButton = new Button("Sort Contacts (By Name)");
      sortButton.setStyle(defaultButtonStyle);
      saveButton = new Button("Save Contacts");
      saveButton.setStyle(defaultButtonStyle);
      this.getChildren()
        .addAll(addButton, deleteButton, sortButton, saveButton); 
      this.setAlignment(Pos.CENTER); 
    }

    public Button getAddButton() {
      return addButton;
    }

    public Button getDeleteButton() {
      return deleteButton;
    }

    public Button getSortButton() {
      return sortButton;
    }
    public Button getSaveButton() {
        return saveButton;
    }
  }

public class Contact extends TableRow<Contact>{
    private ImageView contactImage;
    private TextField name;
    private TextField phone;
    private TextField email;
    public Contact(ImageView contactImage, TextField name, TextField phone, TextField email) {
        this.contactImage = contactImage;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.setPrefSize(500, 20); 
        this.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 0; -fx-font-weight: bold;"); 
    }
    public TextField getName() {
        return this.name;
    }
    public TextField getPhone() {
        return this.phone;
    }
    public TextField getEmail() {
        return this.email;
    }
    public ImageView getContactImage() {
        return this.contactImage;
    }
}

  public static class ContactList {
    private final ObservableList<Contact> contacts = FXCollections.observableArrayList();

    public void addContact(Contact contact) {
        contacts.add(contact);
    }
    public ObservableList<Contact> getContacts() {
        return contacts;
    }
  }
  public class AppFrame extends BorderPane {

    private Header header;
    private Footer footer;
    private ContactList contactList;

    private boolean deleteConfirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete this contact?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }   
    AppFrame() {
        header = new Header();
        footer = new Footer();
        contactList = new ContactList();
        
        this.setTop(header);
        this.setBottom(footer);

        footer.getAddButton().setOnAction(e -> {
            Contact newContact = new Contact(new ImageView(), new TextField(), new TextField(), new TextField());
            contactList.addContact(newContact);
            listTableView.setItems(contactList.getContacts());
        });
        footer.getDeleteButton().setOnAction(event -> {
            Contact selectedContact = listTableView.getSelectionModel().getSelectedItem();
            if (selectedContact != null) {
                boolean confirmed = deleteConfirm();
                if (confirmed) {
                    listTableView.getItems().remove(selectedContact);
                }
            }
        });
        footer.getSortButton().setOnAction(event -> {
            listTableView.getItems().sort((contact1, contact2) -> {
                String name1 = contact1.getName().getText();
                String name2 = contact2.getName().getText();
                if (name1.isEmpty() && name2.isEmpty()) {
                    return 0;  
                } else if (name1.isEmpty()) {
                    return 1;  
                } else if (name2.isEmpty()) {
                    return -1; 
                } else {
                    return name1.compareTo(name2); 
                }            
            });        
            listTableView.refresh();
        });
        footer.getSaveButton().setOnAction(event -> {
            try (FileWriter fw = new FileWriter("/Users/kevinyan/Todo-List-App/src/ContactManagement/contacts.csv");) {
                for (Contact contact : listTableView.getItems()) {
                    String name = contact.getName().getText();
                    String phone = contact.getPhone().getText();
                    String email = contact.getEmail().getText();
                    fw.write(name + "\t" + phone + "\t" + email + "\n");
                }
                fw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }   
        });
    }
}

  public void start(Stage primaryStage) throws Exception {
    AppFrame root = new AppFrame();
    primaryStage.setTitle("Contact Manager");
    FileChooser fileChooser = new FileChooser();
    listTableView = new TableView<>();
    listTableView.setEditable(true); 
    TableColumn<Contact, String> nameColumn = new TableColumn<>("Name");
    TableColumn<Contact, String> emailColumn = new TableColumn<>("Email");
    TableColumn<Contact, String> phoneColumn = new TableColumn<>("Phone Number");
    TableColumn<Contact, Button> imgColumn = new TableColumn<>("Profile Picture");
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    nameColumn.setPrefWidth(180);
    phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
    phoneColumn.setPrefWidth(140);
    emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    emailColumn.setPrefWidth(200);
    imgColumn.setCellFactory(new Callback<TableColumn<Contact,Button>,TableCell<Contact,Button>>() {
        public TableCell<Contact, Button> call(TableColumn<Contact, Button> input) {
            return new TableCell<Contact, Button>() {
                Button uploadImg = new Button("Upload PFP");

                {
                  setAlignment(Pos.CENTER); 
                  uploadImg.setMinWidth(100);
                  uploadImg.setOnAction(event -> {
                    fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
                    File selectedFile = fileChooser.showOpenDialog(primaryStage);
                    if (selectedFile != null) {
                      Image image = new Image(selectedFile.toURI().toString());
                      Contact contact = getTableView().getItems().get(getIndex());
                      contact.getContactImage().setImage(image);
                      contact.getContactImage().setFitWidth(65);
                      contact.getContactImage().setFitHeight(65);
                      uploadImg.setGraphic(contact.getContactImage());
                      uploadImg.setText(""); 
                    }               
                  });    
                }
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                      Contact contact = getTableView().getItems().get(getIndex());
                      if (contact != null) {
                        if (contact.getContactImage() != null) {
                            uploadImg.setGraphic(contact.getContactImage());
                            uploadImg.setText(""); 
                        }
                    }
                        setGraphic(uploadImg);
                    }
                }
            };
        }
    });
    imgColumn.setPrefWidth(100);
    listTableView.getColumns().addAll(imgColumn, nameColumn, phoneColumn, emailColumn);

    root.setCenter(listTableView);
    Scene scene = new Scene(root, 700, 1000);
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
