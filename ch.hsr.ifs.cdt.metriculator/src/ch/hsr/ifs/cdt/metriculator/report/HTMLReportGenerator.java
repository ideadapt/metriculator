package ch.hsr.ifs.cdt.metriculator.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.IPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.converters.MetriculatorToXMLConverter;
import ch.hsr.ifs.cdt.metriculator.model.converters.MetriculatorXMLDocument;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;
import ch.hsr.ifs.cdt.metriculator.report.views.ConfigurationView;
import ch.hsr.ifs.cdt.metriculator.report.views.FileReportConfigurationView;
import ch.hsr.ifs.cdt.metriculator.report.views.HTMLReportConfigurationView;
import ch.hsr.ifs.cdt.metriculator.report.views.IConfigurableReport;

public class HTMLReportGenerator extends FileReportGenerator implements IConfigurableReport {

	private static final String RESOURCES_BASE_PATH = "/export-resources/reports/html/";
	public static final String CONFIG_REPORT = "report_template";
	public String theme = "simple";
	public String report = "static";
	public HTMLReportGenerator() {
		super();
	}

	@Override
	public void run(ReportConfigurationStore configStore, AbstractNode root, Collection<AbstractMetric> metrics) throws Exception{
		readConfiguration(configStore);
		
		MetriculatorToXMLConverter x = new MetriculatorToXMLConverter();
		x.convert(root, metrics);

		MetriculatorXMLDocument doc = x.getResult();
		injectProperties(doc);
		
		export_to_dir = export_to_dir.append(report);
		copyResourcesTo(export_to_dir);
		
		IPath xmlFilename = export_to_dir.append("model").addFileExtension("xml");
		String xml = x.getFormattedXML();
		writeTo(xmlFilename, xml);
		
		IPath htmlFilename = export_to_dir.append("index").addFileExtension("html");
		StreamSource xmlStream = new StreamSource(new File(xmlFilename.toOSString()));
		StreamSource xslStream = new StreamSource(getProjectFile(RESOURCES_BASE_PATH+report+"/html.xslt"));		
		/*
		 * if u get angry here, try setting your system locale to USA
		 * http://netbeans.org/bugzilla/show_bug.cgi?id=64574
		 * */
		transform(xmlStream, xslStream, new StreamResult(htmlFilename.toOSString()));
		openFileWithDefaultHandler(export_to_dir);
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

	private void copyResourcesTo(IPath export_location) {
		copyBundleFolder(RESOURCES_BASE_PATH + report, export_location);
	}

	@Override
	public List<ConfigurationView> getConfigurationViews(ReportConfigurationStore config) {
		List<ConfigurationView> views = new ArrayList<ConfigurationView>();
		views.add(new FileReportConfigurationView(config));
		views.add(new HTMLReportConfigurationView(config));
		return views;
	}

	public void readConfiguration(ReportConfigurationStore configuration) {
		report = (String) configuration.get(this.getClass(), CONFIG_REPORT, report);
		export_to_dir = (IPath) configuration.get(FileReportGenerator.class, FileReportGenerator.CONFIG_EXPORT_DIR, export_to_dir);
	}
}
