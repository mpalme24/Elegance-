public class ReturnBookControl {

	private ReturnBookUI returnBookUI;
	private enum ReturnBookControlState { INITIALISED, READY, INSPECTING };
	private ReturnBookControlState state;
	
	private library library;
	private loan currentLoan;
	

	public ReturnBookControl() {
		this.library = library.INSTANCE();
		state = ReturnBookControlState.INITIALISED;
	}
	
	
	public void setReturnBookUI(ReturnBookUI uI) {
		if (!state.equals(ReturnBookControlState.INITIALISED)) {
			throw new RuntimeException("ReturnBookControl: cannot call setUI except in INITIALISED state");
		}	
		this.returnBookUI = uI;
		uI.setReturnState(ReturnBookUI.ReturnBookUIState.READY);
		state = ReturnBookControlState.READY;		
	}


	public void bookScanned(int bookId) {
		if (!state.equals(ReturnBookControlState.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call bookScanned except in READY state");
		}	
		book currentBook = library.Book(bookId);
		
		if (currentBook == null) {
			returnBookUI.display("Invalid Book Id");
			return;
		}
		if (!currentBook.On_loan()) {
			returnBookUI.display("Book has not been borrowed");
			return;
		}		
		currentLoan = library.LOAN_BY_BOOK_ID(bookId);	
		double overdueFine = 0.0;
		if (currentLoan.OVer_Due()) {
			overdueFine = library.CalculateOverDueFine(currentLoan);
		}
		returnBookUI.display("Inspecting");
		returnBookUI.display(currentBook.toString());
		returnBookUI.display(currentLoan.toString());
		
		if (currentLoan.OVer_Due()) {
			returnBookUI.display(String.format("\nOverdue fine : $%.2f", overdueFine));
		}
		returnBookUI.setReturnState(ReturnBookUI.ReturnBookUIState.INSPECTING);
		state = ReturnBookControlState.INSPECTING;		
	}


	public void bookScanningCompleted() {
		if (!state.equals(ReturnBookControlState.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
		}	
		returnBookUI.setReturnState(ReturnBookUI.ReturnBookUIState.COMPLETED);		
	}


	public void dischargeLoan(boolean isBookDamaged) {
		if (!state.equals(ReturnBookControlState.INSPECTING)) {
			throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
		}	
		library.Discharge_loan(currentLoan, isBookDamaged);
		currentLoan = null;
		returnBookUI.setReturnState(ReturnBookUI.ReturnBookUIState.READY);
		state = ReturnBookControlState.READY;				
	}


}
