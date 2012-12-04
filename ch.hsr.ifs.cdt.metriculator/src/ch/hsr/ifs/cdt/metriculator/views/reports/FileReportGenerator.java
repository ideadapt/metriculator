package ch.hsr.ifs.cdt.metriculator.views.reports;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Bundle;

import ch.hsr.ifs.cdt.metriculator.MetriculatorPluginActivator;
import ch.hsr.ifs.cdt.metriculator.model.AbstractMetric;
import ch.hsr.ifs.cdt.metriculator.model.nodes.AbstractNode;

public abstract class FileReportGenerator {

	public abstract void run() throws Exception;

	/**
	 * @see http://www.mkyong.com/java/how-to-copy-directory-in-java/
	 * */
	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
		}
	}

	IPath export_to_dir;
	Collection<AbstractMetric> metrics;
	AbstractNode root;

	public FileReportGenerator(IPath export_to_dir, AbstractNode root,
			Collection<AbstractMetric> metrics) {
		this.export_to_dir = export_to_dir;
		this.metrics = metrics;
		this.root = root;
	}

	protected void open(IPath filename) {
		File file = new File(filename.toOSString());
		try {
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				String cmd = "rundll32 url.dll,FileProtocolHandler "
						+ file.getCanonicalPath();
				Runtime.getRuntime().exec(cmd);
			} else {
				Desktop.getDesktop().open(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected File getProjectFile(String projectrelativePath) {
		Bundle bundle = MetriculatorPluginActivator.getDefault().getBundle();
		URL resource = bundle.getEntry(projectrelativePath);
		try {
			return new File(FileLocator.resolve(resource).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void writeTo(IPath filename, String content) {
		FileOutputStream fos = null;
		try {
			File file = new File(filename.toOSString());
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			
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
