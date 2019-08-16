import java.util.Scanner;

public class PayFineUI {
	public static enum UiFineState {
		INITIALISED, READY, PAYING, COMPLETED, CANCELLED
	};

	private PayFineControl control;
	private Scanner keybordInput;
	private UiFineState fineState;

	public PayFineUI(PayFineControl newControl) {
		this.control = newControl;
		keybordInput = new Scanner(System.in);
		fineState = UiFineState.INITIALISED;
		newControl.Set_UI(this);
	}

	public void setFineState(UiFineState newFineState) {
		this.fineState = newFineState;
	}

	public void run() {
		output("Pay Fine Use Case UI\n");

		while (true) {

			switch (fineState) {

			case READY:
				String membercard = input("Swipe member card (press <enter> to cancel): ");
				if (membercard.length() == 0) {
					control.CaNcEl();
					break;
				}
				try {
					Integer memberIdInteger = Integer.valueOf(membercard);
					int memberId = memberIdInteger.intValue();
					control.Card_Swiped(memberId);
				} catch (NumberFormatException e) {
					output("Invalid memberId");
				}
				break;

			case PAYING:
				double fineAmount = 0;
				String inputAmount = input("Enter amount (<Enter> cancels) : ");
				if (inputAmount.length() == 0) {
					control.CaNcEl();
					break;
				}
				try {
					Double inputAmountDouble = Double.valueOf(inputAmount);
					fineAmount = inputAmountDouble.doubleValue();
				} catch (NumberFormatException e) {
				}
				if (fineAmount <= 0) {
					output("Amount must be positive");
					break;
				}
				control.PaY_FiNe(fineAmount);
				break;

			case CANCELLED:
				output("Pay Fine process cancelled");
				return;

			case COMPLETED:
				output("Pay Fine process complete");
				return;

			default:
				output("Unhandled state");
				throw new RuntimeException("FixBookUI : unhandled state :" + fineState);

			}
		}
	}

	private String input(String prompt) {
		System.out.print(prompt);
		return keybordInput.nextLine();
	}

	private void output(Object object) {
		System.out.println(object);
	}

	public void display(Object object) {
		output(object);
	}
}
