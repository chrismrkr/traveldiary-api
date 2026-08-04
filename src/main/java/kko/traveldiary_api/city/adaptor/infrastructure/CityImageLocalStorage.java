package kko.traveldiary_api.city.adaptor.infrastructure;

import kko.traveldiary_api.city.application.required.CityImageStoragePort;
import kko.traveldiary_api.city.domain.CityImageNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CityImageLocalStorage implements CityImageStoragePort {
    private final Path baseDir;

    public CityImageLocalStorage(
            @Value("${app.city.image.storage-path:${java.io.tmpdir}/traveldiary/city-images}") String storagePath) {
        this.baseDir = Path.of(storagePath);
    }

    @Override
    public void save(String id, byte[] imageBytes) {
        try {
            Files.createDirectories(baseDir);
            Files.write(resolve(id), imageBytes);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save city image: " + id, e);
        }
    }

    @Override
    public byte[] find(String id) {
        Path path = resolve(id);
        if (!Files.exists(path)) {
            throw new CityImageNotFoundException(id);
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read city image: " + id, e);
        }
    }

    private Path resolve(String id) {
        return baseDir.resolve(Path.of(id).getFileName());
    }
}
