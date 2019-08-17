import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Member implements Serializable {

	private String memberLastName;
	private String memberFirstName;
	private String memberEmail;
	private int memberPhoneNumber;
	private int memberId;
	private double memberFines;
	
	private Map<Integer, Loan> memberLoans;

	
	public Member(String lastName, String firstName, String email, int phoneNo, int id) {
		this.memberLastName = lastName;
		this.memberFirstName = firstName;
		this.memberEmail = email;
		this.memberPhoneNumber = phoneNo;
		this.memberId = id;
		this.memberLoans = new HashMap<>();//not sure what this do could someone fix workitout so is can update it
	}

	
	public String toString() {
		StringBuilder memberStringBuilder = new StringBuilder();
		memberStringBuilder.append("Member:  ");
		memberStringBuilder.append(memberId);
		memberStringBuilder.append("\n");
		memberStringBuilder.append("  Name:  ");
		memberStringBuilder.append(memberLastName);
		memberStringBuilder.append(", ");
		memberStringBuilder.append(memberFirstName);
		memberStringBuilder.append("\n");
		memberStringBuilder.append("  Email: ");
		memberStringBuilder.append(memberEmail);
		memberStringBuilder.append("\n");
		memberStringBuilder.append("  Phone: ");
		memberStringBuilder.append(memberPhoneNumber);
		memberStringBuilder.append("\n");
		memberStringBuilder.append(String.format("  Fines Owed :  $%.2f", memberFines));
		memberStringBuilder.append("\n");
		
		for (Loan loan : memberLoans.values()) {
			memberStringBuilder.append(loan).append("\n");
		}		  
		return memberStringBuilder.toString();
	}

	public int getId() {
		return memberId;
	}

	public List<Loan> getLoans() {
		return new ArrayList<Loan>(memberLoans.values());
	}

	
	public int getNumberOfCurrentLoans() {
		return memberLoans.size();
	}

	
	public double getFinesOwed() {
		return memberFines;
	}

	
	public void takeOutLoan(Loan loan) {
		if (!memberLoans.containsKey(loan.getLoanId())) {
			memberLoans.put(loan.getLoanId(), loan);
		}
		else {
			throw new RuntimeException("Duplicate loan added to member");
		}		
	}

	
	public String getLastName() {
		return memberLastName;
	}

	
	public String getFirstName() {
		return memberFirstName;
	}


	public void addFine(double fine) {
		memberFines += fine;
	}
	
	public double payFine(double amount) {
		if (amount < 0) {
			throw new RuntimeException("Member.payFine: amount must be positive");
		}
		double change = 0;
		if (amount > memberFines) {
			change = amount - memberFines;
			memberFines = 0;
		}
		else {
			memberFines -= amount;
		}
		return change;
	}
	public void dischargeLoan(Loan loan) {
		if (memberLoans.containsKey(loan.getLoanId())) {
			memberLoans.remove(loan.getLoanId());
		}
		else {
			throw new RuntimeException("No such loan held by member");
		}		
	}
}
