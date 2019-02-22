package io.esoma.khr.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * The entity that models a credentials package for a koalibee.
 * 
 * @author Eddy Soma
 *
 */
@Entity
@Table(name = "CREDENTIALS")
public class Credentials implements Serializable {

	private static final long serialVersionUID = 1L;

	private int credentialsId;
	private String email;
	private String passwordHash;
	private String passwordSalt;
	private int koalibeeId;

	public Credentials() {
		super();
	}

	public Credentials(int credentialsId) {
		super();
		this.credentialsId = credentialsId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credentialsIdGen")
	@SequenceGenerator(name = "credentialsIdGen", sequenceName = "SEQ_CREDENTIALS_ID", allocationSize = 1)
	@Column(name = "CREDENTIALS_ID")
	public int getCredentialsId() {
		return credentialsId;
	}

	public void setCredentialsId(int credentialsId) {
		this.credentialsId = credentialsId;
	}

	@Column(name = "EMAIL")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "PASSWORD_HASH")
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	@Column(name = "PASSWORD_SALT")
	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	@Column(name = "KOALIBEE_ID")
	public int getKoalibeeId() {
		return koalibeeId;
	}

	public void setKoalibeeId(int koalibeeId) {
		this.koalibeeId = koalibeeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + credentialsId;
		result = prime * result + koalibeeId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Credentials other = (Credentials) obj;
		if (credentialsId != other.credentialsId) {
			return false;
		}
		if (koalibeeId != other.koalibeeId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Credentials [credentialsId=" + credentialsId + ", email=" + email + ", koalibeeId=" + koalibeeId + "]";
	}

}
