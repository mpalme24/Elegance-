import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Loan implements Serializable {
	
	public static enum LoanState { CURRENT, OVER_DUE, DISCHARGED };
	
	private int loanId;
	private Book book;
	private Member member;
	private Date dueDate;
	private LoanState loanState;

	public Loan(int loanId, Book book, Member member, Date dueDate) {
		this.loanId = loanId;
		this.book = book;
		this.member = member;
		this.dueDate = dueDate;
		this.loanState = LoanState.CURRENT;
	}

	public void checkOverDue() {
		if (loanState == LoanState.CURRENT &&
			Calendar.instanceCalendar().date().after(dueDate)) {
			this.loanState = LoanState.OVER_DUE;			
		}
	}

	
	public boolean getOverDueLoan() {
		return loanState == LoanState.OVER_DUE;
	}

	public Integer getLoanId() {
		return loanId;
	}

	public Date getDueDate() {
		return dueDate;
	}
	
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		StringBuilder loanStringBuilder = new StringBuilder();
		loanStringBuilder.append("Loan:  ");
		loanStringBuilder.append(loanId);
		loanStringBuilder.append("\n");
		loanStringBuilder.append("  Borrower ");
		loanStringBuilder.append(member.getId());
		loanStringBuilder.append(" : ");
		loanStringBuilder.append(member.getLastName());
		loanStringBuilder.append(", ");
		loanStringBuilder.append(member.getFirstName());
		loanStringBuilder.append("\n");
		loanStringBuilder.append("  Book ");
		loanStringBuilder.append(book.getBookId());
		loanStringBuilder.append(" : ");
		loanStringBuilder.append(book.getBookTitle());
		loanStringBuilder.append("\n");
		loanStringBuilder.append("  DueDate: ");
		loanStringBuilder.append(dateFormat.format(dueDate));
		loanStringBuilder.append("\n");
		loanStringBuilder.append("  State: ");
		loanStringBuilder.append(loanState);
		return loanStringBuilder.toString();
	}

	public Member Member() {
		return member;
	}


	public Book Book() {
		return book;
	}

	public void discharge() {
		loanState = LoanState.DISCHARGED;		
	}
}
