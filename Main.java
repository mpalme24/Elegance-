import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Main {

	private static Scanner keybordInput;
	private static library Library;
	private static String MENU;
	private static Calendar Calander;
	private static SimpleDateFormat formatDate;

	private static String Get_menu() {
		StringBuilder sb = new StringBuilder();

		sb.append("\nLibrary Main Menu\n\n").append("  M  : add member\n").append("  LM : list members\n").append("\n")
				.append("  B  : add book\n").append("  LB : list books\n").append("  FB : fix books\n").append("\n")
				.append("  L  : take out a loan\n").append("  R  : return a loan\n").append("  LL : list loans\n")
				.append("\n").append("  P  : pay fine\n").append("\n").append("  T  : increment date\n")
				.append("  Q  : quit\n").append("\n").append("Choice : ");

		return sb.toString();
	}

	public static void main(String[] args) {
		try {
			keybordInput = new Scanner(System.in);
			Library = library.INSTANCE();
			Calander = Calendar.INSTANCE();
			formatDate = new SimpleDateFormat("dd/MM/yyyy");

			for (member m : Library.MEMBERS()) {
				output(m);
			}
			output(" ");
			for (book b : Library.BOOKS()) {
				output(b);
			}

			MENU = Get_menu();

			boolean e = false;

			while (!e) {

				output("\n" + formatDate.format(Calander.Date()));
				String c = input(MENU);

				switch (c.toUpperCase()) {

				case "M":
					ADD_MEMBER();
					break;

				case "LM":
					MEMBERS();
					break;

				case "B":
					ADD_BOOK();
					break;

				case "LB":
					BOOKS();
					break;

				case "FB":
					FIX_BOOKS();
					break;

				case "L":
					BORROW_BOOK();
					break;

				case "R":
					RETURN_BOOK();
					break;

				case "LL":
					CURRENT_LOANS();
					break;

				case "P":
					FINES();
					break;

				case "T":
					INCREMENT_DATE();
					break;

				case "Q":
					e = true;
					break;

				default:
					output("\nInvalid option\n");
					break;
				}

				library.SAVE();
			}
		} catch (RuntimeException e) {
			output(e);
		}
		output("\nEnded\n");
	}

	private static void FINES() {
		new PayFineUI(new PayFineControl()).RuN();
	}

	private static void CURRENT_LOANS() {
		output("");
		for (loan loan : Library.CurrentLoans()) {
			output(loan + "\n");
		}
	}

	private static void BOOKS() {
		output("");
		for (book book : Library.BOOKS()) {
			output(book + "\n");
		}
	}

	private static void MEMBERS() {
		output("");
		for (member member : Library.MEMBERS()) {
			output(member + "\n");
		}
	}

	private static void BORROW_BOOK() {
		new BorrowBookUI(new BorrowBookControl()).run();
	}

	private static void RETURN_BOOK() {
		new ReturnBookUI(new ReturnBookControl()).RuN();
	}

	private static void FIX_BOOKS() {
		new FixBookUI(new FixBookControl()).RuN();
	}

	private static void INCREMENT_DATE() {
		try {
			int days = Integer.valueOf(input("Enter number of days: ")).intValue();
			Calander.incrementDate(days);
			Library.checkCurrentLoans();
			output(formatDate.format(Calander.Date()));

		} catch (NumberFormatException e) {
			output("\nInvalid number of days\n");
		}
	}

	private static void ADD_BOOK() {

		String A = input("Enter bookAuthor: ");
		String T = input("Enter bookTitle: ");
		String C = input("Enter call number: ");
		book B = Library.Add_book(A, T, C);
		output("\n" + B + "\n");

	}

	private static void ADD_MEMBER() {
		try {
			String LN = input("Enter last name: ");
			String FN = input("Enter first name: ");
			String EM = input("Enter email: ");
			int PN = Integer.valueOf(input("Enter phone number: ")).intValue();
			member M = Library.Add_mem(LN, FN, EM, PN);
			output("\n" + M + "\n");

		} catch (NumberFormatException e) {
			output("\nInvalid phone number\n");
		}

	}

	private static String input(String prompt) {
		System.out.print(prompt);
		return keybordInput.nextLine();
	}

	private static void output(Object object) {
		System.out.println(object);
	}

}
