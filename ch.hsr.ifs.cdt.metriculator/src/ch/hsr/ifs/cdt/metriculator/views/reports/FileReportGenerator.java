package ch.hsr.ifs.cdt.metriculator.views.reports;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;

import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Bundle;

import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public abstract class FileReportGenerator {

	public abstract void run() throws Exception;

	public static void copyFolder(String src, IPath dest) {
	
		Bundle bundle = MetriculatorPluginActivator.getDefault().getBundle();
		Enumeration<URL> paths = bundle.findEntries(src, "*.*", true);
		URL path = null;
		while(paths.hasMoreElements() && (path = paths.nextElement()) != null){

			FileOutputStream fos = null;
			InputStream is = null;
			try {
				String relativeSourcePath = path.toURI().getRawPath();
				String relativeDestPath = relativeSourcePath.substring(src.length());

				is = bundle.getEntry(relativeSourcePath).openStream();
				
				File file = createFile(dest.toOSString() + relativeDestPath);
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
					is.close();
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
	
	IPath export_to_dir;
	Collection<AbstractMetric> metrics;
	AbstractNode root;

	public FileReportGenerator(IPath export_to_dir, AbstractNode root, Collection<AbstractMetric> metrics) {
		this.export_to_dir = export_to_dir;
		this.metrics = metrics;
		this.root = root;
	}

	protected void open(IPath filename) {
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
}
