import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

	private BorrowBookUI borrowBookUI;
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

	public void setBorrowBookUI(BorrowBookUI newUI) {
		if (!borrowBookState.equals(BookControlState.INITIALISED)) {
			throw new RuntimeException("BorrowBookControl: cannot call setBorrowBookUi except in INITIALISED state");
		}
		this.borrowBookUI = newUI;
		newUI.setBorrowBookUIState(BorrowBookUI.BorrowBookUIState.READY);
		borrowBookState = BookControlState.READY;
	}

	public void memberCardSwiped(int memberId) {
		if (!borrowBookState.equals(BookControlState.READY)) {
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
		}
		member = library1.getMember(memberId);
		if (member == null) {
			borrowBookUI.displays("Invalid memberId");
			return;
		}
		if (library1.memberCanBorrow(member)) {
			pendingList = new ArrayList<>();
			borrowBookUI.setBorrowBookUIState(BorrowBookUI.BorrowBookUIState.SCANNING);
			borrowBookState = BookControlState.SCANNING;
		} else {
			borrowBookUI.displays("Member cannot borrow at this time");
			borrowBookUI.setBorrowBookUIState(BorrowBookUI.BorrowBookUIState.RESTRICTED);
		}
	}

	public void scannBook(int bookId) {
		book = null;
		if (!borrowBookState.equals(BookControlState.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}
		book = library1.getBook(bookId);
		if (book == null) {
			borrowBookUI.displays("Invalid bookId");
			return;
		}
		if (!book.getBookStateAvailable()) {
			borrowBookUI.displays("Book cannot be borrowed");
			return;
		}
		pendingList.add(book);
		for (book book : pendingList) {
			borrowBookUI.displays(book.toString());
		}
		if ((library1.getLoanRemaining(member) - pendingList.size()) == 0) {
			borrowBookUI.displays("Loan limit reached");
			borrowComplete();
		}
	}

	public void borrowComplete() {
		if (pendingList.size() == 0) {
			cancelBookBorrow();
		} else {
			borrowBookUI.displays("\nFinal Borrowing List");
			for (book book : pendingList) {
				borrowBookUI.displays(book.toString());
			}
			completedList = new ArrayList<loan>();
			borrowBookUI.setBorrowBookUIState(BorrowBookUI.BorrowBookUIState.FINALISING);
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
		borrowBookUI.displays("Completed Loan Slip");
		for (loan loan : completedList) {
			borrowBookUI.displays(loan.toString());
		}
		borrowBookUI.setBorrowBookUIState(BorrowBookUI.BorrowBookUIState.COMPLETED);
		borrowBookState = BookControlState.COMPLETED;
	}

	public void cancelBookBorrow() {
		borrowBookUI.setBorrowBookUIState(BorrowBookUI.BorrowBookUIState.CANCELLED);
		borrowBookState = BookControlState.CANCELLED;
	}

}
