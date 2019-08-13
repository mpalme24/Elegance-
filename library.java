
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
	
	private static final String libraryFile = "library.obj";
	private static final int loanLimit = 2;
	private static final int loanPeriod = 2;
	private static final double finePerDay = 1.0;
	private static final double maxFinesOwed = 1.0;
	private static final double damageFee = 2.0;
	
	private static library self;
	private int bookID;
	private int memberID;
	private int loanID;
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
		bookID = 1;
		memberID = 1;		
		loanID = 1;		
	}

	
	public static synchronized library INSTANCE() {		
		if (self == null) {
			Path PATH = Paths.get(libraryFile);			
			if (Files.exists(PATH)) {	
				try (ObjectInputStream LiF = new ObjectInputStream(new FileInputStream(libraryFile));) {
			    
					self = (library) LiF.readObject();
					Calendar.INSTANCE().setDate(self.loanDate);
					LiF.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else self = new library();
		}
		return self;
	}

	
	public static synchronized void SAVE() {
		if (self != null) {
			self.loanDate = Calendar.INSTANCE().Date();
			try (ObjectOutputStream LoF = new ObjectOutputStream(new FileOutputStream(libraryFile));) {
				LoF.writeObject(self);
				LoF.flush();
				LoF.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	public int bookID() {
		return bookID;
	}
	
	
	public int memberID() {
		return memberID;
	}
	
	
	private int nextBid() {
		return bookID++;
	}

	
	private int nextMid() {
		return memberID++;
	}

	
	private int nextLid() {
		return loanID++;
	}

	
	public List<member> members() {		
		return new ArrayList<member>(members.values()); 
	}


	public List<book> books() {		
		return new ArrayList<book>(catalog.values()); 
	}


	public List<loan> currentLoans() {
		return new ArrayList<loan>(currentLoans.values());
	}


	public member addMem(String lastName, String firstName, String email, int phoneNo) {		
		member member = new member(lastName, firstName, email, phoneNo, nextMid());
		members.put(member.getID(), member);		
		return member;
	}

	
	public book addBook(String a, String t, String c) {		
		book b = new book(a, t, c, nextBid());
		catalog.put(b.ID(), b);		
		return b;
	}

	
	public member member(int memberId) {
		if (members.containsKey(memberId)) 
			return members.get(memberId);
		return null;
	}

	
	public book Book(int bookId) {
		if (catalog.containsKey(bookId)) 
			return catalog.get(bookId);		
		return null;
	}

	
	public int loanLimit() {
		return loanLimit;
	}

	
	public boolean memberCanBorrow(member member) {		
		if (member.currentLoanAmount() == loanLimit ) 
			return false;
				
		if (member.finesOwed() >= maxFinesOwed) 
			return false;
				
		for (loan loan : member.getLoans()) 
			if (loan.overDue()) 
				return false;
			
		return true;
	}

	
	public int remainingLoans(member member) {		
		return loanLimit - member.currentLoanAmount();
	}

	
	public loan issueLoan(book book, member member) {
		Date dueDate = Calendar.INSTANCE().dueDate(loanPeriod);
		loan loan = new loan(NextLID(), book, member, dueDate);
		member.takeOutLoan(loan);
		book.Borrow();
		loans.put(loan.ID(), loan);
		currentLoans.put(book.ID(), loan);
		return loan;
	}
	
	
	public loan loanByBookID(int bookId) {
		if (currentLoans.containsKey(bookId)) {
			return currentLoans.get(bookId);
		}
		return null;
	}

	
	public double calculateOverDueFine(loan loan) {
		if (loan.overDue()) {
			long daysOverDue = Calendar.INSTANCE().getDaysDifference(loan.getDueDate());
			double fine = daysOverDue * finePerDay;
			return fine;
		}
		return 0.0;		
	}


	public void dischargeLoan(loan currentLoan, boolean isDamaged) {
		member member = currentLoan.Member();
		book book  = currentLoan.Book();
		
		double overDueFine = calculateOverDueFine(currentLoan);
		member.addFine(overDueFine);	
		
		member.dischargeLoan(currentLoan);
		book.Return(isDamaged);
		if (isDamaged) {
			member.addFine(damageFee);
			damagedBooks.put(book.ID(), book);
		}
		currentLoan.discharge();
		currentLoans.remove(book.ID());
	}


	public void checkCurrentLoans() {
		for (loan loan : currentLoans.values()) {
			loan.checkOverDue();
		}		
	}


	public void repairBook(book currentBook) {
		if (damagedBooks.containsKey(currentBook.ID())) {
			currentBook.Repair();
			damagedBooks.remove(currentBook.ID());
		}
		else {
			throw new RuntimeException("Library: repairBook: book is not damaged");
		}
		
	}
	
	
}
