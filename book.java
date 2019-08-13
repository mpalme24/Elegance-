import java.io.Serializable;


@SuppressWarnings("serial")
public class book implements Serializable {
	
	private String title;
	private String author;
	private String callNo;
	private int ID;
	
	private enum STATE { available, onLoan, damaged, reserved };
	private STATE State;
	
	
	public book(String author, String title, String callNo, int id) {
		this.author = author;
		this.title = title;
		this.callNo = callNo;
		this.ID = id;
		this.state = state.AVAILABLE;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Book: ").append(ID).append("\n")
		  .append("  Title:  ").append(title).append("\n")
		  .append("  Author: ").append(author).append("\n")
		  .append("  CallNo: ").append(callNo).append("\n")
		  .append("  State:  ").append(state);
		
		return sb.toString();
	}

	public Integer ID() {
		return ID;
	}

	public String title() {
		return title;
	}


	
	public boolean available() {
		return state == state.available;
	}

	
	public boolean onLoan() {
		return State == state.onLoan;
	}

	
	public boolean isDamaged() {
		return State == state.damaged;
	}

	
	public void Borrow() {
		if (state.equals(state.available)) {
			state = state.onLoan;
		}
		else {
			throw new RuntimeException(String.format("Book: cannot borrow while book is in state: %s", state));
		}
		
	}


	public void Return(boolean damaged) {
		if (state.equals(state.onLoan)) {
			if (damaged) {
				state = state.damaged;
			}
			else {
				state = state.available;
			}
		}
		else {
			throw new RuntimeException(String.format("Book: cannot Return while book is in state: %s", state));
		}		
	}

	
	public void repair() {
		if (state.equals(state.damaged)) {
			state = state.available;
		}
		else {
			throw new RuntimeException(String.format("Book: cannot repair while book is in state: %s", state));
		}
	}


}
