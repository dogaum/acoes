/**
 * 
 */
package br.com.dabage.investments.news;

/**
 * @author dsaraujo
 *
 */
public enum NewsFilterType {
	DAY(0), LAST_WEEK(1), THIRTY_DAYS(2), INTERVAL(3);

	private Integer key;

	private NewsFilterType(Integer key) {
		this.key = key;
	}

	public Integer getKey() {
		return key;
	}
}
