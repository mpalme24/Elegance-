public class ReturnBookControl {

	private ReturnBookUI returnBookUi;
	private enum ReturnBookControlState { INITIALISED, READY, INSPECTING };
	private ReturnBookControlState state;
	
	private library library;
	private loan currentLoan;
	

	public ReturnBookControl() {
		this.library = library.instanceLibrary();
		state = ReturnBookControlState.INITIALISED;
	}
	
	
	public void setReturnBookUi(ReturnBookUI ui) {
		if (!state.equals(ReturnBookControlState.INITIALISED)) {
			throw new RuntimeException("ReturnBookControl: cannot call setUI except in INITIALISED state");
		}	
		this.returnBookUi = ui;
		ui.setReturnState(ReturnBookUI.ReturnBookUiState.READY);
		state = ReturnBookControlState.READY;		
	}


	public void bookScanned(int bookId) {
		if (!state.equals(ReturnBookControlState.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call bookScanned except in READY state");
		}	
		book currentBook = library.getBook(bookId);
		
		if (currentBook == null) {
			returnBookUi.display("Invalid Book Id");
			return;
		}
		if (!currentBook.getBookStateOnloan()) {
			returnBookUi.display("Book has not been borrowed");
			return;
		}		
		currentLoan = library.getLoanByBookId(bookId);	
		double overdueFine = 0.0;
		if (currentLoan.isLoanOverDue()) {
			overdueFine = library.getOverdueFine(currentLoan);
		}
		returnBookUi.display("Inspecting");
		returnBookUi.display(currentBook.toString());
		returnBookUi.display(currentLoan.toString());
		
		if (currentLoan.isLoanOverDue()) {
			returnBookUi.display(String.format("\nOverdue fine : $%.2f", overdueFine));
		}
		returnBookUi.setReturnState(ReturnBookUI.ReturnBookUiState.INSPECTING);
		state = ReturnBookControlState.INSPECTING;		
	}


	public void bookScanningCompleted() {
		if (!state.equals(ReturnBookControlState.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
		}	
		returnBookUi.setReturnState(ReturnBookUI.ReturnBookUiState.COMPLETED);		
	}


	public void dischargeLoan(boolean isBookDamaged) {
		if (!state.equals(ReturnBookControlState.INSPECTING)) {
			throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
		}	
		library.setDischargeLoan(currentLoan, isBookDamaged);
		currentLoan = null;
		returnBookUi.setReturnState(ReturnBookUI.ReturnBookUiState.READY);
		state = ReturnBookControlState.READY;				
	}


}
