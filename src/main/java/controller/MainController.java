package controller;

/**
 * @author Benjamin Vinnerholt
 */

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.Conversation;
import model.MainModel;
import model.User;
import view.IMainView;

import java.util.Iterator;
import java.util.List;

public class MainController implements IMainController {
    private IMainView mainView;
    private MainModel mainModel;

    public MainController(IMainView mainView, MainModel mainModel) {
        this.mainView = mainView;
        this.mainModel = mainModel;


    }


    @Override
    public void searchContactsClicked() {
        Iterator<User> contacts = mainModel.searchContacts(mainView.getContactSearchString());
        mainView.updateContactLists(contacts);
    }

    @Override
    public void onSearchContactsTextFieldKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            searchContactsClicked();
            event.consume();
        }
    }

    @Override
    public void searchConversationsClicked() {
        Iterator<Conversation> conversations = mainModel.searchConversations(mainView.getConversationSearchString());
        mainView.updateConversationsList(conversations);
    }

    @Override
    public void onSearchConversationsEnterKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            searchConversationsClicked();
            event.consume();
        }
    }

    @Override
    public void onAddNewContactsButtonClicked() {
        mainView.displayAddContactView();
    }

    public void setActiveConversation(int id) {
        mainModel.setActiveConversation(id);
    }

    public void createConversation(List<User> users, String name) {
        mainModel.createConversation(users, name);
    }

    public void logout() {
        mainModel.setActiveUser(null);
    }
}
