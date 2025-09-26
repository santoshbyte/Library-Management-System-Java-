import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryMS_VMAX 
{

public static void main(String[] args) 
{
    Library lib = Library.loadFromFile("library.dat").orElseGet(Library::new);

    Scanner sc = new Scanner(System.in);
    boolean running = true;

    while (running) 
    {
        System.out.println("\n=== Library Management ===");
        System.out.println("1. Add Book");
        System.out.println("2. Remove Book");
        System.out.println("3. Register User");
        System.out.println("4. Remove User");
        System.out.println("5. Search Books");
        System.out.println("6. List All Books");
        System.out.println("7. List Available Books");
        System.out.println("8. Issue Book");
        System.out.println("9. Return Book");
        System.out.println("10. List Users");
        System.out.println("11. List Issued Books");
        System.out.println("12. Save Library");
        System.out.println("0. Exit");
        System.out.print("Choice: ");

        String choice = sc.nextLine().trim();
        try {
            switch (choice) {
                case "1": addBookInteractive(lib, sc); break;
                case "2": removeBookInteractive(lib, sc); break;
                case "3": registerUserInteractive(lib, sc); break;
                case "4": removeUserInteractive(lib, sc); break;
                case "5": searchBooksInteractive(lib, sc); break;
                case "6": lib.listAllBooks().forEach(System.out::println); break;
                case "7": lib.listAvailableBooks().forEach(System.out::println); break;
                case "8": issueBookInteractive(lib, sc); break;
                case "9": returnBookInteractive(lib, sc); break;
                case "10": lib.listUsers().forEach(System.out::println); break;
                case "11": lib.listIssuedBooks().forEach(System.out::println); break;
                case "12":
                    lib.saveToFile("library.dat");
                    System.out.println("Library saved.");
                    break;
                case "0":
                    lib.saveToFile("library.dat");
                    running = false;
                    System.out.println("Exiting. Library saved to library.dat");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (LibraryException le) {
            System.out.println("Error: " + le.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
    sc.close();
}

static void addBookInteractive(Library lib, Scanner sc) {
    System.out.print("Title: ");
    String title = sc.nextLine().trim();
    System.out.print("Author: ");
    String author = sc.nextLine().trim();
    System.out.print("Publisher: ");
    String publisher = sc.nextLine().trim();
    System.out.print("Year (int): ");
    int year = readInt(sc);
    System.out.print("ISBN: ");
    String isbn = sc.nextLine().trim();

    Book b = lib.addBook(title, author, publisher, year, isbn);
    System.out.println("Added: " + b);
}

static void removeBookInteractive(Library lib, Scanner sc) {
    System.out.print("Book ID to remove: ");
    int id = readInt(sc);
    lib.removeBook(id);
    System.out.println("Removed book id " + id);
}

static void registerUserInteractive(Library lib, Scanner sc) {
    System.out.print("Name: ");
    String name = sc.nextLine().trim();
    System.out.print("Email: ");
    String email = sc.nextLine().trim();
    Role role = Role.MEMBER;
    System.out.print("Role (MEMBER/LIBRARIAN) [default MEMBER]: ");
    String r = sc.nextLine().trim();
    if (!r.isEmpty()) {
        try { role = Role.valueOf(r.toUpperCase()); } catch (Exception ignored) {}
    }
    User u = lib.registerUser(name, email, role);
    System.out.println("Registered: " + u);
}

    static void removeUserInteractive(Library lib, Scanner sc) {
        System.out.print("User ID to remove: ");
        int id = readInt(sc);
        lib.removeUser(id);
        System.out.println("Removed user id " + id);
    }

    static void searchBooksInteractive(Library lib, Scanner sc) {
        System.out.print("Search by (title/author/isbn): ");
        String field = sc.nextLine().trim().toLowerCase();
        System.out.print("Query: ");
        String q = sc.nextLine().trim();
        List<Book> results;
        switch (field) {
            case "title": results = lib.searchByTitle(q); break;
            case "author": results = lib.searchByAuthor(q); break;
            case "isbn": results = lib.searchByIsbn(q); break;
            default:
                System.out.println("Unknown field. Searching title.");
                results = lib.searchByTitle(q);
        }
        if (results.isEmpty()) System.out.println("No results.");
        else results.forEach(System.out::println);
    }

    static void issueBookInteractive(Library lib, Scanner sc) {
        System.out.print("Book ID: ");
        int bookId = readInt(sc);
        System.out.print("User ID: ");
        int userId = readInt(sc);
        System.out.print("Days to borrow (default 14): ");
        String daysStr = sc.nextLine().trim();
        int days = daysStr.isEmpty() ? 14 : Integer.parseInt(daysStr);
        lib.issueBook(bookId, userId, days);
        System.out.println("Issued book " + bookId + " to user " + userId);
    }

    static void returnBookInteractive(Library lib, Scanner sc) {
        System.out.print("Book ID to return: ");
        int bookId = readInt(sc);
        double fine = lib.returnBook(bookId, LocalDate.now());
        if (fine > 0) System.out.printf("Book returned. Fine due: %.2f\n", fine);
        else System.out.println("Book returned. No fine.");
    }

    static int readInt(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); } catch (NumberFormatException e) { System.out.print("Enter a valid integer: "); }
        }
    }
}

/* Domain classes and library logic */

enum Role {
    MEMBER,
    LIBRARIAN
}

class Book implements Serializable 
{
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String title;
    private final String author;
    private final String publisher;
    private final int year;
    private final String isbn;

    private boolean issued;
    private Integer issuedToUserId;
    private LocalDate issuedDate;
    private LocalDate dueDate;

    Book(int id, String title, String author, String publisher, int year, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.isbn = isbn;
        this.issued = false;
    }

int getId() { return id; }
String getTitle() { return title; }
String getAuthor() { return author; }
String getIsbn() { return isbn; }
boolean isIssued() { return issued; }
Integer getIssuedToUserId() { return issuedToUserId; }
LocalDate getIssuedDate() { return issuedDate; }
LocalDate getDueDate() { return dueDate; }

void markIssued(int userId, LocalDate date, int days) 
{
    issued = true;
    issuedToUserId = userId;
    issuedDate = date;
    dueDate = date.plusDays(days);
}

double markReturned(LocalDate returnDate, double finePerDay) 
{
    if (!issued) return 0;
    long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);
    issued = false;
    issuedToUserId = null;
    issuedDate = null;
    dueDate = null;
    return lateDays > 0 ? lateDays * finePerDay : 0;
}

@Override
public String toString() 
{
    StringBuilder sb = new StringBuilder();
    sb.append("Book[").append(id).append("] ").append(title).append(" by ").append(author);
    sb.append(" (").append(year).append(") ISBN: ").append(isbn);
    if (issued) sb.append(" [Issued to ").append(issuedToUserId).append(" until ").append(dueDate).append("]");
    else sb.append(" [Available]");
    return sb.toString();
}
}

class User implements Serializable 
{
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final String email;
    private final Role role;

    User(int id, String name, String email, Role role) 
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    int getId() 
    {
         return id; 
    }
    String getName() { return name; }
    String getEmail() { return email; }
    Role getRole() { return role; }

    @Override
    public String toString() 
    {
        return "User[" + id + "] " + name + " <" + email + "> (" + role + ")";
    }
}

class Library implements Serializable 
{
    private static final long serialVersionUID = 1L;

    private final Map<Integer, Book> books = new HashMap<>();
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> userToBooks = new HashMap<>();

    private int nextBookId = 1;
    private int nextUserId = 1;

    private final int maxBooksPerUser = 5;
    private final double finePerDay = 5.0;

    public Book addBook(String title, String author, String publisher, int year, String isbn) 
    
    {
        int id = nextBookId++;
        Book b = new Book(id, title, author, publisher, year, isbn);
        books.put(id, b);
        return b;
    }

    public void removeBook(int bookId) 
    {
        Book b = books.get(bookId);
        if (b == null) throw new LibraryException("Book not found.");
        if (b.isIssued()) throw new LibraryException("Cannot remove an issued book.");
        books.remove(bookId);
    }

    public User registerUser(String name, String email, Role role) 
    {
        int id = nextUserId++;
        User u = new User(id, name, email, role);
        users.put(id, u);
        userToBooks.put(id, new HashSet<>());
        return u;
    }

    public void removeUser(int userId) 
    {
        User u = users.get(userId);
        if (u == null) throw new LibraryException("User not found.");
        Set<Integer> borrowed = userToBooks.getOrDefault(userId, Collections.emptySet());
        if (!borrowed.isEmpty()) throw new LibraryException("User has borrowed books. Cannot remove.");
        users.remove(userId);
        userToBooks.remove(userId);
    }

    public Optional<Book> findBook(int id) { return Optional.ofNullable(books.get(id)); }
    public Optional<User> findUser(int id) { return Optional.ofNullable(users.get(id)); }

    public List<Book> listAllBooks() 
    {
        return books.values().stream().sorted(Comparator.comparingInt(Book::getId)).collect(Collectors.toList());
    }

    public List<Book> listAvailableBooks() 
    {
        return books.values().stream().filter(b -> !b.isIssued()).sorted(Comparator.comparingInt(Book::getId)).collect(Collectors.toList());
    }

    public List<User> listUsers() 
    {
        return users.values().stream().sorted(Comparator.comparingInt(User::getId)).collect(Collectors.toList());
    }

    public List<Book> listIssuedBooks() 
    {
        return books.values().stream().filter(Book::isIssued).collect(Collectors.toList());
    }

    public List<Book> searchByTitle(String q) 
    {
        String qq = q.toLowerCase();
        return books.values().stream().filter(b -> b.getTitle().toLowerCase().contains(qq)).collect(Collectors.toList());
    }

    public List<Book> searchByAuthor(String q) 
    {
        String qq = q.toLowerCase();
        return books.values().stream().filter(b -> b.getAuthor().toLowerCase().contains(qq)).collect(Collectors.toList());
    }

    public List<Book> searchByIsbn(String q) 
    {
        String qq = q.toLowerCase();
        return books.values().stream().filter(b -> b.getIsbn().toLowerCase().contains(qq)).collect(Collectors.toList());
    }

    public void issueBook(int bookId, int userId, int days) 
    {
        Book b = books.get(bookId);
        if (b == null) throw new LibraryException("Book not found.");
        User u = users.get(userId);
        if (u == null) throw new LibraryException("User not found.");
        if (b.isIssued()) throw new LibraryException("Book already issued.");
        Set<Integer> borrowed = userToBooks.get(userId);
        if (borrowed == null) 
        {
            borrowed = new HashSet<>();
            userToBooks.put(userId, borrowed);
        }
        if (borrowed.size() >= maxBooksPerUser) throw new LibraryException("User has reached borrow limit.");
        b.markIssued(userId, LocalDate.now(), days);
        borrowed.add(bookId);
    }

    public double returnBook(int bookId, LocalDate returnDate) 
    {
        Book b = books.get(bookId);
        if (b == null) throw new LibraryException("Book not found.");
        if (!b.isIssued()) throw new LibraryException("Book is not issued.");
        int userId = b.getIssuedToUserId();
        double fine = b.markReturned(returnDate, finePerDay);
        Set<Integer> borrowed = userToBooks.get(userId);
        if (borrowed != null) borrowed.remove(bookId);
        return fine;
    }

    public void saveToFile(String filename) 
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) 
        {
            oos.writeObject(this);
        } 
        catch (IOException e) 
        {
            throw new LibraryException("Could not save library: " + e.getMessage());
        }
    }

    public static Optional<Library> loadFromFile(String filename) 
    {
        File f = new File(filename);
        if (!f.exists()) return Optional.empty();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) 
        {
            Object o = ois.readObject();
            if (o instanceof Library) return Optional.of((Library) o);
            return Optional.empty();
        } 
        catch (IOException | ClassNotFoundException e) 
        {
            System.out.println("Failed to load saved library: " + e.getMessage());
            return Optional.empty();
        }
    }
}

class LibraryException extends RuntimeException {
    LibraryException(String msg) { super(msg); }
}
