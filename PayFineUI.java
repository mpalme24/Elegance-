import java.util.Scanner;

public class PayFineUI {
	public static enum PayFineUIState {
		INITIALISED, READY, PAYING, COMPLETED, CANCELLED
	};

	private PayFineControl control;
	private Scanner keyboardinput;
	private PayFineUIState fineUIState;

	public PayFineUI(PayFineControl control) {
		this.control = control;
		keyboardinput = new Scanner(System.in);
		fineUIState = PayFineUIState.INITIALISED;
		control.setPayFineUI(this);
	}

	public void setFineUIState(PayFineUIState state) {
		this.fineUIState = state;
	}

	public void runPayFineUI() {
		output("Pay Fine Use Case UI\n");
		while (true) {
			switch (fineUIState) {
			case READY:
				String memberIdString = input("Swipe member card (press <enter> to cancel): ");
				if (memberIdString.length() == 0) {
					control.cancelFinePayment();
					break;
				}
				try {
					Integer memberIdInteger = Integer.valueOf(memberIdString);
					int memberIdInt = memberIdInteger.intValue();
					control.cardSwipped(memberIdInt);
				} catch (NumberFormatException e) {
					output("Invalid memberId");
				}
				break;

			case PAYING:
				double amount = 0;
				String amountString = input("Enter amount (<Enter> cancels) : ");
				if (amountString.length() == 0) {
					control.cancelFinePayment();
					break;
				}
				try {
					Double amountDouble = Double.valueOf(amountString);
					amount = amountDouble.doubleValue();
				} catch (NumberFormatException e) {
				}
				if (amount <= 0) {
					output("Amount must be positive");
					break;
				}
				control.payFine(amount);
				break;

			case CANCELLED:
				output("Pay Fine process cancelled");
				return;

			case COMPLETED:
				output("Pay Fine process complete");
				return;

			default:
				output("Unhandled state");
				throw new RuntimeException("FixBookUI : unhandled state :" + fineUIState);

			}
		}
	}

	private String input(String prompt) {
		System.out.print(prompt);
		return keyboardinput.nextLine();
	}

	private void output(Object object) {
		System.out.println(object);
	}

	public void display(Object object) {
		output(object);
	}

}
