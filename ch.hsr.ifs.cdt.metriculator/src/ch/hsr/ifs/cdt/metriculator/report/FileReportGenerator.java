package ch.hsr.ifs.cdt.metriculator.report;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public abstract class FileReportGenerator {

	public static final String CONFIG_EXPORT_DIR = "export_path";
	protected IPath export_to_dir = Path.EMPTY;
	Collection<AbstractMetric> metrics;
	AbstractNode root;

	public static void copyBundleFolder(String fromFolderPath, IPath toFolderPath) {
	
		Bundle bundle = MetriculatorPluginActivator.getDefault().getBundle();
		Enumeration<URL> paths = bundle.findEntries(fromFolderPath, "*.*", true);
		URL path = null;
		while(paths.hasMoreElements() && (path = paths.nextElement()) != null){

			FileOutputStream fos = null;
			InputStream is = null;
			try {
				String relativeSourcePath = path.toURI().getRawPath();
				String relativeDestPath = relativeSourcePath.substring(fromFolderPath.length());

				is = bundle.getEntry(relativeSourcePath).openStream();
				
				File file = createFile(toFolderPath.toOSString() + relativeDestPath);
				fos = new FileOutputStream(file);
				
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					fos.write(buffer, 0, length);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					if(is != null)
						is.close();
					if(fos != null)
						fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static File createFile(String filename) {
		File file = new File(filename);
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	protected void openPathWithDefaultHandler(IPath filename) {
		File file = new File(filename.toOSString());
		try {
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
				Runtime.getRuntime().exec(cmd);
			} else {
				Desktop.getDesktop().open(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void selectPathInOSFileBrowser(IPath filename) {
		File file = new File(filename.toOSString());
		try {
			String osname = System.getProperty("os.name").toLowerCase();
			if (osname.contains("windows")) {
				String cmd = "Explorer /select," + file.getCanonicalPath();
				Runtime.getRuntime().exec(cmd);
			} else if (osname.contains("os x")){
				String cmd = "open -R " + file.getCanonicalPath();
				Runtime.getRuntime().exec(cmd);
			} else {
				Desktop.getDesktop().open(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected InputStream getProjectFile(String projectrelativePath) {
		Bundle bundle = MetriculatorPluginActivator.getDefault().getBundle();
		try {
			return bundle.getEntry(projectrelativePath).openStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void writeTo(IPath filename, String content) {
		FileOutputStream fos = null;
		try {
			File file = createFile(filename.toOSString());
			
			fos = new FileOutputStream(file);
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public abstract void run(ReportConfigurationStore configStore, AbstractNode root, Collection<AbstractMetric> metrics) throws Exception;
}
