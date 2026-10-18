package vn.devpro.management.model;


public class Book extends BaseEntity {
    private String name;
    private Author author;
    private Category category;
    private int numberOfPages;
    private int quantity;
    private int available;

    public Book(String name, Author author, Category category, int numberOfPages, int quantity) {
        this.name = name;
        this.author = author;
        this.category = category;
        this.numberOfPages = numberOfPages;
        this.quantity = quantity;
        this.available = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAvailable() {
        return available;
    }

    public int getBorrowedCount() {
        return quantity - available;
    }

    /**
     * Lay 1 cuon ra khoi ke (cho muon).
     */
    public void takeCopy() {
        if (available <= 0) throw new IllegalStateException("Het sach tren ke: " + name);
        available--;
    }

    /**
     * Nhan 1 cuon tra ve ke.
     */
    public void returnCopy() {
        if (available >= quantity) throw new IllegalStateException("Sach da du so luong: " + name);
        available++;
    }

    /**
     * Doi tong so luong, giu nguyen so cuon dang cho muon.
     */
    public void changeQuantity(int newQuantity) {
        int borrowed = getBorrowedCount();
        if (newQuantity < borrowed) {
            throw new IllegalArgumentException("So luong moi nho hon so cuon dang cho muon");
        }
        this.quantity = newQuantity;
        this.available = newQuantity - borrowed;
    }
}
