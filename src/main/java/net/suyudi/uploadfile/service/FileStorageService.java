package net.suyudi.uploadfile.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.suyudi.uploadfile.exception.FileStorageException;
import net.suyudi.uploadfile.exception.MyFileNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class FileStorageService {

    @Value("${local.path.file}")
    private String localPath;

    public String storeFile(MultipartFile file) {
        
        try {
            Path targetLocation = Paths.get(localPath).toAbsolutePath().normalize()
                    .resolve(file.getOriginalFilename());
            
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return file.getName();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
        }
    }

    public UrlResource loadFileAsResource(String file) {
        try {
            Path filePath = Paths.get(localPath)
                    .toAbsolutePath()
                    .normalize()
                    .resolve(file)
                    .normalize();

            UrlResource resource = new UrlResource(filePath.toUri());
            
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + file);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + file, ex);
        }
    }
}
