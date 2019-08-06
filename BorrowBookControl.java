import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {
	
	private BorrowBookUI ui;
	
	private library library;
	private member member;
	private enum CONTROL_STATE { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private CONTROL_STATE state;
	
	private List<book> pending;
	private List<loan> completed;
	private book book;
	
	
	public BorrowBookControl() {
		this.library = library.INSTANCE();
		state = CONTROL_STATE.INITIALISED;
	}
	

	public void setUI(BorrowBookUI ui) {
		if (!state.equals(CONTROL_STATE.INITIALISED)) 
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");
			
		this.ui = ui;
		ui.Set_State(BorrowBookUI.UI_STATE.READY);
		state = CONTROL_STATE.READY;		
	}

		
	public void cardSwiped(int memberId) {
		if (!state.equals(CONTROL_STATE.READY)) 
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
			
		member = library.MEMBER(memberId);
		if (member == null) {
			ui.Display("Invalid memberId");
			return;
		}
		if (library.MEMBER_CAN_BORROW(member)) {
			pending = new ArrayList<>();
			ui.Set_State(BorrowBookUI.UI_STATE.SCANNING);
			state = CONTROL_STATE.SCANNING; }
		else 
		{
			ui.Display("Member cannot borrow at this time");
			ui.Set_State(BorrowBookUI.UI_STATE.RESTRICTED); }}
	
	
	public void cardScanned(int bookId) {
		book = null;
		if (!state.equals(CONTROL_STATE.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}	
		book = library.Book(bookId);
		if (book == null) {
			ui.Display("Invalid bookId");
			return;
		}
		if (!book.AVAILABLE()) {
			ui.Display("Book cannot be borrowed");
			return;
		}
		pending.add(book);
		for (book B : pending) {
			ui.Display(B.toString());
		}
		if (library.Loans_Remaining_For_Member(member) - pending.size() == 0) {
			ui.Display("Loan limit reached");
			complete();
		}
	}
	
	
	public void complete() {
		if (pending.size() == 0) {
			cancel();
		}
		else {
			ui.Display("\nFinal Borrowing List");
			for (book B : pending) {
				ui.Display(B.toString());
			}
			completed = new ArrayList<loan>();
			ui.Set_State(BorrowBookUI.UI_STATE.FINALISING);
			state = CONTROL_STATE.FINALISING;
		}
	}


	public void Commit_Loans() {
		if (!state.equals(CONTROL_STATE.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}	
		for (book B : pending) {
			loan LOAN = library.ISSUE_LAON(B, member);
			completed.add(LOAN);			
		}
		ui.Display("Completed Loan Slip");
		for (loan LOAN : completed) {
			ui.Display(LOAN.toString());
		}
		ui.Set_State(BorrowBookUI.UI_STATE.COMPLETED);
		state = CONTROL_STATE.COMPLETED;
	}

	
	public void cancel() {
		ui.Set_State(BorrowBookUI.UI_STATE.CANCELLED);
		state = CONTROL_STATE.CANCELLED;
	}
	
	
}
