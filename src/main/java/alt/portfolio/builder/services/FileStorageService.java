package alt.portfolio.builder.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	@Value("${app.upload.dir:uploads}")
	private String uploadDir;

	/**
	 * Sauvegarde un fichier uploadé et retourne le chemin relatif
	 */
	public String storeFile(MultipartFile file, String subFolder) throws IOException {
		// Créer le dossier si nécessaire
		Path uploadPath = Paths.get(uploadDir, subFolder);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// Générer un nom unique pour éviter les collisions
		String originalFilename = file.getOriginalFilename();
		String extension = "";
		if (originalFilename != null && originalFilename.contains(".")) {
			extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		String newFilename = UUID.randomUUID().toString() + extension;

		// Sauvegarder le fichier
		Path filePath = uploadPath.resolve(newFilename);
		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		// Retourner le chemin relatif pour stockage en BDD
		return "/uploads/" + subFolder + "/" + newFilename;
	}

	/**
	 * Supprime un fichier
	 */
	public boolean deleteFile(String relativePath) {
		if (relativePath == null || relativePath.isEmpty()) {
			return false;
		}
		try {
			// Convertir le chemin relatif en chemin absolu
			String filename = relativePath.replace("/uploads/", "");
			Path filePath = Paths.get(uploadDir, filename);
			return Files.deleteIfExists(filePath);
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Vérifie si le fichier est une image valide
	 */
	public boolean isValidImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return false;
		}
		String contentType = file.getContentType();
		return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png")
				|| contentType.equals("image/gif") || contentType.equals("image/webp"));
	}

	/**
	 * Vérifie la taille du fichier (max 5MB par défaut)
	 */
	public boolean isValidSize(MultipartFile file, long maxSizeInBytes) {
		return file != null && file.getSize() <= maxSizeInBytes;
	}
}