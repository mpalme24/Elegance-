import java.util.Scanner;

public class BorrowBookUI {
	public static enum BorrowBookUIState {
		INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED
	};

	private BorrowBookControl control;
	private Scanner keyboardInput;
	private BorrowBookUIState stateUI;

	public BorrowBookUI(BorrowBookControl inputControl) {
		this.control = inputControl;
		keyboardInput = new Scanner(System.in);
		stateUI = BorrowBookUIState.INITIALISED;
		inputControl.setBorrowBookUI(this);
	}

	private String input(String prompt) {
		System.out.print(prompt);
		return keyboardInput.nextLine();
	}

	private void output(Object object) {
		System.out.println(object);
	}

	public void setBorrowBookUIState(BorrowBookUIState stateUI) {
		this.stateUI = stateUI;
	}

	public void runBorrowBookUI() {
		output("Borrow Book Use Case UI\n");
		while (true) {
			switch (stateUI) {
			case CANCELLED:
				output("Borrowing Cancelled");
				return;
			case READY:
				String memberIdString = input("Swipe member card (press <enter> to cancel): ");
				if (memberIdString.length() == 0) {
					control.cancelBookBorrow();
					break;
				}
				try {
					Integer memberIdInteger = Integer.valueOf(memberIdString);
					control.memberCardSwiped(memberIdInteger.intValue());
				} catch (NumberFormatException e) {
					output("Invalid Member Id");
				}
				break;

			case RESTRICTED:
				input("Press <any key> to cancel");
				control.cancelBookBorrow();
				break;

			case SCANNING:
				String bookIdString = input("Scan Book (<enter> completes): ");
				if (bookIdString.length() == 0) {
					control.borrowComplete();
					break;
				}
				try {
					Integer bookIdInteger = Integer.valueOf(bookIdString).intValue();
					control.scannBook(bookIdInteger);

				} catch (NumberFormatException e) {
					output("Invalid Book Id");
				}
				break;

			case FINALISING:
				String answer = input("Commit loans? (Y/N): ");
				if (answer.toUpperCase().equals("N")) {// nothing checks for Yes "Y" weird
					control.cancelBookBorrow();

				} else {
					control.commitLoan();
					input("Press <any key> to complete ");
				}
				break;

			case COMPLETED:
				output("Borrowing Completed");
				return;

			default:
				output("Unhandled state");
				throw new RuntimeException("BorrowBookUI : unhandled state :" + stateUI);
			}
		}
	}

	public void displays(Object object) {
		output(object);
	}
}
