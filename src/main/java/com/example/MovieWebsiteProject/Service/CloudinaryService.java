package com.example.MovieWebsiteProject.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "Image"));

        return uploadResult.get("secure_url").toString();
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().uploadLarge(
                file.getBytes(), ObjectUtils.asMap("resource_type", "video", "folder", "Video"));
        return uploadResult.get("secure_url").toString();
    }

    public String updateImage(String publicId, MultipartFile file) throws Exception {
        Map updateResult = cloudinary.uploader().upload(
                file.getBytes(), ObjectUtils.asMap("public_id", publicId, "folder", "Image", "overwrite", true));

        return updateResult.get("secure_url").toString();
    }

    public String updateVideo(String publicId, MultipartFile file) throws Exception {
        Map uploadResult = cloudinary.uploader().uploadLarge(
                file.getBytes(), ObjectUtils.asMap(
                        "public_id", publicId, "overwrite", true, "resource_type", "video", "folder", "Video"));
        return uploadResult.get("secure_url").toString();
    }

    public void deleteImages(List<String> publicIds) {
        try {
            Map options = ObjectUtils.asMap("invalidate", true);
            for (String publicId : publicIds) {
                System.out.println("image public id: " + publicId);
                cloudinary.uploader().destroy(publicId, options);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteVideo(String publicId) {
        System.out.println("video public id: " + publicId);
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video", "invalidate", true));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getPublicId(String url) {
        String[] segments = url.split("/");
        String type = segments[segments.length - 2];
        String fileName = segments[segments.length - 1];
        return type + "/" + fileName.substring(0, fileName.lastIndexOf("."));
    }
}
