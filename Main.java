import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Main {

	private static Scanner keyboardInput;
	private static library library;
	private static String menu;
	private static Calendar calendar;
	private static SimpleDateFormat simpleDateFormat;

	private static String getMenu() {
		StringBuilder menuStringBuilder = new StringBuilder();
		menuStringBuilder.append("\nLibrary Main Menu\n\n");
		menuStringBuilder.append("  M  : add member\n");
		menuStringBuilder.append("  LM : list members\n");
		menuStringBuilder.append("\n");
		menuStringBuilder.append("  B  : add book\n");
		menuStringBuilder.append("  LB : list books\n");
		menuStringBuilder.append("  FB : fix books\n");
		menuStringBuilder.append("\n");
		menuStringBuilder.append("  L  : take out a loan\n");
		menuStringBuilder.append("  R  : return a loan\n");
		menuStringBuilder.append("  LL : list loans\n");
		menuStringBuilder.append("\n");
		menuStringBuilder.append("  P  : pay fine\n");
		menuStringBuilder.append("\n");
		menuStringBuilder.append("  T  : increment date\n");
		menuStringBuilder.append("  Q  : quit\n");
		menuStringBuilder.append("\n");
		menuStringBuilder.append("Choice : ");

		return menuStringBuilder.toString();
	}

	public static void main(String[] args) {
		try {
			keyboardInput = new Scanner(System.in);
			library = library.instanceLibrary();
			calendar = Calendar.instanceCalendar();
			simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			for (member member : library.currentMembers()) {
				output(member);
			}
			output(" ");
			for (book book : library.currentBooks()) {
				output(book);
			}

			menu = getMenu();

			boolean e = false;

			while (!e) {

				output("\n" + simpleDateFormat.format(calendar.date()));
				String c = input(menu);

				switch (c.toUpperCase()) {

				case "M": // add member
					addMember();
					break;

				case "LM"://list members
					listMembers();
					break;

				case "B"://add Book
					addBook();
					break;

				case "LB"://ListBooks
					listBooks();
					break;

				case "FB"://fixBook
					fixBook();
					break;

				case "L"://loanBook
					borrowBook();
					break;

				case "R"://return book
					returnBook();
					break;

				case "LL"://listloans
					listCurrentLoans();
					break;

				case "P"://pay
					payFines();
					break;

				case "T":
					incrementDate();
					break;

				case "Q"://quit
					e = true;
					break;

				default:
					output("\nInvalid option\n");
					break;
				}

				library.saveLibrary();
			}
		} catch (RuntimeException e) {
			output(e);
		}
		output("\nEnded\n");
	}

	private static void payFines() {
		PayFineControl PayFineControl = new PayFineControl();
		PayFineUI payFineUI = new PayFineUI(PayFineControl);
		payFineUI.runPayFineUI();
	}

	private static void listCurrentLoans() {
		output("");
		for (loan loan : library.currentLoans()) {
			output(loan + "\n");
		}
	}

	private static void listBooks() {
		output("");
		for (book book : library.currentBooks()) {
			output(book + "\n");
		}
	}

	private static void listMembers() {
		output("");
		for (member member : library.currentMembers()) {
			output(member + "\n");
		}
	}

	private static void borrowBook() {
		BorrowBookControl borrowBookControl = new BorrowBookControl();
		BorrowBookUI borrowBookUI = new BorrowBookUI(borrowBookControl);
		borrowBookUI.runBorrowBookUI();
	}

	private static void returnBook() {
		ReturnBookControl returnBookControl = new ReturnBookControl();
		ReturnBookUI returnBookUI = new ReturnBookUI(returnBookControl);
		returnBookUI.runReturnBookUI();
	}

	private static void fixBook() {
		FixBookControl fixBookControl = new FixBookControl();
		FixBookUI fixBookUI = new FixBookUI(fixBookControl);
		fixBookUI.runFixBookUI();
	}

	private static void incrementDate() {
		try {
			String dayInput = input("Enter number of days: ");
			Integer dayInteger = Integer.valueOf(dayInput);
			int daysInt = dayInteger.intValue();
			calendar.incrementDate(daysInt);
			library.checkCurrentLoans();
			output(simpleDateFormat.format(calendar.date()));
		} catch (NumberFormatException e) {
			output("\nInvalid number of days\n");
		}
	}

	private static void addBook() {

		String author = input("Enter author: ");
		String title = input("Enter title: ");
		String callNumber = input("Enter call number: ");
		book book = library.addBook(author, title, callNumber);
		output("\n" + book + "\n");

	}

	private static void addMember() {
		try {
			String lastName = input("Enter last name: ");
			String firstName = input("Enter first name: ");
			String email = input("Enter email: ");
			String phoneNumberString = input("Enter phone number: ");
			Integer phoneNumberInteger = Integer.valueOf(phoneNumberString);
			int phoneNumber = phoneNumberInteger.intValue();
			member member = library.addMember(lastName, firstName, email, phoneNumber);
			output("\n" + member + "\n");

		} catch (NumberFormatException e) {
			output("\nInvalid phone number\n");
		}
	}

	private static String input(String prompt) {
		System.out.print(prompt);
		return keyboardInput.nextLine();
	}

	private static void output(Object object) {
		System.out.println(object);
	}
}
