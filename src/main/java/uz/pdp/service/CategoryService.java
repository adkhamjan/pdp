package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.*;
import uz.pdp.util.FileUtil;
import uz.pdp.wrapper.CategoryListWrapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        Optional<Category> optional = getByName(category.getName());
        if (optional.isPresent()) {
            return "Mavjud kategoriya";
        }
        Optional<Category> optionalCategory = getCategoryById(id);
        if (optionalCategory.isPresent()) {
            Category toCategory = optionalCategory.get();
            if (toCategory.getNodeType() == null || toCategory.getNodeType()) {
                toCategory.setNodeType(true);
                category.setParentId(id);
                categories.add(category);
                saveCategories();
                return "Successful \n";
            }
        }
        return "Not found category \n";
    }

    public String addCategory(Category category) {
        categories.add(category);
        saveCategories();
        return "Successful \n";
    }

    private Optional<Category> getByName(String name) {
//        for (Category category : categories) {
//            if (category.isActive() && category.getName().equals(name)) {
//                return category;
//            }
//        }
//        return null;
        return categories.stream().filter(category -> category.getName().equals(name) && category.isActive())
                .findFirst();
    }

    public List<Category> getChildCategoryById(UUID id) {
//        List<Category> childCategory = new ArrayList<>();
//        for (Category category : categories) {
//            if (category.isActive() && category.getParentId() != null && category.getParentId().equals(id)) {
//                childCategory.add(category);
//            }
//        }
//        return childCategory;
        return categories.stream().filter(category -> category.isActive() && category.getParentId() != null && category.getParentId().equals(id))
                .collect(Collectors.toList());
    }

    public String deleted(UUID id) {
        Optional<Category> optionalCategory = getCategoryById(id);
        if (optionalCategory.isPresent()) {
            deletedChild(optionalCategory.get());
            saveCategories();
            return "Successful \n";
        }
        return "Not found category \n";
    }

    private void deletedChild(Category currCategory) {
        List<Category> children = categories.stream().filter(c -> c.isActive() &&
                        c.getParentId() != null && c.getParentId().equals(currCategory.getId())).toList();

        if (!children.isEmpty()) {
            children.forEach(this::deletedChild);
        } else {
            ProductService.deletedProductsByCategoryId(currCategory.getId());
        }
        currCategory.setActive(false);
    }

    public void updateCategory(Category category, UUID categoryId, UUID userId) {
        Optional<Category> optionalCategory = getCategoryById(categoryId);
        if (optionalCategory.isPresent()) {
            Category updateCategory = optionalCategory.get();
            updateCategory.setName(category.getName());
            updateCategory.setUpdatedById(userId);
            updateCategory.setUpdateDate(LocalDateTime.now());
            saveCategories();
        }
    }

    public static Optional<Category> getCategoryById(UUID id) {
//        for (Category category : categories) {
//            if (category.isActive() && category.getId().equals(id)) {
//                return category;
//            }
//        }
//        return null;
        return categories.stream().filter(category -> category.isActive() && category.getId().equals(id))
                .findFirst();
    }

    public List<Category> getALLCategories() {
//        List<Category> categoryList = new ArrayList<>();
//        for (Category category : categories) {
//            if (category.isActive()) {
//                categoryList.add(category);
//            }
//        }
//        return categoryList;
        return categories.stream().filter(BaseModel::isActive).collect(Collectors.toList());
    }

    public List<Category> getParentCategories() {
//        List<Category> categoryList = new ArrayList<>();
//        for (Category category : categories) {
//            if (category.isActive() && category.getParentId() == null) {
//                categoryList.add(category);
//            }
//        }
//        return categoryList;
        return categories.stream().filter(category -> category.isActive() && category.getParentId() == null)
                .collect(Collectors.toList());
    }
}