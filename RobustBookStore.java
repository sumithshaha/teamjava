import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

public class RobustBookStore {
    // BookDTO class
    static class BookDTO {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private BigDecimal price;

        // Constructor
        public BookDTO(Long id, String title, String author, String isbn, BigDecimal price) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.isbn = isbn;
            this.price = price;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    // BookDAO interface
    interface BookDAO {
        BookDTO getBook(Long id);
        List<BookDTO> getAllBooks();
        void saveBook(BookDTO book);
        void updateBook(BookDTO book);
        void deleteBook(Long id);
    }

    // BookDAOImpl class
    static class BookDAOImpl implements BookDAO {
        private List<BookDTO> books = new ArrayList<>();
        private AtomicLong idCounter = new AtomicLong();

        @Override
        public BookDTO getBook(Long id) {
            return books.stream()
                    .filter(book -> book.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<BookDTO> getAllBooks() {
            return new ArrayList<>(books);
        }

        @Override
        public void saveBook(BookDTO book) {
            book.setId(idCounter.incrementAndGet());
            books.add(book);
        }

        @Override
        public void updateBook(BookDTO book) {
            for (int i = 0; i < books.size(); i++) {
                if (books.get(i).getId().equals(book.getId())) {
                    books.set(i, book);
                    return;
                }
            }
        }

        @Override
        public void deleteBook(Long id) {
            books.removeIf(book -> book.getId().equals(id));
        }
    }

    // Main BookStore functionality
    private static BookDAO bookDAO = new BookDAOImpl();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Starting RobustBookStore application...");

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add a new book");
            System.out.println("2. View all books");
            System.out.println("3. Update a book");
            System.out.println("4. Delete a book");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");

            int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    addNewBook();
                    break;
                case 2:
                    viewAllBooks();
                    break;
                case 3:
                    updateBook();
                    break;
                case 4:
                    deleteBook();
                    break;
                case 5:
                    System.out.println("Exiting RobustBookStore application. Goodbye.");
                    return;
            }
        }
    }

    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.printf("Please enter a number between %d and %d: ", min, max);
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.nextLine(); // Consume invalid input
            }
        }
    }

    private static BigDecimal getBigDecimalInput() {
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid price: ");
            }
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static void addNewBook() {
        System.out.println("\nAdding a new book:");
        String title = getStringInput("Enter title: ");
        String author = getStringInput("Enter author: ");
        String isbn = getStringInput("Enter ISBN: ");

        System.out.print("Enter price: ");
        BigDecimal price = getBigDecimalInput();

        BookDTO newBook = new BookDTO(null, title, author, isbn, price);
        bookDAO.saveBook(newBook);

        System.out.println("New book added successfully: " + newBook.getTitle());
    }

    private static void viewAllBooks() {
        List<BookDTO> allBooks = bookDAO.getAllBooks();
        System.out.println("\nAll books:");
        if (allBooks.isEmpty()) {
            System.out.println("No books in the store yet.");
        } else {
            for (BookDTO book : allBooks) {
                System.out.println(book.getId() + ": " + book.getTitle() + " by " + book.getAuthor() + " - $" + book.getPrice());
            }
        }
    }

    private static void updateBook() {
        System.out.print("Enter the ID of the book to update: ");
        Long id = (long) getIntInput(1, Integer.MAX_VALUE);

        BookDTO book = bookDAO.getBook(id);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        System.out.println("Updating book: " + book.getTitle());
        System.out.println("Press Enter to keep the current value, or enter a new value.");

        String title = getStringInput("Enter new title (current: " + book.getTitle() + "): ");
        if (!title.isEmpty()) {
            book.setTitle(title);
        }

        String author = getStringInput("Enter new author (current: " + book.getAuthor() + "): ");
        if (!author.isEmpty()) {
            book.setAuthor(author);
        }

        String isbn = getStringInput("Enter new ISBN (current: " + book.getIsbn() + "): ");
        if (!isbn.isEmpty()) {
            book.setIsbn(isbn);
        }

        String priceStr = getStringInput("Enter new price (current: $" + book.getPrice() + "): ");
        if (!priceStr.isEmpty()) {
            book.setPrice(new BigDecimal(priceStr));
        }

        bookDAO.updateBook(book);
        System.out.println("Book updated successfully.");
    }

    private static void deleteBook() {
        System.out.print("Enter the ID of the book to delete: ");
        Long id = (long) getIntInput(1, Integer.MAX_VALUE);

        BookDTO book = bookDAO.getBook(id);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        bookDAO.deleteBook(id);
        System.out.println("Book deleted successfully: " + book.getTitle());
    }
}