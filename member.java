import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class member implements Serializable {

	private String ln;
	private String fn;
	private String em;
	private int pn;
	private int ID;
	private double fines;
	
	private Map<Integer, loan> lns;

	
	public member(String lastName, String firstName, String email, int phoneNo, int id) {
		this.ln = lastName;
		this.fn = firstName;
		this.em = email;
		this.pn = phoneNo;
		this.ID = id;
		
		this.lns = new HashMap<>();
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Member:  ").append(ID).append("\n")
		  .append("  Name:  ").append(ln).append(", ").append(fn).append("\n")
		  .append("  Email: ").append(em).append("\n")
		  .append("  Phone: ").append(pn)
		  .append("\n")
		  .append(String.format("  Fines Owed :  $%.2f", fines))
		  .append("\n");
		
		for (loan loan : lns.values()) {
			sb.append(loan).append("\n");
		}		  
		return sb.toString();
	}

	
	public int GeT_ID() {
		return ID;
	}

	
	public List<loan> GeT_LoAnS() {
		return new ArrayList<loan>(lns.values());
	}

	
	public int Number_Of_Current_Loans() {
		return lns.size();
	}

	
	public double Fines_OwEd() {
		return fines;
	}

	
	public void Take_Out_Loan(loan loan) {
		if (!lns.containsKey(loan.ID())) {
			lns.put(loan.ID(), loan);
		}
		else {
			throw new RuntimeException("Duplicate loan added to member");
		}		
	}

	
	public String Get_LastName() {
		return ln;
	}

	
	public String Get_FirstName() {
		return fn;
	}


	public void Add_Fine(double fine) {
		fines += fine;
	}
	
	public double payFine(double amount) {
		if (amount < 0) {
			throw new RuntimeException("Member.payFine: amount must be positive");
		}
		double change = 0;
		if (amount > fines) {
			change = amount - fines;
			fines = 0;
		}
		else {
			fines -= amount;
		}
		return change;
	}


	public void dIsChArGeLoAn(loan loan) {
		if (lns.containsKey(loan.ID())) {
			lns.remove(loan.ID());
		}
		else {
			throw new RuntimeException("No such loan held by member");
		}		
	}

}
