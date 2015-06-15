package com.iConomy.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Easy File Management Class
 *
 * @copyright Copyright AniGaiku LLC (C) 2010-2011
 * @author Nijikokun <nijikokun@gmail.com>
 *
 *         This program is free software: you can redistribute it and/or modify it under the terms
 *         of the GNU General Public License as published by the Free Software Foundation, either
 *         version 2 of the License, or (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *         without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *         See the GNU General Public License for more details.
 *
 *         You should have received a copy of the GNU General Public License along with this
 *         program. If not, see <http://www.gnu.org/licenses/>.
 */
public final class FileManager {

	private String directory = "";
	private String file = "";
	private String source = "";
	private final LinkedList<String> lines = new LinkedList<>();

	public FileManager(final String directory, final String file, final boolean create) {
		this.directory = directory;
		this.file = file;

		if (create) {
			this.existsCreate();
		}
	}

	public String getSource() {
		return source;
	}

	public LinkedList<String> getLines() {
		return lines;
	}

	public String getDirectory() {
		return directory;
	}

	public String getFile() {
		return file;
	}

	public void setFile(final String file) {
		this.file = file;
	}

	public void setDirectory(final String directory) {
		this.directory = directory;
	}

	private void log(final Level level, final Object message) {
		Logger.getLogger("FileManager").log(level, null, message);
	}

	public boolean exists() {
		return this.exists(this.directory, this.file);
	}

	public boolean exists(final String directory, final String file) {
		return new File(directory, file).exists();
	}

	public void existsCreate() {
		this.existsCreate(this.directory, this.file);
	}

	public void existsCreate(final String directory, final String file) {
		if (!new File(directory).exists()) {
			if (!new File(directory, file).exists()) {
				this.create(directory, file);
			} else {
				this.createDirectory(directory);
			}
		}
	}

	public boolean delete() {
		return new File(directory, file).delete();
	}

	public boolean create() {
		return this.create(this.directory, this.file);
	}

	public boolean create(final String directory, final String file) {
		if (new File(directory).mkdir()) {
			try {
				if (new File(directory, file).createNewFile()) {
					return true;
				}
			} catch (final IOException ex) {
				this.log(Level.SEVERE, ex);
			}
		}

		return false;
	}

	public boolean createDirectory(final String directory) {
		if (new File(directory).mkdir()) {
			return true;
		}

		return false;
	}

	public boolean read() {
		return this.read(this.directory, this.file);
	}

	public boolean read(final String directory, final String file) {
		BufferedReader input;
		String line;

		try {
			input = new BufferedReader(new FileReader(new File(directory, file)));

			try {
				this.source = input.readLine();

				while ((line = input.readLine()) != null) {
					this.lines.add(line);
				}
			} catch (final IOException ex) {
				this.log(Level.SEVERE, ex);

				return false;
			}

			return true;
		} catch (final FileNotFoundException ex) {
			this.log(Level.SEVERE, ex);
		}

		return false;
	}

	public boolean write(final Object data) {
		return this.write(this.directory, this.file, new Object[] { data });
	}

	public boolean write(final String directory, final String file, final Object[] lines) {
		BufferedWriter output;

		this.existsCreate(directory, file);

		try {
			output = new BufferedWriter(new FileWriter(new File(directory, file)));

			try {
				for (final Object line : lines) {
					output.write(String.valueOf(line));
				}
			} catch (final IOException ex) {
				this.log(Level.SEVERE, ex);
				output.close();
				return false;
			}

			output.close();
			return true;
		} catch (final FileNotFoundException ex) {
			this.log(Level.SEVERE, ex);
		} catch (final IOException ex) {
			this.log(Level.SEVERE, ex);
		}

		return false;
	}
}
