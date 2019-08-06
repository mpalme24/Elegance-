
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
	private int bookId;
	private int memberId;
	private int loanId;
	private Date loanDate;

	private Map<Integer, book> CATALOG;
	private Map<Integer, member> MEMBERS;
	private Map<Integer, loan> LOANS;
	private Map<Integer, loan> CURRENT_LOANS;
	private Map<Integer, book> DAMAGED_BOOKS;

	private library() {
		CATALOG = new HashMap<>();
		MEMBERS = new HashMap<>();
		LOANS = new HashMap<>();
		CURRENT_LOANS = new HashMap<>();
		DAMAGED_BOOKS = new HashMap<>();
		bookId = 1;
		memberId = 1;
		loanId = 1;
	}

	public static synchronized library INSTANCE() {
		if (self == null) {
			Path PATH = Paths.get(libraryFile);
			if (Files.exists(PATH)) {
				try (ObjectInputStream LiF = new ObjectInputStream(new FileInputStream(libraryFile));) {

					self = (library) LiF.readObject();
					Calendar.INSTANCE().Set_dATE(self.loanDate);
					LiF.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else
				self = new library();
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
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public int getBookId() {
		return bookId;
	}

	public int MemberID() {
		return memberId;
	}

	private int NextBID() {
		return bookId++;
	}

	private int NextMID() {
		return memberId++;
	}

	private int NextLID() {
		return loanId++;
	}

	public List<member> MEMBERS() {
		return new ArrayList<member>(MEMBERS.values());
	}

	public List<book> BOOKS() {
		return new ArrayList<book>(CATALOG.values());
	}

	public List<loan> CurrentLoans() {
		return new ArrayList<loan>(CURRENT_LOANS.values());
	}

	public member Add_mem(String lastName, String firstName, String email, int phoneNo) {
		member member = new member(lastName, firstName, email, phoneNo, NextMID());
		MEMBERS.put(member.GeT_ID(), member);
		return member;
	}

	public book Add_book(String a, String t, String c) {
		book b = new book(a, t, c, NextBID());
		CATALOG.put(b.getbookId(), b);
		return b;
	}

	public member MEMBER(int memberId) {
		if (MEMBERS.containsKey(memberId))
			return MEMBERS.get(memberId);
		return null;
	}

	public book Book(int bookId) {
		if (CATALOG.containsKey(bookId))
			return CATALOG.get(bookId);
		return null;
	}

	public int LOAN_LIMIT() {
		return loanLimit;
	}

	public boolean MEMBER_CAN_BORROW(member member) {
		if (member.Number_Of_Current_Loans() == loanLimit)
			return false;

		if (member.Fines_OwEd() >= maxFinesOwed)
			return false;

		for (loan loan : member.GeT_LoAnS())
			if (loan.OVer_Due())
				return false;

		return true;
	}

	public int Loans_Remaining_For_Member(member member) {
		return loanLimit - member.Number_Of_Current_Loans();
	}

	public loan ISSUE_LAON(book book, member member) {
		Date dueDate = Calendar.INSTANCE().Due_Date(loanPeriod);
		loan loan = new loan(NextLID(), book, member, dueDate);
		member.Take_Out_Loan(loan);
		book.isBorrowed();
		LOANS.put(loan.ID(), loan);
		CURRENT_LOANS.put(book.getbookId(), loan);
		return loan;
	}

	public loan LOAN_BY_getBookId(int bookId) {
		if (CURRENT_LOANS.containsKey(bookId)) {
			return CURRENT_LOANS.get(bookId);
		}
		return null;
	}

	public double CalculateOverDueFine(loan loan) {
		if (loan.OVer_Due()) {
			long daysOverDue = Calendar.INSTANCE().Get_Days_Difference(loan.Get_Due_Date());
			double fine = daysOverDue * finePerDay;
			return fine;
		}
		return 0.0;
	}

	public void Discharge_loan(loan currentLoan, boolean isDamaged) {
		member member = currentLoan.Member();
		book book = currentLoan.Book();

		double overDueFine = CalculateOverDueFine(currentLoan);
		member.Add_Fine(overDueFine);

		member.dIsChArGeLoAn(currentLoan);
		book.Return(isDamaged);
		if (isDamaged) {
			member.Add_Fine(damageFee);
			DAMAGED_BOOKS.put(book.getbookId(), book);
		}
		currentLoan.DiScHaRgE();
		CURRENT_LOANS.remove(book.getbookId());
	}

	public void checkCurrentLoans() {
		for (loan loan : CURRENT_LOANS.values()) {
			loan.checkOverDue();
		}
	}

	public void Repair_BOOK(book currentBook) {
		if (DAMAGED_BOOKS.containsKey(currentBook.getbookId())) {
			currentBook.Repair();
			DAMAGED_BOOKS.remove(currentBook.getbookId());
		} else {
			throw new RuntimeException("Library: repairBook: book is not damaged");
		}

	}

}
