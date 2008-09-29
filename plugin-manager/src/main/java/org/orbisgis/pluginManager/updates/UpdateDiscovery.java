package org.orbisgis.pluginManager.updates;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.pluginManager.updates.persistence.Update;
import org.orbisgis.pluginManager.updates.persistence.UpdateSite;
import org.orbisgis.utils.FileUtils;

public class UpdateDiscovery {

	private URL updateSite;

	public UpdateDiscovery(URL updateSite) {
		this.updateSite = updateSite;
	}

	public UpdateInfo[] getAvailableUpdatesInfo(String currentVersion)
			throws IOException, JAXBException {
		return getAvailableUpdatesInfo(updateSite, currentVersion);
	}

	UpdateInfo[] getAvailableUpdatesInfo(URL updateSiteURL,
			String currentVersion) throws IOException, JAXBException {
		URL updateDescriptorURL = new URL(updateSiteURL.toExternalForm() + "/"
				+ UpdateUtils.SITE_UPDATES_FILE_NAME);
		File updateSiteFile = File.createTempFile("site-updates", ".xml");

		// create or modify update content
		JAXBContext context = JAXBContext.newInstance(UpdateSite.class
				.getPackage().getName());
		UpdateSite us = null;
		FileUtils.download(updateDescriptorURL, updateSiteFile);
		us = (UpdateSite) context.createUnmarshaller()
				.unmarshal(updateSiteFile);
		List<Update> updateList = us.getUpdate();
		ArrayList<UpdateInfo> ret = new ArrayList<UpdateInfo>();
		for (int i = 0; i < updateList.size(); i++) {
			Update update = updateList.get(i);
			if (compare(update.getVersionNumber(), currentVersion) > 0) {
				URL updateURL = new URL(updateSiteURL.toExternalForm()
						+ "/"
						+ UpdateUtils.getUpdateFileName(update
								.getVersionNumber()));
				ret.add(new UpdateInfo(updateURL, update));
			}
		}

		return ret.toArray(new UpdateInfo[0]);
	}

	int compare(String version1, String version2) {
		String[] numbers1 = version1.split("\\Q.\\E");
		String[] numbers2 = version2.split("\\Q.\\E");
		if (numbers2.length < numbers1.length) {
			numbers2 = complete(numbers2, numbers1.length);
		}
		if (numbers1.length < numbers2.length) {
			numbers1 = complete(numbers1, numbers2.length);
		}
		for (int i = 0; i < numbers2.length; i++) {
			int comparison = numbers1[i].compareTo(numbers2[i]);
			if (comparison != 0) {
				return comparison;
			}
		}

		return 0;
	}

	private String[] complete(String[] numbers, int newSize) {
		String[] ret = new String[newSize];
		System.arraycopy(numbers, 0, ret, 0, numbers.length);
		for (int i = numbers.length; i < ret.length; i++) {
			ret[i] = "0";
		}
		return ret;
	}

	public void download(UpdateInfo updateInfo) throws IOException,
			NoSuchAlgorithmException {
		File zip = File.createTempFile("orbisgis-update", ".zip");
		FileUtils.download(updateInfo.getFileURL(), zip);
		File md5 = File.createTempFile("orbisgis-update", ".zip.md5");
		FileUtils.download(new URL(updateInfo.getFileURL() + ".md5"), md5);

		// verify checksum
		byte[] readMD5 = FileUtils.getContent(md5);
		byte[] calculatedMD5 = FileUtils.getMD5(zip);
		if (readMD5.length != calculatedMD5.length) {
			throw new IOException("md5 checksum failed");
		} else {
			for (int i = 0; i < calculatedMD5.length; i++) {
				if (readMD5[i] != calculatedMD5[i]) {
					throw new IOException("md5 checksum failed");
				}
			}
		}
	}

}
