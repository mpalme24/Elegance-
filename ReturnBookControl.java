public class ReturnBookControl {

	private ReturnBookUI returnBookUi;
	private enum RetunrBookControlState { INITIALISED, READY, INSPECTING };
	private RetunrBookControlState state;
	
	private library library;
	private loan currentLoan;
	

	public ReturnBookControl() {
		this.library = library.instanceLibrary();
		state = RetunrBookControlState.INITIALISED;
	}
	
	
	public void returnBookSetUi(ReturnBookUI ui) {
		if (!state.equals(RetunrBookControlState.INITIALISED)) {
			throw new RuntimeException("ReturnBookControl: cannot call setUI except in INITIALISED state");
		}	
		this.returnBookUi = ui;
		ui.setReturnState(ReturnBookUI.ReturnBookUiState.READY);
		state = RetunrBookControlState.READY;		
	}


	public void bookScanned(int bookId) {
		if (!state.equals(RetunrBookControlState.READY)) {
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
		if (currentLoan.getOverDueLoan()) {
			overdueFine = library.getOverdueFine(currentLoan);
		}
		returnBookUi.display("Inspecting");
		returnBookUi.display(currentBook.toString());
		returnBookUi.display(currentLoan.toString());
		
		if (currentLoan.getOverDueLoan()) {
			returnBookUi.display(String.format("\nOverdue fine : $%.2f", overdueFine));
		}
		returnBookUi.setReturnState(ReturnBookUI.ReturnBookUiState.INSPECTING);
		state = RetunrBookControlState.INSPECTING;		
	}


	public void bookScanningCompleted() {
		if (!state.equals(RetunrBookControlState.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
		}	
		returnBookUi.setReturnState(ReturnBookUI.ReturnBookUiState.COMPLETED);		
	}


	public void dischargeLoan(boolean isBookDamaged) {
		if (!state.equals(RetunrBookControlState.INSPECTING)) {
			throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
		}	
		library.setDischargeLoan(currentLoan, isBookDamaged);
		currentLoan = null;
		returnBookUi.setReturnState(ReturnBookUI.ReturnBookUiState.READY);
		state = RetunrBookControlState.READY;				
	}


}
