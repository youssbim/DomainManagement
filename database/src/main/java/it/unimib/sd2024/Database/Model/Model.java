package it.unimib.sd2024.Database.Model;

import java.io.File;
import java.io.FileWriter;

import it.unimib.sd2024.Database.DBError;
import it.unimib.sd2024.Database.DBErrorVariant;

public abstract class Model {
	public abstract File write2disk(File dir) throws DBError;

	public File write2disk(File dir, String id) throws DBError {
		if (!dir.exists() && !dir.mkdirs()) {
			throw new DBError(DBErrorVariant.CannotCreateDirectoryStructure);
		}

		File file = new File(dir, id + ".json");

		try {
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			FileWriter writer = new FileWriter(file);
			writer.write(this.toString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			throw new DBError(DBErrorVariant.CannotCreateFile);
		}

		return file;
	}
}
