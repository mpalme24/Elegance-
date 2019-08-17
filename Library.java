
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Library implements Serializable {
	private static final String LIBRARY_FILE = "library.obj";
	private static final int LOAN_LIMIT = 2;
	private static final int LOAN_PERIOD = 2;
	private static final double FINE_PER_DAY = 1.0;
	private static final double MAX_FINES_OWED = 1.0;
	private static final double DAMAGE_FEE = 2.0;
	
	private static Library self;
	private int bookId;
	private int memberId;
	private int loanId;
	private Date loanDate;
	
	private Map<Integer, Book> catalog;
	private Map<Integer, Member> members;
	private Map<Integer, Loan> loans;
	private Map<Integer, Loan> currentLoans;
	private Map<Integer, Book> damagedBooks;

	private Library() {
		catalog = new HashMap<>();
		members = new HashMap<>();
		loans = new HashMap<>();
		currentLoans = new HashMap<>();
		damagedBooks = new HashMap<>();
		bookId = 1;
		memberId = 1;
		loanId = 1;
	}

	public static synchronized Library instanceLibrary() {
		if (self == null) {
			Path libraryFilePath = Paths.get(LIBRARY_FILE);
			if (Files.exists(libraryFilePath)) {
				try (FileInputStream inputFile = new FileInputStream(LIBRARY_FILE);
						ObjectInputStream libraryInputFile = new ObjectInputStream(inputFile);) {

					self = (Library) libraryInputFile.readObject();
					Date loanDate = self.loanDate;
					Calendar.instanceCalendar().setCalendarDate(loanDate);
					libraryInputFile.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else
				self = new Library();
		}
		return self;
	}

	public static synchronized void saveLibrary() {
		if (self != null) {
			self.loanDate = Calendar.instanceCalendar().date();
			try (FileOutputStream outputfile = new FileOutputStream(LIBRARY_FILE);
					ObjectOutputStream libraryOutputFile = new ObjectOutputStream(outputfile);) {
				libraryOutputFile.writeObject(self);
				libraryOutputFile.flush();
				libraryOutputFile.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public int getBookId() {
		return bookId;
	}

	public int getMemberID() {
		return memberId;
	}

	private int getNextBookId() {
		return bookId++;
	}

	private int getNextMemberId() {
		return memberId++;
	}

	private int getNextLoanId() {
		return loanId++;
	}

	public List<Member> currentMembers() {
		return new ArrayList<Member>(members.values());
	}

	public List<Book> currentBooks() {
		return new ArrayList<Book>(catalog.values());
	}

	public List<Loan> currentLoans() {
		return new ArrayList<Loan>(currentLoans.values());
	}

	public Member addMember(String lastName, String firstName, String email, int phoneNumber) {
		int newMemberId = getNextMemberId();
		Member member = new Member(lastName, firstName, email, phoneNumber, newMemberId);
		members.put(member.getId(), member);
		return member;
	}

	public Book addBook(String author, String title, String callNumber) {
		int newBookId = getNextBookId();
		Book newBook = new Book(author, title, callNumber, newBookId);
		catalog.put(newBook.getBookId(), newBook);
		return newBook;
	}

	public Member getMember(int memberId) {
		if (members.containsKey(memberId))
			return members.get(memberId);
		return null;
	}

	public Book getBook(int bookId) {
		if (catalog.containsKey(bookId))
			return catalog.get(bookId);
		return null;
	}

	public int getloanLimit() {
		return LOAN_LIMIT;
	}

	public boolean memberCanBorrow(Member member) {
		if (member.getNumberOfCurrentLoans() == LOAN_LIMIT)
			return false;

		if (member.getFinesOwed() >= MAX_FINES_OWED)
			return false;

		for (Loan loan : member.getLoans())
			if (loan.getOverDueLoan())
				return false;

		return true;
	}

	public int getLoanRemaining(Member member) {
		return LOAN_LIMIT - member.getNumberOfCurrentLoans();
	}

	public Loan issueLoan(Book book, Member member) {
		Date dueDate = Calendar.instanceCalendar().loanDueDate(LOAN_PERIOD);
		int nextLoanId = getNextLoanId();
		Loan loan = new Loan(nextLoanId, book, member, dueDate);
		member.takeOutLoan(loan);
		book.setBookBorrowed();
		loans.put(loan.getLoanId(), loan);
		currentLoans.put(book.getBookId(), loan);
		return loan;
	}

	public Loan getLoanByBookId(int bookId) {
		if (currentLoans.containsKey(bookId)) {
			return currentLoans.get(bookId);
		}
		return null;
	}

	public double getOverdueFine(Loan inputLoan) {
		if (inputLoan.getOverDueLoan()) {
			long daysOverDue = Calendar.instanceCalendar().getDaysDifference(inputLoan.getDueDate());
			double fine = daysOverDue * FINE_PER_DAY;
			return fine;
		}
		return 0.0;
	}

	public void setDischargeLoan(Loan currentLoan, boolean isDamaged) {
		Member member = currentLoan.Member();
		Book book = currentLoan.Book();

		double overDueFine = getOverdueFine(currentLoan);
		member.addFine(overDueFine);
		member.dischargeLoan(currentLoan);
		book.setBookReturnedState(isDamaged);
		if (isDamaged) {
			member.addFine(DAMAGE_FEE);
			damagedBooks.put(book.getBookId(), book);
		}
		currentLoan.discharge();
		currentLoans.remove(book.getBookId());
	}

	public void checkCurrentLoans() {
		for (Loan loan : currentLoans.values()) {
			loan.checkOverDue();
		}
	}
	
	public void repairBook(Book inputBook) {
		if (damagedBooks.containsKey(inputBook.getBookId())) {
			inputBook.setBookStateRepair();
			damagedBooks.remove(inputBook.getBookId());
		} else {
			throw new RuntimeException("Library: repairBook: book is not damaged");
		}
	}

}
