package ch.hsr.ifs.cdt.metriculator.views.reports;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.converters.MetriculatorToXMLConverter;
import ch.hsr.ifs.cdt.metriculator.model.converters.MetriculatorXMLDocument;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public class HTMLReportGenerator extends FileReportGenerator {

	public String theme = "simple";
	public String report = "static";
	
	public HTMLReportGenerator(Path export_to_dir, AbstractNode root, Collection<AbstractMetric> metrics) {
		super(export_to_dir, root, metrics);
	}

	@Override
	public void run() throws Exception{
		MetriculatorToXMLConverter x = new MetriculatorToXMLConverter();
		x.convert(root, metrics);

		MetriculatorXMLDocument doc = x.getResult();
		injectProperties(doc);
		
		// maybe was set by the user, e.g. in a wizard
		if (export_to_dir.isEmpty()) {
			export_to_dir = Path.fromOSString(System.getProperty("user.home")).append("metriculator-export");
		}
		
		export_to_dir = export_to_dir.append(report);
		IPath htmlFilename = export_to_dir.append("index").addFileExtension("html");
		IPath xmlFilename = export_to_dir.append("model").addFileExtension("xml");
		String xml = x.getFormattedXML();
		writeTo(xmlFilename, xml);
		StreamSource xmlStream = new StreamSource(new File(xmlFilename.toOSString()));
		StreamSource xslStream = new StreamSource(getProjectFile("export-resources/reports/html/"+report+"/html.xslt"));
		
		copyResourcesTo(export_to_dir);
		/*
		 * if u get angry here, try setting your system locale to USA's
		 * http://netbeans.org/bugzilla/show_bug.cgi?id=64574
		 * */
		transform(xmlStream, xslStream, new StreamResult(htmlFilename.toOSString()));
		open(export_to_dir);
	}

	private void injectProperties(MetriculatorXMLDocument xml) {
		Node propEl = xml.propertiesElement;

		Element themeEl = xml.doc.createElement("theme");
		themeEl.setAttribute("name", theme);
		propEl.appendChild(themeEl);
	}

	private void transform(StreamSource xmlStream, StreamSource xslStream, StreamResult result)  {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Templates template = factory.newTemplates(xslStream);
			Transformer transformer = template.newTransformer();

			transformer.transform(xmlStream, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private void copyResourcesTo(IPath export_location) throws IOException {
		File source = getProjectFile("export-resources/reports/html/" + report);
		File dest = new File(export_location.toOSString());
		copyFolder(source, dest);
	}
}
