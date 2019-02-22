package io.esoma.khr.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * The entity that models a user of Koality Harmonia.
 * 
 * @author Eddy Soma
 *
 */
@Entity
@Table(name = "KOALIBEE")
public class Koalibee implements Serializable {

	private static final long serialVersionUID = 1L;

	private int koalibeeId;
	private String firstName;
	private String lastName;
	private String email;
	private int etaBalance;
	private byte[] avatar;
	private String avatarType;

	private List<Album> albumList;
	private String avatarDataUrl;

	public Koalibee() {
		super();
	}

	public Koalibee(int koalibeeId) {
		super();
		this.koalibeeId = koalibeeId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "koalibeeIdGen")
	@SequenceGenerator(name = "koalibeeIdGen", sequenceName = "SEQ_KOALIBEE_ID", allocationSize = 1)
	@Column(name = "KOALIBEE_ID")
	public int getKoalibeeId() {
		return koalibeeId;
	}

	public void setKoalibeeId(int koalibeeId) {
		this.koalibeeId = koalibeeId;
	}

	@Column(name = "FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "LAST_NAME")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "EMAIL")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "ETA_BALANCE")
	public int getEtaBalance() {
		return etaBalance;
	}

	public void setEtaBalance(int etaBalance) {
		this.etaBalance = etaBalance;
	}

	@Lob
	@Column(name = "AVATAR", columnDefinition = "BLOB")
	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	@Column(name = "AVATAR_TYPE")
	public String getAvatarType() {
		return avatarType;
	}

	public void setAvatarType(String avatarType) {
		this.avatarType = avatarType;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "KOALIBEE_ALBUM", joinColumns = @JoinColumn(name = "KOALIBEE_ID"), inverseJoinColumns = @JoinColumn(name = "ALBUM_ID"))
	public List<Album> getAlbumList() {
		return albumList;
	}

	public void setAlbumList(List<Album> albumList) {
		this.albumList = albumList;
	}

	@Transient
	public String getAvatarDataUrl() {
		return avatarDataUrl;
	}

	public void setAvatarDataUrl(String avatarDataUrl) {
		this.avatarDataUrl = avatarDataUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Koalibee other = (Koalibee) obj;
		if (koalibeeId != other.koalibeeId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Koalibee [koalibeeId=" + koalibeeId + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
				+ email + "]";
	}

}
