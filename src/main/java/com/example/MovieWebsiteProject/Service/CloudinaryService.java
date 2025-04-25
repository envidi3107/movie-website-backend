package com.example.MovieWebsiteProject.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "Image"));

        return uploadResult.get("secure_url").toString();
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().uploadLarge(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "video", "folder", "Video"
        ));
        return uploadResult.get("secure_url").toString();
    }

    public String updateImage(String publicId, MultipartFile file) throws Exception {
        Map updateResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("public_id", publicId, "folder", "Image", "overwrite", true));

        return updateResult.get("secure_url").toString();
    }

    public String updateVideo(String publicId, MultipartFile file) throws Exception {
        Map uploadResult = cloudinary.uploader().uploadLarge(file.getBytes(), ObjectUtils.asMap(
                "public_id", publicId, "overwrite", true, "resource_type", "video", "folder", "Video"
        ));
        return uploadResult.get("secure_url").toString();
    }

    public String deleteImage(String publicId) throws IOException {
        Map options = ObjectUtils.asMap("invalidate", true);
        return cloudinary.uploader().destroy(publicId, options).get("result").toString();
    }

    public String deleteVideo(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video")).get("result").toString();
    }
}
