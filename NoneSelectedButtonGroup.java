package origamiProject;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

public class NoneSelectedButtonGroup extends ButtonGroup {

	  /**
	 * 
	 */
	private static final long serialVersionUID = -7522480076297805279L;

	@Override
	  public void setSelected(ButtonModel model, boolean selected) {
	    if (selected) {
	      super.setSelected(model, selected);
	    } else if (getSelection() != model) {
	      clearSelection();
	    }
	  }
	}

