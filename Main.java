import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class Main {
	private static Scanner keybordInput;
	private static Library library;
	private static String menu;
	private static Calendar calendar;
	private static SimpleDateFormat simpleDateFormat;
	private static String mainMenu() {
		StringBuilder mainMenu = new StringBuilder();	
		mainMenu.append("\nLibrary Main Menu\n\n");
		mainMenu.append("  M  : add member\n");
		mainMenu.append("  LM : list members\n");
		mainMenu.append("\n");
		mainMenu.append("  B  : add book\n");
		mainMenu.append("  LB : list books\n");
		mainMenu.append("  FB : fix books\n");
		mainMenu.append("\n");
		mainMenu.append("  L  : take out a loan\n");
		mainMenu.append("  R  : return a loan\n");
		mainMenu.append("  LL : list loans\n");
		mainMenu.append("\n");
		mainMenu.append("  P  : pay fine\n");
		mainMenu.append("\n");
		mainMenu.append("  T  : increment date\n");
		mainMenu.append("  Q  : quit\n");
		mainMenu.append("\n");
		mainMenu.append("Choice : ");		  
		return mainMenu.toString();
	}
	public static void main(String[] args) {		
		try {			
			keybordInput = new Scanner(System.in);
			library = Library.INSTANCE();
			calendar = Calendar.INSTANCE();
			simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");	
			for (Member member : library.MEMBERS()) output(member);
			output(" ");
			for (book book : library.BOOKS()) {
				output(book);
			}
			menu = mainMenu();		
			boolean e = false;			
			while (!e) {
				
				output("\n" + simpleDateFormat.format(calendar.Date()));
				String calendar = input(menu);
				
				switch (calendar.toUpperCase()) {
				
				case "M": //add member
					addMember();
					break;
					
				case "LM": //list members
					listMembers();
					break;
					
				case "B": //add book
					addBook();
					break;
					
				case "LB": //list books
					listBooks();
					break;
					
				case "FB": //fix Book
					fixBook();
					break;
					
				case "L": //Loan book
					borrowBook();
					break;
					
				case "R": //Retrun Book
					returnBook();
					break;
					
				case "LL"://List loans
					listCurrentLoans();
					break;
					
				case "P": //pay
					payfines();
					break;
					
				case "T": 
					incrementDate();
					break;
					
				case "Q": //quit
					e = true;
					break;
					
				default: 
					output("\nInvalid option\n");
					break;
				}
				
				Library.SAVE();
			}			
		} catch (RuntimeException e) {
			output(e);
		}		
		output("\nEnded\n");
	}	
	private static void payfines() {
		PayFineControl PayFineControl = new PayFineControl();
		PayFineUI payFineUI= new PayFineUI(PayFineControl);
		payFineUI.RuN();
	}
	private static void listCurrentLoans() {
		output("");
		for (loan loan : library.CurrentLoans()) {
			output(loan + "\n");
		}		
	}
	private static void listBooks() {
		output("");
		for (book book : library.BOOKS()) {
			output(book + "\n");
		}		
	}
	private static void listMembers() {
		output("");
		for (Member member : library.MEMBERS()) {
			output(member + "\n");
		}		
	}
	private static void borrowBook() {
		BorrowBookControl borrowBookControl = new BorrowBookControl();
		BorrowBookUI borrowBookUI = new BorrowBookUI(borrowBookControl);
		borrowBookUI.run();
	}
	private static void returnBook() {
		ReturnBookControl returnBookControl = new ReturnBookControl();
		ReturnBookUI returnBookUI = new ReturnBookUI(returnBookControl);
		returnBookUI.RuN();		
	}
	private static void fixBook() {
		FixBookControl fixBookControl = new FixBookControl();
		FixBookUI fixBookUI=new FixBookUI(fixBookControl);
		fixBookUI.RuN();	
	}
	private static void incrementDate() {
		try {
			String dayInput=input("Enter number of days: ");
			Integer dayInteger=Integer.valueOf(dayInput);
			int days = dayInteger.intValue();
			calendar.incrementDate(days);
			library.checkCurrentLoans();
			Date dateDate = calendar.Date();
			String dateString = simpleDateFormat.format(dateDate);
			output(dateString);	
		} catch (NumberFormatException e) {
			 output("\nInvalid number of days\n");
		}
	}
	private static void addBook() {
		
		String A = input("Enter author: ");
		String T  = input("Enter title: ");
		String C = input("Enter call number: ");
		book B = library.Add_book(A, T, C);
		output("\n" + B + "\n");
		
	}
	private static void addMember() {
		try {
			String lastName = input("Enter last name: ");
			String firstName  = input("Enter first name: ");
			String emailAddress = input("Enter email: ");
			String phoneNumberString = input("Enter phone number: ");
			Integer phoneNumberInteger=Integer.valueOf(phoneNumberString);
			int phoneNumber = phoneNumberInteger.intValue();
			Member member = library.Add_mem(lastName, firstName, emailAddress, phoneNumber);
			output("\n" + member + "\n");			
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
