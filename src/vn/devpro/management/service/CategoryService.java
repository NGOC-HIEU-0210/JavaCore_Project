package vn.devpro.management.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import vn.devpro.management.exception.BusinessException;
import vn.devpro.management.model.Book;
import vn.devpro.management.model.Category;
import vn.devpro.management.repository.Repository;

public class CategoryService {
    private final Repository<Category> categories;
    private final Repository<Book> books;

    public CategoryService(Repository<Category> categories, Repository<Book> books) {
        this.categories = categories;
        this.books = books;
    }

    public List<Category> findAll() {
        return categories.findAll();
    }

    public List<Category> findAllSortedByName() {
        List<Category> list = new ArrayList<>(categories.findAll());
        list.sort(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public Category getByCode(String code) {
        return categories.findByCode(code)
                .orElseThrow(() -> new BusinessException("Danh muc khong ton tai: " + code));
    }

    public Category create(String name) {
        return categories.save(new Category(Validation.requireText(name, "Ten danh muc")));
    }


    public Category update(String code, String name) {
        Category category = getByCode(code);
        category.setName(Validation.keepIfBlank(name, category.getName()));
        return categories.save(category);
    }

    public void delete(String code) {
        Category category = getByCode(code);
        boolean inUse = books.findAll().stream().anyMatch(b -> b.getCategory().getId() == category.getId());
        if (inUse) {
            throw new BusinessException("Khong the xoa: danh muc dang co sach");
        }
        categories.delete(category);
    }
}
