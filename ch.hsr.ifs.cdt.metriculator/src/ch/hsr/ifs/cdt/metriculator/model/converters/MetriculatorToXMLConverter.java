package ch.hsr.ifs.cdt.metriculator.model.converters;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.AbstractChecker;
import org.eclipse.cdt.codan.core.model.ICheckersRegistry;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.param.BasicProblemPreference;
import org.eclipse.cdt.codan.core.param.IProblemPreference;
import org.eclipse.cdt.codan.core.param.MapProblemPreference;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class MetriculatorToXMLConverter extends ModelToXMLConverter {

	@Override
	public void convert(AbstractNode node, Collection<AbstractMetric> metrics) {
		super.convert(node, metrics);
		createProblemPreferencesElements(metrics);
	}

	private void createProblemPreferencesElements(Collection<AbstractMetric> metrics) {
		
		if(metrics == null){
			return;
		}
		Node prefElement = xml.propertiesElement.appendChild(xml.doc.createElement("preferences"));
		for(AbstractMetric m : metrics){
			
			Element metricPrefElement = xml.doc.createElement(m.getName().toLowerCase());
			prefElement.appendChild(metricPrefElement);
			
			AbstractChecker checker = m.getChecker();
			for(IProblem problem : getProblemsFor(checker)){
				metricPrefElement.setAttribute("longname", problem.getName());
				metricPrefElement.setAttribute("shortname", m.getName());
				
				Element problemElement = xml.doc.createElement("problem");
				problemElement.setAttribute("message", problem.getMessagePattern());
				metricPrefElement.appendChild(problemElement);
				
				for(IProblemPreference pref : ((MapProblemPreference) problem.getPreference()).getChildDescriptors()){
					if(pref instanceof BasicProblemPreference){
						
						Element problemPrefElement = xml.doc.createElement(pref.getKey());
						Object prefValue = pref.getValue();
						problemPrefElement.setTextContent(prefValue.toString());
						problemElement.appendChild(problemPrefElement);
					}
				}
			}
		}
	}
	
	private Collection<IProblem> getProblemsFor(AbstractChecker checker){
		ICheckersRegistry checkersRegistry = CodanRuntime.getInstance().getCheckersRegistry();
		IProblem[] profileProblems         = checkersRegistry.getWorkspaceProfile().getProblems();
		Collection<IProblem> problems = new ArrayList<IProblem>();
		for(IProblem p : checkersRegistry.getRefProblems(checker)){
			for(IProblem profileProblem : profileProblems){
				if(profileProblem.getId().equalsIgnoreCase(p.getId())){
					problems.add(p);
				}
			}
		}
		return problems;
	}	
}
