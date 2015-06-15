package com.iConomy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Downloader {

	protected static long lastModified;
	protected static boolean cancelled;

	public Downloader() {
	}

	public static void install(final String location, final String filename) {
		try {
			cancelled = false;
			System.out.println("[iConomy] Downloading Dependencies");
			if (cancelled) {
				return;
			}
			System.out.println("   + " + filename + " downloading...");
			download(location, filename);
			System.out.println("   - " + filename + " finished.");
			System.out.println("[iConomy] Downloading " + filename + "...");
		} catch (final IOException ex) {
			System.out.println("[iConomy] Error Downloading File: " + ex);
		}
	}

	protected static synchronized void download(final String location, final String filename) throws IOException {
		final URLConnection connection = new URL(location).openConnection();
		connection.setUseCaches(false);
		lastModified = connection.getLastModified();
		final int filesize = connection.getContentLength();
		final String destination = "lib" + File.separator + filename;
		final File parentDirectory = new File(destination).getParentFile();

		if (parentDirectory != null) {
			parentDirectory.mkdirs();
		}

		final InputStream in = connection.getInputStream();
		final OutputStream out = new FileOutputStream(destination);

		final byte[] buffer = new byte[65536];
		int currentCount = 0;
		for (;;) {
			if (cancelled) {
				break;
			}

			final int count = in.read(buffer);

			if (count < 0) {
				break;
			}

			out.write(buffer, 0, count);
			currentCount += count;
		}

		in.close();
		out.close();
	}

	public final long getLastModified() {
		return lastModified;
	}
}
