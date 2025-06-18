package uz.pdp.service;

import lombok.SneakyThrows;
import uz.pdp.model.*;
import uz.pdp.util.FileUtil;

import java.util.*;



public class CategoryService {
    private static final String fileJson = "categories.json";
    private static final String fileXml = "categories.xml";
    private static List<Category> categories;

    @SneakyThrows
    public CategoryService() {
        categories = new ArrayList<>();
        categories = FileUtil.read(fileJson, Category.class);
    }

    public List<CategoryNode> buildTree() {
        Map<UUID, CategoryNode> map = new HashMap<>();
        List<CategoryNode> roots = new ArrayList<>();

        for (Category category : categories) {
            map.put(category.getId(), new CategoryNode(category));
        }

        for (Category category : categories) {
            CategoryNode node = map.get(category.getId());
            if (category.getParentId() == null) {
                roots.add(node);
            } else {
                CategoryNode parent = map.get(category.getParentId());
                if (parent != null){
                    parent.getChildren().add(node);
                }
            }
        }
        return roots;
    }

    @SneakyThrows
    public void saveCategories() {
        FileUtil.write(fileJson, categories);
        FileUtil.writeToXml(fileXml, new CategoryListWrapper(buildTree()));
    }

    public String addCategory(Category category, UUID id) {
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
}