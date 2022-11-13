package com.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;

public class ProductSaveHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);
	
	public static void DeleteExtraImagesFromDirectory(Product products) {
		String extraImageDir = "../product-photos/" + products.getId() + "/extras/";
		Path dirPath = Paths.get(extraImageDir);
		
		try {
			Files.list(dirPath).forEach(file -> {
				String fileName = file.toFile().getName();
				
				if(!products.containsImageName(fileName)) {
					try {
						Files.delete(file);
						LOGGER.info("Deleted extra images: " + fileName);
					} catch (Exception e) {
						LOGGER.error("Could not delete extra image: " + fileName);
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			LOGGER.error("Could not list image directory: " + dirPath);
			e.printStackTrace();
		}
	}

	public static void setExistingExtraImagesName(Product products, String[] imageIds, String[] imageNames) {
		if(imageIds == null || imageIds.length == 0) return;
		
		Set<ProductImage> images = new HashSet<>();
		
		for (int i = 0; i < imageIds.length; i++) {
			Integer id = Integer.parseInt(imageIds[i]);
			String name = imageNames[i];
			images.add(new ProductImage(id, name, products));
		}
		
		products.setImages(images);
	}

	public static void setProductDetails(Product products, String[] detailIDs, String[] detailNames, String[] detailValues) {
		if(detailNames == null || detailNames.length == 0) return;
		
		for (int i = 0; i < detailNames.length; i++) {
			String name = detailNames[i];
			String value = detailValues[i];
			Integer id = Integer.parseInt(detailIDs[i]);
			
			if(id != 0) {
				products.addDetail(id, name, value);
			} else if(!name.isEmpty() && !value.isEmpty()) {
				products.addDetail(name, value);
			}
		}
	}

	public static void saveUploadedImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraImageMultipartFiles,
			Product savedProduct) throws IOException {
		
		if (!mainImageMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
			String uploadDir = "../product-photos/" + savedProduct.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);
		}
		
		
		if (extraImageMultipartFiles.length > 0) {
			String uploadDir = "../product-photos/" + savedProduct.getId() + "/extras/";
			for (MultipartFile multipartFile : extraImageMultipartFiles) {
				if (multipartFile.isEmpty()) continue;
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
	}

	public static void setNewExtraImageName(Product products, MultipartFile[] extraImageMultipartFiles) {
		if (extraImageMultipartFiles.length > 0) {
			for (MultipartFile multipartFile : extraImageMultipartFiles) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					
					if(!products.containsImageName(fileName)) {
						products.addExtraImages(fileName);
					}
				}
			}
		}
	}

	public static void setMainImageName(Product products, MultipartFile mainImageMultipartFile) {
		
		if (!mainImageMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
			products.setMainImage(fileName);
		} else {
			if(products.getMainImage().isEmpty()) {
				products.setMainImage(null);
			}
		}
	}

	public static String getRedirectURLtoAffectedUser(Product products) {
		String name = products.getName();
		return "redirect:/products/page/1?sortField=id&sortDir=asc&keyword=" + name;
	}
}
