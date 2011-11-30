package wyclipse.builder;

import java.io.*;
import java.util.*;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import wyil.path.Path.*;
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
public class IBinaryRoot implements Root {
	private final IContainer dir;	
		
	/**
	 * Construct a directory root from a given directory and file filter.
	 * 
	 * @param file
	 *            --- location of directory on filesystem.
	 */
	public IBinaryRoot(IContainer dir) {
		this.dir = dir;
	}
	
	public boolean exists(PkgID pkg) throws IOException {
		String pkgname = pkg.toString().replace('.', '/');
		return dir.exists(new Path(pkgname));
	}
	
	public List<Entry> list(PkgID pkg) throws CoreException {
		Path path = new Path(pkg.toString().replace('.','/'));
		IResource member = dir.findMember(path);
		
		if (member.exists() && member instanceof IContainer) {
			IContainer container = (IContainer) member;
			ArrayList<Entry> entries = new ArrayList<Entry>();

			for (IResource file : container.members()) {				
				if(file instanceof IFile && file.getFileExtension().equals("class")) {
					String filename = file.getName();
					String name = filename.substring(0, filename.lastIndexOf('.'));
					ModuleID mid = new ModuleID(pkg, name);
					entries.add(new IEntry(mid, (IFile) file));				
				}
			}

			return entries;
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	
	public Entry lookup(ModuleID mid) {
		Path path = new Path(mid.toString().replace('.', '/'));
		IResource member = dir.findMember(path);

		if (member.exists() && member instanceof IFile) {
			return new IEntry(mid, (IFile) member);
		} else {
			return null; // not found
		}
	}

	public String toString() {
		return dir.toString();
	}
	
	/**
	 * A WFile is a file on the file system which represents a Whiley module. The
	 * file may be encoded in a range of different formats. For example, it may be a
	 * source file and/or a binary wyil file.
	 * 
	 * @author djp
	 * 
	 */
	public static class IEntry implements Entry {
		private final ModuleID mid;
		private final IFile file;		
		
		public IEntry(ModuleID mid, IFile file) {
			this.mid = mid;
			this.file = file;
		}
		
		public ModuleID id() {
			return mid;
		}
		
		public String location() {
			return file.toString();
		}
		
		public long lastModified() {
			return file.getModificationStamp();
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
		
		public InputStream contents() throws Exception {
			return file.getContents();		
		}		
	}	
}