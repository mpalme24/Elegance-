import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

	private BorrowBookUI borrowBookUi;
	private library library1;
	private member member;

	private enum BookControlState {
		INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED
	};

	private BookControlState borrowBookState;

	private List<book> pendingList;
	private List<loan> completedList;
	private book book;

	public BorrowBookControl() {
		this.library1 = library1.instanceLibrary();
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
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
		}
		member = library1.getMember(memberId);
		if (member == null) {
			borrowBookUi.Display("Invalid memberId");
			return;
		}
		if (library1.memberCanBorrow(member)) {
			pendingList = new ArrayList<>();
			borrowBookUi.setBorrowBookUiState(BorrowBookUI.BorrowBookUiState.SCANNING);
			borrowBookState = BookControlState.SCANNING;
		} else {
			borrowBookUi.Display("Member cannot borrow at this time");
			borrowBookUi.setBorrowBookUiState(BorrowBookUI.BorrowBookUiState.RESTRICTED);
		}
	}

	public void scannBook(int bookId) {
		book = null;
		if (!borrowBookState.equals(BookControlState.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}
		book = library1.getBook(bookId);
		if (book == null) {
			borrowBookUi.Display("Invalid bookId");
			return;
		}
		if (!book.getBookStateAvailable()) {
			borrowBookUi.Display("Book cannot be borrowed");
			return;
		}
		pendingList.add(book);
		for (book book : pendingList) {
			borrowBookUi.Display(book.toString());
		}
		if ((library1.getLoanRemaining(member) - pendingList.size()) == 0) {
			borrowBookUi.Display("Loan limit reached");
			borrowComplete();
		}
	}

	public void borrowComplete() {
		if (pendingList.size() == 0) {
			cancelBookBorrow();
		} else {
			borrowBookUi.Display("\nFinal Borrowing List");
			for (book book : pendingList) {
				borrowBookUi.Display(book.toString());
			}
			completedList = new ArrayList<loan>();
			borrowBookUi.setBorrowBookUiState(BorrowBookUI.BorrowBookUiState.FINALISING);
			borrowBookState = BookControlState.FINALISING;
		}
	}

	public void commitLoan() {
		if (!borrowBookState.equals(BookControlState.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}
		for (book book : pendingList) {
			loan loan = library1.issueLoan(book, member);
			completedList.add(loan);
		}
		borrowBookUi.Display("Completed Loan Slip");
		for (loan loan : completedList) {
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
