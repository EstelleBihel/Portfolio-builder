package alt.portfolio.builder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration pour servir les fichiers uploadés (avatars, images projets)
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

	@Value("${app.upload.dir:uploads}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Servir les fichiers du dossier uploads via /uploads/**
		registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadDir + "/");
	}
}