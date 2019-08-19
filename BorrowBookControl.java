import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

	private BorrowBookUi borrowBookUi;
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

	public void setBorrowBookUi(BorrowBookUi newUi) {
		if (!borrowBookState.equals(BookControlState.INITIALISED)) {
			throw new RuntimeException("BorrowBookControl: cannot call setBorrowBookUi except in INITIALISED state");
		}
		this.borrowBookUi = newUi;
		newUi.setBorrowBookUiState(BorrowBookUi.BorrowBookUiState.READY);
		borrowBookState = BookControlState.READY;
	}

	public void memberCardSwiped(int memberId) {
		if (!borrowBookState.equals(BookControlState.READY)) {
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
		}
		member = library1.getMember(memberId);
		if (member == null) {
			borrowBookUi.displays("Invalid memberId");
			return;
		}
		if (library1.memberCanBorrow(member)) {
			pendingList = new ArrayList<>();
			borrowBookUi.setBorrowBookUiState(BorrowBookUi.BorrowBookUiState.SCANNING);
			borrowBookState = BookControlState.SCANNING;
		} else {
			borrowBookUi.displays("Member cannot borrow at this time");
			borrowBookUi.setBorrowBookUiState(BorrowBookUi.BorrowBookUiState.RESTRICTED);
		}
	}

	public void scannedBook(int bookId) {
		book = null;
		if (!borrowBookState.equals(BookControlState.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}
		book = library1.getBook(bookId);
		if (book == null) {
			borrowBookUi.displays("Invalid bookId");
			return;
		}
		if (!book.getBookStateAvailable()) {
			borrowBookUi.displays("Book cannot be borrowed");
			return;
		}
		pendingList.add(book);
		for (book book : pendingList) {
			borrowBookUi.displays(book.toString());
		}
		if ((library1.getLoanRemaining(member) - pendingList.size()) == 0) {
			borrowBookUi.displays("Loan limit reached");
			borrowComplete();
		}
	}

	public void borrowComplete() {
		if (pendingList.size() == 0) {
			cancelBookBorrow();
		} else {
			borrowBookUi.displays("\nFinal Borrowing List");
			for (book book : pendingList) {
				borrowBookUi.displays(book.toString());
			}
			completedList = new ArrayList<loan>();
			borrowBookUi.setBorrowBookUiState(BorrowBookUi.BorrowBookUiState.FINALISING);
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
		borrowBookUi.displays("Completed Loan Slip");
		for (loan loan : completedList) {
			borrowBookUi.displays(loan.toString());
		}
		borrowBookUi.setBorrowBookUiState(BorrowBookUi.BorrowBookUiState.COMPLETED);
		borrowBookState = BookControlState.COMPLETED;
	}

	public void cancelBookBorrow() {
		borrowBookUi.setBorrowBookUiState(BorrowBookUi.BorrowBookUiState.CANCELLED);
		borrowBookState = BookControlState.CANCELLED;
	}

}
