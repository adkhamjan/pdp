package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.*;
import uz.pdp.util.FileUtil;
import uz.pdp.wrapper.CategoryListWrapper;

import java.time.LocalDateTime;
import java.util.*;

public class CategoryService {
    private static final String fileName = "categories.xml";
    private static List<Category> categories;

    @SneakyThrows
    public CategoryService() {
        CategoryListWrapper wrapper = FileUtil.readFromXml(fileName, CategoryListWrapper.class);
        categories = wrapper.getCategories() != null ? wrapper.getCategories() : new ArrayList<>();
    }

    @SneakyThrows
    public static void saveCategories() {
        FileUtil.writeToXml(fileName, new CategoryListWrapper(categories));
    }

    public String addCategory(Category category, UUID id) {
        Category category1 = getByName(category.getName());
        if (category1 != null) {
            return "Mavjud kategoriya";
        }
        Category toCategory = getCategoryById(id);
        if (toCategory != null && (toCategory.getNodeType() == null || toCategory.getNodeType())) {
            toCategory.setNodeType(true);
            category.setParentId(id);
            categories.add(category);
            saveCategories();
            return "Successful \n";
        }
        return "Not found category \n";
    }

    public String addCategory(Category category) {
        categories.add(category);
        saveCategories();
        return "Successful \n";
    }

    private Category getByName(String name) {
        for (Category category : categories) {
            if (category.isActive() && category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    public List<Category> getChildCategoryById(UUID id) {
        List<Category> childCategory = new ArrayList<>();
        for (Category category : categories) {
            if (category.isActive() && category.getParentId() != null && category.getParentId().equals(id)) {
                childCategory.add(category);
            }
        }
        return childCategory;
    }

    public String deleted(UUID id) {
        Category currCategory = getCategoryById(id);
        if (currCategory != null) {
            deletedChild(id);
            saveCategories();
            return "Successful \n";
        }
        return "Not found category \n";
    }

    private void deletedChild(UUID id) {
        for (Category category : categories) {
            if (category.isActive() && category.getParentId() != null && category.getParentId().equals(id)) {
                deletedChild(category.getId());
            }
        }
        ProductService.deletedProductsByCategoryId(id);
        getCategoryById(id).setActive(false);
    }

    public void updateCategory(Category category, UUID categoryId, UUID userId) {
        Category updateCategory = getCategoryById(categoryId);
        if (updateCategory != null) {
            updateCategory.setName(category.getName());
            updateCategory.setUpdatedById(userId);
            updateCategory.setUpdateDate(LocalDateTime.now());
            saveCategories();
        }
    }

    public static Category getCategoryById(UUID id) {
        for (Category category : categories) {
            if (category.isActive() && category.getId().equals(id)) {
                return category;
            }
        }
        return null;
    }

    public List<Category> getALLCategories() {
        List<Category> categoryList = new ArrayList<>();
        for (Category category : categories) {
            if (category.isActive()) {
                categoryList.add(category);
            }
        }
        return categoryList;
    }

    public List<Category> getParentCategories() {
        List<Category> categoryList = new ArrayList<>();
        for (Category category : categories) {
            if (category.isActive() && category.getParentId() == null) {
                categoryList.add(category);
            }
        }
        return categoryList;
    }
}