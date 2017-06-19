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

/**
 * Used for displaying a quick user guide to get to know the interface.
 * @author Christian Gärtner
 */
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
		
		descriptionName = "BlitzEdit HelpGuide";
		description = "This guide shall help you to use BlitzEdit with all its excellent features."
				+ "\n\nExpand the other tabs below to learn more."
				+ "\n\nTo get started drag an element from the library on the left to the editor pane."
				+ "\nYou can always create a new circuit by going to 'File>New' or pressing 'Strg+N'."
				+ "\n\nTo hide this window right click on it and select 'hide' or go to view and select 'Toggle Tutorial'.";
		panes.add(buildTitledPane(descriptionName, description));
		
		descriptionName = "Working with elements"; //TODO
		description = "Select:"
				+ "\n - Click on the element"
				+ "\n - Hold your left mousebutton and move it over elements to select one or more elements"
				+ "\n - When elements are already selected add an element to the selection by selecting it with 'Shift+left click'"
				+ "\n - Press 'Strg+A' to select all elements"
				+ "\n\nRotate:"
				+ "\n - Right click on the element and select 'rotate'"
				+ "\n - Select an element and scroll up or down to rotate left or right"
				+ "\n\nCopy & Paste:"
				+ "\n - Through right click menu on element"
				+ "\n - Select element(s) and press 'Strg+C' and 'Strg+V'";
		panes.add(buildTitledPane(descriptionName, description));
		
		descriptionName = "Working with connections"; //TODO
		description = "Color code:"
				+ "\nBlue:\t\tSelected link"
				+ "\nRed:\t\tConnectable links"
				+ "\nGreen:\tConnected links"
				+ "\n\nConnect two elements:"
				+ "\nClick on the link of an element to select it and select the link of another element to connect them."
				+ "\nClick on another link to create several connections at once."
				+ "\nClick somewhere else to deselect the link."
				+ "\n\nDelete a connection:"
				+ "\nSelect the links of a connection one after the other."
				+ "\n\nMove a link:"
				+ "\nDrag and drop a selected link to move it along the axis of the element.";
		panes.add(buildTitledPane(descriptionName, description));
		
		descriptionName = "Adding components"; //TODO
		description = "To existing library:"
				+ "\nSelect a library you want to extend."
				+ "\nGo to 'File>Import>Component' and select a xml file from the file system."
				+ "\n\nTo a new library:"
				+ "\nGo to 'File>Import>Library' and select a folder with xml file(s) from the file system.";
		panes.add(buildTitledPane(descriptionName, description));
		
		descriptionName = "Miscellaneous"; //TODO
		description = "Toggle grid:"
				+ "\nBy default the elements will be aligned to the grid."
				+ "\nTo turn this off go to 'View>Toggle Grid'"
				+ "\n\nZoom In/Out:"
				+ "\n - Click on the editor pane and scroll up or down to zoom in or out."
				+ "\n - Go to 'View>Zoom In' or 'View>Zoom Out'.";
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
