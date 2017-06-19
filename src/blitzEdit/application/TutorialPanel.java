package blitzEdit.application;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class TutorialPanel extends TitledPane
{
	private ArrayList<TitledPane> panes;
	private Accordion parent;
	private AnchorPane parentAnchor;
	private boolean visible = true;

	public TutorialPanel(Accordion accordion)
	{
		parent = accordion;
		parentAnchor = (AnchorPane)parent.getParent();
	}
	
	public TitledPane[] create()
	{
		panes = new ArrayList<TitledPane>();
		
		String descriptionName = "";
		String description = "";
		
		descriptionName = "Lorem Ipsum Test";
		description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
				+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
				+ " At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,"
				+ " no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,"
				+ " consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,"
				+ " sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,"
				+ " no sea takimata sanctus est Lorem ipsum dolor sit amet.";
		panes.add(buildTitledPane(descriptionName, description));
		
		descriptionName = "2"; //TODO
		description = "Text2";
		panes.add(buildTitledPane(descriptionName, description));
		
		descriptionName = "3"; //TODO
		description = "Text3";
		panes.add(buildTitledPane(descriptionName, description));
		
		descriptionName = "4"; //TODO
		description = "Text4";
		panes.add(buildTitledPane(descriptionName, description));
		
		
		parent.setOnMousePressed(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				if (click.isSecondaryButtonDown())
				{
					initiateRightClickMenu().show(Main.mainStage, click.getScreenX(), click.getScreenY());;
				}
			}
		});
		
		return getPanes();
	}

	public TitledPane[] getPanes()
	{
		return panes.toArray(new TitledPane[]{});
	}
	
	public void toggleVisibilty()
	{
		if(visible) //hide
		{
			for(TitledPane pane : panes)
			{
				pane.setVisible(false);
			}
			AnchorPane anchor = (AnchorPane)parent.getParent();
			anchor.setMinWidth(0);
			anchor.setMaxWidth(1);
			visible = false;
		}
		else //show
		{
			for(TitledPane pane : panes)
			{
				pane.setVisible(true);
			}
			AnchorPane anchor = (AnchorPane)parent.getParent();
			anchor.setMinWidth(250.0);
			anchor.setMaxWidth(250.0);
			visible = true;
		}
	}
	
	private TitledPane buildTitledPane(String name, String content)
	{
		AnchorPane root = new AnchorPane();
		TitledPane pane = new TitledPane(name, null);
		Text text = new Text(content);
		text.setTextAlignment(TextAlignment.LEFT);
		text.setX(20);
		text.setY(20);
		text.setFont(Font.font("verdana", FontWeight.LIGHT, FontPosture.REGULAR, 14));
		TextFlow textFlow = new TextFlow(text);
		
		pane.setOnMouseClicked(event -> {
            if (MouseButton.SECONDARY.equals(event.getButton())) {
            	initiateRightClickMenu().show(Main.mainStage, event.getScreenX(), event.getScreenY());;
            }
        });
		
		pane.setContent(root);
		pane.setContent(textFlow);
		
		return pane;
	}
	
	private ContextMenu initiateRightClickMenu()
	{
		ContextMenu rightClickMenu = new ContextMenu();

		MenuItem hide = new MenuItem("Hide");
		
		hide.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent click)
			{
				toggleVisibilty();
			}
		});

		rightClickMenu.getItems().addAll(hide);
		return rightClickMenu;
	}
	
	
}
