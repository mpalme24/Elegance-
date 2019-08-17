public class PayFineControl {
	
	private PayFineUI payFineUi;
	private enum FineControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private FineControlState fineState;
	
	private Library library;
	private Member member;

	public PayFineControl() {
		this.library = library.instanceLibrary();
		fineState = FineControlState.INITIALISED;
	}
	
	public void payFineUi(PayFineUI ui) {
		if (!fineState.equals(FineControlState.INITIALISED)) {
			throw new RuntimeException("PayFineControl: cannot call payFine except in INITIALISED state");
		}	
		this.payFineUi = ui;
		ui.setFIneUiState(PayFineUI.PayFineUiState.READY);
		fineState = FineControlState.READY;		
	}

	public void cardSwipped(int memberId) {
		if (!fineState.equals(FineControlState.READY)) {
			throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
		}	
		member = library.getMember(memberId);
		
		if (member == null) {
			payFineUi.display("Invalid Member Id");
			return;
		}
		payFineUi.display(member.toString());
		payFineUi.setFIneUiState(PayFineUI.PayFineUiState.PAYING);
		fineState = FineControlState.PAYING;
	}
	
	public void cancelFinePayment() {
		payFineUi.setFIneUiState(PayFineUI.PayFineUiState.CANCELLED);
		fineState = FineControlState.CANCELLED;
	}

	public double payFine(double fineAmount) {
		if (!fineState.equals(FineControlState.PAYING)) {
			throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
		}	
		double changeFromFine = member.payFine(fineAmount);
		if (changeFromFine > 0) {
			String change = String.format("Change: $%.2f", changeFromFine);
			payFineUi.display(change);
		}
		payFineUi.display(member.toString());
		payFineUi.setFIneUiState(PayFineUI.PayFineUiState.COMPLETED);
		fineState = FineControlState.COMPLETED;
		return changeFromFine;
	}

}
