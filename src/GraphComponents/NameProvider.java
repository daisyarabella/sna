package GraphComponents;

import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

import GraphComponents.LabelledNode;

public class NameProvider extends Object implements VertexNameProvider {
	public String getVertexName(Object node) {
		if (((LabelledNode) node).getAdoptedStatus()) {
			return "adopted";
		}
		else
			return "not-adopted";
	}
}
