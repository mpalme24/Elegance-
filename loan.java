import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class loan implements Serializable {
	
	public static enum LOAN_STATE { CURRENT, OVER_DUE, DISCHARGED };
	
	private int ID;
	private book B;
	private member M;
	private date D;
	private loanState state;

	
	public loan(int loanId, book book, member member, Date dueDate) {
		this.ID = loanId;
		this.b = book;
		this.m = member;
		this.d = dueDate;
		this.state = LOAN_STATE.CURRENT;
	}

	
	public void checkOverDue() {
		if (state == LOAN_STATE.CURRENT &&
			Calendar.INSTANCE().Date().after(D)) {
			this.state = LOAN_STATE.OVER_DUE;			
		}
	}

	
	public boolean overDue() {
		return state == LOAN_STATE.OVER_DUE;
	}

	
	public Integer ID() {
		return ID;
	}


	public Date getDueDate() {
		return D;
	}
	
	
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder sb = new StringBuilder();
		sb.append("Loan:  ").append(ID).append("\n")
		  .append("  Borrower ").append(m.getID()).append(" : ")
		  .append(m.getLastName()).append(", ").append(m.getFirstName()).append("\n")
		  .append("  Book ").append(b.ID()).append(" : " )
		  .append(b.title()).append("\n")
		  .append("  DueDate: ").append(sdf.format(D)).append("\n")
		  .append("  State: ").append(state);		
		return sb.toString();
	}


	public member member() {
		return m;
	}


	public book book() {
		return b;
	}


	public void discharge() {
		state = loanStateDischarged;		
	}

}
