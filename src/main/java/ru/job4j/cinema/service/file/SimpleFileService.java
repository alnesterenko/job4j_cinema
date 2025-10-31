package ru.job4j.cinema.service.file;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.file.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleFileService implements FileService {

    private final FileRepository fileRepository;

    public SimpleFileService(FileRepository sql2oFileRepository) {
        this.fileRepository = sql2oFileRepository;
    }

    @Override
    public Optional<FileDto> findFileById(int id) {
        Optional<FileDto> optionalFileDto = Optional.empty();
        var fileOptional = fileRepository.findById(id);
        if (fileOptional.isPresent()) {
            var content = readFileAsBytes(fileOptional.get().getPath());
            optionalFileDto = Optional.of(new FileDto(fileOptional.get().getName(), content));
        }
        return optionalFileDto;
    }

    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<FileDto> findAllFiles() {
        List<FileDto> fileDtoList = new ArrayList<>();
        for (File oneFile : fileRepository.findAll()) {
            var content = readFileAsBytes(oneFile.getPath());
            fileDtoList.add(new FileDto(oneFile.getName(), content));
        }
        return fileDtoList;
    }
}
