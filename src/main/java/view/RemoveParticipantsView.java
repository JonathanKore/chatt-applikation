package view;

import controller.IControllerFactory;
import controller.IRemoveParticipantsController;
import controller.RemoveParticipantsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import model.Conversation;
import model.MainModel;
import model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemoveParticipantsView extends AnchorPane implements IParticipantView {

    MainModel mainModel;
    private IChatView chatView;
    private Conversation conversation;

    @FXML
    private FlowPane participantsFlowPane;

    @FXML
    private TextField searchParticipantsTextField;

    @FXML
    private ImageView searchParticipantsImageView;

    @FXML
    private Label noMatchingParticipantsLabel;

    @FXML
    private FlowPane participantsToRemoveFlowPane;

    @FXML
    private Button removeParticipantsButton;

    public RemoveParticipantsView(MainModel mainModel, IChatView chatView, Conversation conversation, IControllerFactory factory) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ParticipantList.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.mainModel = mainModel;
        this.chatView = chatView;
        this.conversation = conversation;

        IRemoveParticipantsController controller = factory.getRemoveParticipantsController(this, mainModel);

        searchParticipantsImageView.setOnMouseClicked(event -> controller.searchParticipants());
        searchParticipantsTextField.setOnKeyPressed(controller::isEnterPressed);
        removeParticipantsButton.setOnAction(event -> controller.removeParticipants());
    }




    public void showSearchResult(Iterator<User> iterator){
        participantsFlowPane.getChildren().clear();
        if(!iterator.hasNext()) participantsFlowPane.getChildren().add(noMatchingParticipantsLabel);
        while(iterator.hasNext()){
            User next = iterator.next();
            participantsFlowPane.getChildren().add(new ParticipantItem(next, this));
        }
    }

    @Override
    public void showSearch(Iterator<User> usersToShow) {

    }

    @Override
    public String getSearchString(){
        return searchParticipantsTextField.getText();
    }



    @Override
    public void addOrRemoveFromConversation() {

    }

    @Override
    @FXML
    public void closeView() {
        chatView.closeRemoveParticipants();
    }

    @Override
    public void update() {
        this.conversation = mainModel.getActiveConversation();
        participantsFlowPane.getChildren().clear();
        participantsToRemoveFlowPane.getChildren().clear();

        Iterator<User> participants = mainModel.getParticipants(conversation);
        while(participants.hasNext()){
            participantsFlowPane.getChildren().add(new ParticipantItem(participants.next(), this));
        }

    }

    @Override
    public void select(ParticipantItem participantItem) {
        participantsFlowPane.getChildren().remove(participantItem);
        participantsToRemoveFlowPane.getChildren().add(participantItem);
    }

    @Override
    public Iterator<User> getParticipantsToAddOrRemove() {
        ArrayList<User> usersToRemove = new ArrayList<>();

        for(Node userNode : participantsToRemoveFlowPane.getChildren()){
            ParticipantItem userParticipantItem = (ParticipantItem) userNode;
            usersToRemove.add(userParticipantItem.getUser());
        }
        return usersToRemove.iterator();
    }

    @Override
    public Conversation getConversation() {
        return conversation;
    }

}
