import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

	private BorrowBookUI borrowBookUi;
	private Library library;
	private Member member;

	private enum BookControlState {
		INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED
	};

	private BookControlState borrowBookState;

	private List<Book> pendingList;
	private List<Loan> completedList;
	private Book book;

	public BorrowBookControl() {
		this.library = library.instanceLibrary();
		borrowBookState = BookControlState.INITIALISED;
	}

	public void setBorrowBookUi(BorrowBookUI newUi) {
		if (!borrowBookState.equals(BookControlState.INITIALISED)) {
			throw new RuntimeException("BorrowBookControl: cannot call setBorrowBookUi except in INITIALISED state");
		}
		this.borrowBookUi = newUi;
		newUi.setBorrowBookUiState(BorrowBookUI.BorrowBookUiState.READY);
		borrowBookState = BookControlState.READY;
	}
	public void memberCardSwiped(int memberId) {
		if (!borrowBookState.equals(BookControlState.READY)) {
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");}
		member = library.getMember(memberId);
		if (member == null) {
			borrowBookUi.Display("Invalid memberId");
			return;
		}
		if (library.memberCanBorrow(member)) {
			pendingList = new ArrayList<>();
			borrowBookUi.setBorrowBookUiState(BorrowBookUI.BorrowBookUiState.SCANNING);
			borrowBookState = BookControlState.SCANNING;
		} else {
			borrowBookUi.Display("Member cannot borrow at this time");
			borrowBookUi.setBorrowBookUiState(BorrowBookUI.BorrowBookUiState.RESTRICTED);
		}
	}

	public void scanneBook(int bookId) {
		book = null;
		if (!borrowBookState.equals(BookControlState.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}
		book = library.getBook(bookId);
		if (book == null) {
			borrowBookUi.Display("Invalid bookId");
			return;
		}
		if (!book.getBookStateAvailable()) {
			borrowBookUi.Display("Book cannot be borrowed");
			return;
		}
		pendingList.add(book);
		for (Book book : pendingList) {
			borrowBookUi.Display(book.toString());
		}
		if ((library.getLoanRemaining(member) - pendingList.size() )== 0) {
			borrowBookUi.Display("Loan limit reached");
			borrowComplete();
		}
	}

	public void borrowComplete() {
		if (pendingList.size() == 0) {
			cancelBookBorrow();
		} else {
			borrowBookUi.Display("\nFinal Borrowing List");
			for (Book book : pendingList) {
				borrowBookUi.Display(book.toString());
			}
			completedList = new ArrayList<Loan>();
			borrowBookUi.setBorrowBookUiState(BorrowBookUI.BorrowBookUiState.FINALISING);
			borrowBookState = BookControlState.FINALISING;
		}
	}

	public void commitLoan() {
		if (!borrowBookState.equals(BookControlState.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}
		for (Book book : pendingList) {
			Loan loan = library.issueLoan(book, member);
			completedList.add(loan);
		}
		borrowBookUi.Display("Completed Loan Slip");
		for (Loan loan : completedList) {
			borrowBookUi.Display(loan.toString());
		}
		borrowBookUi.setBorrowBookUiState(BorrowBookUI.BorrowBookUiState.COMPLETED);
		borrowBookState = BookControlState.COMPLETED;
	}
	public void cancelBookBorrow() {
		borrowBookUi.setBorrowBookUiState(BorrowBookUI.BorrowBookUiState.CANCELLED);
		borrowBookState = BookControlState.CANCELLED;
	}

}
