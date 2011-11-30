package wyclipse.builder;

import java.io.*;
import java.util.*;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import wyil.lang.Module;
import wyil.lang.ModuleID;
import wyil.lang.PkgID;

/**
 * A Directory represents a directory on the file system. Using this, we can
 * list items on the path and see what is there.
 * 
 * @author djp
 * 
 */
public class DirectoryRoot implements Path.Root {
	private final IContainer dir;	
	
	/**
	 * Construct a directory root from a given directory and file filter.
	 * 
	 * @param file
	 *            --- location of directory on filesystem.
	 * @param filter
	 *            --- filter which determines what constitutes a valid entry for
	 *            this directory.
	 */
	public DirectoryRoot(IContainer dir) {
		this.dir = dir;
	}
	
	public List<wyil.path.Path.Entry> list(PkgID pkg) throws IOException {
		IPath path = new Path(pkg.fileName().replace('.','/'));
		
		if (dir.exists(path)) {
			ArrayList<wyil.path.Path.Entry> entries = new ArrayList();

			for(IResource resource : dir.members())
			for (File file : location.listFiles(filter)) {
				String filename = file.getName();
				String name = filename.substring(0, filename.lastIndexOf('.'));
				ModuleID mid = new ModuleID(pkg, name);
				entries.add(new Entry(mid, file));
			}

			return entries;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	public String toString() {
		return dir.getPath();
	}
	
	/**
	 * A WFile is a file on the file system which represents a Whiley module. The
	 * file may be encoded in a range of different formats. For example, it may be a
	 * source file and/or a binary wyil file.
	 * 
	 * @author djp
	 * 
	 */
	public static class Entry implements Path.Entry {
		private final ModuleID mid;
		private final java.io.File file;		
		
		public Entry(ModuleID mid, java.io.File file) {
			this.mid = mid;
			this.file = file;
		}
		
		public ModuleID id() {
			return mid;
		}
		
		public String location() {
			return file.getPath();
		}
		
		public long lastModified() {
			return file.lastModified();
		}
		
		public String suffix() {
			String filename = file.getName();
			String suffix = "";
			int pos = filename.lastIndexOf('.');
			if (pos > 0) {
				suffix = filename.substring(pos + 1);
			}
			return suffix;
		}
		
		public InputStream contents() throws IOException {
			return new FileInputStream(file);
		}		
	}	
}