package com.shopme.admin.brand.service;

import com.shopme.admin.brand.exception.BrandNotFoundException;
import com.shopme.admin.brand.repository.BrandRepository;
import com.shopme.admin.utils.FileUploadUtil;
import com.shopme.common.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public List<Brand> listAll() {
        return (List<Brand>) brandRepository.findAll();
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public void save(Brand brand,
                     MultipartFile multipart,
                     RedirectAttributes redirectAttributes) throws IOException {
        String message;
        message = brand.getId() == null ?
                "New Brand has been created!" : "Brand has been updated successfully!";

        if (!multipart.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(multipart.getOriginalFilename()));
            brand.setLogo(originalFilename);
            Brand savedBrand = save(brand); // brand == savedBrand: true
            System.out.println("brand.getId(): " + brand.getId());
            String uploadDir = "../brand-logos/" + savedBrand.getId(); // brand.getId() works as well, because the brand and savedBrand objects are the same.
            FileUploadUtil.saveFile(uploadDir, originalFilename, multipart);
        } else {
            System.out.println("brand.getLogo(): " + brand.getLogo()); // brand.getLogo():
            // In the create mode, if the user does not upload a new file, photos field will sent as
            // empty string "", not null, because of <input type="hidden" th:field="*{photos}"> in the
            // brand_form, and that will cause a constraint violation in getLogoPath() method in
            // Brand class. So we must set photos to default.png.
            if (brand.getLogo().isEmpty()) // not needed, just for the sake of completeness, as the image field is not nullable
                brand.setLogo("default.png");
            save(brand);
        }

        redirectAttributes.addFlashAttribute("message", message);
    }

    public Brand findById(Integer id) throws BrandNotFoundException {
        return brandRepository.findById(id).orElseThrow(
                () -> new BrandNotFoundException("Could not find any brand with ID: " + id)
        );
    }

    public void delete(int id) throws BrandNotFoundException {
        Integer count = brandRepository.countById(id);
        if (count == 0)
            throw new BrandNotFoundException("Could not find any brand with ID: " + id);

        brandRepository.deleteById(id);
        FileUploadUtil.removeDir("../brand-logos/" + id);
    }

    public boolean checkUniqueName(String name, Integer id) {
        Brand byName  = brandRepository.findByName(name);
        if (byName == null)
            return true;

        // Check if the name belongs to the edited brand (i.e. user doesn't need to change the brand name)
        // If new brand case, returns false as the id param is null
        return byName.getId().equals(id);
    }
}
