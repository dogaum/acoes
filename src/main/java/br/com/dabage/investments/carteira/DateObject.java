/**
 * 
 */
package br.com.dabage.investments.carteira;

import java.util.Date;

/**
 * @author dogaum
 *
 */
public class DateObject implements Comparable<DateObject> {

	public DateObject (Date date, Object data) {
		this.date = date;
		this.data = data;
	}

	private Date date;

	private Object data;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public int compareTo(DateObject o) {
		if (this.date == o.date)
			return 0;

		if (o.date.after(this.date))
			return 1;
		else
			return -1;
	}

}
