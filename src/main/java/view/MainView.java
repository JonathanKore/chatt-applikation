package view;

import controller.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import model.Conversation;
import model.MainModel;
import model.User;
import model.observerpattern.ModelObserver;

import java.net.URL;
import java.util.*;

/**
 * The MainView is the main class of the view package. Linking all the different views together and forwards info
 * given to the class from the model package via the observer class.
 *
 * @author Filip Andréasson
 * @author Gustav Häger
 * @author Jonathan Köre
 * @author Gustaf Spjut
 * @author Benjamin Vinnerholt
 * @since 2018-09-09
 */

public class MainView extends AnchorPane implements Initializable, IMainView, ModelObserver {

    private MainModel mainModel;
    private ChatView chatView;
    private LoginView loginView;
    private CreateConvoView createConvoView;
    private UserPageView userPage;
    private User detailedUser;
    private UserToolbar userToolbar;
    private CreateUserView createUserView;
    private ContactDetailView contactDetailView;
    private IControllerFactory factory;
    private IParticipantView addParticipantsView;
    private IParticipantView removeParticipantView;
    //mainView
    @FXML
    private
    AnchorPane mainViewAnchorPane;

    @FXML
    private
    FlowPane contactsFlowPane;

    @FXML
    private
    FlowPane conversationsFlowPane;

    @FXML
    private
    HBox mainViewHBox;

    @FXML
    private
    HBox loginHBox;

    @FXML
    private
    HBox createConvoHBox;

    @FXML
    private
    HBox detailViewHBox;

    @FXML
    StackPane mainViewStackPane;

    @FXML
    private
    ImageView currentUserImageView;

    @FXML
    private
    ImageView statusImageView;

    @FXML
    private
    MenuButton statusMenu;

    @FXML
    Button newConvoButton;

    @FXML
    private
    AnchorPane currentUserAnchorPane;

    @FXML
    private
    TextField searchContactsTextField;

    @FXML
    private
    TextField searchConversationsTextField;

    @FXML
    private
    ImageView searchContactsImageView;

    @FXML
    private
    ImageView searchConversationsImageView;

    @FXML
    private
    Label noContactsFoundLabel;

    @FXML
    private
    Label noConversationsFoundLabel;


    /**
     * Initializes the class and loads the views that makes out the complete mainView.
     * Proceeds to show the loginpage to the user
     * Finally it adds itself as an observer to the model
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayLoginPage();
        IMainController controller = factory.getMainController(mainModel, this);
        searchContactsImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controller.searchContactsClicked();
            }
        });

        searchContactsTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                controller.onSearchContactsTextFieldKeyPressed(event);
            }
        });

        searchConversationsImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controller.searchConversationsClicked();
            }
        });

        searchConversationsTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                controller.onSearchConversationsEnterKeyPressed(event);
            }
        });

    }

    public MainView(MainModel mainModel, IControllerFactory factory) {
        this.factory = factory;
        this.mainModel = mainModel;
        IChatController chatController = factory.getChatController(chatView, mainModel);
        ILoginController loginController = factory.getLoginController(loginView, mainModel);
        ICreateConvoController convoController = factory.getCreateConvoController(this, createConvoView, mainModel);
        IUserPageController userPageController = factory.getUserPageController(mainModel);
        ICreateUserViewController createUserViewController = factory.getCreateUserViewController(mainModel, this, createUserView);
        IUserToolbarController userToolbarController = factory.getUserToolBarController(mainModel, this);
        IContactDetailViewController contactDetailViewController = factory.getContactDetailViewController(mainModel, this);
        IAddParticipantsController addParticipantsController = factory.getAddParticipantsController(addParticipantsView, mainModel);
        IRemoveParticipantsController removeParticipantsController = factory.getRemoveParticipantsController(removeParticipantView, mainModel);
        this.chatView = new ChatView(mainModel, this, chatController, addParticipantsController, removeParticipantsController);
        this.loginView = new LoginView(mainModel, loginController);
        this.createConvoView = new CreateConvoView(mainModel, this, convoController);
        this.userPage = new UserPageView(this, mainModel, userPageController);
        this.createUserView = new CreateUserView(this, mainModel, createUserViewController);
        this.userToolbar = new UserToolbar(this, mainModel, userToolbarController);
        this.contactDetailView = new ContactDetailView(this, mainModel, contactDetailViewController);
    }


    /**
     * This method is called whenever the any ModelObservable object calls the method 'notifyObservers'.
     * The method will use a switch case to call the relevant update method(s) in the application.
     *
     * @param updateType is the type of task the update method will perform
     */
    @Override
    public void update(MainModel.UpdateTypes updateType) {
        switch (updateType) {
            case ACTIVE_CONVERSATION:
                chatView.update();
                break;
            case CONTACTS:
                updateContactsList();
                break;
            case CONVERSATIONS:
                updateConversationsList();
                break;
            case INIT:
                displayMainView();
                //Cant be run in Init since there are no conversations yet
                displayChat();
                updateContactsList();
                updateConversationsList();
                userToolbar.init();
                displayCurrentUser();
                setDefaultConversation();
                chatView.init();
                break;
            case USER_INFO:
                updateUserInfoTextFields();
                userToolbar.updateCurrentUserInfo();
                break;
            default:
                break;
        }
        userToolbar.setCurrentUserImageView();
    }

    private void updateUserInfoTextFields() {
        userPage.updateUserInfoTextFields();
    }

