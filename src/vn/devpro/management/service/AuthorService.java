package vn.devpro.management.service;

import java.util.ArrayList;
import java.util.List;

import vn.devpro.management.exception.BusinessException;
import vn.devpro.management.model.Author;
import vn.devpro.management.model.Book;
import vn.devpro.management.model.Person;
import vn.devpro.management.repository.Repository;

public class AuthorService {
    private final Repository<Author> authors;
    private final Repository<Book> books;

    public AuthorService(Repository<Author> authors, Repository<Book> books) {
        this.authors = authors;
        this.books = books;
    }

    public List<Author> findAll() {
        return authors.findAll();
    }

    public List<Author> findAllSortedByName() {
        List<Author> list = new ArrayList<>(authors.findAll());
        list.sort(Person.BY_NAME);
        return list;
    }

    public Author getByCode(String code) {
        return authors.findByCode(code)
                .orElseThrow(() -> new BusinessException("Tac gia khong ton tai: " + code));
    }

    public Author create(String familyName, String givenName) {
        return authors.save(new Author(
                Validation.requireText(familyName, "Ho"),
                Validation.requireText(givenName, "Ten")));
    }


    public Author update(String code, String familyName, String givenName) {
        Author author = getByCode(code);
        author.setFamilyName(Validation.keepIfBlank(familyName, author.getFamilyName()));
        author.setGivenName(Validation.keepIfBlank(givenName, author.getGivenName()));
        return authors.save(author);
    }

    public void delete(String code) {
        Author author = getByCode(code);
        boolean inUse = books.findAll().stream().anyMatch(b -> b.getAuthor().getId() == author.getId());
        if (inUse) {
            throw new BusinessException("Khong the xoa: tac gia dang co sach trong he thong");
        }
        authors.delete(author);
    }
}
