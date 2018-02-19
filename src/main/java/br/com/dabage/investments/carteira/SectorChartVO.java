/**
 * 
 */
package br.com.dabage.investments.carteira;

/**
 * @author Douglas dos Santos Araujo (dogaum@gmail.com)
 *
 */
public class SectorChartVO {

	public SectorChartVO(String sector) {
		this.sector = sector;
	}

	private String sector;

	private Double value;

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sector == null) ? 0 : sector.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SectorChartVO other = (SectorChartVO) obj;
		if (sector == null) {
			if (other.sector != null)
				return false;
		} else if (!sector.equals(other.sector))
			return false;
		return true;
	}

}