    /**
     * Clears the contactFlowPane and fills it with new ContactListItems corresponding to different Users
     */
    private void updateContactsList() {
        contactsFlowPane.getChildren().clear();
        Iterator<User> iterator = mainModel.getContacts();
        while (iterator.hasNext()) {
            contactsFlowPane.getChildren().add(new ContactListItem(iterator.next(), this));
        }
    }

    public void updateContactList(Iterator<User> iterator) {
        contactsFlowPane.getChildren().clear();
        if (!iterator.hasNext()) {
            contactsFlowPane.getChildren().add(noContactsFoundLabel);
        }

        while (iterator.hasNext()) {
            contactsFlowPane.getChildren().add(new ContactListItem(iterator.next(), this));
        }
    }


    /**
     * Clears the conversationsFlowPane and fills it with new ConversationListItems corresponding to
     * different Conversations.
     */
    public void updateConversationsList() {

        conversationsFlowPane.getChildren().clear();
        Iterator<Conversation> iterator = mainModel.getUsersConversations();
        while (iterator.hasNext()) {
            conversationsFlowPane.getChildren().add(new ConversationListItem(iterator.next(), this.mainModel, this));
        }

    }

    public void updateConversationsList(Iterator<Conversation> iterator) {
        conversationsFlowPane.getChildren().clear();
        if (!iterator.hasNext()) {
            conversationsFlowPane.getChildren().add(noConversationsFoundLabel);
        }
        while (iterator.hasNext()) {
            conversationsFlowPane.getChildren().add(new ConversationListItem(iterator.next(), this.mainModel, this));
        }

    }

    @Override
    public void setDefaultConversation() {
        IMainController controller = factory.getMainController(mainModel, this);
        updateConversationsList();
        if (!conversationsFlowPane.getChildren().isEmpty()) {
            controller.setActiveConversation(((ConversationListItem) conversationsFlowPane.getChildren().get(0)).getConversation().getId());
        }
    }

    @Override
    public void displayLoginPage() {
        mainViewHBox.toBack();
        loginHBox.toFront();
        loginHBox.getChildren().clear();
        loginHBox.getChildren().add(loginView);
    }

    public void displayCreateUserView() {
        mainViewAnchorPane.getChildren().clear();
        mainViewAnchorPane.getChildren().add(createUserView);
    }

    @Override
    public void displayCreateConvoPage() {
        createConvoHBox.toFront();
        createConvoHBox.getChildren().clear();
        createConvoHBox.getChildren().add(createConvoView);
        createConvoView.updateCreateConversationLists();
        createConvoView.setMinWidth(mainViewAnchorPane.getWidth());
    }

    @Override
    public void displayContactDetailView(User user) {
        detailViewHBox.getChildren().clear();
        detailViewHBox.getChildren().add(contactDetailView);
        contactDetailView.setDetailedUser(user);
        detailViewHBox.setMinWidth(mainViewAnchorPane.getWidth());
        detailViewHBox.toFront();
    }

    @Override
    public void displayChat() {
        mainViewAnchorPane.getChildren().add(chatView);
    }

    @Override
    public void displayCurrentUser() {
        currentUserAnchorPane.getChildren().clear();
        currentUserAnchorPane.getChildren().add(userToolbar);
    }

    @FXML
    @Override
    public void displayUserPage() {
        mainViewAnchorPane.getChildren().clear();
        mainViewAnchorPane.getChildren().add(userPage);
        userPage.prefWidthProperty().bind(mainViewAnchorPane.widthProperty());
        userPage.prefHeightProperty().bind(mainViewAnchorPane.heightProperty());
        updateUserInfoTextFields();

    }

    @Override
    public void displayMainView() {
        mainViewHBox.toFront();
    }


    public void backToChat() {
        mainViewAnchorPane.getChildren().clear();
        mainViewAnchorPane.getChildren().add(chatView);
    }


    public void updateContactsList(List<User> contacts) {
        contactsFlowPane.getChildren().clear();
        for (User user : contacts) {
            ContactListItem contactListItemView = new ContactListItem(user, this);
            contactsFlowPane.getChildren().add(contactListItemView);
        }
    }

    //TODO try to delete?
    public void updateCurrentUserInfo() {
        currentUserImageView.setImage(new Image(mainModel.getActiveUser().getProfileImagePath()));
        statusImageView.setImage(new Image(mainModel.getActiveUser().getStatusImagePath()));
        statusMenu.setText(mainModel.getActiveUser().getStatus().toString());
    }


    //contactDetailView functionality
    @FXML
    public void contactDetailViewCloseButtonClicked() {
        contactDetailView.toBack();
    }

    @FXML
    public void contactDetailViewCreateConvoButtonClicked() {
        IMainController controller = factory.getMainController(mainModel, this);
        ArrayList<User> users = new ArrayList<>();
        users.add(detailedUser);
        users.add(mainModel.getActiveUser());
        controller.createConversation(users, detailedUser.getFullName());
        contactDetailView.toBack();
        updateConversationsList();
        displayMainView();
    }

    @Override
    public void logout() {
        IMainController controller = factory.getMainController(mainModel, this);
        userToolbar.statusMenu.getItems().clear();
        controller.logout();
        displayLoginPage();
        loginView.clearTextFields();
        chatView.createUserButtonInVisible();
    }

    @FXML
    public void newConvoButtonClicked() {
        displayCreateConvoPage();
    }

    public String getContactSearchString() {
        return searchContactsTextField.getText();
    }

    public String getConversationSearchString() {
        return searchConversationsTextField.getText();
    }


    public void createConvoViewToBack() {
        createConvoHBox.toBack();
    }

    public void setActiveConversation(int id) {
        IMainController controller = factory.getMainController(mainModel, this);
        controller.setActiveConversation(id);
    }
}