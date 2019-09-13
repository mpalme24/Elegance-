
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
public class library implements Serializable {
	private static final String LIBRARY_FILE = "library.obj";
	private static final int LOAN_LIMIT = 2;
	private static final int LOAN_PERIOD = 2;
	private static final double FINE_PER_DAY = 1.0;
	private static final double MAX_FINES_OWED = 1.0;
	private static final double DAMAGE_FEE = 2.0;
	
	private static library self;
	private int bookId;
	private int memberId;
	private int loanId;
	private Date loanDate;
	
	private Map<Integer, book> catalog;
	private Map<Integer, member> members;
	private Map<Integer, loan> loans;
	private Map<Integer, loan> currentLoans;
	private Map<Integer, book> damagedBooks;

	private library() {
		catalog = new HashMap<>();
		members = new HashMap<>();
		loans = new HashMap<>();
		currentLoans = new HashMap<>();
		damagedBooks = new HashMap<>();
		bookId = 1;
		memberId = 1;
		loanId = 1;
	}

	public static synchronized library instanceLibrary() {
		if (self == null) {
			Path libraryFilePath = Paths.get(LIBRARY_FILE);
			if (Files.exists(libraryFilePath)) {
				try (FileInputStream inputFile = new FileInputStream(LIBRARY_FILE);
						ObjectInputStream libraryInputFile = new ObjectInputStream(inputFile);) {

					self = (library) libraryInputFile.readObject();
					Date loanDate = self.loanDate;
					Calendar.instanceCalendar().setCalendarDate(loanDate);
					libraryInputFile.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else
				self = new library();
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

	public List<member> currentMembers() {
		return new ArrayList<member>(members.values());
	}

	public List<book> currentBooks() {
		return new ArrayList<book>(catalog.values());
	}

	public List<loan> currentLoans() {
		return new ArrayList<loan>(currentLoans.values());
	}

	public member addMember(String lastName, String firstName, String email, int phoneNumber) {
		int newMemberId = getNextMemberId();
		member member = new member(lastName, firstName, email, phoneNumber, newMemberId);
		members.put(member.getId(), member);
		return member;
	}

	public book addBook(String author, String title, String callNumber) {
		int newBookId = getNextBookId();
		book newBook = new book(author, title, callNumber, newBookId);
		catalog.put(newBook.getBookId(), newBook);
		return newBook;
	}

	public member getMember(int memberId) {
		if (members.containsKey(memberId))
			return members.get(memberId);
		return null;
	}

	public book getBook(int bookId) {
		if (catalog.containsKey(bookId))
			return catalog.get(bookId);
		return null;
	}

	public int getloanLimit() {
		return LOAN_LIMIT;
	}

	public boolean memberCanBorrow(member member) {
		if (member.getNumberOfCurrentLoans() == LOAN_LIMIT)
			return false;

		if (member.getFinesOwed() >= MAX_FINES_OWED)
			return false;

		for (loan loan : member.getLoans())
			if (loan.isLoanOverDue())
				return false;

		return true;
	}

	public int getLoanRemaining(member member) {
		return LOAN_LIMIT - member.getNumberOfCurrentLoans();
	}

	public loan issueLoan(book book, member member) {
		Date dueDate = Calendar.instanceCalendar().loanDueDate(LOAN_PERIOD);
		int nextLoanId = getNextLoanId();
		loan loan = new loan(nextLoanId, book, member, dueDate);
		member.takeOutLoan(loan);
		book.setBookBorrowed();
		loans.put(loan.getLoanId(), loan);
		currentLoans.put(book.getBookId(), loan);
		return loan;
	}

	public loan getLoanByBookId(int bookId) {
		if (currentLoans.containsKey(bookId)) {
			return currentLoans.get(bookId);
		}
		return null;
	}

	public double getOverdueFine(loan inputLoan) {
		if (inputLoan.isLoanOverDue()) {
			long daysOverDue = Calendar.instanceCalendar().getDaysDifference(inputLoan.getDueDate());
			double fine = daysOverDue * FINE_PER_DAY;
			return fine;
		}
		return 0.0;
	}

	public void setDischargeLoan(loan currentLoan, boolean isDamaged) {
		member member = currentLoan.member();
		book book = currentLoan.book();

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
		for (loan loan : currentLoans.values()) {
			loan.checkOverDue();
		}
	}
	
	public void repairBook(book inputBook) {
		if (damagedBooks.containsKey(inputBook.getBookId())) {
			inputBook.setBookStateRepair();
			damagedBooks.remove(inputBook.getBookId());
		} else {
			throw new RuntimeException("Library: repairBook: book is not damaged");
		}
	}

}
