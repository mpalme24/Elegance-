public class ReturnBookControl {

	private ReturnBookUI Ui;

	private enum CONTROLSTATE {
		INITIALISED, READY, INSPECTING
	};

	private CONTROLSTATE state;

	private library library1;
	private loan currentLoan;

	public ReturnBookControl() {
		this.library1 = library1.INSTANCE();
		state = CONTROLSTATE.INITIALISED;
	}

	public void setReturnBookUi(ReturnBookUI newUI) {
		if (!state.equals(CONTROLSTATE.INITIALISED)) {
			throw new RuntimeException("ReturnBookControl: cannot call setUI except in INITIALISED state");
		}
		this.Ui = newUI;
		newUI.Set_State(ReturnBookUI.UI_STATE.READY);
		state = CONTROLSTATE.READY;
	}

	public void bookScanned(int bookID) {
		if (!state.equals(CONTROLSTATE.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call bookScanned except in READY state");
		}
		book currentBook = library1.Book(bookID);

		if (currentBook == null) {
			Ui.display("Invalid Book Id");
			return;
		}
		if (!currentBook.On_loan()) {
			Ui.display("Book has not been borrowed");
			return;
		}
		currentLoan = library1.LOAN_BY_BOOK_ID(bookID);
		double Over_Due_Fine = 0.0;
		if (currentLoan.OVer_Due()) {
			Over_Due_Fine = library1.CalculateOverDueFine(currentLoan);
		}
		Ui.display("Inspecting");
		Ui.display(currentBook.toString());
		Ui.display(currentLoan.toString());

		if (currentLoan.OVer_Due()) {
			Ui.display(String.format("\nOverdue fine : $%.2f", Over_Due_Fine));
		}
		Ui.Set_State(ReturnBookUI.UI_STATE.INSPECTING);
		state = CONTROLSTATE.INSPECTING;
	}

	public void setScanningComplete() {
		if (!state.equals(CONTROLSTATE.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
		}
		Ui.Set_State(ReturnBookUI.UI_STATE.COMPLETED);
	}

	public void dischargeloan(boolean isDamaged) {
		if (!state.equals(CONTROLSTATE.INSPECTING)) {
			throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
		}
		library1.Discharge_loan(currentLoan, isDamaged);
		currentLoan = null;
		Ui.Set_State(ReturnBookUI.UI_STATE.READY);
		state = CONTROLSTATE.READY;
	}

}
