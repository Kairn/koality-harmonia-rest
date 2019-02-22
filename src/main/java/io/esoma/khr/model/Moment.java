package io.esoma.khr.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * The entity that models a daily moment on Koality Harmonia.
 * 
 * @author Eddy Soma
 *
 */
@Entity
@Table(name = "MOMENT")
public class Moment implements Serializable {

	private static final long serialVersionUID = 1L;

	private int momentId;
	private LocalDate postDate;
	private String postComment;

	private Koalibee koalibee;
	private String koalibeeName;

	public Moment() {
		super();
	}

	public Moment(int momentId, LocalDate postDate) {
		super();
		this.momentId = momentId;
		this.postDate = postDate;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "momentIdGen")
	@SequenceGenerator(name = "momentIdGen", sequenceName = "SEQ_MOMENT_ID", allocationSize = 1)
	@Column(name = "MOMENT_ID")
	public int getMomentId() {
		return momentId;
	}

	public void setMomentId(int momentId) {
		this.momentId = momentId;
	}

	@Column(name = "POST_DATE", columnDefinition = "DATE")
	public LocalDate getPostDate() {
		return postDate;
	}

	public void setPostDate(LocalDate postDate) {
		this.postDate = postDate;
	}

	@Column(name = "POST_COMMENT")
	public String getPostComment() {
		return postComment;
	}

	public void setPostComment(String postComment) {
		this.postComment = postComment;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "KOALIBEE_ID")
	public Koalibee getKoalibee() {
		return koalibee;
	}

	public void setKoalibee(Koalibee koalibee) {
		this.koalibee = koalibee;
	}

	@Transient
	public String getKoalibeeName() {
		return koalibeeName;
	}

	public void setKoalibeeName(String koalibeeName) {
		this.koalibeeName = koalibeeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + momentId;
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
		Moment other = (Moment) obj;
		if (momentId != other.momentId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Moment [momentId=" + momentId + ", postDate=" + postDate + "]";
	}

}
