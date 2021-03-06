package br.com.dabage.investments.repositories;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;

/**
 * Base class for document classes.
 * 
 */
public class AbstractDocument implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	@Id
	protected BigInteger id;

	/**
	 * Returns the identifier of the document.
	 * 
	 * @return the id
	 */
	public BigInteger getId() {
		return id;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (this.id == null || obj == null || !(this.getClass().equals(obj.getClass()))) {
			return false;
		}

		AbstractDocument that = (AbstractDocument) obj;

		return this.id.equals(that.getId());
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}

}