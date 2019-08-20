public class PayFineControl {
	
	private PayFineUI payFineUI;
	private enum FineControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private FineControlState fineState;
	
	private library library;
	private member member;

	public PayFineControl() {
		this.library = library.instanceLibrary();
		fineState = FineControlState.INITIALISED;
	}
	
	public void setPayFineUI(PayFineUI uI) {
		if (!fineState.equals(FineControlState.INITIALISED)) {
			throw new RuntimeException("PayFineControl: cannot call payFine except in INITIALISED state");
		}	
		this.payFineUI = uI;
		uI.setFineUIState(PayFineUI.PayFineUIState.READY);
		fineState = FineControlState.READY;		
	}

	public void cardSwipped(int memberId) {
		if (!fineState.equals(FineControlState.READY)) {
			throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
		}	
		member = library.getMember(memberId);
		
		if (member == null) {
			payFineUI.display("Invalid Member Id");
			return;
		}
		payFineUI.display(member.toString());
		payFineUI.setFineUIState(PayFineUI.PayFineUIState.PAYING);
		fineState = FineControlState.PAYING;
	}
	
	public void cancelFinePayment() {
		payFineUI.setFineUIState(PayFineUI.PayFineUIState.CANCELLED);
		fineState = FineControlState.CANCELLED;
	}

	public double payFine(double fineAmount) {
		if (!fineState.equals(FineControlState.PAYING)) {
			throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
		}	
		double changeFromFine = member.payFine(fineAmount);
		if (changeFromFine > 0) {
			String change = String.format("Change: $%.2f", changeFromFine);
			payFineUI.display(change);
		}
		payFineUI.display(member.toString());
		payFineUI.setFineUIState(PayFineUI.PayFineUIState.COMPLETED);
		fineState = FineControlState.COMPLETED;
		return changeFromFine;
	}

}
