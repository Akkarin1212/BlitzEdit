package tools;

import javafx.scene.Cursor;
import javafx.scene.paint.Color;

/**
 * Container for all graphical design variables used in the programm as colors, line widths and cursors.
 * 
 * @author Christian Gärtner
 */
public class GraphicDesignContainer
{
	
	// selection
	public static final double selected_stroke_width = 5;
	public static final Color selection_rect_color = Color.BLACK;
	public static final Color selected_element_color = Color.DARKGRAY;
	public static final Color selected_connector_color = Color.DODGERBLUE;
	public static final Color selected_line_color = Color.DODGERBLUE;
	
	// connector
	public static final double connector_line_width = 5;
	public static final Color connector_line_color = Color.BLACK;
	public static final Color connector_highlight_color = Color.RED;
	public static final Color connected_connector_color = Color.LIMEGREEN;
	
	// element
	public static final Color elements_color = Color.BLACK;

	// line
	public static final Color line_color = Color.BLACK;
	public static final double line_width = 5;
	
	// canvas grid
	public static double grid_spacing = 25;
	public static final double grid_line_width = 0.1;
	public static final Color grid_color = Color.GRAY;
	
	// circuit canvas
	public static final double zoom_factor = 0.125; // must be between interval (0;0.5)
	
	// cursor
	public static final Cursor move_cursor = Cursor.MOVE;
	public static final Cursor default_cursor = Cursor.DEFAULT;
}
