

import java.awt.BasicStroke;
import java.awt.Color;

public interface ColorSettings {
	public final static BasicStroke CONNECTION_STROKE= new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	public final static BasicStroke LINE_STROKE= new BasicStroke(1,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER);
	
	public final static BasicStroke AREA_STROKE= new BasicStroke(4,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER);
	final static float dash1[] = {2.0f};
	public final static BasicStroke CONDITION_STROKE= new BasicStroke(1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f, dash1, 0.0f);
public final static Color NODE_COLOR=new Color(0,128,192);
public final static Color SELECTED_NODE_COLOR=new Color(0,128,64);
public final static Color SYMMERTY_LINE_COLOR=new Color(255,0,128);
public final static BasicStroke SYMMERTY_LINE_STROKE=new BasicStroke(2);
public final static Color CONDITION_LINE_COLOR=new Color(128,0,255);
public final static Color CREASE_COLOR=new Color(0,0,0);
public final static Color UN_USED_AREA_COLOR=new Color(255,0,0);
}
