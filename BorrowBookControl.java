import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

	private BorrowBookUserInterface userInterface;

	private Library library;
	private Member member;

	private enum ControlState {
		INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED
	};

	private ControlState state;

	private List<Book> pending;
	private List<Loan> completed;
	private Book book;

	public BorrowBookControl() {
		this.library = Library.getInstance();//eclips says error may occur i say it wont it wonts me to make getinstant() a static
		state = ControlState.INITIALISED;
	}

	public void setUserInterface(BorrowBookUserInterface newUserInterface) {
		if (!state.equals(ControlState.INITIALISED))
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");

		this.userInterface = newUserInterface;
		newUserInterface.setState(BorrowBookUserInterface.UserInterfaceState.READY);
		state = ControlState.READY;
	}

	public void setCardSwiped(int memberId) {
		if (!state.equals(ControlState.READY))
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");

		member = library.member(memberId);
		if (member == null) {
			userInterface.display("Invalid memberId");
			return;
		}
		if (library.setMemberCanBorrow(member)) {
			pending = new ArrayList<>();
			userInterface.setState(BorrowBookUserInterface.UserInterfaceState.SCANNING);
			state = ControlState.SCANNING;
		} else {
			userInterface.display("Member cannot borrow at this time");
			userInterface.setState(BorrowBookUserInterface.UserInterfaceState.RESTRICTED);
		}
	}

	public void scanned(int bookId) {
		book = null;
		if (!state.equals(ControlState.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}
		book = library.setBook(bookId);
		if (book == null) {
			userInterface.display("Invalid bookId");
			return;
		}
		if (!book.setAvailble()) {
			userInterface.display("Book cannot be borrowed");
			return;
		}
		pending.add(book);
		for (Book book : pending) {
			userInterface.display(book.toString());
		}
		if (library.setLoansRemainingForMember(member) - pending.size() == 0) {
			userInterface.display("Loan limit reached");
			complete();
		}
	}

	public void complete() {
		if (pending.size() == 0) {
			cancel();
		} else {
			userInterface.display("\nFinal Borrowing List");
			for (Book book : pending) {
				userInterface.display(book.toString());
			}
			completed = new ArrayList<Loan>();
			userInterface.setState(BorrowBookUserInterface.UserInterfaceState.FINALISING);
			state = ControlState.FINALISING;
		}
	}

	public void commitLoans() {
		if (!state.equals(ControlState.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}
		for (Book book : pending) {
			Loan loan = library.issueLoan(book, member);
			completed.add(loan);
		}
		userInterface.display("Completed Loan Slip");
		for (Loan loan : completed) {
			userInterface.display(loan.toString());
		}
		userInterface.setState(BorrowBookUserInterface.UserInterfaceState.COMPLETED);
		state = ControlState.COMPLETED;
	}

	public void cancel() {
		userInterface.setState(BorrowBookUserInterface.UserInterfaceState.CANCELLED);
		state = ControlState.CANCELLED;
	}

}
